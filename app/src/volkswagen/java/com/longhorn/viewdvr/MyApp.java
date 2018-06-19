package com.longhorn.viewdvr;

import android.app.Application;

import com.flyzebra.flydown.download.DownFileManager;
import com.longhorn.viewdvr.http.FlyOkHttp;
import com.longhorn.viewdvr.module.wifi.NioSocketTools;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-下午2:25.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FlyOkHttp.getInstance().Init(getApplicationContext());
        DownFileManager.install(getApplicationContext());
        NioSocketTools.getInstance().init();
    }
}
