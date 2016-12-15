package com.azinecllc.champy.interfaces;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Query;

public interface CreateChallenge {

    @FormUrlEncoded
    @POST("/v1/challenges")
    Call<com.azinecllc.champy.model.create_challenge.CreateChallenge> createChallenge(
            @Field("name") String name,
            @Field("type") String type_id,
            @Field("description") String description,
            @Field("details") String details,
            @Field("duration") String duration,
            @Query("token") String token

    );

}
