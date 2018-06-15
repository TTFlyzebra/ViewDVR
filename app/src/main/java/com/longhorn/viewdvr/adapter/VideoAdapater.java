package com.longhorn.viewdvr.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyzebra.flydown.download.DownFileManager;
import com.ksyun.media.player.misc.KSYProbeMediaInfo;
import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.module.cache.DoubleBitmapCache;
import com.longhorn.viewdvr.activity.PlayVideoActivity;
import com.longhorn.viewdvr.utils.FlyLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-下午3:06.
 */

public class VideoAdapater extends RecyclerView.Adapter<VideoAdapater.ViewHolder> implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private List<String> mList;
    private Context mContext;
    private Set<GetUrlVideoBitmatTask> tasks = new HashSet<>();
    private DoubleBitmapCache doubleBitmapCache;

    public VideoAdapater(Context context, List<String> list, RecyclerView recyclerView) {
        mContext = context;
        mList = list;
        mRecyclerView = recyclerView;
        doubleBitmapCache = DoubleBitmapCache.getInstance(context.getApplicationContext());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case 0:
                        int first = ((GridLayoutManager) (mRecyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                        int last = ((GridLayoutManager) (mRecyclerView.getLayoutManager())).findLastVisibleItemPosition();
                        GetBitmap(first, last);
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

    public void cancleAllTask() {
        for (GetUrlVideoBitmatTask task : tasks) {
            task.cancel(true);
        }
        tasks.clear();
    }

    private void GetBitmap(int first, int last) {
        try {
            for (int i = first; i <= last; i++) {
                Bitmap bitmap = doubleBitmapCache.get(mList.get(i));
                if (bitmap != null) {
                    ImageView imageView = mRecyclerView.findViewWithTag(mList.get(i));
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                } else {
                    GetUrlVideoBitmatTask task = new GetUrlVideoBitmatTask(mList.get(i));
                    task.execute(mList.get(i));
                    tasks.add(task);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playrecord, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.iv01.setTag("1"+mList.get(position));
        holder.bt01.setTag("2"+mList.get(position));
        holder.bt02.setTag("3"+mList.get(position));
        holder.bt03.setTag("4"+mList.get(position));
        Bitmap bitmap = doubleBitmapCache.get(mList.get(position));
        if (bitmap != null) {
            holder.iv01.setImageBitmap(bitmap);
        } else {
            holder.iv01.setImageResource(R.drawable.img_index);
            GetUrlVideoBitmatTask task = new GetUrlVideoBitmatTask(mList.get(position));
            task.execute(mList.get(position));
            tasks.add(task);
        }

        holder.iv01.setOnClickListener(this);
        holder.bt01.setOnClickListener(this);
        holder.bt02.setOnClickListener(this);

        String url = mList.get(position);
        String str = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
        String time = "时间：" + str.substring(0, 4) + "年" + str.substring(4, 6) + "月" + str.substring(6, 8) + "日" + str.substring(9, 11) + ":" + str.substring(11, 13) + ":" + str.substring(13);

        holder.tv01.setText(time);
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onClick(View v) {
        String path = (String) v.getTag();
        String url = path.substring(1);
        switch (v.getId()) {
            case R.id.itme_iv01:
            case R.id.item_ib01:
                Intent intent = new Intent(mContext, PlayVideoActivity.class);
                intent.putExtra(PlayVideoActivity.PLAY_URL, url);
                mContext.startActivity(intent);
                break;
            case R.id.item_ib02:
                int fir = url.lastIndexOf('/')+1;
                String name = url.substring(fir);
                DownFileManager.getInstance().addDownUrl(url,name,url);
                break;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv01;
        TextView tv01;
        ImageButton bt01;
        ImageButton bt02;
        ImageButton bt03;

        ViewHolder(View itemView) {
            super(itemView);
            iv01 = itemView.findViewById(R.id.itme_iv01);
            tv01 = itemView.findViewById(R.id.item_tv01);
            bt01 = itemView.findViewById(R.id.item_ib01);
            bt02 = itemView.findViewById(R.id.item_ib02);
            bt03 = itemView.findViewById(R.id.item_ib03);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetUrlVideoBitmatTask extends AsyncTask<String, Bitmap, Bitmap> {
        private String url;

        GetUrlVideoBitmatTask(String url) {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
//                MediaMetadataRetriever media = new MediaMetadataRetriever();
//                media.setDataSource(strings[0], new HashMap<String, String>());
//                bitmap = media.getFrameAtTime(0);
//
//                Matrix matrix = new Matrix();
//                float scale = 320f/bitmap.getWidth();
//                matrix.postScale(scale,scale);
//                Bitmap bm = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
//
//                bitmap = bm==null?bitmap:bm;
//                final Bitmap bmp = bitmap;
//                final String path = url;
//                if (bitmap != null) {
//                    publishProgress(bitmap);
//                    if (doubleBitmapCache != null) {
//                        doubleBitmapCache.put(path, bmp);
//                    }
//                }
                KSYProbeMediaInfo ksyProbeMediaInfo = new KSYProbeMediaInfo();
                bitmap = ksyProbeMediaInfo.getVideoThumbnailAtTime(strings[0],1,320,180);
                final String path = url;
                if (bitmap != null) {
                    publishProgress(bitmap);
                    if (doubleBitmapCache != null) {
                        doubleBitmapCache.put(path, bitmap);
                    }
                }
                FlyLog.d("Get bitmap from http ok, url = %s, bitmap = " + bitmap, strings[0]);
            } catch (Exception e) {
                FlyLog.i("Get bitmap faile url = %s", strings[0]);
                e.printStackTrace();
            }
            FlyLog.d("bitmap=" + bitmap);
            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            ImageView imageView = mRecyclerView.findViewWithTag("1"+url);
            if (imageView != null) {
                imageView.setImageBitmap(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
        }

    }

}
