package com.example.ivan.champy_v2.interfaces;

import com.example.ivan.champy_v2.model.Friend.Friend;

import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by ivan on 07.03.16.
 */
public interface Friends {
    @FormUrlEncoded
    @POST("/v1/users/{owner}/friends")
    Call<Friend> sendFriendRequest(
            @Path("owner") String owner,
            @Field("friend") String friend,
            @Query("token") String token);

    @GET("/v1/users/{owner}/friends")
    Call<Friend> getUserFriends(
            @Path("owner") String owner,
            @Query("token") String token
    );

    @PUT("/v1/users/{owner}/friends/{friend}")
    Call<Friend> acceptFriendRequest(
            @Path("owner") String owner,
            @Path("friend") String friend,
            @Query("token") String token);

    @DELETE("/v1/users/{owner}/friends/{friend}")
    Call<Friend> removeFriend(
            @Path("owner") String owner,
            @Path("friend") String friend,
            @Query("token") String token);
}
