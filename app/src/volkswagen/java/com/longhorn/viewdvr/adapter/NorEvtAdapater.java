package com.longhorn.viewdvr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksyun.media.player.misc.KSYProbeMediaInfo;
import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.data.DvrFile;
import com.longhorn.viewdvr.module.cache.DoubleBitmapCache;
import com.longhorn.viewdvr.utils.FlyLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-下午3:06.
 */

public class NorEvtAdapater extends RecyclerView.Adapter<ViewHolder> implements IAdapater {
    private static final int smallImageWidth = 128;
    private static final int smallImageHeight = 86;
    private static final int smallImageSize = smallImageWidth * smallImageHeight * 2;
    private Set<GetDvrVideoBitmatTask> tasks = new HashSet<>();
    private DoubleBitmapCache doubleBitmapCache;
    private RecyclerView mRecyclerView;

    private List<DvrFile> mList;
    private Context mContext;
    private int mColumnNum;
    private OnLongItemClick mOnLongItemClick;
    private OnItemClick mOnItemClick;

    private int first,last;

    @Override
    public void setOnLongItemClick(OnLongItemClick onLongItemClick) {
        mOnLongItemClick = onLongItemClick;
    }

    @Override
    public void setOnItemClick(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }

    public NorEvtAdapater(Context context, List<DvrFile> list, int columnNum,RecyclerView recyclerView) {
        doubleBitmapCache = DoubleBitmapCache.getInstance(context);
        mContext = context;
        mList = list;
        mColumnNum = columnNum;
        mRecyclerView = recyclerView;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case SCROLL_STATE_IDLE:
                        first = ((GridLayoutManager) (recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                        last = ((GridLayoutManager) (recyclerView.getLayoutManager())).findLastVisibleItemPosition();
                        if (first >= 0) {
                            loadImageView(first,last);
                        }
                        break;
                    default:
                        cancleAllTask();
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
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
            photoHolder.imageView.setTag(mList.get(position).getUrl());
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
            Bitmap bitmap = doubleBitmapCache.get(mList.get(position).getUrl());
            if (null != bitmap) {
                photoHolder.imageView.setImageBitmap(bitmap);
            } else {
                loadImageView(position,position);
                photoHolder.imageView.setImageResource(R.drawable.img_index);
            }
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

    private static class PhotoHolder extends ViewHolder {
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

    private static class MenuHolder extends ViewHolder {
        TextView textView;

        public MenuHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itme_tv01);
        }
    }

    public void cancleAllTask() {
        for (GetDvrVideoBitmatTask task : tasks) {
            task.cancel(true);
        }
        tasks.clear();
    }

    public void loadImageView(int first, int last) {
        FlyLog.d("loadImageView %d-%d", first, last);
        try {
            if (mList == null || first < 0 || first >= mList.size() || last < 0 || last >= mList.size()) {
                FlyLog.e("mList==null||first<=0||first>=mList.size()||last<=0||last>=mList.size() first=%d,last=%d",first,last);
                return;
            }
            if (mList.get(first).type == 2) {
                return;
            }
            for (int i = first; i <= last; i++) {
                Bitmap bitmap = doubleBitmapCache.get(mList.get(i).getUrl());
                if (null != bitmap) {
                    ImageView imageView = mRecyclerView.findViewWithTag(mList.get(i));
                    if (null != imageView) {
                        imageView.setImageBitmap(bitmap);
                    }
                } else {
                    GetDvrVideoBitmatTask task = new GetDvrVideoBitmatTask(mList.get(i));
                    task.execute(mList.get(i).getUrl());
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GetDvrVideoBitmatTask extends AsyncTask<String, Bitmap, Bitmap> {
        private DvrFile dvrFile;

        GetDvrVideoBitmatTask(DvrFile dvrFile) {
            this.dvrFile = dvrFile;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                final String path = strings[0];
//                if (0 != dvrFile.offset) {
//                    byte[] bytes = HttpDown.downRange(path, dvrFile.offset, smallImageSize);
//                    if (null != bytes && smallImageSize == bytes.length) {
//                        bitmap = BitmapTools.rawByteArray2RGBABitmap2(bytes, smallImageWidth, smallImageHeight);
//                    }
//                }
                KSYProbeMediaInfo ksyProbeMediaInfo = new KSYProbeMediaInfo();
                bitmap = ksyProbeMediaInfo.getVideoThumbnailAtTime(strings[0],1,smallImageWidth,smallImageHeight);
                if (bitmap != null) {
                    publishProgress(bitmap);
                    if (doubleBitmapCache != null) {
                        doubleBitmapCache.put(path, bitmap);
                    }
                }

                if (bitmap != null) {
                    if (doubleBitmapCache != null) {
                        doubleBitmapCache.put(path, bitmap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = mRecyclerView.findViewWithTag(dvrFile.getUrl());
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.img_index);
                }
            }
        }

    }
}
