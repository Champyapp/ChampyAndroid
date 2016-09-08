package com.example.ivan.champy_v2.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.util.Log;

import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.activity.RoleControllerActivity;
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
import com.example.ivan.champy_v2.model.active_in_progress.Recipient;
import com.example.ivan.champy_v2.model.active_in_progress.Sender;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
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

    private static final String TAG = "AppSync";
    final String API_URL = "http://46.101.213.24:3007";
    Context context;
    String facebookId, gcm, token, path;


    public AppSync(String fb_id, String gcm, String path_to_pic, Context context) throws JSONException {
        this.facebookId = fb_id;
        this.gcm = gcm;
        this.token = getToken(this.facebookId, this.gcm);
        this.path = path_to_pic;
        this.context = context;
    }


    public String getToken(String fb_id, String gcm)  throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("facebookId", fb_id);
        jsonObject.put("AndroidOS", gcm);
        String string = jsonObject.toString();
        //Log.i("AppSync", "getToken: " + jwtString);
        return Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setPayload(string).signWith(SignatureAlgorithm.HS256, "secret").compact();
    }


    // when login
    public void getUserProfile() {
        final String facebookId = this.facebookId;
        final String jwtString = this.token;
        final String path_to_pic = this.path;
        final String gcm = this.gcm;
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        Log.i(TAG, "TOKEN: " + jwtString);

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
                    final String userId = data.get_id();

                    String pushN = data.getProfileOptions().getPushNotifications().toString();
                    String newChallReq = data.getProfileOptions().getNewChallengeRequests().toString();
                    String acceptedYour = data.getProfileOptions().getAcceptedYourChallenge().toString();
                    String challegeEnd = data.getProfileOptions().getChallengeEnd().toString();

                    SessionManager sessionManager = new SessionManager(context);
                    sessionManager.setRefreshPending("false"); // TODO: 26.08.2016 change for true maybe? for auto update pending list?
                    sessionManager.setRefreshFriends("true");
                    sessionManager.createUserLoginSession(user_name, email, facebookId, path_to_pic,
                            jwtString, userId, pushN, newChallReq, acceptedYour, challegeEnd, "true", gcm);
                    sessionManager.setChampyOptions(
                            data.getAllChallengesCount().toString(),
                            data.getSuccessChallenges().toString(),
                            data.getScore().toString(),
                            data.getLevel().getNumber().toString()
                    );


                    /**
                     * у нас был Call с помощью которого мы брали данные юзера, а внутри call-а
                     * мы делали еще 2, чтобы взять его InProgressChallenges и UserFriends.
                     * поэтому здесь надо вызывать эти 2 метода. После вызывался отдельный метод
                     * который брал инфу про друзей. Сейчас мы вызываем его здесь.
                     */
                    getUserInProgressChallenges(userId);
                    getUserPending(userId);
                    getUserFriendsInfo(gcm);
                    refreshCardsForPendingDuel();
                    loadUserProgressBarInfo();

                    String api_path;
                    if (data.getPhoto() != null){
                        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
                        File file = new File(path, "profile.jpg");
                        if (!file.exists()){
                            com.example.ivan.champy_v2.model.User.Photo photo = data.getPhoto();
                            api_path = API_URL + photo.getLarge();
                            Log.i("AppSync", "GetUserPhoto: " + api_path);
                        } else {
                            Log.i("AppSync", "GetUserPhoto: User photo already exist");
                        }
                    }

                    Log.i(TAG, "getUserProfile: Success!");
                    Intent goToRoleActivity = new Intent(context, RoleControllerActivity.class);
                    context.startActivity(goToRoleActivity);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("TAG", "VSE huynya");
            }
        });


    }

    // get peoples in pending [friend activity]
    public void getUserPending(final String userId) {
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("pending", null, null);
        final ContentValues cv = new ContentValues();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
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
                                    cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.getId());
                                    cv.put("inProgressChallengesCount", friend.getInProgressChallengesCount());
                                    cv.put("allChallengesCount", friend.getAllChallengesCount());
                                    cv.put("successChallenges", friend.getSuccessChallenges());
                                    cv.put("owner", "false");
                                    db.insert("pending", null, cv);
                                } else {
                                    Owner friend = datum.getOwner();
                                    cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.get_id());
                                    cv.put("inProgressChallengesCount", friend.getInProgressChallengesCount());
                                    cv.put("allChallengesCount", friend.getAllChallengesCount());
                                    cv.put("successChallenges", friend.getSuccessChallenges());
                                    cv.put("owner", "true");
                                    db.insert("pending", null, cv);
                                }
                            }
                        }
                    }
                    Log.i(TAG, "getUserPending: Success!");
                }
            }
            @Override
            public void onFailure(Throwable t) { }
        });
    }

    // generate method like in CC
    public void getUserInProgressChallenges(final String userId) {
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
                Recipient recipient = datum.getRecipient();
                Sender sender = datum.getSender();

                String challenge_description = challenge.getDescription(); // no smoking
                String challenge_detail = challenge.getDetails(); // no smoking + " during this period"
                String challenge_status = datum.getStatus();      // active or not
                String challenge_id = datum.get_id();             // us magic id
                String challenge_type = challenge.getType();      // self, duel or wake up
                String challenge_name = challenge.getName();      // name like "wake up"
                int challenge_update = challenge.getUpdated();
                String duration = "";
                if (datum.getEnd() != null) {
                    int end = datum.getEnd();
                    int days = round((end - unixTime) / 86400);
                    duration = "" + days;
                }

                if (challenge_description.equals("Wake Up")) {
                    cv.put("name", "Wake Up");
                    cv.put("recipient", "false");
                } else if (challenge_type.equals("567d51c48322f85870fd931a")) {
                    cv.put("name", "Self-Improvement");
                    cv.put("recipient", "false");
                } else if (challenge_type.equals("567d51c48322f85870fd931b")) {
                    cv.put("name", "Duel");

                    if (userId.equals(recipient.getId())) {
                        cv.put("recipient", "true");
                        cv.put("versus", sender.getName());
                    } else {
                        cv.put("recipient", "false");
                        cv.put("versus", recipient.getName());
                    }
                }

                String mChallenge_update = checkForUpdateChallenge(challenge_id);
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


        Log.i(TAG, "getUserInProgressChallenges: Success!");
    }

    // get information about friends (wins/total/lvl etc.)
    public void getUserFriendsInfo(final String gcm) {
        final String API_URL = "http://46.101.213.24:3007";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        final NewUser newUser = retrofit.create(NewUser.class);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("mytable", null, null);
        final ContentValues cv = new ContentValues();

        final GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                @Override
                public void onCompleted(JSONArray array, GraphResponse response) {
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            final String fb_id = array.getJSONObject(i).getString("id");
                            final String user_name = array.getJSONObject(i).getString("name");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("facebookId", fb_id);
                            jsonObject.put("AndroidOS", gcm);
                            String string = jsonObject.toString();
                            final String jwtString = Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setPayload(string).signWith(SignatureAlgorithm.HS256, "secret").compact();
                            Call<User> call = newUser.getUserInfo(jwtString);
                            call.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Response<User> response, Retrofit retrofit) {
                                    if (response.isSuccess()) {
                                        Data data = response.body().getData();
                                        String photo = null;

                                        if (data.getPhoto() != null) photo = API_URL + data.getPhoto().getMedium();
                                        else { try {
                                                URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                                photo = profile_pic.toString();
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        String name = data.getName();
                                        cv.put("name", name);
                                        cv.put("photo", photo);
                                        cv.put("user_id", data.get_id());
                                        cv.put("challenges", data.getAllChallengesCount());
                                        cv.put("wins", data.getSuccessChallenges());
                                        cv.put("total", data.getScore());
                                        cv.put("level", data.getLevel().getNumber());
                                        if (!checkPendingFriends(data.get_id())) db.insert("mytable", null, cv); // ?
                                        else Log.i("AppSync", "GetUserFriendsInfo | DBase: not added");
                                    } else {
                                        Log.i("AppSync", "GetUserFriendsInfo | onResponse: " + response.message());
                                        URL profile_pic;
                                        String photo = null;
                                        try {
                                            profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                            photo = profile_pic.toString();
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        }
                                        cv.put("name", user_name);
                                        cv.put("photo", photo);
                                        cv.put("challenges", "0");
                                        cv.put("wins", "0");
                                        cv.put("total", "0");
                                        cv.put("level", "0");
                                        db.insert("mytable", null, cv);
                                    }
                                    Log.i(TAG, "getUserFriendsInfo: Success!");
                                }

                                @Override
                                public void onFailure(Throwable t) {}
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        request.executeAndWait();
    }


    // check pending duels
    public void refreshCardsForPendingDuel() {
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int clearCount = db.delete("pending_duel", null, null);
        final ContentValues cv = new ContentValues();
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        final SessionManager sessionManager = new SessionManager(context);
        final String update = "0"; //1457019726
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        final String userId = user.get("id");
        String token = user.get("token");

        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);

        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(userId, update, token);
        call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<com.example.ivan.champy_v2.model.active_in_progress.Datum> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);
                        Recipient recipient = datum.getRecipient();
                        Sender sender = datum.getSender();
                        Challenge challenge = datum.getChallenge();
                        String inProgressId = datum.get_id();
                        String challengeId = challenge.get_id();
                        String challengeStatus = datum.getStatus();
                        String challengeDescription = challenge.getDescription();
                        int challengeDuration = challenge.getDuration();

                        //cv.clear();
                        if (challenge.getType().equals("567d51c48322f85870fd931b")) {
                            if (challengeStatus.equals("pending")) {
                                // TODO: 29.08.2016 maybe change for "rejectedBySender"??? or comment this line voobwe?
                                if (!challengeStatus.equals("failedBySender") && !challengeStatus.equals("rejectedByRecipient")) {

                                    if (userId.equals(recipient.getId())) {
                                        cv.put("recipient", "true");
                                        cv.put("versus", sender.getName());
                                    } else {
                                        cv.put("recipient", "false");
                                        cv.put("versus", recipient.getName());
                                    }
                                    cv.put("challenge_id", inProgressId);
                                    cv.put("description", challengeDescription);
                                    cv.put("duration", challengeDuration);
                                    db.insert("pending_duel", null, cv);
                                }
                            }
                        }
                    }
                    Log.i("RefreshPendingDuels", "onResponse: VSE OK");
                } else {
                    Log.i("RefreshPendingDuels", "onResponse: FAILED: " + response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    // load progress bars in main activity
    public void loadUserProgressBarInfo() {
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        NewUser newUser = retrofit.create(NewUser.class);

        Call<User> userCall = newUser.getUserInfo(token);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                User decodedResponse = response.body();
                Data data = decodedResponse.getData();
                SessionManager sessionManager = new SessionManager(context);
                String size = sessionManager.get_duel_pending();
                String getInProgressChallengesCount = data.getInProgressChallengesCount().toString();
                String getSuccessChallengesCount    = data.getSuccessChallenges().toString();
                String getAllChallengesCount        = data.getAllChallengesCount().toString();
                sessionManager.set_duel_pending(size);
                sessionManager.setChampyOptions(
                        data.getAllChallengesCount().toString(),
                        data.getSuccessChallenges().toString(),
                        data.getScore().toString(),
                        data.getLevel().getNumber().toString());

                Log.i("LoadUserProgressBarInfo", "onResponse: VSE OK");
                Log.i("onResponse", "USER DATA: \nAllChallengeCount = " + getInProgressChallengesCount
                        +"\nSuccessChallenges = " + getSuccessChallengesCount
                        +"\ngetScore          = " + getAllChallengesCount
                        +"\ngetLevel          = " + data.getLevel()
                        +"\ngetPendingDuel    = " + sessionManager.get_duel_pending());
            }

            @Override
            public void onFailure(Throwable t) { }
        });

    }

    // helper method for checking "accepted friends". works like if (!friends) -> add in other.
    private Boolean checkPendingFriends(String id) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Boolean ok = false;
        Cursor c = db.query("pending", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int userId = c.getColumnIndex("user_id");
            do {
                String user_id = c.getString(userId);
                if (user_id.equals(id)) {
                    ok = true;
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();
        return ok;
    }

    // done for today? true/false
    private String checkForUpdateChallenge(String challenge_id) {
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("updated", null, null, null, null, null, null);
        String status = "false";
        if (c.moveToFirst()) {
            int colchallenge_id = c.getColumnIndex("challenge_id");
            do {
                if (c.getString(colchallenge_id).equals(challenge_id)){
                    status = c.getString(c.getColumnIndex("updated"));
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();
        return status;
    }

}
