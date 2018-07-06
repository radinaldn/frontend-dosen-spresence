package com.inkubator.radinaldn.smartabsendosen.responses;

/**
 * Created by radinaldn on 06/07/18.
 */
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inkubator.radinaldn.smartabsendosen.models.Dosen;

public class ResponseLogin {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<Dosen> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Dosen> getData() {
        return data;
    }

    public void setData(List<Dosen> data) {
        this.data = data;
    }

}