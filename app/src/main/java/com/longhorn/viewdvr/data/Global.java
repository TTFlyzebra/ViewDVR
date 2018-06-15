package com.longhorn.viewdvr.data;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-2-下午4:54.
 */

public class Global {
    public static String DVR_IP = "192.168.42.1";
    public static String WEB_PORT = "8080";
    public static int CMD_PORT = 7878;
    public static String DVR_RTSP = "rtsp://"+DVR_IP+"/live";
    public static String DVR_WEB = "http://"+DVR_IP+":"+WEB_PORT;
//    public static String PATH_PHO = DVR_WEB+"/DCIM/PHO/PHO_";
//    public static String PATH_NOR = DVR_WEB+"/DCIM/NOR/NOR_";
//    public static String PATH_EVT = DVR_WEB+"/DCIM/EVT/EVT_";
    public static String PATH_PHO = DVR_WEB+"/DCIM/PHO/";
    public static String PATH_NOR = DVR_WEB+"/DCIM/NOR/";
    public static String PATH_EVT = DVR_WEB+"/DCIM/EVT/";
}
