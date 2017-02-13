package com.azinecllc.champy.interfaces;

import com.azinecllc.champy.model.Token;
import com.azinecllc.champy.model.user.LoginData;
import com.azinecllc.champy.model.user.User;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface NewUser {

    @POST("/v1/users")
    Call<User> register(
            @Body LoginData loginData
    );

    @GET("/v1/users/me")
    Call<User> getUserInfo(
            @Query("token") String token);


    @GET("/v1/users/{id}/token")
    Call<Token> getUserToken(
            @Path("id") String id,
            @Query(value = "AndroidOS", encoded = true) String token
    );




}
