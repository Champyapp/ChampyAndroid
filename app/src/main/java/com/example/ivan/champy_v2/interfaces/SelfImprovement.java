package com.example.ivan.champy_v2.interfaces;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ivan on 14.03.16.
 */
public interface SelfImprovement {

    @GET("/v1/challenges")
    Call<com.example.ivan.champy_v2.model.Self.SelfImprovement> getChallenges(
         @Query("token") String token
    );
}
