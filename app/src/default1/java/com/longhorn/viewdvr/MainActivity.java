package com.longhorn.viewdvr;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.longhorn.viewdvr.data.Global;
import com.longhorn.viewdvr.activity.BaseActivity;
import com.longhorn.viewdvr.activity.PhotoActivity;
import com.longhorn.viewdvr.activity.VideoActivity;
import com.longhorn.viewdvr.module.wifi.CommandType;
import com.longhorn.viewdvr.module.wifi.ResultData;
import com.longhorn.viewdvr.module.wifi.SocketResult;
import com.longhorn.viewdvr.module.wifi.SocketTools;
import com.longhorn.viewdvr.view.RtspVideoView;



/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-上午11:48-上午11:49.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener,CommandType {
    private RtspVideoView mViewPlayer;
    private Button bt01,bt02,bt03,bt04,bt05,bt06,bttest;
    private LinearLayout ll01,ll02;
    private Toolbar toolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = findViewById(R.id.toolbar);
        toolBar.setTitle(getString(R.string.longhorn));


        mViewPlayer = findViewById(R.id.ac_main_flyvp01);

        ll01 = findViewById(R.id.ac_main_ll01);
        ll02 = findViewById(R.id.ac_main_ll02);

        bttest = findViewById(R.id.ac_main_test);
        bt01 = findViewById(R.id.ac_main_bt01);
        bt02 = findViewById(R.id.ac_main_bt02);
        bt03 = findViewById(R.id.ac_main_bt03);
        bt04 = findViewById(R.id.ac_main_bt04);
        bt05 = findViewById(R.id.ac_main_bt05);
        bt06 = findViewById(R.id.ac_main_bt06);

        bttest.setOnClickListener(this);
        bt01.setOnClickListener(this);
        bt02.setOnClickListener(this);
        bt03.setOnClickListener(this);
        bt04.setOnClickListener(this);
        bt05.setOnClickListener(this);
        bt06.setOnClickListener(this);

        mViewPlayer.setPlayUrlArr(new String[]{Global.DVR_RTSP});

        mViewPlayer.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ac_main_test:
                SocketTools.getInstance().sendCommand(GET_FILE_EVT, new SocketResult() {
                    @Override
                    public void result(ResultData msg) {

                    }
                });
                break;
            case R.id.ac_main_bt01:
                startActivity(new Intent(this,PhotoActivity.class));
                break;
            case R.id.ac_main_bt02:
                startActivity(new Intent(this,VideoActivity.class).putExtra(VideoActivity.FRAGMENT,"NOR"));
                break;
            case R.id.ac_main_bt03:
                startActivity(new Intent(this,VideoActivity.class).putExtra(VideoActivity.FRAGMENT,"EVT"));
                break;
            case R.id.ac_main_bt04:
                startActivity(new Intent(this,VideoActivity.class).putExtra(VideoActivity.FRAGMENT,"LOC"));
                break;
            case R.id.ac_main_bt05:
                SocketTools.getInstance().sendCommand(FAST_PHOTOGRAPHY, new SocketResult() {
                    @Override
                    public void result(ResultData msg) {

                    }
                });
                break;
            case R.id.ac_main_bt06:
                SocketTools.getInstance().sendCommand(FAST_EMERGE, new SocketResult() {
                    @Override
                    public void result(ResultData msg) {

                    }
                });
                break;
            case R.id.ac_main_flyvp01:
                if(!full){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                full = !full;
                break;
        }
    }

    boolean full = false;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if(dm.widthPixels> dm.heightPixels){
            WindowManager.LayoutParams params1 = getWindow().getAttributes();
            params1.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(params1);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            toolBar.setVisibility(View.GONE);
            ll02.setVisibility(View.GONE);
        }else{
            WindowManager.LayoutParams params2 = getWindow().getAttributes();
            params2.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(params2);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            toolBar.setVisibility(View.VISIBLE);
            ll02.setVisibility(View.VISIBLE);
        }
    }

}
