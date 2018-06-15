package com.longhorn.viewdvr.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by FlyZebra on 2018/5/25.
 * Descrip:
 */

public class HttpDown {
    public static byte[] downRange(String downUrl, int start, int length) {
        byte[] buf = new byte[length];
        HttpURLConnection con = null;
        InputStream ins = null;
        int size = 0;
        try {
            final URL url = new URL(downUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setChunkedStreamingMode(0);
            con.setRequestProperty("RANGE", "bytes=" + start + "-" + (start + length - 1));
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK || con.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                ins = con.getInputStream();
                int nRead = 0;
                while ((nRead = ins.read(buf, size, length - size)) > 0) {
                    size += nRead;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return size == length ? buf : null;
    }
}
