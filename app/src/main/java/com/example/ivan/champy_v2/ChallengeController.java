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

import com.example.ivan.champy_v2.activity.FriendsActivity;
import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.duel.Duel;
import com.example.ivan.champy_v2.helper.CHLoadUserProgressBarInfo;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.CreateChallenge;
import com.example.ivan.champy_v2.interfaces.SingleInProgress;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;
import com.example.ivan.champy_v2.model.active_in_progress.Datum;
import com.example.ivan.champy_v2.model.active_in_progress.Recipient;
import com.example.ivan.champy_v2.model.active_in_progress.Sender;
import com.example.ivan.champy_v2.single_inprogress.Data;

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
    private long unixTime = System.currentTimeMillis() / 1000L;
    private int hour, minute, seconds;
    private String duration, details, description, update = "0"; //1457019726;
    private Context context;
    private Activity firstActivity;
    private Retrofit retrofit;
    private ContentValues cv;
    private DBHelper dbHelper;
    private SQLiteDatabase db;


    public ChallengeController(Context mContext, Activity activity, int mHour, int mMinute, int mSeconds) {
        context = mContext;
        firstActivity = activity;
        hour = mHour;
        minute = mMinute;
        seconds = mSeconds;
    }


    public void createNewSelfImprovementChallenge(final String description, int days, final String token, final String userId) {
        final String type_id = "567d51c48322f85870fd931a";
        duration = "" + (days * 86400);
        details = description + "";
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call = createChallenge.createChallenge("User_Challenge", type_id, description, details, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForSelf(challengeId, token, userId);
//                    Log.i(TAG, "createNewSelfImprovementChallenge Status: VSE OK"
//                            + "\n CHALL_ID    = " + challengeId
//                            + "\n TYPE_ID     = " + type_id
//                            + "\n DESCRIPTION = " + description
//                            + "\n DETAILS     = " + details
//                            + "\n DURATION    = " + duration);
                } else Log.i(TAG, "createNewSelfImprovementChallenge Status: Failed " + response.message());
            }

            @Override
            public void onFailure(Throwable t) {}
        });

    }

    public void createNewDuelChallenge(final String description, int days, final String friend_id, final String token) {
        final String type_id = "567d51c48322f85870fd931b";
        duration = "" + (days * 86400);
        details = description + "";
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call = createChallenge.createChallenge("User_Challenge", type_id, description, details, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForDuel(challengeId, friend_id, token);
//                    Log.i(TAG, "createNewDuelChallenge OnResponse: VSE OK"
//                            + "\n CHALL_ID    = " + challengeId
//                            + "\n TYPE_ID     = " + type_id
//                            + "\n DESCRIPTION = " + description
//                            + "\n DETAILS     = " + details
//                            + "\n DURATION    = " + duration
//                            + "\n recipientId = " + friend_id);
                } else Log.i(TAG, "createNewDuelChallenge OnResponse: Failed");
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    public void createNewWakeUpChallenge(int days, final String type_id) {
        CurrentUserHelper user = new CurrentUserHelper(context);
        final String token = user.getToken();
        final String userId = user.getUserObjectId();
        duration = "" + (days * 86400);
        description = "Wake Up";
        String sHour = "" + hour;
        String sMinute = "" + minute;
        if (hour < 10) sHour = "0" + sHour; if (minute < 10) sMinute = "0" + sMinute;
        long currentTime = System.currentTimeMillis() / 1000;
        final String wakeUpName = "Wake up at "+ sHour +":"+ sMinute;
        final String stringIntentId = String.valueOf(Integer.parseInt(sHour + sMinute));
        final int intentId = Integer.parseInt(sHour + sMinute);

        Date date = new Date();
        Calendar myCalendar = GregorianCalendar.getInstance();
        myCalendar.setTime(date);
        myCalendar.get(Calendar.HOUR_OF_DAY);
        myCalendar.get(Calendar.HOUR);
        myCalendar.get(Calendar.MONTH);
        final long currentMidnight = currentTime - (myCalendar.get(Calendar.HOUR_OF_DAY) * 60 * 60) - (myCalendar.get(Calendar.MINUTE) * 60) - (myCalendar.get(Calendar.SECOND));
//        Log.i(TAG, "createNewWakeUpChallenge CurrentMidNight: " + currentMidnight);

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
        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call = createChallenge.createChallenge(wakeUpName, type_id, description, stringIntentId, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForWakeUp(challengeId, intentId, currentMidnight, token, userId);
//                    Log.i(TAG, "createNewWakeUpChallenge Status: OK"
//                            + "\n Intent_ID   = " + intentId
//                            + "\n _ID         = " + challengeId
//                            + "\n TYPE_ID     = " + type_id
//                            + "\n DESCRIPTION = " + description
//                            + "\n DETAILS     = " + stringIntentId // change for myDetails
//                            + "\n DURATION    = " + duration + " (21 day in seconds)");
                } else Log.i(TAG, "createNewWakeUpChallenge Status: Failed");
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }




    public void sendSingleInProgressForSelf(String challenge, final String token, final String userId) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = singleinprogress.start_single_in_progress(challenge, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    com.example.ivan.champy_v2.single_inprogress.SingleInProgress data = response.body();
                    String inProgressId = data.getData().get_id();
                    cv.put("challenge_id", inProgressId);
                    cv.put("updated", "false");
                    db.insert("updated", null, cv);
                    Log.i("sendSingleInProgress", "InProgressId: " + inProgressId);
                    generateCardsForMainActivity(token, userId);
                } else Log.i("sendSingleInProgress", "Status: FAILED: " + response.code());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void sendSingleInProgressForDuel(final String challenge, final String friend_id, final String token) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
        final Intent goToFriends = new Intent(firstActivity, FriendsActivity.class);
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.duel.Duel> call = singleinprogress.Start_duel(friend_id, challenge, token);
        call.enqueue(new Callback<Duel>() {
            @Override
            public void onResponse(Response<Duel> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Duel duel = response.body();
                    String inProgressId = duel.getData().getId();
                    cv.put("challenge_id", inProgressId);
                    cv.put("updated", "false");
                    db.insert("updated", null, cv);
                    firstActivity.startActivity(goToFriends);
                    Log.i("startDuelInProgress", "Status: VSE OK");
                } else Log.i("startDuelInProgress", "Status: FAILED" + response.code() + response.message());
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    private void sendSingleInProgressForWakeUp(String challenge, final int intentId, final long currentMidnight, final String token, final String userId) {
        Date date = new Date();
        date.setTime(((minute * 60) + (hour * 60 * 60) + currentMidnight) * 1000);
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.setTime(date);
        myCalendar.set(Calendar.SECOND, 0);

        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();

        final long current = Calendar.getInstance().getTimeInMillis();
        final long userInputTime = myCalendar.getTimeInMillis();
        final Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);

        if (current > userInputTime) myCalendar.add(Calendar.DATE, 1);

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = singleinprogress.start_single_in_progress(challenge, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    com.example.ivan.champy_v2.single_inprogress.SingleInProgress data = response.body();
                    String inProgressId = data.getData().get_id();
//                    Log.i("WakeUpActivity", "CurrentTime     = " + unixTime);
//                    Log.i("WakeUpActivity", "CurrentMidnight = " + currentMidnight);
//                    Log.i("WakeUpActivity", "UserInputTime   = " + userInputTime);
//                    Log.i("WakeUpActivity", "Current         = " + current);
//                    Log.i("WakeUpActivity", "Cur - InputTime = " + (current - userInputTime));
                    myIntent.putExtra("inProgressId", inProgressId);
                    myIntent.putExtra("intentId", intentId);

                    cv.put("challenge_id", inProgressId);
                    cv.put("updated", "false");
                    db.insert("updated", null, cv);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(firstActivity, intentId, myIntent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, userInputTime, AlarmManager.INTERVAL_DAY, pendingIntent);

                    generateCardsForMainActivity(token, userId);
//                    Log.i("sendSingleInProgress", "IntentId: " + intentId);
//                    Log.i("sendSingleInProgress", "InProgressId: " + inProgressId);
                } else Log.i("sendSingleInProgress", "Status: FAILED: " + response.code());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }




    public void joinToChallenge(final String inProgressId, final String token, final String userId) {
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress singleInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = singleInProgress.Join(inProgressId, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("JoinToChallenge", "onResponse: VSE OK");
                    refreshCardsForPendingDuel(token, userId);
                } else Log.i("JoinToChallenge", "onResponse: WTF" + " | ERROR = " + response.code());
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    public void rejectInviteForPendingDuel(String inProgressId, final String token, final String userId) throws IOException {
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = activeInProgress.Reject(inProgressId, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()){
                    refreshCardsForPendingDuel(token, userId);
                    Log.i(TAG, "RejectInviteForDuel onResponse: VSE OK");
                } else Log.i(TAG, "RejectInviteForDuel onResponse: FAILED" + " | ERROR: " + response.code() + " " + response.message());
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }




    public void doneForToday(final String inProgressId, final String token, final String userId) throws IOException {
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = activeInProgress.CheckChallenge(inProgressId, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    dbHelper = new DBHelper(context);
                    db = dbHelper.getWritableDatabase();
                    cv = new ContentValues();
                    cv.put("updated", "true");
                    db.update("updated", cv, "challenge_id = ?", new String[]{inProgressId});
                    //db.update("myChallenges", cv, "challenge_id = ?", new String[]{inProgressId});
                    generateCardsForMainActivity(token, userId);
                    Log.i(TAG, "doneForToday onResponse: VSE OK");
                } else {
                    Log.i(TAG, "doneForToday onResponse: FAILED " + response.code() + response.message() + response.body());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    public void give_up(final String id, final int intentId, final String token, final String userId) throws IOException {
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = activeInProgress.Surrender(id, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Data data = response.body().getData();
                    String type = data.getChallenge().getType();
                    List<Object> senderProgress = response.body().getData().getSenderProgress();

                    // TODO: 20.09.2016 Set 'senderProgress: 0' or clean up db;

                    OfflineMode offlineMode = new OfflineMode();
                    if (offlineMode.isConnectedToRemoteAPI(firstActivity)) {
                        //если это wake up, то отключаем будильник
                        if (type.equals("567d51c48322f85870fd931c")) {
                            //String challengeDetails = data.getChallenge().getDetails();
                            //challengeDetails = challengeDetails.replace("[", "");
                            //challengeDetails = challengeDetails.replace("]", "");
                            //String[] array = challengeDetails.split(", ");

                            //Log.i("GiveUp", "OUR ARRAY = " + Arrays.toString(array));
                            //String i = array[1];
                            //int intentId = Integer.parseInt(i); // TODO: 19.08.2016 take here intentId from creating
                            //Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);
                            //PendingIntent pendingIntent = PendingIntent.getBroadcast(firstActivity, intentId, myIntent, 0);
                            //AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            //alarmManager.cancel(pendingIntent);
                            Log.i(TAG, "GiveUp Challenge = Wake Up");
                            Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(firstActivity, intentId, myIntent, 0);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            alarmManager.cancel(pendingIntent);
                            Log.i("GiveUp", "AlarmManager status: " + alarmManager);
                        }
                    }
                    generateCardsForMainActivity(token, userId);
                    Log.i(TAG, "GiveUp onResponse: VSE OK");
                } else Log.i(TAG, "GiveUp onResponse: FAILED: " + response.code());
            }

            @Override
            public void onFailure(Throwable t) { }
        });

    }




    public void refreshCardsForPendingDuel(final String token, final String userId) {
        cv = new ContentValues();
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        final int clearCount = db.delete("pending_duel", null, null);
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

                        //cv.clear();
                        if (challenge.getType().equals("567d51c48322f85870fd931b")) {
                            if (challengeStatus.equals("pending")) {
                                // TODO: 29.08.2016 maybe change for "rejectedBySender"???
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
                    generateCardsForMainActivity(token, userId);
                    Log.i(TAG, "RefreshPendingDuels onResponse: VSE OK");
                } else {
                    Log.i(TAG, "RefreshPendingDuels onResponse: FAILED: " + response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });

    }

    public void generateCardsForMainActivity(final String token, final String userId) {
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
                        String challenge_updated     = getLastUpdated(challenge_id); // bool check method;
                        String challenge_duration    = "";

                        if (datum.getEnd() != null) {
                            int end = datum.getEnd();
                            int days = round((end - unixTime) / 86400);
                            challenge_duration = "" + days;
                        }

                        List<Object> senderProgress = datum.getSenderProgress();
                        String stringSenderProgress[] = new String[senderProgress.size()];
                        for (int j = 0; j < senderProgress.size(); j++) {
                            try {
                                JSONObject json = new JSONObject(senderProgress.get(j).toString());
                                long at = json.getLong("at");
                                Log.i(TAG, "json : " + at + " <-- update time in millis");
                                stringSenderProgress[j] = String.valueOf(at);
                            } catch (JSONException e) {
                                Log.i(TAG, "onCatch: " + e);
                                e.printStackTrace();
                            }
                        }

                        if (challenge_description.equals("Wake Up")) {
                            cv.put("name", "Wake Up"); // just name of Challenge
                            cv.put("wakeUpTime", challenge_detail); // our specific field for delete wakeUp (example: 1448);
                        } else if (challenge_type.equals("567d51c48322f85870fd931a")) {
                            cv.put("name", "Self-Improvement"); // just name of Challenge
                        } else if (challenge_type.equals("567d51c48322f85870fd931b")) {
                            cv.put("name", "Duel"); // just name of Challenge
                            if (userId.equals(recipient.getId())) {
                                cv.put("recipient", "true");
                                cv.put("versus", sender.getName());
                            } else {
                                cv.put("recipient", "false");
                                cv.put("versus", recipient.getName());
                            }
                        }

                        //final String myDetails = Arrays.toString(stringSenderProgress);
                        cv.put("challengeName", challenge_name); // default 'challenge'. this column only for wake up time
                        cv.put("description", challenge_description); // smoking free life / wake up at 14:48
                        cv.put("duration", challenge_duration); // duration of challenge
                        cv.put("challenge_id", challenge_id); // in progress id
                        cv.put("status", challenge_status); // active or not
                        cv.put("updated", challenge_updated); // true / false
                        cv.put("senderProgress", Arrays.toString(stringSenderProgress)); // last update time in millis
                        db.insert("myChallenges", null, cv);

                        //Log.i(TAG, "Challenge | Description: " + challenge_detail);
                        //Log.i(TAG, "Challenge | Challenge_updated: " + challenge_updated);
                        //Log.i(TAG, "Challenge | SenderProgress: " + Arrays.toString(stringSenderProgress));
                    }

                    Log.i(TAG, "Generate onResponse: VSE OK");
                    Intent intent = new Intent(firstActivity, MainActivity.class);
                    firstActivity.startActivity(intent);

                } else {
                    Log.i(TAG, "Generate onResponse: FAILED: " + response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });

        CHLoadUserProgressBarInfo loadData = new CHLoadUserProgressBarInfo(firstActivity);
        loadData.loadUserProgressBarInfo();

    }




//    private String getDuelLastUpdate(String challenge_id) {
//        DBHelper dbHelper = new DBHelper(firstActivity);
//        final SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor c = db.query("updated", null, null, null, null, null, null);
//        String ok = "waitingForStart";
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
//                    ok = c.getString(c.getColumnIndex("updated"));
//                    break;
//                }
//            } while (c.moveToNext());
//        }
//        c.close();
//        return ok;
//    }

    private String getLastUpdated(String challenge_id) {
        DBHelper dbHelper = new DBHelper(firstActivity);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("updated", null, null, null, null, null, null);
        String lastUpdate = "createdButNotUpdated";
        if (c.moveToFirst()) {
            int colchallenge_id = c.getColumnIndex("challenge_id");
            do {
                // в методе "sendSingleForDuel мы засовываем challenge_id в колонку "challenge_id" в
                // таблице "updated", а тут мы ее проверяем. если она есть, то вернуть время когда
                // мы нажимали "дан" для дуелей, если её здесь нету, то возвращаем "false" - это для
                // wake-up и self-improvement челенджей.
                // Соответственно данные про update time для дуелей находятся в таблице "updated",
                // а для отсального в таблице "myChallenges".
                if (c.getString(colchallenge_id).equals(challenge_id)) {
                    lastUpdate = c.getString(c.getColumnIndex("updated"));
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();
        return lastUpdate;
    }

    // method for check is active challenge for wake up
    public boolean isActive(String description) {
        DBHelper dbHelper = new DBHelper(firstActivity);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        boolean ok = false;
        if (c.moveToFirst()) {
            int coldescription = c.getColumnIndex("name"); // change for 'description' if this will not working
            do {
                if (c.getString(c.getColumnIndex("status")).equals("started")) {
                    if (c.getString(coldescription).equals(description)) {
                        ok = true;
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        return ok;
    }

}
