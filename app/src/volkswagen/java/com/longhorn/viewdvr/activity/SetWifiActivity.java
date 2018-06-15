package com.longhorn.viewdvr.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.longhorn.viewdvr.R;


public class SetWifiActivity extends Activity implements View.OnClickListener{
    private LinearLayout set_left;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setwifi);
        set_left = findViewById(R.id.set_back);
        set_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back:
                finish();
                break;
            case R.id.set_wifiname:
                break;
            case R.id.set_wifipswd1:
                break;
        }
    }
}
