package com.azinecllc.champy.controller;

import android.database.sqlite.SQLiteDatabase;

import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.interfaces.Update_user;
import com.azinecllc.champy.model.user.Delete;
import com.azinecllc.champy.model.user.Profile_data;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.SessionManager;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.path;

/**
 * Created by SashaKhyzhun on 2/13/17.
 */

public class UserController {

    private SessionManager session;
    private Retrofit retrofit;
    private String userID, userToken;

    public UserController(SessionManager session, Retrofit retrofit) {
        this.session = session;
        this.retrofit = retrofit;
        userID = session.getUserId();
        userToken = session.getToken();
    }

    // @Call to API
    public void updateProfile(HashMap<String, String> map) {
        session.toggleChallengeEnd(map.get("challengeEnd"));
        session.togglePushNotification(map.get("pushNotifications"));
        session.toggleChallengesForToday(map.get("challengesForToday"));
        session.toggleNewChallengeRequest(map.get("newChallengeRequests"));
        session.toggleAcceptYourChallenge(map.get("acceptedYourChallenge"));

        Update_user update_user = retrofit.create(Update_user.class);
        Profile_data profile_data = new Profile_data(map);
        Call<User> call = update_user.update_profile_options(userID, userToken, profile_data);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    // @Call to API
    public void updateUserName(String newName) {
        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_user_name(userID, userToken, newName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


    public void deleteUserProfile(DBHelper dbHelper) {
        Update_user update_user = retrofit.create(Update_user.class);
        Call<Delete> callForDeleteUser = update_user.delete_user(userID, userToken);
        callForDeleteUser.enqueue(new Callback<Delete>() {
            @Override
            public void onResponse(Response<Delete> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    File profile = new File(path, "profile.jpg");
                    profile.delete();
                    File blurred = new File(path, "blurred.png");
                    blurred.delete();

                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("pending", null, null);
                    db.delete("pending_duel", null, null);
                    db.delete("duel", null, null);
                    db.delete("friends", null, null);
                    db.delete("updated", null, null);
                    db.delete("myChallenges", null, null);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    public void uploadPhotoForAPI(String path) {
        File userPhotoFile = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), userPhotoFile);

        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_photo(userID, userToken, requestBody);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });

    }

    public void updatePushIdentifier() {
        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_gcm(userID, userToken, session.getTokenAndroid(), "none");
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
