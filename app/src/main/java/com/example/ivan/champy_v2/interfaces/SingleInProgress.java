package com.example.ivan.champy_v2.interfaces;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by ivan on 16.03.16.
 */
public interface SingleInProgress {

    @FormUrlEncoded
    @POST("/v1/in-progress-challenges/single")
    Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> start_single_in_progress(
            @Field("challenge") String challenge,
            @Query("token") String token
    );
}
