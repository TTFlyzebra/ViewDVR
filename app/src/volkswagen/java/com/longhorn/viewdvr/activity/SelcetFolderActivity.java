package com.longhorn.viewdvr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.longhorn.viewdvr.R;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-上午11:48.
 */

public class SelcetFolderActivity extends Activity implements View.OnClickListener {
    private LinearLayout onBack;
    private LinearLayout norFolder,evtFolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectfolder);
        onBack = findViewById(R.id.set_back);
        norFolder = findViewById(R.id.ac_secletfolder_nor);
        evtFolder = findViewById(R.id.ac_secletfolder_evt);

        onBack.setOnClickListener(this);
        norFolder.setOnClickListener(this);
        evtFolder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back:
                finish();
                break;
            case R.id.ac_secletfolder_nor:
                startActivity(new Intent(this,ExplorerNorActivity.class));
                break;
            case R.id.ac_secletfolder_evt:
                startActivity(new Intent(this,ExplorerEvtActivity.class));
                break;
        }
    }
}
