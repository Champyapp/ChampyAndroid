package com.azinecllc.champy.helper;

import android.content.Context;

import com.azinecllc.champy.interfaces.Update_user;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.SessionManager;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;

public class CHUploadPhoto {

    private Context context;

    public CHUploadPhoto(Context context) {
        this.context = context;
    }

    public void uploadPhotoForAPI(String path) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SessionManager sessionManager = SessionManager.getInstance(context);
        final String token = sessionManager.getToken();
        final String id = sessionManager.getUserId();

        File userPhotoFile = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), userPhotoFile);

        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_photo(id, token, requestBody);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
            }
            @Override
            public void onFailure(Throwable t) {
            }
        });

    }

}
