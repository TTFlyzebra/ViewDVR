package com.longhorn.viewdvr.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.longhorn.viewdvr.utils.FlyLog;

import java.io.IOException;



/**
 * Author: FlyZebra
 * Time: 18-1-28 下午3:18.
 * Discription: This is FlyPlayer
 */

public class RtspVideoView extends SurfaceView implements IFlyPlayer, TextureView.SurfaceTextureListener, SurfaceHolder.Callback {
    private String urlArr[];
    private String adUrlArr[];
    private KSYMediaPlayer iMediaPlayer;
    private int currentPlayAdItem = 0;
    private int currentPlayItem = 0;
    private boolean isLoop = true;
    private int playstate = PlayContent.PLAY_NO;
    private int width, height;
//    private Surface mSurface;

    public RtspVideoView(@NonNull Context context) {
        this(context, null);
    }

    public RtspVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RtspVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
//        setSurfaceTextureListener(this);
        getHolder().addCallback(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        float scale = 480f / 848f;
        if (iMediaPlayer != null) {
            float h = iMediaPlayer.getVideoHeight();
            float w = iMediaPlayer.getVideoWidth();
            if (h != 0 && w != 0) {
                scale = h / w;
            }
        }
        height = (int) (width * scale);
        setMeasuredDimension(width, height);
    }

    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            FlyLog.i("onVideoSizeChanged: %d,%d,%d,%d", i, i1, i2, i3);
            if (i != 0 && i1 != 0) {
                width = getWidth();
                height = i1 * width / i;
                layout(0, 0, width, height);
                requestLayout();
            }
        }
    };
    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            FlyLog.i();
            if (!isLoop) {
                return;
            }
            switch (playstate) {
                case PlayContent.PLAY_AD:
                    currentPlayItem++;
                    playVideo();
                    break;
                case PlayContent.PLAY_NO:
                case PlayContent.PLAY_VIDEO:
                    currentPlayAdItem++;
                    playAd();
                    break;
                default:
                    break;

            }
        }
    };
    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            iMediaPlayer.start();
        }
    };

    private IMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            FlyLog.d("onError i=%d,i1=%d", i, i1);
            return false;
        }
    };
    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            FlyLog.i("onInfo: %d,%d", i, i1);
            return false;
        }
    };
    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
//            FlyLog.d();
        }
    };


    @Override
    public RtspVideoView setPlayUrlArr(String[] urlArr) {
        this.urlArr = urlArr;
        return this;
    }

    @Override
    public RtspVideoView setPlayAdUrlArr(String[] adUrlArr) {
        this.adUrlArr = adUrlArr;
        return this;
    }

    @Override
    public IFlyPlayer setLoop(boolean isLoop) {
        this.isLoop = isLoop;
        return this;
    }

    @Override
    public void play() {
        playVideo();
    }

    private void playVideo() {
        if (urlArr == null || urlArr.length == 0) {
            FlyLog.d("urlArr is empty.");
            return;
        }
        try {
            currentPlayItem = currentPlayItem % urlArr.length;
            initMediaPlayer();
            iMediaPlayer.setDataSource(urlArr[currentPlayItem]);
//            iMediaPlayer.setSurface(mSurface);
            iMediaPlayer.setDisplay(getHolder());
            iMediaPlayer.prepareAsync();
            playstate = PlayContent.PLAY_VIDEO;
        } catch (Exception e) {
            FlyLog.i(e.toString());
            e.printStackTrace();
        }
    }

    private void playAd() {
        if (adUrlArr == null || adUrlArr.length == 0) {
            playVideo();
            return;
        }
        try {
            currentPlayAdItem = currentPlayAdItem % adUrlArr.length;
            initMediaPlayer();
            iMediaPlayer.setDataSource(getContext(), Uri.parse(adUrlArr[currentPlayAdItem]));
//            iMediaPlayer.setSurface(mSurface);
            iMediaPlayer.setDisplay(getHolder());
            iMediaPlayer.prepareAsync();
            playstate = PlayContent.PLAY_AD;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        FlyLog.i("surface width=%d,height=%d", width, height);
//        mSurface = new Surface(surface);
        playVideo();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        FlyLog.i("surface width=%d,height=%d", width, height);

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        FlyLog.d("onSurfaceTextureDestroyed");
        iMediaPlayer.stop();
        iMediaPlayer.release();
        iMediaPlayer = null;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void initMediaPlayer() {
        if (iMediaPlayer == null) {
            iMediaPlayer = new KSYMediaPlayer.Builder(getContext()).build();
            iMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            iMediaPlayer.setScreenOnWhilePlaying(true);
            iMediaPlayer.setOnPreparedListener(onPreparedListener);
            iMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
            iMediaPlayer.setOnCompletionListener(onCompletionListener);
            iMediaPlayer.setOnErrorListener(onErrorListener);
            iMediaPlayer.setOnInfoListener(onInfoListener);
            iMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
//            iMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 128L);
//            iMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240L);
//            iMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
//            iMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48L);
//            iMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_frame", 8L);
//            iMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1L);
//            iMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0L);//  关闭播放器缓冲
//            iMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1L);
        } else {
            iMediaPlayer.reset();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        FlyLog.i("surface width=%d,height=%d", width, height);
//        mSurface = getHolder().getSurface();
        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        FlyLog.d("onSurfaceTextureDestroyed");
        iMediaPlayer.stop();
        iMediaPlayer.release();
        iMediaPlayer = null;
    }

    interface PlayContent {
        int PLAY_NO = 0;
        int PLAY_AD = 1;
        int PLAY_VIDEO = 2;
    }
}
