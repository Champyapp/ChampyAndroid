package com.azinecllc.champy.interfaces;

import com.azinecllc.champy.model.duel.Duel;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface SingleInProgress {

    @FormUrlEncoded
    @POST("/v1/in-progress-ic_score_progress/single")
    Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> startSingleInProgress(
            @Field("challenge") String challenge,
            @Query("token") String token
    );

    @GET("/v1/in-progress-ic_score_progress/{id}/surrender")
    Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> surrender(
                    @Path("id") String id,
                    @Query("token") String token
    );

    @GET("/v1/in-progress-ic_score_progress/{id}/reject")
    Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> reject(
            @Path("id") String id,
            @Query("token") String token
    );

    @GET("/v1/in-progress-ic_score_progress/{id}/join")
    Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> join(
            @Path("id") String id,
            @Query("token") String token
    );

    @GET("/v1/in-progress-ic_score_progress/{id}")
    Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> getChallenges(
            @Path("id") String id,
            @Query("token") String token
    );

    @GET("/v1/in-progress-ic_score_progress/{id}/check")
    Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> checkChallenge(
            @Path("id") String id,
            @Query("token") String token
    );

    @FormUrlEncoded
    @POST("/v1/in-progress-ic_score_progress/button_duel")
    Call<Duel> startDuel(
            @Field("recipient") String recipient,
            @Field("challenge") String challenge,
            @Query("token") String token
    );
}
