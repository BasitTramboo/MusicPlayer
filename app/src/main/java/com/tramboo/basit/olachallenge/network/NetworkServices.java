package com.tramboo.basit.olachallenge.network;

import com.tramboo.basit.olachallenge.model.Songs;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
/**
 * Created by basit on 12/19/17.
 */

public interface NetworkServices {
    @GET("studio")
    Call<List<Songs>> getSongs();

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
