package com.longhorn.viewdvr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.utils.FlyLog;
import com.longhorn.viewdvr.view.RtspVideoView;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-30-上午10:06.
 */

public class RtspFullPlayActivity extends Activity {
    public static final String PLAY_URL = "PLAY_URL";
    private String playurl;
    private RtspVideoView mViewPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mViewPlayer = findViewById(R.id.ac_play_flyvp01);
        Intent intent = getIntent();
        playurl = intent.getStringExtra(PLAY_URL);
        FlyLog.d("Play url = %s", playurl);
        if(!TextUtils.isEmpty(playurl)){
            mViewPlayer.setPlayUrlArr(new String[]{playurl});
        }else{
            //TODO：无效的播放地址如何处理
        }

    }
}
