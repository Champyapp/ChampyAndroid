package com.example.ivan.champy_v2.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.util.Log;

import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.NewUser;
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

public class CHUserDataLoader {

    String TAG;
    Activity activity;

    public void getUserData(final String fb_id, final String path_to_pic, String gcm) throws JSONException {
        final String API_URL = "http://46.101.213.24:3007";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("facebookId", fb_id);
        jsonObject.put("AndroidOS", gcm);
        String string = jsonObject.toString();
        final String jwtString = Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setPayload(string).signWith(SignatureAlgorithm.HS256, "secret").compact();
        Log.d(TAG, "TOKEN: "+jwtString);
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NewUser newUser = retrofit.create(NewUser.class);
        Call<User> call = newUser.getUserInfo(jwtString);

        final String finalId = fb_id;
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                Log.d(TAG, "Status: get_user" + response.code());
                User decodedResponse = response.body();
                if (response.isSuccess()) {
                 /*   Log.d("TAG", "Status: " + decodedResponse.getDescription());
                    Log.d("TAG", "Status: "+jwtString);*/

                    Data data = decodedResponse.getData();
                    String email = data.getEmail();
                    final String user_name = data.getName();
                    final String id = data.get_id();

                    String pushN = data.getProfileOptions().getPushNotifications().toString();
                    String newChallReq = data.getProfileOptions().getNewChallengeRequests().toString();
                    String acceptedYour = data.getProfileOptions().getAcceptedYourChallenge().toString();
                    String challegeEnd = data.getProfileOptions().getChallengeEnd().toString();
                    Log.d(TAG, "Status: " + id + " " +fb_id);

                    SessionManager sessionManager = new SessionManager(activity);
                    sessionManager.setRefreshPending("false"); // change to false
                    sessionManager.setRefreshFriends("false"); // change to false
                    sessionManager.createUserLoginSession(user_name, email, fb_id, path_to_pic, jwtString, id, pushN, newChallReq, acceptedYour, challegeEnd, "true");
                    sessionManager.setChampyOptions(
                            data.getAllChallengesCount().toString(),
                            data.getSuccessChallenges().toString(),
                            data.getScore().toString(),
                            data.getLevel().getNumber().toString()
                    );
                    DBHelper dbHelper = new DBHelper(activity);
                    final SQLiteDatabase db = dbHelper.getWritableDatabase();
                    int clearCount = db.delete("pending", null, null);
                    final ContentValues cv = new ContentValues();
                    final String  user_id = id;

                    com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);

                    Call<com.example.ivan.champy_v2.model.Friend.Friend> call = friends.getUserFriends(id, jwtString);
                    call.enqueue(new Callback<Friend>() {
                        @Override
                        public void onResponse(Response<Friend> response, Retrofit retrofit) {
                            if (response.isSuccess()){
                                List<Datum> data = response.body().getData();
                                Log.d(TAG, "Status: "+data.size());

                                for (int i=0; i<data.size(); i++) {
                                    Datum datum = data.get(i);
                                    Log.d(TAG, "Status: "+response.body().toString());

                                    if ((datum.getFriend() != null) && (datum.getOwner() != null)) {

                                        if (datum.getStatus().toString().equals("false")) {
                                            Log.d(TAG, "Status: "+datum.getOwner().get_id());

                                            if (datum.getOwner().get_id().equals(user_id)) {
                                                Friend_ friend = datum.getFriend();
                                                Log.d(TAG, "Status: "+friend);

                                                if (friend.getName() != null) {
                                                    cv.put("name", friend.getName());
                                                }

                                                Log.d(TAG, "Status: "+friend.getPhoto());
                                                if (friend.getPhoto() != null) {
                                                    cv.put("photo", friend.getPhoto().getMedium());
                                                }
                                                else {
                                                    cv.put("photo", "");
                                                }

                                                Log.d(TAG, "Friend");
                                                cv.put("user_id", friend.getId());
                                                cv.put("owner", "false");
                                                db.insert("pending", null, cv);}

                                            else if (datum.getStatus().toString().equals("true")) {
                                                Friend_ friend = datum.getFriend();
                                                Log.d(TAG, "Status: "+friend);

                                                if (friend.getName() != null) {
                                                    cv.put("name", friend.getName());
                                                }
                                                Log.d(TAG, "Status: "+friend.getPhoto());
                                                if (friend.getPhoto() != null) {
                                                    cv.put("photo", friend.getPhoto().getMedium());
                                                }
                                                else {
                                                    cv.put("photo", "");
                                                }
                                                Log.d(TAG, "Friend");
                                                cv.put("user_id", friend.getId());
                                                cv.put("owner", "false");
                                                db.insert("friends", null, cv);
                                            }
                                            else {
                                                Owner friend = datum.getOwner();
                                                cv.put("name", friend.getName());
                                                if (friend.getPhoto() != null) {
                                                    cv.put("photo", friend.getPhoto().getMedium());
                                                }
                                                else {
                                                    cv.put("photo", "");
                                                }
                                                Log.d(TAG, "Owner");
                                                cv.put("user_id", friend.get_id());
                                                cv.put("owner", "true");
//                                                db.insert("pending", null, cv);
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

                    clearCount = db.delete("myChallenges", null, null);
                    ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
                    final long unixTime = System.currentTimeMillis() / 1000L;
                    String update = "1457019726";
                    Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(id, update, jwtString);
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        List<com.example.ivan.champy_v2.model.active_in_progress.Datum>  list  = call1.execute().body().getData();
                        for (int i = 0; i < list.size(); i++) {
                            com.example.ivan.champy_v2.model.active_in_progress.Datum datum = list.get(i);
                            Challenge challenge = datum.getChallenge();
                            String desctiption = challenge.getDetails();
                          /*  int end = datum.getEnd();
                            int days = round((end - unixTime) / 86400);*/
                            String duration = "" + 21;
                            String challenge_id = datum.get_id();
                            if (challenge.getDescription().equals("Wake Up")) {
                                cv.put("name", "Wake Up");
                            }
                            else cv.put("name", "Self Improvement");
                            cv.put("description", desctiption);
                            cv.put("duration", duration);
                            cv.put("challenge_id", challenge_id);
                            cv.put("status", datum.getStatus());
                            db.insert("myChallenges", null, cv);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                  /*  call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
                        @Override
                        public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                List<com.example.ivan.champy_v2.model.active_in_progress.Datum> data = response.body().getData();
                                for (int i = 0; i < data.size(); i++) {
                                    com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);
                                    Challenge challenge = datum.getChallenge();
                                    cv.clear();
                                    String desctiption = challenge.getDescription();
                                    int end = datum.getEnd();
                        -->         int days = round((end - unixTime) / 86400);
                                    String duration = "" + days;
                                    String challenge_id = challenge.get_id();
                                    cv.put("name", "Self Improvement");
                                    cv.put("description", desctiption);
                                    cv.put("duration", duration);
                                    cv.put("challenge_id", challenge_id);
                                    db.insert("myChallenges", null, cv);
                                    Log.d(TAG, "Challenge: "+desctiption);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });*/
                    String api_path = null;
                    if (data.getPhoto() != null){
                        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
                        File f=new File(path, "profile.jpg");
                        if (!f.exists()){
                            com.example.ivan.champy_v2.model.User.Photo photo = data.getPhoto();
                            api_path = API_URL+photo.getLarge();
                            Log.d(TAG, "Image: "+api_path);
                        }
                    }

                    Intent intent = new Intent(activity, MainActivity.class);
                    if (api_path == null) intent.putExtra("path_to_pic", path_to_pic);
                    else {
                        intent.putExtra("path_to_pic", api_path);
                        sessionManager.change_avatar(api_path);
                    }
                    intent.putExtra("name", user_name) ;
                    activity.startActivity(intent);
                }
                else Log.d("TAG", "Status: "+decodedResponse);
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "VSE huynya");
            }
        });

    }

}
