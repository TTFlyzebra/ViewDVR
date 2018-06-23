package com.longhorn.viewdvr.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.module.wifi.CommandType;
import com.longhorn.viewdvr.module.wifi.NioSocketTools;
import com.longhorn.viewdvr.module.wifi.ResultData;
import com.longhorn.viewdvr.module.wifi.SocketResult;
import com.longhorn.viewdvr.utils.ByteTools;
import com.longhorn.viewdvr.utils.FlyLog;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;


public class SetWifiActivity extends Activity implements View.OnClickListener, SocketResult {
    private LinearLayout set_left;
    private Button set_wifi_confirm, set_wifi_cancle;
    private ImageView set_show_pass;
    private EditText set_wifi_name, set_wifi_oldpswd, set_wifi_pswd1, set_wifi_pswd2;
    private boolean isShowPswd = false;
    private NioSocketTools nioSocketTools = NioSocketTools.getInstance();
    private boolean isStop = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setwifi);

        progressDialog = new ProgressDialog(this);

        set_left = findViewById(R.id.set_back);
        set_wifi_confirm = findViewById(R.id.set_wifi_confirm);
        set_wifi_cancle = findViewById(R.id.set_wifi_cancle);
        set_show_pass = findViewById(R.id.set_show_pass);
        set_wifi_name = findViewById(R.id.set_wifiname);
        set_wifi_oldpswd = findViewById(R.id.set_wifi_oldpswd);
        set_wifi_pswd1 = findViewById(R.id.set_wifi_pswd1);
        set_wifi_pswd2 = findViewById(R.id.set_wifi_pswd2);

        set_left.setOnClickListener(this);
        set_wifi_confirm.setOnClickListener(this);
        set_wifi_cancle.setOnClickListener(this);
        set_show_pass.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        nioSocketTools.registerSocketResult(this);
        isStop = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        nioSocketTools.sendCommand(CommandType.GET_WIFI_CFG);
    }

    @Override
    protected void onStop() {
        isStop = true;
        nioSocketTools.unregisterSocketResult(this);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back:
                finish();
                break;
            case R.id.set_wifi_confirm:
                byte command[] = new byte[106];
                if (!((set_wifi_pswd1.getText().toString()).equals(set_wifi_pswd2.getText().toString()))) {
                    Toast.makeText(this, "两次输入密码不一致!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(set_wifi_pswd1.getText().toString())){
                    Toast.makeText(this, "不能设置空密码!", Toast.LENGTH_LONG).show();
                    return;
                }
                System.arraycopy(CommandType.SET_WIFI_CFG, 0, command, 0, CommandType.SET_WIFI_CFG.length);
                try {
                    byte wifiName[] = set_wifi_name.getText().toString().getBytes("utf-8");
                    byte passWord[] = set_wifi_pswd1.getText().toString().getBytes("utf-8");
                    System.arraycopy(wifiName, 0, command, CommandType.SET_WIFI_CFG.length, wifiName.length);
                    System.arraycopy(passWord, 0, command, CommandType.SET_WIFI_CFG.length + 50, passWord.length);
                    nioSocketTools.sendCommand(command);
                    progressDialog.setMessage("正在设置Wifi密码......");
                    progressDialog.show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.set_wifi_cancle:
                finish();
                break;
            case R.id.set_show_pass:
                if (isShowPswd) {
                    set_show_pass.setImageResource(R.drawable.set_see_01);
                    set_wifi_oldpswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    set_wifi_pswd1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    set_wifi_pswd2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isShowPswd = false;
                } else {
                    set_show_pass.setImageResource(R.drawable.set_see_02);
                    set_wifi_oldpswd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    set_wifi_pswd1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    set_wifi_pswd2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    isShowPswd = true;
                }
                break;
        }
    }

    @Override
    public void result(ResultData msg) {
        if (isStop) return;
        try {
            byte recv[] = msg.getBytes();
            int command = ByteTools.bytes2ShortInt2(recv, 0);
            switch (command) {
                //GET_WIFI_CFG
                case 0x1101:
                    int nameLen = 0;
                    while (recv[2 + nameLen] != 0x00) {
                        nameLen++;
                    }
                    byte bName[] = new byte[nameLen];
                    System.arraycopy(recv, 2, bName, 0, nameLen);
                    String strName = new String(bName, "utf-8");
                    set_wifi_name.setText(strName);
                    int passLen = 0;
                    while (recv[52 + passLen] != 0x00) {
                        passLen++;
                    }
                    byte bPass[] = new byte[passLen];
                    System.arraycopy(recv, 52, bPass, 0, passLen);
                    String strPass = new String(bPass, "utf-8");
                    set_wifi_oldpswd.setText(strPass);
                    break;
                //SET_WIFI_CFG
                case 0x1205:
                    nioSocketTools.sendCommand(CommandType.GET_WIFI_CFG);
                    if(ByteTools.bytes2ShortInt(recv,2)==0){
                        progressDialog.setMessage("wifi设置修改成功，请重启！");
                    }else{
                        //TODO:设置密码失败
                        progressDialog.setMessage("wifi设置失败，请重试！");
                    }
                    break;
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }
}
