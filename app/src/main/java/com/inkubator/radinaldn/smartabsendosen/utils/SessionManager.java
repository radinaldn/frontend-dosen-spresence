package com.inkubator.radinaldn.smartabsendosen.utils;

/**
 * Created by radinaldn on 06/07/18.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import java.util.HashMap;

/**
 * Created by radinaldn on 03/07/18.
 */

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context _context;

    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String NIP = "nip";
    public static final String PASSWORD = "password";
    public static final String IMEI = "imei";
    public static final String NAMA = "nama";
    public static final String JK = "jk";
    public static final String FOTO = "foto";

    public Context get_context(){
        return _context;
    }

    // constructor
    public SessionManager(Context context){
        this._context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String nip, String password, String imei, String nama, String jk, String foto){
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(NIP, nip);
        editor.putString(PASSWORD, password);
        editor.putString(IMEI, imei);
        editor.putString(NAMA, nama);
        editor.putString(JK, jk);
        editor.putString(FOTO, foto);
        editor.commit();
    }

    public HashMap<String, String> getDosenDetail(){
        HashMap<String,String> dosen = new HashMap<>();
        dosen.put(NIP, sharedPreferences.getString(NIP,null));
        dosen.put(PASSWORD, sharedPreferences.getString(PASSWORD, null));
        dosen.put(IMEI, sharedPreferences.getString(IMEI,null));
        dosen.put(NAMA, sharedPreferences.getString(NAMA,null));
        dosen.put(JK, sharedPreferences.getString(JK,null));
        dosen.put(FOTO, sharedPreferences.getString(FOTO,null));

        return dosen;
    }

    public void logoutDosen(){
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }
}
