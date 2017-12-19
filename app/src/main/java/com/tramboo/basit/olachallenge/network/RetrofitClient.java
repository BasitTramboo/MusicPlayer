package com.tramboo.basit.olachallenge.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tramboo.basit.olachallenge.common.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by basit on 12/19/17.
 */

public class RetrofitClient {
    private static Retrofit retrofit;
    public static Retrofit getClient() {
        if (retrofit==null) {
            Gson gson = new GsonBuilder().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(new OkHttpClient.Builder().build())
                    .build();
            return retrofit;
        } else {
            return retrofit;
        }
    }
}
