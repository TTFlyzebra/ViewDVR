package com.longhorn.viewdvr.activity;

import android.content.Intent;
import android.view.View;

import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.adapter.NorEvtAdapater;
import com.longhorn.viewdvr.adapter.PhoAdapater;
import com.longhorn.viewdvr.data.DvrFile;
import com.longhorn.viewdvr.module.wifi.CommandType;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-上午11:48.
 */

public class ExplorerEvtActivity extends ExplorerBaseActivity {
    @Override
    protected void initParameters() {
        mAdapater = new NorEvtAdapater(this, mList, 3,mRecyclerView);
        mCommand = CommandType.GET_FILE_EVT;
        title = getString(R.string.title_evt);
        ridFormat = R.string.format2;
    }

    @Override
    protected void onImageItemClick(View v, int pos) {
        Intent intent = new Intent(this,PlayVideoActivity.class);
        intent.putExtra("PLAY_URL",mList.get(pos).getPlayUrl());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        ((NorEvtAdapater)mAdapater).cancleAllTask();
        super.onDestroy();
    }
}
