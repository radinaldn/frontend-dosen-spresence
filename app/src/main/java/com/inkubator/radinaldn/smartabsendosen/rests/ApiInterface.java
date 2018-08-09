package com.inkubator.radinaldn.smartabsendosen.rests;

import com.inkubator.radinaldn.smartabsendosen.responses.ResponseHistoriMengajar;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseKehadiranDosen;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseLogin;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseMengajar;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponsePresensi;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponsePresensiDetail;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseRuangan;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseStatusPresensi;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by radinaldn on 06/07/18.
 */

public interface ApiInterface {

    /*
    API Dosen
     */

    // untuk mendapatkan data kehadiran dosen
    @GET("dosen/find-all-by-status-kehadiran")
    Call<ResponseKehadiranDosen> dosenFindAllByStatusKehadiran(
            @Query("status_kehadiran") String status_kehadiran
    );

    // untuk mendapatkan status presensi ditutup/dibuka
    @GET("presensi/is-close")
    Call<ResponseStatusPresensi> isCloseByIdPresensi(
            @Query("id_presensi") String id_presensi
    );

    // untuk mendapatkan all ruangan
    @GET("ruangan/find-all")
    Call<ResponseRuangan> ruanganFindAll();

    // untuk mendapatkan histori mengajar (day 1, 2, ...) by id_presensi
    @GET("presensi/histori-mengajar-by-id-mengajar")
    Call<ResponseHistoriMengajar> presensiHistoriMengajarByIdMengajar(
            @Query("id_mengajar") String id_mengajar
    );

    // untuk mendapatkan data mahasiswa yang berhasil scan qr-code
    @GET("presensi-detail/find-all-mahasiswa-by-id-presensi-and-status-kehadiran")
    Call<ResponsePresensiDetail> presensiDetailFindAllMahasiswaByIdPresensiAndStatusKehadiran(
            @Query("id_presensi") String id_presensi,
            @Query("status_kehadiran") String status_kehadiran
    );

    // untuk melihat jadwal mengajar
    @GET("mengajar/find-all-by-nip-and-dayname")
    Call<ResponseMengajar> mengajarFindAllByNipAndName(
            @Query("nip") String nip,
            @Query("dayname") String dayname);

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
    Call<ResponsePresensi> presensiAdd(
            @Field("id_mengajar") String id_mengajar,
            @Field("id_ruangan") String id_ruangan,
            @Field("lat") String lat,
            @Field("lng") String lng
    );

    // untuk mengkonfirmasi presensi
    @FormUrlEncoded
    @POST("presensi-detail/konfirmasi-all")
    Call<ResponsePresensiDetail> presensiDetailKonfirmasiAll(
            @Field("id_presensi") String id_presensi
    );

    // untuk membatalkan presensi
    @FormUrlEncoded
    @POST("presensi-detail/batalkan-presensi")
    Call<ResponsePresensiDetail> presensiDetailBatalkanPresensi(
            @Field("id_presensi") String id_presensi,
            @Field("nim") String nim
    );

    // untuk menerima presensi
    @FormUrlEncoded
    @POST("presensi-detail/terima-presensi")
    Call<ResponsePresensiDetail> presensiDetailTerimaPresensi(
            @Field("id_presensi") String id_presensi,
            @Field("nim") String nim
    );
}
