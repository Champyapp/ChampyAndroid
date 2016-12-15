package com.azinecllc.champy.interfaces;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SelfImprovement {

    @GET("/v1/ic_score_progress")
    Call<com.azinecllc.champy.model.self.SelfImprovement> getChallenges(
         @Query("token") String token
    );
}
