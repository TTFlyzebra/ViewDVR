package com.longhorn.viewdvr.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.data.Global;
import com.longhorn.viewdvr.http.FlyOkHttp;
import com.longhorn.viewdvr.http.IHttp;
import com.longhorn.viewdvr.adapter.PhotoAdapater;
import com.longhorn.viewdvr.utils.FlyLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-上午11:48.
 */

public class PhotoActivity extends BaseActivity implements IHttp.HttpResult{
    private final String HTTPTAG = "PhotoActivity" + Math.random();
    private IHttp iHttp = FlyOkHttp.getInstance();
    private RecyclerView rv01;
    private List<String> rvList = new ArrayList<>();
    private PhotoAdapater photoAdapater;
    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        toolBar = findViewById(R.id.toolbar);
        toolBar.setTitle(getString(R.string.view_photo));
        rv01 = findViewById(R.id.ac_photo_rv01);
        rv01.setLayoutManager(new GridLayoutManager(this, 1));
        photoAdapater = new PhotoAdapater(this, rvList);
        rv01.setAdapter(photoAdapater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        iHttp.getString(Global.PATH_PHO, HTTPTAG, this);
    }

    @Override
    protected void onStop() {
        iHttp.cancelAll(HTTPTAG);
        super.onStop();
    }

    @Override
    public void succeed(Object object) {
        if (object != null) {
            String str = (String) object;
            Pattern pattern = Pattern.compile("[0-9]{8}_[0-9]{6}.[J,j][P,p][G,g]");
            Matcher matcher = pattern.matcher(str);
            Set<String> set = new HashSet<>();
            while (matcher.find()) {
                String address = Global.PATH_PHO+"/" + matcher.group(0);
                set.add(address);
            }

            List<String> list = new ArrayList<>();

            list.addAll(set);

            if (!list.isEmpty()) {
                rvList.clear();
                rvList.addAll(list);
                photoAdapater.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void failed(Object object) {
        FlyLog.d(""+object);
    }
}
