package com.inkubator.radinaldn.smartabsendosen.rests;

import com.inkubator.radinaldn.smartabsendosen.responses.ResponseReverseGeocoding;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by radinaldn on 10/08/18.
 */

public interface GoogleApiInterface {
    // untuk mendapatkan response dari Google Reverse Geocoding
    @GET("geocode/json")
    Call<ResponseReverseGeocoding> geocodeJson(
            @Query("latlng") String latlng,
            @Query("key") String key
    );
}
