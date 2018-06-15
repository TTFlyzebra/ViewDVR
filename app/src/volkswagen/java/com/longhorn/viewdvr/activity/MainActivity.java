package com.longhorn.viewdvr.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.data.Global;
import com.longhorn.viewdvr.module.wifi.CommandType;
import com.longhorn.viewdvr.module.wifi.ResultData;
import com.longhorn.viewdvr.module.wifi.SocketResult;
import com.longhorn.viewdvr.module.wifi.SocketTools;
import com.longhorn.viewdvr.utils.ByteTools;
import com.longhorn.viewdvr.utils.FlyLog;
import com.longhorn.viewdvr.view.RtspVideoView;


/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-上午11:48
 */
public class MainActivity extends Activity implements CommandType, View.OnClickListener, SocketResult {
    private RtspVideoView mViewPlayer;
    private RelativeLayout bt01, bt02, bt03, bt04;
    private LinearLayout ll01, ll02, ll_top;
    private ImageView ivline01, ivline02;
    private LinearLayout ivSet, ivLink;
    private ImageView full_record, full_pho, evtstate, soundstate, fullcontrol, recordstate;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean full = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPlayer = findViewById(R.id.ac_main_flyvp01);

        ll01 = findViewById(R.id.ac_main_ll01);
        ll02 = findViewById(R.id.ac_main_ll02);
        ll_top = findViewById(R.id.ac_main_ll_top);
        ivline01 = findViewById(R.id.ac_main_iv_line01);
        ivline02 = findViewById(R.id.ac_main_iv_line02);
        ivSet = findViewById(R.id.ac_main_set);
        ivLink = findViewById(R.id.ac_main_link);
        bt01 = findViewById(R.id.ac_main_bt01);
        bt02 = findViewById(R.id.ac_main_bt02);
        bt03 = findViewById(R.id.ac_main_bt03);
        bt04 = findViewById(R.id.ac_main_bt04);
//full_record,full_pho,evtstate,soundstate,fullcontrol,recordstate;
        full_record = findViewById(R.id.full_record);
        full_pho = findViewById(R.id.full_pho);
        evtstate = findViewById(R.id.ac_main_evt_status);
        soundstate = findViewById(R.id.ac_main_sound_status);
        fullcontrol = findViewById(R.id.ac_main_full);
        recordstate = findViewById(R.id.ac_main_record_status);

        full_pho.setVisibility(View.GONE);
        full_record.setVisibility(View.GONE);

        ivLink.setOnClickListener(this);
        ivSet.setOnClickListener(this);
        bt01.setOnClickListener(this);
        bt02.setOnClickListener(this);
        bt03.setOnClickListener(this);
        bt04.setOnClickListener(this);
        full_pho.setOnClickListener(this);
        full_record.setOnClickListener(this);
        fullcontrol.setOnClickListener(this);

        mViewPlayer.setPlayUrlArr(new String[]{Global.DVR_RTSP});
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (dm.widthPixels > dm.heightPixels) {
            WindowManager.LayoutParams params1 = getWindow().getAttributes();
            params1.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(params1);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            ll02.setVisibility(View.GONE);
            ll_top.setVisibility(View.GONE);
            ivline01.setVisibility(View.GONE);
            ivline02.setVisibility(View.GONE);
            full_pho.setVisibility(View.VISIBLE);
            full_record.setVisibility(View.VISIBLE);
        } else {
            WindowManager.LayoutParams params2 = getWindow().getAttributes();
            params2.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(params2);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            ll02.setVisibility(View.VISIBLE);
            ll_top.setVisibility(View.VISIBLE);
            ivline01.setVisibility(View.VISIBLE);
            ivline02.setVisibility(View.VISIBLE);
            full_pho.setVisibility(View.GONE);
            full_record.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.ac_main_link:
                finish();
                break;
            case R.id.ac_main_set:
                startActivity(new Intent(MainActivity.this, SetActivity.class));
                break;
            case R.id.ac_main_bt01:
                v.setEnabled(false);
                SocketTools.getInstance().sendCommand(FAST_PHOTOGRAPHY, new SocketResult() {
                    @Override
                    public void result(ResultData msg) {
                        v.setEnabled(true);
                        if (msg.getMark() > 0) {
                            Toast.makeText(MainActivity.this, "拍照完成", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, msg.getMsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            case R.id.ac_main_bt02:
                v.setEnabled(false);
                SocketTools.getInstance().sendCommand(FAST_EMERGE, new SocketResult() {
                    @Override
                    public void result(ResultData msg) {
                        if (msg.getMark() > 0) {
                            Toast.makeText(MainActivity.this, "开始紧急录像...", Toast.LENGTH_LONG).show();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    v.setEnabled(true);
                                    Toast.makeText(MainActivity.this, "紧急录像完成", Toast.LENGTH_LONG).show();
                                }
                            }, 30000);
                        } else {
                            v.setEnabled(true);
                            Toast.makeText(MainActivity.this, msg.getMsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            case R.id.ac_main_bt03:
                startActivity(new Intent(MainActivity.this, ExplorerPhoActivity.class));
                break;
            case R.id.ac_main_bt04:
                startActivity(new Intent(MainActivity.this, SelcetFolderActivity.class));
                break;
            case R.id.ac_main_full:
                if (!full) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    fullcontrol.setImageResource(R.drawable.nofull);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fullcontrol.setImageResource(R.drawable.full);
                }
                full = !full;
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (full) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fullcontrol.setImageResource(R.drawable.full);
            full = false;
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void result(ResultData msg) {
        FlyLog.d("length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
    }
}
