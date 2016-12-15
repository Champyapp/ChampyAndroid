package com.azinecllc.champy.interfaces;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ActiveInProgress {

    @GET("/v1/users/{id}/history/events/{updated}")
    Call<com.azinecllc.champy.model.active_in_progress.ActiveInProgress> getActiveInProgress(
            @Path("id") String id,
            @Path("updated") String updated,
            @Query("token") String token
    );

}
