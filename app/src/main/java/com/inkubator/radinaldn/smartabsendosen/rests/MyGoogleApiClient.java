package com.inkubator.radinaldn.smartabsendosen.rests;

import com.inkubator.radinaldn.smartabsendosen.config.ServerConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by radinaldn on 10/08/18.
 */

public class MyGoogleApiClient {

    private static final String GOOGLE_BASE_URL = ServerConfig.GOOGLE_API_ENDPOINT;
    private static Retrofit retrofit = null;

    public static Retrofit getGoogleApiClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(GOOGLE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
