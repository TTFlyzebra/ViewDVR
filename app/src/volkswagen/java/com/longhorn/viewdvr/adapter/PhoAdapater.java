package com.longhorn.viewdvr.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.data.DvrFile;

import java.util.List;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-下午3:06.
 */

public class PhoAdapater extends RecyclerView.Adapter<ViewHolder> implements IAdapater {
    private List<DvrFile> mList;
    private Context mContext;
    private int mColumnNum;
    private OnLongItemClick mOnLongItemClick;
    private OnItemClick mOnItemClick;

    @Override
    public void setOnLongItemClick(OnLongItemClick onLongItemClick) {
        mOnLongItemClick = onLongItemClick;
    }

    @Override
    public void setOnItemClick(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }

    public PhoAdapater(Context context, List<DvrFile> list, int columnNum) {
        mContext = context;
        mList = list;
        mColumnNum = columnNum;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        switch (viewType) {
            case 1:
                View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
                viewHolder = new MenuHolder(v0);
                break;
            case 2:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
                viewHolder = new PhotoHolder(v1);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof PhotoHolder) {
            PhotoHolder photoHolder = (PhotoHolder) holder;
            photoHolder.imageView.setTag(R.id.glideid, position);
            photoHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnLongItemClick != null) {
                        mOnLongItemClick.onLongItemClick(v, (int) v.getTag(R.id.glideid));
                    }
                    return false;
                }
            });
            photoHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClick != null) {
                        int pos = (int) v.getTag(R.id.glideid);
                        mOnItemClick.onItemClick(v, mList.get(pos));
                    }
                }
            });
            Glide.with(mContext).load(mList.get(position).getUrl()).into(photoHolder.imageView);
            photoHolder.textView.setText(mList.get(position).getTime());
            if (mList.get(position).isSelect) {
                photoHolder.checkBox.setChecked(true);
                photoHolder.checkBox.setVisibility(View.VISIBLE);
            } else {
                photoHolder.checkBox.setChecked(false);
                photoHolder.checkBox.setVisibility(View.GONE);
            }
        } else if (holder instanceof MenuHolder) {
            MenuHolder textHolder = (MenuHolder) holder;
            textHolder.textView.setText(mList.get(position).getMenu());
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).rvType;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mList.get(position).rvType) {
                    case 1:
                        return mColumnNum;
                    case 2:
                        return 1;
                    default:
                        return mColumnNum;
                }
            }
        });
    }

    private static class PhotoHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CheckBox checkBox;

        public PhotoHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itme_iv01);
            textView = itemView.findViewById(R.id.itme_tv01);
            checkBox = itemView.findViewById(R.id.itme_ck01);
        }
    }

    private static class MenuHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MenuHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itme_tv01);
        }
    }
}
