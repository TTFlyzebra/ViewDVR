package com.longhorn.viewdvr.module;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.RawRes;


import com.longhorn.viewdvr.R;

import java.util.HashMap;

/**
 * 声音播放类
 * Created by FlyZebra on 2016/8/19.
 */
public class SoundPlay {
    private Context context;
    private SoundPool sp;
    private HashMap<Integer, Integer> hm;
    private int currentPlaySound = -1;

    public int soundRes[] = new int[]{
            R.raw.m01,
    };
    public String[] soundStrings = new String[]{
            "01",
    };

    public SoundPlay(Context context) {
        this.context = context;
        sp = new SoundPool(4, AudioManager.STREAM_ALARM, 0);
        initSoundPool();
    }

    public void initSoundPool(@RawRes int resID[]) {
        hm = new HashMap<Integer, Integer>();
        for (int i = 0; i < resID.length; i++) {
            hm.put(i + 1, sp.load(context, resID[i], 1));
        }
    }

    public void initSoundPool() {
        hm = new HashMap<Integer, Integer>();
        for (int i = 0; i < soundRes.length; i++) {
            hm.put(i + 1, sp.load(context, soundRes[i], 1));
        }
    }

    public int playSound(int sound, int loop) {
//        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        int streamVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_SYSTEM);
//        int streamVolumeMax = am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
//        float volume = (float) streamVolumeCurrent /(float) streamVolumeMax;
//        FlyLog.d("system sound %d,%d",streamVolumeCurrent,streamVolumeMax);
        if (sound < 1 || sound > hm.size()) {
            sound = 1;
        }
        currentPlaySound = sp.play(hm.get(sound), 1, 1, 1, loop, 1.0f);
        return currentPlaySound;
    }

    public void stopSound() {
        if (currentPlaySound != -1) {
            sp.stop(currentPlaySound);
        }
    }


}
