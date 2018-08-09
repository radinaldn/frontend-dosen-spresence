package com.inkubator.radinaldn.smartabsendosen.models;

/**
 * Created by radinaldn on 09/07/18.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mengajar {
    public Mengajar(String idMengajar, String namaKelas, String waktuMulai, String namaMatakuliah, String sks) {
        this.idMengajar = idMengajar;
        this.nip = nip;
        this.namaDosen = namaDosen;
        this.namaKelas = namaKelas;
        this.waktuMulai = waktuMulai;
        this.hari = hari;
        this.namaMatakuliah = namaMatakuliah;
        this.sks = sks;
        this.status = status;
    }

    @SerializedName("id_mengajar")
    @Expose
    private String idMengajar;
    @SerializedName("nip")
    @Expose
    private String nip;
    @SerializedName("nama_dosen")
    @Expose
    private String namaDosen;
    @SerializedName("nama_kelas")
    @Expose
    private String namaKelas;
    @SerializedName("waktu_mulai")
    @Expose
    private String waktuMulai;
    @SerializedName("hari")
    @Expose
    private String hari;
    @SerializedName("nama_matakuliah")
    @Expose
    private String namaMatakuliah;
    @SerializedName("sks")
    @Expose
    private String sks;
    @SerializedName("status")
    @Expose
    private String status;

    public String getIdMengajar() {
        return idMengajar;
    }

    public void setIdMengajar(String idMengajar) {
        this.idMengajar = idMengajar;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    public void setNamaDosen(String namaDosen) {
        this.namaDosen = namaDosen;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public String getWaktuMulai() {
        return waktuMulai;
    }

    public void setWaktuMulai(String waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getNamaMatakuliah() {
        return namaMatakuliah;
    }

    public void setNamaMatakuliah(String namaMatakuliah) {
        this.namaMatakuliah = namaMatakuliah;
    }

    public String getSks() {
        return sks;
    }

    public void setSks(String sks) {
        this.sks = sks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
