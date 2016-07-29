package com.example.ivan.champy_v2.interfaces;

import com.example.ivan.champy_v2.duel.Duel;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
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
    @GET("/v1/in-progress-challenges/{id}/surrender")
    Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> Surrender(
                    @Path("id") String id,
                    @Query("token") String token
            );

    @GET("/v1/in-progress-challenges/{id}/join")
    Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> Join (
            @Path("id") String id,
            @Query("token") String token
    );

    @GET("/v1/in-progress-challenges/{id}")
    Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> get_challenge(
            @Path("id") String id,
            @Query("token") String token
    );

    @FormUrlEncoded
    @POST("/v1/in-progress-challenges/duel")
    Call<Duel> Start_duel(
      @Field("recipient") String recipient,
      @Field("challenge") String challenge,
      @Query("token") String token
    );
}
