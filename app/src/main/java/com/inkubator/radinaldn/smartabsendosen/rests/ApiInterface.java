package com.inkubator.radinaldn.smartabsendosen.rests;

import com.inkubator.radinaldn.smartabsendosen.responses.ResponseLogin;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by radinaldn on 06/07/18.
 */

public interface ApiInterface {

    /*
    API Dosen
     */

    // untuk login
    @FormUrlEncoded
    @POST("dosen/login")
    Call<ResponseLogin> login(
            @Field("username") String nip,
            @Field("password") String password,
            @Field("imei") String imei
    );

    // untuk memulai/membuka presensi
    @FormUrlEncoded
    @POST("presensi/add")
    Call<ResponseBody> presensiAdd(
            @Field("id_mengajar") String id_mengajar,
            @Field("id_ruangan") String id_ruangan,
            @Field("lat") String lat,
            @Field("lng") String lng
    );
}
