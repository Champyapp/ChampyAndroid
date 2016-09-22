package com.example.ivan.champy_v2.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;
import com.example.ivan.champy_v2.model.active_in_progress.Datum;
import com.example.ivan.champy_v2.model.active_in_progress.Recipient;
import com.example.ivan.champy_v2.model.active_in_progress.Sender;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static java.lang.Math.round;

public class CHSocket {

    public static final String TAG = "CHSockets";
    private Socket mSocket;
    private String token, userId;
    private Activity activity;
    private Context context;


    public CHSocket(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }


    public void tryToConnect() {
        try {
            Log.i(TAG, "tryToConnect: trying...");
            mSocket = IO.socket("http://46.101.213.24:3007");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public void connectAndEmmit() {
        mSocket.on("connect",                               onConnect);
        mSocket.on("connected",                             onConnected);

        mSocket.on("InProgressChallenge:accepted", onAcceptedChallenge);
//        mSocket.on("InProgressChallenge:won",      onWonChallenge);
//        mSocket.on("InProgressChallenge:failed",            onAcceptedChallenge);
//        mSocket.on("InProgressChallenge:new",               onRefreshPending);
//        mSocket.on("InProgressChallenge:checked",           onNewChallenge);
//        mSocket.on("InProgressChallenge:updated",           onNewChallenge);
//        mSocket.on("InProgress:finish",                     onNewChallenge);
//        mSocket.on("InProgressChallenge:recipient:checked", onNewChallenge);
//        mSocket.on("InProgressChallenge:sender:checked",    onNewChallenge);

        mSocket.connect();
    }




    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            CurrentUserHelper currentUser = new CurrentUserHelper(context);
            mSocket.emit("ready", currentUser.getToken());
            Log.i(TAG, "Sockets: onConnect");
        }
    };
    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "Sockets: onConnected");
        }
    };

    private Emitter.Listener onAcceptedChallenge = new Emitter.Listener()  {
        @Override
        public void call(final Object... args) {
            try {
                ChallengeController cc = new ChallengeController(context, activity, 0, 0, 0);
                CurrentUserHelper user = new CurrentUserHelper(context);
                token = user.getToken();
                userId = user.getUserObjectId();
                cc.refreshCardsForPendingDuel(token, userId);
                Log.i(TAG, "Sockets: onAcceptedChallenge success!");
            } catch (Exception e) {
                Log.i(TAG, "Sockets: ERROR: " + e);
            }
        }
    };

//    private Emitter.Listener onWonChallenge = new Emitter.Listener()  {
//        @Override
//        public void call(final Object... args) {
//            try {
////                ChallengeController cc = new ChallengeController(context, activity, 0, 0, 0);
////                cc.generateCardsForMainActivity();
//                refreshCards();
//                Log.i(TAG, "Sockets: onWonChallenge success!");
//            } catch (Exception e) {
//                Log.i(TAG, "Sockets: ERROR: " + e);
//            }
//        }
//    };

//    public void refreshCards() {
//        DBHelper dbHelper = new DBHelper(context);
//        final SQLiteDatabase db = dbHelper.getWritableDatabase();
//        int clearCount = db.delete("myChallenges", null, null);
//        final ContentValues cv = new ContentValues();
//        final String API_URL = "http://46.101.213.24:3007";
//        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
//
//        final SessionManager sessionManager = new SessionManager(context);
//        HashMap<String, String> user;
//        user = sessionManager.getUserDetails();
//        String token = user.get("token");
//        final String id = user.get("id");
//        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
//        final long unixTime = System.currentTimeMillis() / 1000L;
//        final String update = "0"; //1457019726
//        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(id, update, token);
//        call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
//            @Override
//            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
//                if (response.isSuccess()) {
//                    List<Datum> data = response.body().getData();
//                    for (int i = 0; i < data.size(); i++) {
//                        com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);
//                        Challenge challenge = datum.getChallenge();
//                        Recipient recipient = datum.getRecipient();
//                        Sender sender = datum.getSender();
//
//                        String challenge_name = challenge.getName();
//                        String challenge_description = challenge.getDescription(); // bla-bla
//                        String challenge_detail = challenge.getDetails(); // bla-bla + " during this period"
//                        String challenge_status = datum.getStatus();      // active or not
//                        String challenge_id = datum.get_id();
//                        String challenge_type = challenge.getType(); // self, duel or wake up
//                        String duration = "";
//
//                        if (datum.getEnd() != null) {
//                            int end = datum.getEnd();
//                            int days = round((end - unixTime) / 86400);
//                            duration = "" + days;
//                        }
//
//                        if (challenge_description.equals("Wake Up")) {
//                            cv.put("name", "Wake Up");
//                            cv.put("recipient", "false");
//                        } else if (challenge_type.equals("567d51c48322f85870fd931a")) {
//                            cv.put("name", "Self-Improvement");
//                            cv.put("recipient", "false");
//                        } else if (challenge_type.equals("567d51c48322f85870fd931b")) {
//                            cv.put("name", "Duel");
//
//                            if (id.equals(recipient.getId())) {
//                                cv.put("recipient", "true");
//                                cv.put("versus", sender.getName());
//                            } else {
//                                cv.put("recipient", "false");
//                                cv.put("versus", recipient.getName());
//                            }
//                        }
//
//                        cv.put("challengeName", challenge_name);
//                        cv.put("description", challenge_detail);
//                        cv.put("duration", duration);
//                        cv.put("challenge_id", challenge_id);
//                        cv.put("status", challenge_status);
//                        String updated = find(challenge_id);
//                        cv.put("updated", updated);
//                        db.insert("myChallenges", null, cv);
//                    }
//
//                    Log.i(TAG, "refreshCards Response: VSE OK");
//                    Intent intent = new Intent(activity, MainActivity.class);
//                    activity.startActivity(intent);
//
//                } else {
//                    Log.i(TAG, "refreshCards Response: FAILED: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) { }
//        });
//
//        CHLoadUserProgressBarInfo loadData = new CHLoadUserProgressBarInfo(activity);
//        loadData.loadUserProgressBarInfo();
//
//    }
//
//    private String find(String challenge_id) {
//        DBHelper dbHelper = new DBHelper(activity);
//        final SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor c = db.query("updated", null, null, null, null, null, null);
//        String ok = "false";
//        if (c.moveToFirst()) {
//            int colchallenge_id = c.getColumnIndex("challenge_id");
//            do {
//                if (c.getString(colchallenge_id).equals(challenge_id)){
//                    ok = c.getString(c.getColumnIndex("updated"));
//                    break;
//                }
//            } while (c.moveToNext());
//        }
//        c.close();
//        return ok;
//    }


}
