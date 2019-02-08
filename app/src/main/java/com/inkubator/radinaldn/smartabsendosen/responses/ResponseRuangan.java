package com.inkubator.radinaldn.smartabsendosen.responses;

/**
 * Created by radinaldn on 10/07/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inkubator.radinaldn.smartabsendosen.models.Ruangan;

import java.util.List;

public class ResponseRuangan {

    @SerializedName("master")
    @Expose
    private List<Ruangan> ruangan = null;

    public ResponseRuangan(List<Ruangan> ruangan) {
        this.ruangan = ruangan;
    }

    public List<Ruangan> getRuangan() {
        return ruangan;
    }

}