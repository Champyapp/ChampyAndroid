package com.example.ivan.champy_v2.interfaces;

import com.example.ivan.champy_v2.model.Token;
import com.example.ivan.champy_v2.model.User.LoginData;
import com.example.ivan.champy_v2.model.User.User;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by ivan on 01.03.16.
 */
public interface NewUser {
    @POST("/v1/users")
    Call<User> register(
            @Body LoginData loginData
    );

    @GET("/v1/users/me")
    Call<User> getUserInfo(@Query("token") String token);

    @GET("/v1/users/{id}/token")
    Call<Token> getUserToken(
      @Path("id") String id,
      @Field("AndroidOS") String token
    );




}
