package com.longhorn.viewdvr.adapter;

import android.view.View;

import com.longhorn.viewdvr.data.DvrFile;

/**
 * Created by FlyZebra on 2018/5/31.
 * Descrip:
 */

public interface OnItemClick {
    void onItemClick(View v, DvrFile dvrFile);
}
