package com.example.ivan.champy_v2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHLoadUserProgressBarInfo;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.CreateChallenge;
import com.example.ivan.champy_v2.interfaces.SingleInProgress;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;
import com.example.ivan.champy_v2.model.active_in_progress.Datum;
import com.example.ivan.champy_v2.model.active_in_progress.Recipient;
import com.example.ivan.champy_v2.model.active_in_progress.Sender;
import com.example.ivan.champy_v2.model.duel.Duel;
import com.example.ivan.champy_v2.model.single_in_progress.Data;
import com.example.ivan.champy_v2.utils.Constants;
import com.example.ivan.champy_v2.utils.OfflineMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static java.lang.Math.round;

public class ChallengeController {

    public static final String API_URL = "http://46.101.213.24:3007";
    public static final String TAG = "ChallengeController";
    private String duration, details, update = "0";
    public String token, userId;
    private Context context;
    private Activity firstActivity;
    private Retrofit retrofit;
    private ContentValues cv;
    private DBHelper dbHelper;
    private SQLiteDatabase db;


    public ChallengeController(Context mContext, Activity activity, String uToken, String uID) {
        context = mContext;
        firstActivity = activity;
        token = uToken;
        userId = uID;
    }



    public void createNewSelfImprovementChallenge(final String description, int days) {
        final String type_id = "567d51c48322f85870fd931a";
        duration = "" + (days * 86400);
        details = description + " during this period: " + days + " days";

        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);

        Call<com.example.ivan.champy_v2.model.create_challenge.CreateChallenge> call = createChallenge.createChallenge("User_Challenge", type_id, description, details, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForSelf(challengeId);
                    Log.d(TAG, "createNewSelfImprovementChallenge Status: VSE OK");
//                            + "\n CHALL_ID    = " + challengeId
//                            + "\n TYPE_ID     = " + type_id
//                            + "\n DESCRIPTION = " + description
//                            + "\n DETAILS     = " + details
//                            + "\n DURATION    = " + duration);
                } else Log.d(TAG, "createNewSelfImprovementChallenge Status: Failed " + response.message());
            }

            @Override
            public void onFailure(Throwable t) {}
        });

    }

    public void sendSingleInProgressForSelf(String challenge) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();

        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);

        Call<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> call = singleinprogress.start_single_in_progress(challenge, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress data = response.body();
                    String inProgressId = data.getData().get_id();
                    cv.put("challenge_id", inProgressId);
                    cv.put("updated", "false");
                    db.insert("updated", null, cv);
                    Log.d("sendSingleInProgress", "InProgressId: " + inProgressId);
                    generateCardsForMainActivity();
                } else {
                    Log.d("sendSingleInProgress", "Status: FAILED: " + response.code() + response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }



    public void createNewDuelChallenge(final String description, int days, final String friend_id) {
        final String type_id = "567d51c48322f85870fd931b";
        duration = "" + (days * 86400);
        details = description + " during this period: " + days + " days";
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.example.ivan.champy_v2.model.create_challenge.CreateChallenge> call = createChallenge.createChallenge("User_Challenge", type_id, description, details, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForDuel(challengeId, friend_id);
//                    Log.d(TAG, "createNewDuelChallenge OnResponse: VSE OK"
//                            + "\n CHALL_ID    = " + challengeId
//                            + "\n TYPE_ID     = " + type_id
//                            + "\n DESCRIPTION = " + description
//                            + "\n DETAILS     = " + details
//                            + "\n DURATION    = " + duration
//                            + "\n recipientId = " + friend_id);
                } else Log.d(TAG, "createNewDuelChallenge OnResponse: Failed " + response.message());
            }

            @Override
            public void onFailure(Throwable t) { }
        });

    }

    public void sendSingleInProgressForDuel(final String challenge, final String friend_id) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();

        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.model.duel.Duel> call = singleinprogress.Start_duel(friend_id, challenge, token);
        call.enqueue(new Callback<Duel>() {
            @Override
            public void onResponse(Response<Duel> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Duel duel = response.body();
                    String inProgressId = duel.getData().getId();
                    cv.put("challenge_id", inProgressId);
                    cv.put("updated", "false");
                    db.insert("updated", null, cv);

                    refreshCardsForPendingDuel();

                    Log.d("startDuelInProgress", "Status: VSE OK");
                } else
                    Log.d("startDuelInProgress", "Status: FAILED " + response.code() + response.message());
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }



    public void createNewWakeUpChallenge(int days, final String type_id, String sHour, String sMinute) {
        duration = "" + (days * 86400);
        String description = "Wake Up";
//        String sHour = "" + hour;
//        String sMinute = "" + minute;
//        if (hour < 10) sHour = "0" + sHour;
//        if (minute < 10) sMinute = "0" + sMinute;
        //long currentTime = System.currentTimeMillis() / 1000;
        final String wakeUpName = "Wake up at "+ sHour +":"+ sMinute;
        final String stringIntentId = String.valueOf(Integer.parseInt(sHour + sMinute));

        final int intHour  = Integer.parseInt(sHour); // this need for sending in progress method
        final int intMin   = Integer.parseInt(sMinute); // this need for sending in progress method
        final int intentId = Integer.parseInt(sHour + sMinute); // our unique id for pending intent

//        Log.d(TAG, "createNewWakeUpChallenge CurrentMidNight: " + currentMidnight);

//        final String[] details = new String[21];
//        for (int i = 0; i <= 20; i++) {
//            details[i] = String.valueOf(minute * 60 + hour * 60 * 60 + i*(24*60*60) + currentMidnight);
//        }

//        final String myDetails = Arrays.toString(details);

//        boolean ok = checkActive(sHour + sMinute);
//        if (!ok) {
//            Toast.makeText(context, "Already Exist!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // change stringIntentId for myDetails
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.example.ivan.champy_v2.model.create_challenge.CreateChallenge> call = createChallenge
                .createChallenge(wakeUpName, type_id, description, stringIntentId, duration, token);

        call.enqueue(new Callback<com.example.ivan.champy_v2.model.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForWakeUp(challengeId, intentId, intMin, intHour);
//                    Log.d(TAG, "createNewWakeUpChallenge Status: OK"
//                            + "\n Intent_ID   = " + intentId
//                            + "\n _ID         = " + challengeId
//                            + "\n TYPE_ID     = " + type_id
//                            + "\n DESCRIPTION = " + description
//                            + "\n DETAILS     = " + stringIntentId // change for myDetails
//                            + "\n DURATION    = " + duration + " (21 day in seconds)");
                } else Log.d(TAG, "createNewWakeUpChallenge Status: Failed");
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    private void sendSingleInProgressForWakeUp(String challenge, final int intentId, int minute, int hour) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();

        Date date = new Date();
        Calendar c = GregorianCalendar.getInstance();
        final long currentMidnight = Constants.unixTime - (c.get(Calendar.HOUR_OF_DAY)*60*60) - (c.get(Calendar.MINUTE)*60) - (c.get(Calendar.SECOND));
        Log.d(TAG,"currentMidnight: " + currentMidnight);
        date.setTime(((minute * 60) + (hour * 60 * 60) + currentMidnight) * 1000);
        c.setTime(date);

        // if user picked time which biggest than current we
        if (Calendar.getInstance().getTimeInMillis() > c.getTimeInMillis()) c.add(Calendar.DATE, 1); // mb +(24*60*60) ?
        final long userInputTime = c.getTimeInMillis();
        Log.d(TAG, "userInputTime final result: " + userInputTime);


        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> call = singleinprogress.start_single_in_progress(challenge, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress data = response.body();
                    String inProgressId = data.getData().get_id();

                    Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);
                    myIntent.putExtra("inProgressId", inProgressId);
                    myIntent.putExtra("intentId", intentId);

                    cv.put("challenge_id", inProgressId);
                    cv.put("updated", "false");
                    db.insert("updated", null, cv);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(firstActivity, intentId, myIntent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, userInputTime, AlarmManager.INTERVAL_DAY, pendingIntent);

                    generateCardsForMainActivity();
                } else Log.d("sendSingleInProgress", "Status: FAILED: " + response.code());
            }

            @Override
            public void onFailure(Throwable t) { }

        });
    }




    public void joinToChallenge(final String inProgressId) {
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress singleInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> call = singleInProgress.Join(inProgressId, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.d("JoinToChallenge", "onResponse: VSE OK");
                    refreshCardsForPendingDuel();
                } else Log.d("JoinToChallenge", "onResponse: WTF" + " | ERROR = " + response.code());
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    public void rejectInviteForPendingDuel(String inProgressId) throws IOException {
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> call = activeInProgress.Reject(inProgressId, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()){
                    refreshCardsForPendingDuel();
                    Log.d(TAG, "RejectInviteForDuel onResponse: VSE OK");
                } else Log.d(TAG, "RejectInviteForDuel onResponse: FAILED" + " | ERROR: " + response.code() + " " + response.message());
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }




    public void doneForToday(final String inProgressId) throws IOException {
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> call = activeInProgress.CheckChallenge(inProgressId, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    dbHelper = new DBHelper(context);
                    db = dbHelper.getWritableDatabase();
                    cv = new ContentValues();
                    cv.put("updated", "true");
                    db.update("updated",      cv, "challenge_id = ?", new String[]{inProgressId});
                    db.update("myChallenges", cv, "challenge_id = ?", new String[]{inProgressId});
                    generateCardsForMainActivity();
                    Log.d(TAG, "doneForToday onResponse: VSE OK");
                } else {
                    Log.d(TAG, "doneForToday onResponse: FAILED " + response.code() + response.message() + response.body());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    public void give_up(final String id, final int intentId) throws IOException {
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> call = activeInProgress.Surrender(id, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Data data = response.body().getData();
                    String type = data.getChallenge().getType();
                    OfflineMode offlineMode = new OfflineMode();
                    if (offlineMode.isConnectedToRemoteAPI(firstActivity)) {
                        //if this is "wake up" challenge then stop alarm manager;
                        if (type.equals("567d51c48322f85870fd931c")) {
                            Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(firstActivity, intentId, myIntent, 0);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            alarmManager.cancel(pendingIntent);
                            Log.d("GiveUp", "AlarmManager status: stopped " + alarmManager);
                        }
                    }
                    Log.d(TAG, "GiveUp onResponse: VSE OK");
                    generateCardsForMainActivity();
                } else Log.d(TAG, "GiveUp onResponse: FAILED: " + response.code());
            }

            @Override
            public void onFailure(Throwable t) { }
        });

    }




    public void refreshCardsForPendingDuel() {
        cv = new ContentValues();
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        final int clearCount = db.delete("pending_duel", null, null);
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(userId, update, token);
        call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
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

                        if (challengeStatus.equals("pending")) {
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

                    generateCardsForMainActivity();

                    Log.d(TAG, "RefreshPendingDuels onResponse: VSE OK");
                } else {
                    Log.d(TAG, "RefreshPendingDuels onResponse: FAILED: " + response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    private void generateCardsForMainActivity() {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
        int clearCount = db.delete("myChallenges", null, null);
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(userId, update, token);
        call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();

                    for (int i = 0; i < data.size(); i++) {
                        com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);

                        Challenge challenge          = datum.getChallenge();
                        Recipient recipient          = datum.getRecipient();
                        Sender sender                = datum.getSender();

                        String challenge_description = challenge.getDescription();   // no smoking
                        String challenge_detail      = challenge.getDetails();       // no smoking + " during this period"
                        String challenge_status      = datum.getStatus();            // active or not
                        String challenge_id          = datum.get_id();               // im progress id
                        String challenge_type        = challenge.getType();          // 567d51c48322f85870fd931a / b / c
                        String challenge_name        = challenge.getName();          // wake up / self / duel
                        String challenge_wakeUpTime  = challenge.getWakeUpTime();    // our specific time (intentId)
                        String challenge_updated     = isUpdated(challenge_id);      // bool check method;
                        String needsToCheckSender    = datum.getNeedsToCheckSender();// true / false for today
                        String needsToCheckRecipient = datum.getNeedsToCheckRecipient(); // true / false if this is duel and i'm recipient
                        List<Object> senderProgress  = datum.getSenderProgress();    // sender progress
                        String challenge_duration    = "";
                        String constDuration         = "";

                        if (datum.getEnd() != null) {
                            int end = datum.getEnd();
                            int begin = datum.getBegin();
                            int days = round((end - Constants.unixTime) / 86400);
                            int constDays = round((end - begin) / 86400);
                            challenge_duration = String.valueOf(days);
                            constDuration = String.valueOf(constDays);
                        }

                        String stringSenderProgress[] = new String[senderProgress.size()];
                        for (int j = 0; j < senderProgress.size(); j++) {
                            try {
                                JSONObject json = new JSONObject(senderProgress.get(j).toString());
                                long at = json.getLong("at");
                                stringSenderProgress[j] = String.valueOf(at);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (challenge_description.equals("Wake Up")) {
                            // just name of Challenge
                            cv.put("name", "Wake Up");
                            // our specific field for delete wakeUp (example: 1448);
                            cv.put("wakeUpTime", challenge_detail);
                            // method for check challenge for "needToCheck"
                            cv.put("needsToCheckSender", needsToCheckSender);
                        } else if (challenge_type.equals("567d51c48322f85870fd931a")) {
                            // just name of Challenge
                            cv.put("name", "Self-Improvement");
                            // method for check challenge for "needToCheck"
                            cv.put("needsToCheckSender", needsToCheckSender);
                        } else if (challenge_type.equals("567d51c48322f85870fd931b")) {
                            // just name of Challenge
                            cv.put("name", "Duel");
                            if (userId.equals(recipient.getId())) {
                                // if I accepted challenge, i'm "recipient"
                                cv.put("recipient", "true");
                                // name of the person with whom we have a duel
                                cv.put("versus", sender.getName());
                                // method for check challenge for "needToCheck"
                                cv.put("needsToCheckRecipient", needsToCheckRecipient);
                            } else {
                                // if I sent the challenge, i'm "sender"
                                cv.put("recipient", "false");
                                // name of the person with whom we have a duel
                                cv.put("versus", recipient.getName());
                                // method for check challenge for "needToCheck"
                                cv.put("needsToCheckSender", needsToCheckSender);
                            }
                        }

                        // default 'challenge'. this column only for wake up time
                        cv.put("challengeName", challenge_name);
                        // smoking free life or wake up at 14:48
                        cv.put("description", challenge_description);
                        // duration of challenge
                        cv.put("duration", challenge_duration);
                        // in progress id
                        cv.put("challenge_id", challenge_id);
                        // active or not
                        cv.put("status", challenge_status);
                        // true or false
                        cv.put("updated", challenge_updated);
                        // last update time in millis
                        cv.put("senderProgress", Arrays.toString(stringSenderProgress));
                        // our constant value of challenge duration
                        cv.put("constDuration", constDuration);
                        // db when we store all challenges and information about them
                        db.insert("myChallenges", null, cv);
                    }
                    Log.d(TAG, "Generate onResponse: VSE OK");
                    Intent intent = new Intent(firstActivity, MainActivity.class);
                    firstActivity.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });

        CHLoadUserProgressBarInfo loadData = new CHLoadUserProgressBarInfo(context);
        loadData.loadUserProgressBarInfo();

    }



    // method which returns our last update information (true or false);
    private String isUpdated(String challenge_id) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("updated", null, null, null, null, null, null);
        String lastUpdate = "false";
        if (c.moveToFirst()) {
            int colchallenge_id = c.getColumnIndex("challenge_id");
            do {
                // в методе "sendSingleForDuel мы засовываем challenge_id в колонку "challenge_id" в
                // таблице "updated", а тут мы ее проверяем. если она есть, то вернуть true для
                // дуелей, если её здесь нету, то возвращаем "false" - это для wake-up и
                // self-improvement челенджей. Соответственно данные про update time для дуелей
                // находятся в таблице "updated", а для отсального в таблице "myChallenges".
                try {
                    if (c.getString(colchallenge_id).equals(challenge_id)) {
                        lastUpdate = c.getString(c.getColumnIndex("updated"));
                        Log.d(TAG, "isUpdated: " + lastUpdate);
                        return lastUpdate;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "isUpdated: vse xyuinja: " + e.getMessage());
                }
            } while (c.moveToNext());
        }
        c.close();
        return lastUpdate;
    }

    // method for check is active challenge self / duel
    public boolean isActive(String description) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        boolean ok = false;
        if (c.moveToFirst()) {
            int coldescription = c.getColumnIndex("description");
            do {
                try {
                    if (c.getString(c.getColumnIndex("status")).equals("started")) {
                        if (c.getString(coldescription).equals(description)) {
                            ok = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "isActive: vse xyunja: " + e.getMessage());
                }
            } while (c.moveToNext());
        }
        c.close();
        return ok;
    }

    // method for check is active challenge for wake up
    public boolean isActiveWakeUp(String time) {
        boolean ok = true;
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int colDescription = c.getColumnIndex("wakeUpTime");
            int status = c.getColumnIndex("status");
            do {
                try {
                    if (c.getString(status).equals("started")) {
                        if (c.getString(colDescription).equals(time)) {
                            ok = false;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "isActiveWakeUp: vse xyunja: " + e.getMessage());
                }
            } while (c.moveToNext());
        }
        c.close();
        return ok;
    }

}
