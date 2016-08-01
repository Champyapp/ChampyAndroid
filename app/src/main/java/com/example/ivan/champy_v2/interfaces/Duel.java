package com.example.ivan.champy_v2.interfaces;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface Duel {

    @GET("/v1/in-progress-challenges/{id}/join")
    Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> Join (
            @Path("id") String id,
            @Query("token") String token
    );

}
