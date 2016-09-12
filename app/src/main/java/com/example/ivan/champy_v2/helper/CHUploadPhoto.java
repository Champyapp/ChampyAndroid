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

    public static final String TAG = "CHUploadPhoto";
    Context context;

    public CHUploadPhoto(Context context) {
        this.context = context;
    }

    public void uploadPhotoForAPI(String path) {
        final String API_URL = "http://46.101.213.24:3007";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");

        String id = user.get("id");
        File file = new File(path);
        Log.i("MainActivity", "UploadPhotoFile: " + file);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);

        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_photo(id, token, requestBody);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) Log.d(TAG, "Status: VSE OK");
                else Log.d(TAG, "Status :" + response.code());
//                String log = (response.isSuccess()) ? TAG + "VSE OK" : TAG + response.code();
//                Log.i(TAG, "onResponse: " + log);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Status: " + t);
            }
        });
    }
}
