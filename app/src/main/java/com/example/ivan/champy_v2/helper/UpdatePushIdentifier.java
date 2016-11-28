package com.example.ivan.champy_v2.helper;

import android.util.Log;

import com.example.ivan.champy_v2.interfaces.Update_user;
import com.example.ivan.champy_v2.model.user.User;
import com.example.ivan.champy_v2.utils.SessionManager;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.example.ivan.champy_v2.utils.Constants.API_URL;

/**
 * Created by Sasha Khyzhun on 11/25/16.
 * Class helper for send user's push identifier on API (android_token);
 */

public class UpdatePushIdentifier {

    public void updatePushIdentifier(SessionManager sessionManager) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        String userID = sessionManager.getUserId();
        String androidToken = sessionManager.getTokenAndroid();
        String token = sessionManager.getToken();
        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_gcm(userID, token, androidToken, "none");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                String log = (response.isSuccess()) ? "Profile Updated" : "ERROR: " + response.message();
                Log.d("UpdatePushIdentifier", "onResponse: Status: " + log);
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d("UpdatePushIdentifier", "VSE huynya");
            }
        });
    }

}
