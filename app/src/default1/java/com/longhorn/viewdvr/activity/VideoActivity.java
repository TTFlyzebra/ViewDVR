package com.longhorn.viewdvr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.fragment.VideoEvtFragment;
import com.longhorn.viewdvr.fragment.VideoLocFragment;
import com.longhorn.viewdvr.fragment.VideoNorFragment;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-上午11:48.
 */

public class VideoActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolBar;
    private Button bt01, bt02, bt03;
    public static final String FRAGMENT = "FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        toolBar = findViewById(R.id.toolbar);

        bt01 = findViewById(R.id.ac_video_bt01);
        bt02 = findViewById(R.id.ac_video_bt02);
        bt03 = findViewById(R.id.ac_video_bt03);

        bt01.setOnClickListener(this);
        bt02.setOnClickListener(this);
        bt03.setOnClickListener(this);

        loadFragment();
    }

    private void loadFragment() {
        Intent intent = getIntent();
        String fstr = intent.getStringExtra(FRAGMENT);
        switch (fstr) {
            case "EVT":
                getFragmentManager().beginTransaction().replace(R.id.ac_video_fl01, new VideoEvtFragment()).commit();
                toolBar.setTitle(getString(R.string.view_video_evt));
                break;
            case "LOC":
                getFragmentManager().beginTransaction().replace(R.id.ac_video_fl01, new VideoLocFragment()).commit();
                toolBar.setTitle(getString(R.string.view_video_loc));
                break;
            case "NOR":
            default:
                getFragmentManager().beginTransaction().replace(R.id.ac_video_fl01, new VideoNorFragment()).commit();
                toolBar.setTitle(getString(R.string.view_video_nor));
                break;

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_video_bt01:
                getFragmentManager().beginTransaction().replace(R.id.ac_video_fl01, new VideoNorFragment()).commit();
                toolBar.setTitle(getString(R.string.view_video_nor));
                break;
            case R.id.ac_video_bt02:
                getFragmentManager().beginTransaction().replace(R.id.ac_video_fl01, new VideoEvtFragment()).commit();
                toolBar.setTitle(getString(R.string.view_video_evt));
                break;
            case R.id.ac_video_bt03:
                getFragmentManager().beginTransaction().replace(R.id.ac_video_fl01, new VideoLocFragment()).commit();
                toolBar.setTitle(getString(R.string.view_video_loc));
                break;
        }
    }
}
