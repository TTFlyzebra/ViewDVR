package com.longhorn.viewdvr.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.bumptech.glide.Glide;
import com.longhorn.viewdvr.R;

import java.util.List;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-12-上午9:01.
 */

public class FullPhotoActivity extends BaseActivity {
    private ViewPager viewPager;
    private List<String> list;
    private int cPos;
    private MyPagerAdapater adapater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullphoto);

        list = getIntent().getStringArrayListExtra("LIST");
        cPos = getIntent().getIntExtra("ITEM",0);

        viewPager = findViewById(R.id.ac_fullphoto_iv01);
        adapater = new MyPagerAdapater();
        viewPager.setAdapter(adapater);
        viewPager.setCurrentItem(cPos);



    }

    class MyPagerAdapater extends PagerAdapter{

        @Override
        public int getCount() {
            return list==null?0:list.size();
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
            ImageView iv = new ImageView(FullPhotoActivity.this);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(lp);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setTag(R.id.glideid,position);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            container.addView(iv);
            Glide.with(FullPhotoActivity.this).load(list.get(position)).into(iv);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
