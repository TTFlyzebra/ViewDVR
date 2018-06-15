package com.longhorn.viewdvr.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.longhorn.viewdvr.R;
import com.longhorn.viewdvr.data.DvrFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-12-上午9:01.
 */

public class FullPhotoActivity extends Activity {
    private ViewPager viewPager;
    private List<DvrFile> mList = new ArrayList<>();
    private int cPos;
    private MyPagerAdapater adapater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullphoto);

        List<DvrFile> list = (List<DvrFile>) getIntent().getSerializableExtra("LIST");
        int pos = getIntent().getIntExtra("ITEM",0);

        mList.clear();
        int tempInt = 0;
        for(int i=0;i<list.size();i++){
            if(list.get(i).rvType==2){
                if(i==pos){
                    cPos = tempInt;
                }
                mList.add(list.get(i));
                tempInt++;
            }
        }
        viewPager = findViewById(R.id.ac_fullphoto_iv01);
        adapater = new MyPagerAdapater();
        viewPager.setAdapter(adapater);
        viewPager.setCurrentItem(cPos);



    }

    class MyPagerAdapater extends PagerAdapter{

        @Override
        public int getCount() {
            return mList ==null?0: mList.size();
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView iv = new PhotoView(FullPhotoActivity.this);
            iv.enable();
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(lp);
            iv.setScaleType(PhotoView.ScaleType.FIT_CENTER);
            iv.setTag(R.id.glideid,position);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            container.addView(iv);
            Glide.with(FullPhotoActivity.this).load(mList.get(position).getUrl()).into(iv);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
