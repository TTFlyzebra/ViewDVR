package com.longhorn.viewdvr.view;


/**
 * Author: FlyZebra
 * Time: 18-1-28 下午3:18.
 * Discription: This is IFlyPlayer
 */

public interface IFlyPlayer {
    /**
     * 设置播放地址：
     * @param urlArr
     */
    IFlyPlayer setPlayUrlArr(String urlArr[]);

    /**
     * 设置播放广告地址：
     * @param adUrlArr
     */
    IFlyPlayer setPlayAdUrlArr(String adUrlArr[]);

    IFlyPlayer setLoop(boolean isLoop);

    void play();

}
