package com.azinecllc.champy.controller;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.azinecllc.champy.activity.RoleControllerActivity;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.interfaces.Update_user;
import com.azinecllc.champy.model.user.Delete;
import com.azinecllc.champy.model.user.Profile_data;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.login.LoginManager;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.activity.MainActivity.CURRENT_TAG;
import static com.azinecllc.champy.activity.MainActivity.navItemIndex;
import static com.azinecllc.champy.utils.Constants.TAG_CHALLENGES;
import static com.azinecllc.champy.utils.Constants.path;

/**
 * Created by SashaKhyzhun on 2/13/17.
 * Class-Controller which contain all calls to API which are relevant to USER
 */
public class UserController {

    private SessionManager session;
    private Retrofit retrofit;
    private String userID, userToken;


    /**
     * Constructor for this class. I had selected only needed values to don't override it for
     * each method
     *
     * @param session - our Session Manager when we store all data locally (singleton). From this
     *                 parameter we can get any data for user.
     * @param retrofit - simple interface to call on API. I transmit it to avoid overriding, no more.
     */
    public UserController(SessionManager session, Retrofit retrofit) {
        this.session = session;
        this.retrofit = retrofit;
        userID = session.getUserId();
        userToken = session.getToken();
    }


    /**
     * Method to update profile data (only for toggles). After each changes in settingsFragment we
     * put new data inside the session manager and make call to API, when we get this information and
     * send to the server.
     * @param map - simple map, why map? because we store all data in map for session manager, so here
     *              we need to get data from map. Map is very strong and useful interface in java
     */
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
                System.out.println("uploadProfile response: " + ((response.isSuccess()) ? "success" : response.message()));
            }
            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


    /**
     * Method to update user name. Here we need to make call and input new name.
     * @param newName - new user name, from EditText field.
     */
    public void updateUserName(String newName) {
        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_user_name(userID, userToken, newName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                System.out.println("updateUserName response: " + ((response.isSuccess()) ? "success" : response.message()));
            }
            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


    /**
     * Method to delete user profile. We make call to API and if response is success then we can
     * clear our Session Manager, delete local files, clear cache and other information.
     * @param dbHelper - our DataBase when we store all data (status bars, challenges, etc). Our
     *                 database is singleton, we can don't transmit it, but when we create database
     *                 we should put in the context (which we do not have). So we are select what
     *                 transmit inside: already created dbHelper or context to create new dbHelper.
     * @param activity - transmitted activity to make SessionManager.Logout and redirect the user
     *                 after all action to Login Activity across Splash screen.
     */
    public void deleteUserProfile(DBHelper dbHelper, Activity activity) {
        Update_user update_user = retrofit.create(Update_user.class);
        Call<Delete> callForDeleteUser = update_user.delete_user(userID, userToken);
        callForDeleteUser.enqueue(new Callback<Delete>() {
            @Override
            public void onResponse(Response<Delete> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    System.out.println("deleteProfile response: " + ((response.isSuccess()) ? "success" : response.message()));
                    ////////// not sure what we need this ////////
                    //String root = Environment.getExternalStorageDirectory().toString();
                    //File avatar = new File(root + "/android/data/com.azinecllc.champy/images", "profile.jpg");
                    //avatar.delete();
                    //File blurred = new File(path, "blurred.png");
                    //blurred.delete();
                    // ///////////////////////////////////////////

                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("pending", null, null);
                    db.delete("pending_duel", null, null);
                    db.delete("duel", null, null);
                    db.delete("friends", null, null);
                    db.delete("updated", null, null);
                    db.delete("myChallenges", null, null);

                    CURRENT_TAG = TAG_CHALLENGES;
                    navItemIndex = 0;
                    session.logout(activity);
                    //reminder.disableDailyNotificationReminder();
                    LoginManager.getInstance().logOut();
                    activity.startActivity(new Intent(activity, RoleControllerActivity.class));
                }
            }
            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


    /**
     * Method to load new profile picture on API. Just push the file to Api (without saving locally)
     * @param path - this is path to storage when we have already exist picture, because we should
     *             load a file on the server. We get this path and create new file (profile picture)
     *             after that we can make call and update this
     */
    public void uploadPhotoForAPI(String path) {
        File userPhotoFile = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), userPhotoFile);
        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_photo(userID, userToken, requestBody);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                System.out.println("uploadPhotoForAPI response: " + ((response.isSuccess()) ? "success" : response.message()));
            }
            @Override
            public void onFailure(Throwable t) {
                System.out.println("uploadPhoto response failed: " + t.getMessage() + t.getCause());
            }
        });
    }


    /**
     * Method to update user's push identifier, we use it only inside "registerUser" and "singInUser"
     * methods, this parameter needed on server side. On the client side this is useless data.
     */
    public void updatePushIdentifier() {
        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_gcm(userID, userToken, session.getTokenAndroid(), "none");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                System.out.println("updatePushIdentifier" + (response.isSuccess() ? "success" : response.message()));
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


}
