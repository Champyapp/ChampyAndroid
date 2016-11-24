package com.example.ivan.champy_v2.helper;

import android.content.Context;
import android.util.Log;

import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.user.Data;
import com.example.ivan.champy_v2.model.user.User;
import com.example.ivan.champy_v2.utils.SessionManager;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class CHLoadUserProgressBarInfo {

    private Context context;

    public CHLoadUserProgressBarInfo(Context context) {
        this.context = context;
    }

    public void loadUserProgressBarInfo() {
        CurrentUserHelper user = new CurrentUserHelper(context);
        String token = user.getToken();
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        NewUser newUser = retrofit.create(NewUser.class);
        Call<User> userCall = newUser.getUserInfo(token);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    User decodedResponse = response.body();
                    Data data = decodedResponse.getData();
                    SessionManager sessionManager = new SessionManager(context);
                    sessionManager.setChampyOptions(
                            data.getInProgressChallenges().toString(),
                            data.getSuccessChallenges().toString(),
                            data.getAllChallengesCount().toString(),
                            data.getLevel().getNumber().toString());
                } else Log.d("LoadUserProgressBar", "onResponse: failed! " + response.message());
            }

            @Override
            public void onFailure(Throwable t) { }
        });

    }

}
