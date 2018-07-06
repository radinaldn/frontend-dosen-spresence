package com.inkubator.radinaldn.smartabsendosen;

/**
 * Created by radinaldn on 17/03/18.
 */

public class Koneksi {
    public String getServer(){
        String isi = "http://192.168.1.103/";
        return isi;
    }

    public String getUrl()
    {
        String isi = getServer()+"qrcode/API/"; //over online web
        return isi;
    }

    public String getImagesDir(){
        String isi = getServer()+"qrcode/images/";
        return isi;
    }
}
