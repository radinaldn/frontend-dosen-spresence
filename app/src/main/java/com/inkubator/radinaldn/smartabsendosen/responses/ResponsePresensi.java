package com.inkubator.radinaldn.smartabsendosen.responses;

/**
 * Created by radinaldn on 10/07/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponsePresensi {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("id_presensi")
    @Expose
    private String idPresensi;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("code")
    @Expose
    private String code;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ResponsePresensi(String status, String idPresensi) {
        this.status = status;
        this.idPresensi = idPresensi;
    }

    public String getIdPresensi() {
        return idPresensi;
    }

    public void setIdPresensi(String idPresensi) {
        this.idPresensi = idPresensi;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
