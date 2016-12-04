package com.example.ivan.champy_v2.helper;

import android.content.Context;
import android.util.Log;

import com.example.ivan.champy_v2.interfaces.Update_user;
import com.example.ivan.champy_v2.model.user.User;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.example.ivan.champy_v2.utils.Constants.API_URL;

public class CHUploadPhoto {

    public final String TAG = "CHUploadPhoto";
    Context context;

    public CHUploadPhoto(Context context) {
        this.context = context;
    }

    public void uploadPhotoForAPI(String path) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        String id = sessionManager.getUserId();

        File userPhotoFile = new File(path);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), userPhotoFile);

        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_photo(id, token, requestBody);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) { Log.d(TAG, "Status: Success photo_uploaded");}
                else Log.d(TAG, "Status failed:" + response.code()); }

            @Override
            public void onFailure(Throwable t) {Log.d(TAG, "Status: vse hyunya: " + t ); }
        });

    }
}
