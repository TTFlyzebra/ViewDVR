package com.longhorn.viewdvr.module.wifi;

import android.os.Handler;
import android.os.Looper;


import com.longhorn.viewdvr.data.Global;
import com.longhorn.viewdvr.utils.ByteTools;
import com.longhorn.viewdvr.utils.FlyLog;
import com.longhorn.viewdvr.utils.IPAdressTools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.CRC32;

/**
 * Created by FlyZebra on 2018/6/14.
 * Descrip:
 */

public class NioSocketTools {
    private List<SocketResult> mSocketResultList = new ArrayList<>();
    private static final Executor executro = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private SocketChannel client;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private ByteBuffer writeBuffer = ByteBuffer.allocateDirect(4096);
    private ByteBuffer readBuffer = ByteBuffer.allocateDirect(4096);
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private HeartbeatThread heartbeatThread;
    private static final long HEARTBAT_MILLIS = 1000;

    public void registerSocketResult(SocketResult mSocketResult) {
        mSocketResultList.add(mSocketResult);
    }

    public void unregisterSocketResult(SocketResult socketResult) {
        mSocketResultList.remove(socketResult);
    }

    private static class NioSocketToolsHolder {
        public static final NioSocketTools sInstance = new NioSocketTools();
    }

    public static NioSocketTools getInstance() {
        return NioSocketTools.NioSocketToolsHolder.sInstance;
    }

    public void loopRecvBytes() {
        try {
            FlyLog.d("SocketChannel.open()");
            client = SocketChannel.open();
            client.configureBlocking(false);
            boolean flag = client.connect(new InetSocketAddress(Global.DVR_IP, Global.CMD_PORT));
            FlyLog.d("connect result=%b", flag);
            while (!client.finishConnect()) {
                Thread.sleep(100);
            }

            //发送心跳包
            new Thread(new Runnable() {
                @Override
                public void run() {
                    loopSendHeartBeat(HEARTBAT_MILLIS);
                }
            }).start();

            //接收消息循环
            FlyLog.d("recv loop start");
            while (isRunning.get()) {
                readBuffer.clear();
                int readLen = 1;
                while (readLen > 0) {
                    readLen = client.read(readBuffer);
                }
                readBuffer.flip();
                parseRecvBytes(readBuffer);
                Thread.sleep(100);
            }
            FlyLog.d("revc loop end");

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
    }

    public void init() {
        if (threadNum < 0 || heartbeatThread == null) {
            isRunning.set(true);
            heartbeatThread = new HeartbeatThread();
        }
    }

    public void close() {
        isRunning.set(false);
        mSocketResultList.clear();
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void parseRecvBytes(ByteBuffer readBuffer) {
        while (readBuffer.hasRemaining()) {
            byte ee = readBuffer.get();
            if (ee != (byte) 0xEE) {
                break;
            }
            byte aa = readBuffer.get();
            if (ee != (byte) 0xEE) {
                break;
            }

            byte lenBytes[] = new byte[4];
            readBuffer.get(lenBytes, 0, 4);
            final int length = ByteTools.bytes2Int(lenBytes, 0);
            if (length > 4096) {
                break;
            }
            final byte recv[] = new byte[length];
            if (readBuffer.remaining() < length) {
                break;
            }
            readBuffer.get(recv, 0, length);
            int crc32 = readBuffer.getInt();
            FlyLog.d("recv length=%d,data=%s", length, ByteTools.bytes2HexString(recv));
            //TODO:做crc32校验
            //返回数据到主线程调用
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (SocketResult socketResult : mSocketResultList) {
                        socketResult.result(new ResultData(length, recv, "success"));
                    }
                }
            });

        }
    }

    public void sendCommand(final byte command[]) {
        executro.execute(new Runnable() {
            @Override
            public void run() {
                send(command);
            }
        });
    }

    private synchronized void send(byte command[]) {
        try {
            writeBuffer.clear();
            writeBuffer.putShort((short) 0xEEAA);
            writeBuffer.put(command);
            CRC32 crc32 = new CRC32();
            crc32.update(command);
            long lcrc32 = crc32.getValue();
//            byte intcrc32[] = ByteTools.intToBytes((int) lcrc32);
//            writeBuffer.put(intcrc32, 0, 4);
            writeBuffer.putInt((int) lcrc32);
            writeBuffer.flip();
            writeBuffer.mark();
            byte send[] = new byte[writeBuffer.remaining()];
            writeBuffer.get(send, 0, send.length);
            writeBuffer.reset();
            FlyLog.d("send wifi command=%s", ByteTools.bytes2HexString(send));
            if (client != null && client.isConnected() && isRunning.get()) {
                while (writeBuffer.hasRemaining() && isRunning.get()) {
                    int len = client.write(writeBuffer);
                }
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
    }

    private void loopSendHeartBeat(long millis) {
        FlyLog.d("loop SendHeartBeat.");
        while (isRunning.get()) {
            send(CommandType.HEARTBEAT);
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static int threadNum = 0;

    private class HeartbeatThread extends Thread {
        public HeartbeatThread() {
            if (threadNum < 1) {
                threadNum++;
                start();
            }
        }

        @Override
        public void run() {
            FlyLog.d("heardBatThread start.");
            while (isRunning.get()) {
                //获取RTSP IP地址
                if (isRunning.get()) {
                    //显示Rtsp视频
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            for (SocketResult socketResult : mSocketResultList) {
                                socketResult.result(new ResultData(8, new byte[]{0x00,0x00}, "get ip ok"));
                            }
                        }
                    });
                    //连接服务端
                    loopRecvBytes();
                }
            }
            threadNum--;
        }
    }
}
