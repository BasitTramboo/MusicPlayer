package com.tramboo.basit.olachallenge.network;

import com.tramboo.basit.olachallenge.model.Songs;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by basit on 12/16/17.
 */

public interface NetworkServices {
    @GET("studio")
    Call<List<Songs>> getSongs();
}
