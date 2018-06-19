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

import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.data.Global;
import com.longhorn.viewdvr.module.SoundPlay;
import com.longhorn.viewdvr.module.wifi.CommandType;
import com.longhorn.viewdvr.module.wifi.NioSocketTools;
import com.longhorn.viewdvr.module.wifi.ResultData;
import com.longhorn.viewdvr.module.wifi.SocketResult;
import com.longhorn.viewdvr.utils.ByteTools;
import com.longhorn.viewdvr.utils.FlyLog;
import com.longhorn.viewdvr.view.RtspVideoView;


/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-上午11:48
 */
public class MainActivity extends Activity implements CommandType, View.OnClickListener, SocketResult {
    private RtspVideoView mViewPlayer;
    private RelativeLayout bt_pho, bt_evt, bt03, bt04;
    private LinearLayout ll01, ll02, ll_top;
    private ImageView ivline01, ivline02;
    private LinearLayout ivSet, ivLink;
    private ImageView full_evt, full_pho, evtstate, soundstate, fullcontrol, recordstate;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean full = false;
    private NioSocketTools nioSocketTools = NioSocketTools.getInstance();
    private boolean isStop = false;
    private boolean isEvtRecord = false;
    private SoundPlay soundPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlay = new SoundPlay(this);
        soundPlay.initSoundPool();

        mViewPlayer = findViewById(R.id.ac_main_flyvp01);

        ll01 = findViewById(R.id.ac_main_ll01);
        ll02 = findViewById(R.id.ac_main_ll02);
        ll_top = findViewById(R.id.ac_main_ll_top);
        ivline01 = findViewById(R.id.ac_main_iv_line01);
        ivline02 = findViewById(R.id.ac_main_iv_line02);
        ivSet = findViewById(R.id.ac_main_set);
        ivLink = findViewById(R.id.ac_main_link);
        bt_pho = findViewById(R.id.ac_main_bt_pho);
        bt_evt = findViewById(R.id.ac_main_bt_evt);
        bt03 = findViewById(R.id.ac_main_bt03);
        bt04 = findViewById(R.id.ac_main_bt04);
//full_record,full_pho,evtstate,soundstate,fullcontrol,recordstate;
        full_evt = findViewById(R.id.full_evt);
        full_pho = findViewById(R.id.full_pho);
        evtstate = findViewById(R.id.ac_main_evt_status);
        soundstate = findViewById(R.id.ac_main_sound_status);
        fullcontrol = findViewById(R.id.ac_main_full);
        recordstate = findViewById(R.id.ac_main_record_status);

        full_pho.setVisibility(View.GONE);
        full_evt.setVisibility(View.GONE);

        ivLink.setOnClickListener(this);
        ivSet.setOnClickListener(this);
        bt_pho.setOnClickListener(this);
        bt_evt.setOnClickListener(this);
        bt03.setOnClickListener(this);
        bt04.setOnClickListener(this);
        full_pho.setOnClickListener(this);
        full_evt.setOnClickListener(this);
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
            full_evt.setVisibility(View.VISIBLE);
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
            full_evt.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        isStop = false;
        nioSocketTools.registerSocketResult(this);
    }

    @Override
    protected void onStop() {
        isStop=true;
        nioSocketTools.unregisterSocketResult(this);
        mHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        nioSocketTools.close();
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
            case R.id.ac_main_bt_pho:
            case R.id.full_pho:
                bt_pho.setEnabled(false);
                full_pho.setEnabled(false);
                nioSocketTools.sendCommand(FAST_PHOTOGRAPHY);
                break;
            case R.id.ac_main_bt_evt:
            case R.id.full_evt:
                bt_evt.setEnabled(false);
                full_evt.setEnabled(false);
                nioSocketTools.sendCommand(FAST_EMERGE);
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
        if (isStop) return;
        try {
            byte recv[] = msg.getBytes();
            int command = ByteTools.bytes2ShortInt2(recv, 0);
            switch (command) {
                //HEARTBEAT
                case 0x0010:
                    isEvtRecord = (recv[4] == 0x01 || recv[4] == 0x02);
                    evtstate.setVisibility(isEvtRecord?View.VISIBLE:View.GONE);
                    bt_evt.setEnabled(!isEvtRecord);
                    full_evt.setEnabled(!isEvtRecord);
                    recordstate.setVisibility((recv[3] == 0x02 || recv[3] == 0x01)?View.VISIBLE:View.GONE);
                    soundstate.setImageResource((recv[5] == 0x02 || recv[5] == 0x01)?R.drawable.sound_01:R.drawable.sound_02);
                    break;
                //拍照FAST_PHOTOGRAPHY
                case 0x0211:
                    bt_pho.setEnabled(true);
                    full_pho.setEnabled(true);
//                    if(ByteTools.bytes2Int(recv,2)==0) {
                    soundPlay.playSound(1, 0);
//                    }else{
                        //TODO:拍照失败
//                    }
                    break;
                //获取文件列表GET_FILE_PHO
                case 0x1002:
                    break;
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }
}
