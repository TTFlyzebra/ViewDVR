package com.longhorn.viewdvr.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.module.wifi.CommandType;
import com.longhorn.viewdvr.module.wifi.NioSocketTools;
import com.longhorn.viewdvr.module.wifi.ResultData;
import com.longhorn.viewdvr.module.wifi.SocketResult;
import com.longhorn.viewdvr.utils.ByteTools;
import com.longhorn.viewdvr.utils.FlyLog;

import static com.longhorn.viewdvr.module.wifi.CommandType.FACTORY_RESET;
import static com.longhorn.viewdvr.module.wifi.CommandType.SDCARD_FORMATTING;


public class SetActivity extends Activity implements View.OnClickListener, SocketResult {
    private LinearLayout set_left;
    private LinearLayout set_wifi, set_video, set_vtime, set_zlgy, set_spdj, set_time, set_version, set_format, set_factory;
    private TextView set_tv_pix, set_tv_mins, set_tv_sensor;
    private NioSocketTools nioSocketTools = NioSocketTools.getInstance();
    private boolean isStop = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        progressDialog = new ProgressDialog(this);

        set_left = findViewById(R.id.set_back);
        set_wifi = findViewById(R.id.set_wifi);
        set_video = findViewById(R.id.set_video);
        set_vtime = findViewById(R.id.set_vtime);
        set_zlgy = findViewById(R.id.set_zlgy);
        set_spdj = findViewById(R.id.set_spdj);
        set_time = findViewById(R.id.set_time);
        set_version = findViewById(R.id.set_version);
        set_format = findViewById(R.id.set_format);
        set_factory = findViewById(R.id.set_factory);
        set_tv_pix = findViewById(R.id.set_tv_pix);
        set_tv_mins = findViewById(R.id.set_tv_mins);
        set_tv_sensor = findViewById(R.id.set_tv_sensor);

        set_left.setOnClickListener(this);
        set_wifi.setOnClickListener(this);
        set_video.setOnClickListener(this);
        set_vtime.setOnClickListener(this);
        set_zlgy.setOnClickListener(this);
        set_spdj.setOnClickListener(this);
        set_time.setOnClickListener(this);
        set_version.setOnClickListener(this);
        set_format.setOnClickListener(this);
        set_factory.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStop = false;
        nioSocketTools.registerSocketResult(this);
        nioSocketTools.sendCommand(CommandType.GET_G_SENSOR_CFG);
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
            case R.id.set_wifi:
                startActivity(new Intent(this, SetWifiActivity.class));
                break;
            case R.id.set_video:
                break;
            case R.id.set_vtime:
                break;
            case R.id.set_zlgy:
                break;
            case R.id.set_time:
                break;
            case R.id.set_version:
                break;
            case R.id.set_format:
                AlertDialog.Builder formatDialog = new AlertDialog.Builder(this);
                formatDialog.setTitle(getString(R.string.alert1))//设置对话框的标题
                        .setMessage(getString(R.string.format_alert))//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                nioSocketTools.sendCommand(SDCARD_FORMATTING);
                                progressDialog.setMessage(getString(R.string.formatting));
                                progressDialog.show();
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.set_factory:
                AlertDialog.Builder factoryDialog = new AlertDialog.Builder(this);
                factoryDialog.setTitle(getString(R.string.alert1))//设置对话框的标题
                        .setMessage(getString(R.string.factoy_alert))//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                nioSocketTools.sendCommand(FACTORY_RESET);
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
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
                //HEARTBEAT
                case 0x0010:
                    set_tv_pix.setText(recv[7] == 0 ? "1080P" : "720P");
                    String mins = recv[8] + "min";
                    set_tv_mins.setText(mins);
                    break;
//                SDCARD_FORMATTING
                case 0x1220:
                    progressDialog.dismiss();
                    break;
//                FACTORY_RESET
                case 0x1221:
                    break;
//                GET_G_SENSOR_CFG
                case 0x1123:
                    set_tv_sensor.setText(recv[2] == 0 ? "OFF" : recv[2] == 1 ? "低" : recv[2] == 2 ? "中" : "高");
                    break;
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }
}
