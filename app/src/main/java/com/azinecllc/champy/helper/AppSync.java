package com.azinecllc.champy.helper;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.activity.RoleControllerActivity;
import com.azinecllc.champy.controller.DailyRemindController;
import com.azinecllc.champy.interfaces.NewUser;
import com.azinecllc.champy.model.user.Data;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.SessionManager;

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

import static com.azinecllc.champy.utils.Constants.API_URL;

public class AppSync {

    private Context context;
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


    public void getUserProfile() {
        final String facebookId = this.fbId;
        final String jwtString = this.token;
        final String path_to_pic = this.path;
        final String gcm = this.gcm;
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        NewUser newUser = retrofit.create(NewUser.class);
        Call<User> callGetUserInfo = newUser.getUserInfo(jwtString);
        callGetUserInfo.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {

                    sessionManager = SessionManager.getInstance(context);
                    Data data = response.body().getData();
                    String user_name = data.getName();
                    String userId = data.get_id();
                    String email = data.getEmail();
                    String pushN = data.getProfileOptions().getPushNotifications().toString();
                    String challengeEnd = data.getProfileOptions().getChallengeEnd().toString();
                    String acceptedYour = data.getProfileOptions().getAcceptedYourChallenge().toString();
                    String newChallengeReq = data.getProfileOptions().getNewChallengeRequests().toString();
                    String challengesForToday = data.getProfileOptions().getChallengesForToday().toString();

                    sessionManager.setRefreshFriends("true");
                    sessionManager.setRefreshPending("false");
                    sessionManager.setRefreshOthers("true");
                    sessionManager.createUserLoginSession(
                            user_name, email, facebookId, path_to_pic, jwtString, userId, pushN, newChallengeReq,
                            acceptedYour, challengeEnd, challengesForToday, "true", gcm, token_android
                    );
                    sessionManager.setChampyOptions(
                            data.getAllChallengesCount().toString(),
                            data.getSuccessChallenges().toString(),
                            data.getInProgressChallenges().toString(),
                            data.getLevel().getNumber().toString()
                    );

                    UpdatePushIdentifier pushIdentifier = new UpdatePushIdentifier();
                    pushIdentifier.updatePushIdentifier(sessionManager);

                    CHGetFacebookFriends getFbFriends = new CHGetFacebookFriends(context, retrofit);
                    getFbFriends.getUserFacebookFriends(gcm);

                    CHGetPendingFriends getPendingFriends = new CHGetPendingFriends(context, retrofit);
                    getPendingFriends.getUserPending(userId, token);

                    DailyRemindController dailyRemind = new DailyRemindController(context);
                    dailyRemind.enableDailyNotificationReminder();


                    String api_path = null;
                    if (data.getPhoto() != null){
                        String path = "/data/data/com.azinecllc.champy/app_imageDir/";
                        File file = new File(path, "profile.jpg");
                        if (!file.exists()){
                            com.azinecllc.champy.model.user.Photo photo = data.getPhoto();
                            api_path = API_URL + photo.getLarge();
                        }
                    }

                    Intent extras = new Intent(context, MainActivity.class);
                    if (api_path == null) {
                        extras.putExtra("path_to_pic", path_to_pic);
                        sessionManager.change_avatar(path_to_pic);
                    } else {
                        extras.putExtra("path_to_pic", api_path);
                        sessionManager.change_avatar(api_path);
                    }
                    Intent goToRoleActivity = new Intent(context, RoleControllerActivity.class);
                    context.startActivity(goToRoleActivity);


                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }

        });


    }


    private String getToken(String fb_id, String gcm) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("facebookId", fb_id);
        jsonObject.put("AndroidOS", gcm);
        String string = jsonObject.toString();
        return Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .setPayload(string)
                .signWith(SignatureAlgorithm.HS256, "secret")
                .compact();
    }



}
