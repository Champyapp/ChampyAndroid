package com.example.ivan.champy_v2.helper;

import android.content.Context;
import android.util.Log;

import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.interfaces.Update_user;
import com.example.ivan.champy_v2.model.User.User;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class CHUploadPhoto {

    Context context;

    private static final String TAG = "myLog";

    public void uploadPhoto(String path) {
        final String API_URL = "http://46.101.213.24:3007";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");

        String id = user.get("id");

        Log.d(TAG, "USER: "+token+" "+id);

        File f=new File(path);

        Log.d(TAG, "Status: " + f);

        RequestBody requestBody =
                RequestBody.create(MediaType.parse("image/jpeg"), f);

        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_photo(id, token, requestBody);
        Log.d(TAG, "Status: RUN");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.d(TAG, "Status: photo_uploaded");
                } else Log.d(TAG, "Status :" + response.code());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Status: "+t);
            }
        });
    }

}
