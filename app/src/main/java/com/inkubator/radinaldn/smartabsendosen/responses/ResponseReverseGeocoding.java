package com.inkubator.radinaldn.smartabsendosen.responses;

/**
 * Created by radinaldn on 10/08/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inkubator.radinaldn.smartabsendosen.models.googles.ResultReverseGeocoding;

import java.util.List;

public class ResponseReverseGeocoding {

    @SerializedName("results")
    @Expose
    private List<ResultReverseGeocoding> results = null;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("error_message")
    @Expose
    private String error_message;

    public List<ResultReverseGeocoding> getResults() {
        return results;
    }

    public void setResults(List<ResultReverseGeocoding> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
}