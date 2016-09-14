package com.example.ivan.champy_v2.helper;

import android.app.Activity;
import android.util.Log;

import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.User.Data;
import com.example.ivan.champy_v2.model.User.User;

import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class CHLoadUserProgressBarInfo {


    Activity activity;


    public CHLoadUserProgressBarInfo(Activity activity) {
        this.activity = activity;
    }

    public void loadUserProgressBarInfo() {
        final SessionManager sessionManager = new SessionManager(activity);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        NewUser newUser = retrofit.create(NewUser.class);

        Call<User> userCall = newUser.getUserInfo(token);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                User decodedResponse = response.body();
                Data data = decodedResponse.getData();
                SessionManager sessionManager = new SessionManager(activity);
                String size = sessionManager.get_duel_pending();
                sessionManager.set_duel_pending(size);
                sessionManager.setChampyOptions(
                        data.getAllChallengesCount().toString(),
                        data.getSuccessChallenges().toString(),
                        data.getScore().toString(),
                        data.getLevel().getNumber().toString());

                Log.i("LoadUserProgressBarInfo", "onResponse: VSE OK");
            }

            @Override
            public void onFailure(Throwable t) { }
        });

    }

}
