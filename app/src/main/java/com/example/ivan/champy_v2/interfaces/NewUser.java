package com.example.ivan.champy_v2.interfaces;

import com.example.ivan.champy_v2.model.Token;
import com.example.ivan.champy_v2.model.User.LoginData;
import com.example.ivan.champy_v2.model.User.User;

import java.util.List;

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

    @GET("/v1/users/getusersbyfacebookid")
    Call<User> getFacebookFriends(
            @Query("token") String token
            //@Query("facebookFriends[]") List<String> facebookFriends
    );


    @GET("/v1/users/{id}/token")
    Call<Token> getUserToken(
      @Path("id") String id,
      @Query(value = "AndroidOS", encoded = true) String token
    );




}
