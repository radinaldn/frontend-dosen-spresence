package com.inkubator.radinaldn.smartabsendosen.config;

import com.inkubator.radinaldn.smartabsendosen.BuildConfig;

/**
 * Created by radinaldn on 06/07/18.
 */

public class ServerConfig {
    public static final String DOMAIN_SERVER = BuildConfig.DOMAIN_SERVER;
    public static final String SERVER_URL = DOMAIN_SERVER + "smart-presence/api/v1/";
    public static final String API_ENDPOINT = SERVER_URL;
    public static final String GOOGLE_API_ENDPOINT = "https://maps.googleapis.com/maps/api/";
    public static final String GOOGLE_API_KEY = BuildConfig.GOOGLE_API_KEY;
    public static final String IMAGE_PATH = DOMAIN_SERVER + "smart-presence/web/files/images/";
    public static final String QRCODE_PATH = DOMAIN_SERVER + "smart-presence/web/files/qrcode/";
}
