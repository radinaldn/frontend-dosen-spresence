package com.inkubator.radinaldn.smartabsendosen.responses;

/**
 * Created by radinaldn on 09/07/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inkubator.radinaldn.smartabsendosen.models.Mengajar;

import java.util.List;

public class ResponseMengajar {

    @SerializedName("master")
    @Expose
    private List<Mengajar> mengajar = null;

    public ResponseMengajar(List<Mengajar> mengajar) {
        this.mengajar = mengajar;
    }

    public List<Mengajar> getMengajar() {
        return this.mengajar;
    }


}
