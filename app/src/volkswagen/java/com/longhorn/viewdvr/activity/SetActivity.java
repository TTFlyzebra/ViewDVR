package com.longhorn.viewdvr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.longhorn.viewdvr.R;


public class SetActivity extends Activity implements View.OnClickListener {
    private LinearLayout set_left;
    private LinearLayout set_wifi, set_video, set_vtime, set_zlgy, set_spdj, set_time, set_version, set_format, set_factory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
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
                break;
            case R.id.set_factory:
                break;
        }
    }
}
