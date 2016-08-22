package com.example.ivan.champy_v2.helper;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.util.Log;

import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.Friends;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.interfaces.SingleInProgress;
import com.example.ivan.champy_v2.model.Friend.Datum;
import com.example.ivan.champy_v2.model.Friend.Friend;
import com.example.ivan.champy_v2.model.Friend.Friend_;
import com.example.ivan.champy_v2.model.Friend.Owner;
import com.example.ivan.champy_v2.model.User.Data;
import com.example.ivan.champy_v2.model.User.User;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static java.lang.Math.round;

public class AppSync {

    final String API_URL = "http://46.101.213.24:3007";
    Context context;
    String fbId, gcm, token;


    public AppSync(String fb_id, String gcm) throws JSONException {
        this.fbId = fb_id;
        this.gcm = gcm;
        this.token = getToken(this.fbId, this.gcm);

    }

    public String getToken(String fb_id, String gcm)  throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("facebookId", fb_id);
        jsonObject.put("AndroidOS", gcm);
        String string = jsonObject.toString();
        final String jwtString = Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setPayload(string).signWith(SignatureAlgorithm.HS256, "secret").compact();

        return jwtString;
    }

    public void getUserProfile() {
        final String facebookId = this.fbId;
        final String jwtString = this.token;
        final String path_to_pic = ""; // TODO

        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        NewUser newUser = retrofit.create(NewUser.class);

        Call<User> callGetUserInfo = newUser.getUserInfo(jwtString);
        callGetUserInfo.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                User decodedResponse = response.body();
                if (response.isSuccess()) {
                    Data data = decodedResponse.getData();
                    String email = data.getEmail();
                    final String user_name = data.getName();
                    final String id = data.get_id();

                    String pushN = data.getProfileOptions().getPushNotifications().toString();
                    String newChallReq = data.getProfileOptions().getNewChallengeRequests().toString();
                    String acceptedYour = data.getProfileOptions().getAcceptedYourChallenge().toString();
                    String challegeEnd = data.getProfileOptions().getChallengeEnd().toString();
                    Log.i("GetUserData", "UserId = " + id + "\nFacebook_Id = " + facebookId);

                    SessionManager sessionManager = new SessionManager(context);
                    sessionManager.setRefreshPending("false");
                    sessionManager.setRefreshFriends("true");
                    sessionManager.createUserLoginSession(user_name, email, facebookId, path_to_pic, jwtString, id, pushN, newChallReq, acceptedYour, challegeEnd, "true", gcm);
                    sessionManager.setChampyOptions(
                            data.getAllChallengesCount().toString(),
                            data.getSuccessChallenges().toString(),
                            data.getScore().toString(),
                            data.getLevel().getNumber().toString()
                    );


                    String api_path = null;
                    if (data.getPhoto() != null){
                        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
                        File file = new File(path, "profile.jpg");
                        if (!file.exists()){
                            com.example.ivan.champy_v2.model.User.Photo photo = data.getPhoto();
                            api_path = API_URL + photo.getLarge();
                            Log.d("data.getPhoto()", "Image: " + api_path);
                        }
                    }


                } else Log.d("TAG", "Status: FAILED = " + decodedResponse);
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d("TAG", "VSE huynya");
            }
        });


    }

    public void getFriends(final String userId) {

        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("pending", null, null);
        final ContentValues cv = new ContentValues();

        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);
        Call<com.example.ivan.champy_v2.model.Friend.Friend> callGetUserFriends = friends.getUserFriends(userId, this.token);
        callGetUserFriends.enqueue(new Callback<Friend>() {
            @Override
            public void onResponse(Response<Friend> response, Retrofit retrofit) {
                if (response.isSuccess()){
                    List<Datum> data = response.body().getData();

                    for (int i = 0; i < data.size(); i++) {
                        Datum datum = data.get(i);

                        if ((datum.getFriend() != null) && (datum.getOwner() != null)) {
                            if (datum.getStatus().toString().equals("false")) {

                                if (datum.getOwner().get_id().equals(userId)) {
                                    Friend_ friend = datum.getFriend();
                                    if (friend.getName() != null)  cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.getId());
                                    cv.put("owner", "false");
                                    db.insert("pending", null, cv);
                                }


                                else if (datum.getStatus().toString().equals("true")) {
                                    Friend_ friend = datum.getFriend();
                                    if (friend.getName() != null) cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.getId());
                                    cv.put("owner", "false");
                                    db.insert("friends", null, cv);
                                }


                                else {
                                    Owner friend = datum.getOwner();
                                    cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.get_id());
                                    cv.put("owner", "true");
                                    //db.insert("pending", null, cv); //comment this line if something goes wrong
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(Throwable t) {
            }
        });


    }

    public void getUsers() {}

    public void getChallenges() {}

    public void getInProgressChallenges(final String userId) {

        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("myChallenges", null, null);
        final ContentValues cv = new ContentValues();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        final long unixTime = System.currentTimeMillis() / 1000L;
        String update = "0"; //1457019726

        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);

        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> callActiveInProgress = activeInProgress.getActiveInProgress(userId, update, this.token);
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            List<com.example.ivan.champy_v2.model.active_in_progress.Datum> list  = callActiveInProgress.execute().body().getData();
            for (int i = 0; i < list.size(); i++) {
                com.example.ivan.champy_v2.model.active_in_progress.Datum datum = list.get(i);
                Challenge challenge = datum.getChallenge();
                String challenge_description = challenge.getDescription(); // bla-bla
                String challenge_detail = challenge.getDetails(); // $bla-bla + " during this period"
                String challenge_status = datum.getStatus();      // active or not
                String challenge_id = datum.get_id();             // us magic id
                String challenge_type = challenge.getType();      // self, duel or wake up
                String challenge_name = challenge.getName();
                String duration = "";
                if (datum.getEnd() != null) {
                    int end = datum.getEnd();
                    int days = round((end - unixTime) / 86400);
                    duration = "" + days;
                }

                if (challenge_description.equals("Wake Up")) {
                    cv.put("name", "Wake Up");
                } else if (challenge_type.equals("567d51c48322f85870fd931a")) {
                    cv.put("name", "Self-Improvement");
                } else if (challenge_type.equals("567d51c48322f85870fd931b")) {
                    cv.put("name", "Duel");
                }

                cv.put("challengeName", challenge_name);
                cv.put("description", challenge_detail);
                cv.put("duration", duration);
                cv.put("challenge_id", challenge_id);
                cv.put("status", challenge_status);
                db.insert("myChallenges", null, cv);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
