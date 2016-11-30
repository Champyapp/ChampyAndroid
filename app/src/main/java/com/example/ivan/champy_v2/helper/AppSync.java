package com.example.ivan.champy_v2.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ivan.champy_v2.activity.RoleControllerActivity;
import com.example.ivan.champy_v2.controller.DailyRemindController;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.user.Data;
import com.example.ivan.champy_v2.model.user.User;
import com.example.ivan.champy_v2.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.example.ivan.champy_v2.utils.Constants.API_URL;

public class AppSync {

    private final String TAG = "AppSync";
    private Context context;
    private DBHelper dbHelper;
    private ContentValues cv;
    private SQLiteDatabase db;
    private SessionManager sessionManager;
    private String fbId, gcm, token, path, token_android;


    public AppSync(String fb_id, String gcm, String path_to_pic, Context context, String token_android) throws JSONException {
        this.fbId = fb_id;
        this.gcm = gcm;
        this.token = getToken(this.fbId, this.gcm);
        this.path = path_to_pic;
        this.context = context;
        this.token_android = token_android;
    }


    public String getToken(String fb_id, String gcm) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("facebookId", fb_id);
        jsonObject.put("AndroidOS", gcm);
        String string = jsonObject.toString();
        return Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setPayload(string).signWith(SignatureAlgorithm.HS256, "secret").compact();
    }


    public void getUserProfile() {
        final String facebookId = this.fbId;
        final String jwtString = this.token;
        final String path_to_pic = this.path;
        final String gcm = this.gcm;
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        cv = new ContentValues();
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();

        NewUser newUser = retrofit.create(NewUser.class);
        Call<User> callGetUserInfo = newUser.getUserInfo(jwtString);
        callGetUserInfo.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.d(TAG, "getUserProfile onResponse: Success");


                    Data data = response.body().getData();
                    String email = data.getEmail();
                    String user_name = data.getName();
                    String userId = data.get_id();
                    String daily = getDailyRemind();
                    String pushN = data.getProfileOptions().getPushNotifications().toString();
                    String newChallReq = data.getProfileOptions().getNewChallengeRequests().toString();
                    String acceptedYour = data.getProfileOptions().getAcceptedYourChallenge().toString();
                    String challegeEnd = data.getProfileOptions().getChallengeEnd().toString();

                    sessionManager = new SessionManager(context);
                    sessionManager.setRefreshPending("false");
                    sessionManager.setRefreshFriends("true");
                    sessionManager.createUserLoginSession(
                            user_name, email, facebookId, path_to_pic,
                            jwtString, userId, pushN, newChallReq,
                            acceptedYour, challegeEnd, daily, "true", gcm, token_android
                    );


                    Log.d(TAG, "onResponse: QWERTY TOKEN: " + token);
                    sessionManager.setChampyOptions(
                            data.getAllChallengesCount().toString(),
                            data.getSuccessChallenges().toString(),
                            data.getInProgressChallenges().toString(),
                            data.getLevel().getNumber().toString()
                    );

//                    HashMap<String, String> hashTokenAndroid = new HashMap<>();
//                    hashTokenAndroid.put("GCM", token_android);
                    UpdatePushIdentifier pushIdentifier = new UpdatePushIdentifier();
                    pushIdentifier.updatePushIdentifier(sessionManager);

                    CHGetFacebookFriends getFbFriends = new CHGetFacebookFriends(context);
                    getFbFriends.getUserFacebookFriends(gcm);

                    CHGetPendingFriends getPendingFriends = new CHGetPendingFriends(context);
                    getPendingFriends.getUserPending(userId, token);

                    DailyRemindController dailyRemind = new DailyRemindController(context);
                    dailyRemind.enableDailyNotificationReminder();


                    String api_path = null;
                    if (data.getPhoto() != null){
                        @SuppressLint("SdCardPath")
                        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
                        File file = new File(path, "profile.jpg");
                        if (!file.exists()){
                            com.example.ivan.champy_v2.model.user.Photo photo = data.getPhoto();
                            api_path = API_URL + photo.getLarge();
                            Log.i("AppSync", "GetUserPhoto: " + api_path);
                        }
                    }

                    Intent goToRoleActivity = new Intent(context, RoleControllerActivity.class);
                    if (api_path == null) {
                        goToRoleActivity.putExtra("path_to_pic", path_to_pic);
                        sessionManager.change_avatar(path_to_pic);
                    } else {
                        goToRoleActivity.putExtra("path_to_pic", api_path);
                        sessionManager.change_avatar(api_path);
                    }
                    context.startActivity(goToRoleActivity);


                } else {
                    Log.i(TAG, "getUserProfile onResponse: Failed " + response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "VSE huynya");
            }
        });


    }


    private String getDailyRemind() {
        Cursor c = db.query("updated", null, null, null, null, null, null);
        String dailyNotification = "true";

        if (c.moveToFirst()) {
            int dailyNotifInt = c.getColumnIndex("dailyRemind");
            do {
                if (c.getString(dailyNotifInt).equals("false")) {
                    dailyNotification = "false";
                }
            } while (c.moveToNext());
        }
        c.close();
        return dailyNotification;
    }


}
