package com.example.ivan.champy_v2.interfaces;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by ivan on 16.03.16.
 */
public interface ActiveInProgress {

    @GET("/v1/users/{id}/history/events/{updated}")
    Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> getActiveInProgress(
            @Path("id") String id,
            @Path("updated") String updated,
            @Query("token") String token
    );
}
