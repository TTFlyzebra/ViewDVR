package com.longhorn.viewdvr.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.adapter.IAdapater;
import com.longhorn.viewdvr.adapter.OnItemClick;
import com.longhorn.viewdvr.adapter.OnLongItemClick;
import com.longhorn.viewdvr.data.DvrFile;
import com.longhorn.viewdvr.module.wifi.CommandType;
import com.longhorn.viewdvr.module.wifi.DataParse;
import com.longhorn.viewdvr.module.wifi.ResultData;
import com.longhorn.viewdvr.module.wifi.SocketResult;
import com.longhorn.viewdvr.module.wifi.SocketTools;
import com.longhorn.viewdvr.utils.ByteTools;
import com.longhorn.viewdvr.utils.FlyLog;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-上午11:48.
 */

public abstract class ExplorerBaseActivity extends Activity implements SocketResult, View.OnClickListener {
    protected RecyclerView mRecyclerView;
    private LinearLayout mOnBack;
    private LinearLayout mOnDown, mOnDel, mOnSelectAll,mOnMove;
    private TextView mTitle,mCancle;

    protected ArrayList<DvrFile> mList = new ArrayList<>();
    protected IAdapater mAdapater;
    private int mSumItem;
    private int mSumItemSelect = 0;

    protected byte[] mCommand = CommandType.GET_FILE_PHO;
    protected String title = "";
    protected int ridFormat = R.string.format1;

    protected abstract void initParameters();

    protected abstract void onImageItemClick(View v,int pos);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mRecyclerView = findViewById(R.id.ac_photo_rv01);
        mOnBack = findViewById(R.id.set_back);
        mOnDown = findViewById(R.id.ac_photo_down);
        mOnSelectAll = findViewById(R.id.ac_photo_selectall);
        mOnDel = findViewById(R.id.ac_photo_del);
        mTitle = findViewById(R.id.ac_photo_title);
        mCancle = findViewById(R.id.ac_photo_cancle);
        mOnMove = findViewById(R.id.ac_photo_move);

        mOnBack.setOnClickListener(this);
        mOnDown.setOnClickListener(this);
        mOnSelectAll.setOnClickListener(this);
        mOnDel.setOnClickListener(this);
        mCancle.setOnClickListener(this);

        initParameters();
        if(mCommand==CommandType.GET_FILE_PHO){
            mOnMove.setVisibility(View.GONE);
        }else{
            mOnMove.setVisibility(View.VISIBLE);
            mOnMove.setOnClickListener(this);
        }

        mTitle.setText(title);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//        rv01.setPullRefreshEnabled(true);
//        rv01.setLoadingMoreEnabled(false);
//        rv01.setLoadingListener(new XRecyclerView.LoadingListener() {
//            @Override
//            public void onRefresh() {
//                rv01.refreshComplete();
//            }
//
//            @Override
//            public void onLoadMore() {
//            }
//        });
        mAdapater.setOnLongItemClick(new OnLongItemClick() {
            @Override
            public void onLongItemClick(View v, int postion) {
                mList.get(postion).isSelect = !mList.get(postion).isSelect;
                mAdapater.notifyDataSetChanged();
                upTitleDisplay();
            }
        });
        mAdapater.setOnItemClick(new OnItemClick() {
            @Override
            public void onItemClick(View v, DvrFile dvrFile) {
                if(mSumItemSelect>0){
                    dvrFile.isSelect = !dvrFile.isSelect;
                    mAdapater.notifyDataSetChanged();
                    upTitleDisplay();
                }else{
                    //TODO:点击图片执行动作
                    onImageItemClick(v, (Integer) v.getTag(R.id.glideid));
                }
            }
        });
        mRecyclerView.setAdapter((RecyclerView.Adapter) mAdapater);
    }


    private void updata() {
        SocketTools.getInstance().sendCommand(mCommand, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updata();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back:
                finish();
                break;
            case R.id.ac_photo_down:
                break;
            case R.id.ac_photo_selectall:
                for (DvrFile dvrFile : mList) {
                    dvrFile.isSelect = true;
                }
                mAdapater.notifyDataSetChanged();
                upTitleDisplay();
                break;
            case R.id.ac_photo_del:
                break;
            case R.id.ac_photo_cancle:
                for (DvrFile dvrFile : mList) {
                    dvrFile.isSelect = false;
                }
                mAdapater.notifyDataSetChanged();
                upTitleDisplay();
                break;
            //视频文件才有此动作
            case R.id.ac_photo_move:
                break;
        }
    }

    private void upTitleDisplay() {
        mSumItemSelect = 0;
        for(DvrFile dvrFile:mList){
            if(dvrFile.rvType==2&&dvrFile.isSelect){
                mSumItemSelect++;
            }
        }
        if(mSumItemSelect >0){
            String text = String.format(getString(ridFormat), mSumItemSelect);
            mTitle.setText(text);
            mCancle.setVisibility(View.VISIBLE);
        }else{
            mTitle.setText(title);
            mCancle.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void result(ResultData msg) {
        FlyLog.d("length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
        if (msg.getMark() > 0) {
            try {
                List<DvrFile> tempList = new ArrayList<>();
                byte[] data = msg.getBytes();
                int[] pos = {8};
                mSumItem = ByteTools.bytes2Int(data, pos[0]);
                pos[0] += 4;
                for (int i = 0; i < mSumItem; i++) {
                    DvrFile dvrFile = DataParse.getDvrFile(data, pos);
                    dvrFile.rvType = 2;
                    tempList.add(dvrFile);
                }
                Collections.sort(tempList, new Comparator<DvrFile>() {
                    @Override
                    public int compare(DvrFile lhs, DvrFile rhs) {
                        return (rhs.date - lhs.date);
                    }
                });

                mList.clear();
                String strMenu = "";
                for (DvrFile dvrFile : tempList) {
                    //插入日期菜单
                    if (!strMenu.equals(dvrFile.getMenu())) {
                        DvrFile menuDvrFile = new DvrFile();
                        menuDvrFile.rvType = 1;
                        menuDvrFile.date = dvrFile.date;
                        mList.add(menuDvrFile);
                        strMenu = dvrFile.getMenu();
                    }
                    mList.add(dvrFile);
                }
                mAdapater.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
