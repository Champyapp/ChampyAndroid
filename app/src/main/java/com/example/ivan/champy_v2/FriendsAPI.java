package com.example.ivan.champy_v2;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by ivan on 05.02.16.
 */
public interface FriendsAPI {
    @GET("/api/v1/random")
    public void getBooks(Callback<List<MainPojo>> response);
}
