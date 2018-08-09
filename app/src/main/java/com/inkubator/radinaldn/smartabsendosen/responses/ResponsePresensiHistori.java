package com.inkubator.radinaldn.smartabsendosen.responses;

/**
 * Created by radinaldn on 19/07/18.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inkubator.radinaldn.smartabsendosen.models.Presensi;

public class ResponsePresensiHistori {

    @SerializedName("master")
    @Expose
    private List<Presensi> presensi = null;

    public List<Presensi> getPresensi() {
        return presensi;
    }

    public void setMaster(List<Presensi> presensi) {
        this.presensi= presensi;
    }

}