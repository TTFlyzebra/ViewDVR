package com.longhorn.viewdvr.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.module.wifi.NioSocketTools;


public class SetWifiActivity extends Activity implements View.OnClickListener{
    private LinearLayout set_left;
    private Button set_wifi_confirm,set_wifi_cancle;
    private ImageView set_show_pass;
    private EditText set_wifi_name,set_wifi_oldpswd,set_wifi_pswd1,set_wifi_pswd2;
    private boolean isShowPswd = false;
    private NioSocketTools nioSocketTools = NioSocketTools.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setwifi);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back:
                finish();
                break;
            case R.id.set_wifi_confirm:
                break;
            case R.id.set_wifi_cancle:
                finish();
                break;
            case R.id.set_show_pass:
                if(isShowPswd){
                    set_show_pass.setImageResource(R.drawable.set_see_01);
                    set_wifi_oldpswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    set_wifi_pswd1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    set_wifi_pswd2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isShowPswd = false;
                }else {
                    set_show_pass.setImageResource(R.drawable.set_see_02);
                    set_wifi_oldpswd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    set_wifi_pswd1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    set_wifi_pswd2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    isShowPswd = true;
                }
                break;
        }
    }
}
