package com.longhorn.viewdvr.adapter;

/**
 * Created by FlyZebra on 2018/5/31.
 * Descrip:
 */

public interface IAdapater {
    void setOnLongItemClick(OnLongItemClick onLongItemClick);

    void notifyDataSetChanged();

    void setOnItemClick(OnItemClick onItemClick);
}
