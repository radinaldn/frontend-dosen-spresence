package com.inkubator.radinaldn.smartabsendosen.config;

/**
 * Created by radinaldn on 06/07/18.
 */

public class ServerConfig {
    //public static final String DOMAIN_SERVER = "http://10.20.227.159/";
    public static final String DOMAIN_SERVER = "http://192.168.1.103/";
//    public static final String DOMAIN_SERVER = "http://192.168.43.217/";
//    public static final String DOMAIN_SERVER = "https://topapp.id/";
    public static final String SERVER_URL = DOMAIN_SERVER+"smart-presence/api/v1/";
    public static final String API_ENDPOINT = SERVER_URL;
    public static final String GOOGLE_API_ENDPOINT = "https://maps.googleapis.com/maps/api/";
    public static final String GOOGLE_API_KEY = "AIzaSyABTxpRVD2u_czfqMX7bb6H_WYBxbeXvC4";
    public static final String IMAGE_PATH = DOMAIN_SERVER+"smart-presence/web/files/images/";
    public static final String QRCODE_PATH = DOMAIN_SERVER+"smart-presence/web/files/qrcode/";
}
