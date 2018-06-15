package com.longhorn.viewdvr.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.provider.Settings;
import android.view.WindowManager;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-4-下午4:12.
 */

public class ScreenBrightness {
    public static int getScreenBrightness(Activity activity){
        int value = 0;
        ContentResolver cr = activity.getContentResolver();
        try {
            value = Settings.System.getInt(cr,Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void setScreenBrightness(Activity activity,int value){
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.screenBrightness = value /255f;
        activity.getWindow().setAttributes(params);
    }
}
