package com.inkubator.radinaldn.smartabsendosen.models;

/**
 * Created by radinaldn on 10/07/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ruangan {

    @SerializedName("id_ruangan")
    @Expose
    private String idRuangan;
    @SerializedName("nama")
    @Expose
    private String nama;

    public String getIdRuangan() {
        return idRuangan;
    }

    public Ruangan(String idRuangan, String nama) {
        this.idRuangan = idRuangan;
        this.nama = nama;
    }

    public void setIdRuangan(String idRuangan) {
        this.idRuangan = idRuangan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
