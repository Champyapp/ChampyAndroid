package com.example.ivan.champy_v2.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ivan.champy_v2.DailyRemindController;
import com.example.ivan.champy_v2.activity.RoleControllerActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.interfaces.Update_user;
import com.example.ivan.champy_v2.model.user.Data;
import com.example.ivan.champy_v2.model.user.Profile_data;
import com.example.ivan.champy_v2.model.user.User;
import com.example.ivan.champy_v2.utils.Constants;
import com.example.ivan.champy_v2.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

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
                    dailyRemind.activateDailyNotificationReminder();


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

//      // relocated in roleActivity
//    private void getUserInProgressChallenges(final String userId) {
//        DBHelper dbHelper = new DBHelper(context);
//        final SQLiteDatabase db = dbHelper.getWritableDatabase();
//        int clearCount = db.delete("myChallenges", null, null);
//        final ContentValues cv = new ContentValues();
//        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
//        final long unixTime = System.currentTimeMillis() / 1000L;
//        String update = "0";
//
//        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
//        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> callActiveInProgress = activeInProgress.getActiveInProgress(userId, update, this.token);
//        try {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//            List<com.example.ivan.champy_v2.model.active_in_progress.Datum> list  = callActiveInProgress.execute().body().getData();
//            for (int i = 0; i < list.size(); i++) {
//                com.example.ivan.champy_v2.model.active_in_progress.Datum datum = list.get(i);
//                Challenge challenge = datum.getChallenge();
//                Recipient recipient = datum.getRecipient();
//                Sender sender = datum.getSender();
//
//                String challenge_description = challenge.getDescription();   // no smoking
//                String challenge_detail      = challenge.getDetails();       // no smoking + " during this period"
//                String challenge_status      = datum.getStatus();            // active or not
//                String challenge_id          = datum.get_id();               // im progress id
//                String challenge_type        = challenge.getType();          // 567d51c48322f85870fd931a / b / c
//                String challenge_name        = challenge.getName();          // wake up / self / duel
//                String challenge_wakeUpTime  = challenge.getWakeUpTime();    // our specific time (intentId)
//                String challenge_updated     = getLastUpdated(challenge_id); // bool check method;
//                String challenge_duration    = "";
//                String constDuration         = "";
//
//                if (datum.getEnd() != null) {
//                    int end = datum.getEnd();
//                    int begin = datum.getBegin();
//                    int days = round((end - unixTime) / 86400);
//                    int constDays = round((end - begin) / 86400);
//                    challenge_duration = "" + days;
//                    constDuration = "" + constDays;
//                }
//
//                List<Object> senderProgress = datum.getProgress();
//                String stringSenderProgress[] = new String[senderProgress.size()];
//                for (int j = 0; j < senderProgress.size(); j++) {
//                    try {
//                        JSONObject json = new JSONObject(senderProgress.get(j).toString());
//                        long at = json.getLong("at");
//                        stringSenderProgress[j] = String.valueOf(at);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (challenge_description.equals("Wake Up")) {
//                    // just name of Challenge
//                    cv.put("name", "Wake Up");
//                    // our specific field for delete wakeUp (example: 1448);
//                    cv.put("wakeUpTime", challenge_detail);
//                } else if (challenge_type.equals("567d51c48322f85870fd931a")) {
//                    // just name of Challenge
//                    cv.put("name", "Self-Improvement");
//                } else if (challenge_type.equals("567d51c48322f85870fd931b")) {
//                    // just name of Challenge
//                    cv.put("name", "Duel");
//                    if (userId.equals(recipient.getId())) {
//                        // if I accepted challenge, i'm "recipient"
//                        cv.put("recipient", "true");
//                        // name of the person with whom we have a duel
//                        cv.put("versus", sender.getName());
//                    } else {
//                        // if I sent the challenge, i'm "sender"
//                        cv.put("recipient", "false");
//                        // name of the person with whom we have a duel
//                        cv.put("versus", recipient.getName());
//                    }
//                }
//
//                // default 'challenge'. this column only for wake up time
//                cv.put("challengeName", challenge_name);
//                // smoking free life or wake up at 14:48
//                cv.put("description", challenge_description);
//                // duration of challenge
//                cv.put("duration", challenge_duration);
//                // in progress id
//                cv.put("challenge_id", challenge_id);
//                // active or not
//                cv.put("status", challenge_status);
//                // true or false
//                cv.put("updated", challenge_updated);
//                // last update time in millis
//                cv.put("senderProgress", Arrays.toString(stringSenderProgress));
//                // our constant value of challenge duration
//                cv.put("constDuration", constDuration);
//                // db when we store all challenges and information about them
//                db.insert("myChallenges", null, cv);
//            }
//
//        } catch (IOException e) { e.printStackTrace(); }
//
//    }
//
//
//
//    // method which get our last update (true or false);
//    private String getLastUpdated(String challenge_id) {
//        DBHelper dbHelper = new DBHelper(context);
//        final SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor c = db.query("updated", null, null, null, null, null, null);
//        String lastUpdate = "createdButNotUpdated";
//        if (c.moveToFirst()) {
//            int colchallenge_id = c.getColumnIndex("challenge_id");
//            do {
//                // в методе "sendSingleForDuel мы засовываем challenge_id в колонку "challenge_id" в
//                // таблице "updated", а тут мы ее проверяем. если она есть, то вернуть время когда
//                // мы нажимали "дан" для дуелей, если её здесь нету, то возвращаем "false" - это для
//                // wake-up и self-improvement челенджей.
//                // Соответственно данные про update time для дуелей находятся в таблице "updated",
//                // а для отсального в таблице "myChallenges".
//                if (c.getString(colchallenge_id).equals(challenge_id)) {
//                    lastUpdate = c.getString(c.getColumnIndex("updated"));
//                    break;
//                }
//            } while (c.moveToNext());
//        }
//        c.close();
//        return lastUpdate;
//    }



    // if this method return TRUE it mean what current user in "pending" table... (else in "other")
//    private Boolean isInOtherTable(String someUserId) {
//        DBHelper dbHelper = new DBHelper(context);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Boolean ok = false;
//        Cursor c = db.query("pending", null, null, null, null, null, null);
//        if (c.moveToFirst()) {
//            int pendingUserId = c.getColumnIndex("user_id");
//            do {
//                String somePendingId = c.getString(pendingUserId);
//                if (somePendingId.equals(someUserId)) {
//                    ok = true;
//                    break;
//                }
//            } while (c.moveToNext());
//        }
//        c.close();
//        return ok;
//    }

}
