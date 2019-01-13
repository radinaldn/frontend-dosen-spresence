package com.inkubator.radinaldn.smartabsendosen.rests;

import com.inkubator.radinaldn.smartabsendosen.responses.ResponseHistoriMengajar;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseKehadiranDosen;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseLogin;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseMengajar;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponsePresensi;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponsePresensiDetail;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseReverseGeocoding;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseRuangan;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseStatusPresensi;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseUpdateLocation;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseUpdatePassword;

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

    // untuk mengupdate password dosen
    @FormUrlEncoded
    @POST("dosen/update-password")
    Call<ResponseUpdatePassword> dosenUpdatePassword(
            @Field("nip") String nip,
            @Field("old_password") String old_password,
            @Field("new_password") String new_password
    );

    // untuk mendapatkan data kehadiran dosen berdasarkan inputan nama
    @GET("dosen/kehadiran-dosen-find-by-name")
    Call<ResponseKehadiranDosen> dosenKehadiranFindByName(
            @Query("name") String name
    );

    // untuk mendapatkan data perkuliahan by nip dan date
    @GET("presensi/find-by-nip-and-date")
    Call<ResponseHistoriMengajar> presensiFindByNipAndDate(
            @Query("nip") String nip,
            @Query("date") String date
    );

    // untuk mendapatkan data mengajar hari ini
    @GET("mengajar/find-all-today-by-nip")
    Call<ResponseMengajar> mengajarFindAllTodayByNip(
            @Query("nip") String nip
    );

    // untuk mengupdate data kehadiran dosen
    @FormUrlEncoded
    @POST("dosen/update-location")
    Call<ResponseUpdateLocation> dosenUpdateLocation(
            @Field("nip") String nip,
            @Field("status_kehadiran") String status_kehadiran,
            @Field("nama_kota") String nama_kota
    );

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
            @Field("lng") String lng,
            @Field("nip") String nip,
            @Field("nama_ruangan") String nama_ruangan
    );

    // untuk mengkonfirmasi presensi
    @FormUrlEncoded
    @POST("presensi-detail/konfirmasi-all")
    Call<ResponseStatusPresensi> presensiDetailKonfirmasiAll(
            @Field("id_presensi") String id_presensi
    );

    // untuk membatalkan presensi
    @FormUrlEncoded
    @POST("presensi-detail/batalkan-presensi")
    Call<ResponsePresensiDetail> presensiDetailBatalkanPresensi(
            @Field("id_presensi") String id_presensi,
            @Field("nim") String nim
    );

    // untuk membatalkan presensi dan menambah poin tidak hadir
    @FormUrlEncoded
    @POST("presensi-detail/batalkan-presensi-dan-tambah-tidak-hadir")
    Call<ResponsePresensi> presensiDetailBatalkanPresensiDanTambahTidakHadir(
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

    // untuk menerima presensi dan mengurangi poin tidak hadir
    @FormUrlEncoded
    @POST("presensi-detail/terima-presensi-dan-kurangi-tidak-hadir")
    Call<ResponsePresensi> presensiDetailTerimaPresensiDanKurangiTidakHadir(
            @Field("id_presensi") String id_presensi,
            @Field("nim") String nim
    );

}
