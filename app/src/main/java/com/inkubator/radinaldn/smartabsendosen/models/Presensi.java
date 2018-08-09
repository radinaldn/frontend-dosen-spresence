package com.inkubator.radinaldn.smartabsendosen.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by radinaldn on 19/07/18.
 */

public class Presensi {
    @SerializedName("id_presensi")
    @Expose
    private String idPresensi;
    @SerializedName("nama_dosen")
    @Expose
    private String namaDosen;
    @SerializedName("nama_matakuliah")
    @Expose
    private String namaMatakuliah;
    @SerializedName("pertemuan")
    @Expose
    private String pertemuan;
    @SerializedName("nama_kelas")
    @Expose
    private String namaKelas;
    @SerializedName("nama_ruangan")
    @Expose
    private String namaRuangan;
    @SerializedName("waktu")
    @Expose
    private String waktu;

    public String getIdPresensi() {
        return idPresensi;
    }

    public void setIdPresensi(String idPresensi) {
        this.idPresensi = idPresensi;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    public void setNamaDosen(String namaDosen) {
        this.namaDosen = namaDosen;
    }

    public String getNamaMatakuliah() {
        return namaMatakuliah;
    }

    public void setNamaMatakuliah(String namaMatakuliah) {
        this.namaMatakuliah = namaMatakuliah;
    }

    public String getPertemuan() {
        return pertemuan;
    }

    public void setPertemuan(String pertemuan) {
        this.pertemuan = pertemuan;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public String getNamaRuangan() {
        return namaRuangan;
    }

    public void setNamaRuangan(String namaRuangan) {
        this.namaRuangan = namaRuangan;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

}