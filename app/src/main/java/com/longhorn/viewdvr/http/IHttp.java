package com.longhorn.viewdvr.http;

import java.util.Map;

/**
 * Author: FlyZebra
 * Created by FlyZebra on 2018/3/29-下午1:24.
 */
public interface IHttp {
    /**
     * 发送网络请求
     * @param url 请求的URL地址
     * @param tag 请求标识
     * @param result 主线程回调监听
     */

    void getString(String url, Object tag, HttpResult result);

    void postString(String url, Map<String, String> map, Object tag, HttpResult result);

    void postJson(String url, String json, Object tag, HttpResult result);

    String readDiskCache(String url);

    void cancelAll(Object tag);

    interface HttpResult {
        void succeed(Object object);
        void failed(Object object);
    }

}
