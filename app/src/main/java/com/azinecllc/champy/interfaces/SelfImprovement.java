package com.azinecllc.champy.interfaces;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SelfImprovement {

    @GET("/v1/challenges")
    Call<com.azinecllc.champy.model.self.SelfImprovement> getChallenges(
         @Query("token") String token
    );
}
