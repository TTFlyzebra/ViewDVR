package com.longhorn.viewdvr.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.longhorn.viewdvr.utils.DateTools;

/**
 * Created by FlyZebra on 2018/6/1.
 * Descrip:
 */

@SuppressLint("AppCompatCustomView")
public class TimeClock extends TextView{
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable setTimeTask = new Runnable() {
        @Override
        public void run() {
            String time = DateTools.getStringTime("HH:mm:ss");
            setText(time);
            mHandler.postDelayed(setTimeTask,1000);
        }
    };
    public TimeClock(Context context) {
        super(context);
    }

    public TimeClock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeClock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.post(setTimeTask);
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacks(setTimeTask);
        super.onDetachedFromWindow();
    }
}
