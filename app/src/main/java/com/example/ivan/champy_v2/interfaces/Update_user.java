package com.example.ivan.champy_v2.interfaces;

import com.example.ivan.champy_v2.model.user.Delete;
import com.example.ivan.champy_v2.model.user.Profile_data;
import com.example.ivan.champy_v2.model.user.User;
import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

public interface Update_user {

    @FormUrlEncoded
    @PUT("/v1/users/{id}")
    Call<User> update_user_name(
      @Path("id") String id,
      @Query("token") String token,
      @Field("name") String name
    );

    @Multipart
    @PUT("/v1/users/{id}/photo")
    Call<User> update_photo(
       @Path("id") String id,
       @Query("token") String token,
       @Part("photo\"; filename=\"profile.jpeg\" ") RequestBody file
    );

    //@FormUrlEncoded
    @PUT("/v1/users/{id}/profile-options")
    Call<User> update_profile_options(
            @Path("id") String id,
            @Query("token") String token,
            @Body Profile_data profile_data
    );

    @DELETE("/v1/users/{id}")
    Call<Delete> delete_user(
            @Path("id") String id,
            @Query("token") String token
    );

    // check this
    @POST("/v1/users/surrender")
    Call<User> surrenderAllChallenge(
            @Query("token") String token
    );

}
