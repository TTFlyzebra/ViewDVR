package com.longhorn.viewdvr.activity;

import android.content.Intent;
import android.view.View;

import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.adapter.IAdapater;
import com.longhorn.viewdvr.adapter.PhoAdapater;
import com.longhorn.viewdvr.data.DvrFile;
import com.longhorn.viewdvr.module.wifi.CommandType;

import java.io.Serializable;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-上午11:48.
 */

public class ExplorerPhoActivity extends ExplorerBaseActivity {

    @Override
    protected void initParameters() {
        mAdapater = new PhoAdapater(this, mList, 3);
        mCommand = CommandType.GET_FILE_PHO;
        title = getString(R.string.title_pho);
        ridFormat = R.string.format1;
    }

    @Override
    protected void onImageItemClick(View v, int pos) {
        Intent intent = new Intent(this,FullPhotoActivity.class);
        intent.putExtra("LIST",mList);
        intent.putExtra("ITEM",pos);
        startActivity(intent);
    }
}
