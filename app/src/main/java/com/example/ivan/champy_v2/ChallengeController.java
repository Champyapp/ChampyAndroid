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
import com.fasterxml.jackson.databind.util.JSONPObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static java.lang.Math.round;

public class ChallengeController {

    public static final String TAG = "ChallengeController";
    int hour, minute, seconds;
    Context context;
    Activity firstActivity;

    public ChallengeController(Context mContext, Activity activity, int mHour, int mMinute, int mSeconds) {
        context = mContext;
        firstActivity = activity;
        hour = mHour;
        minute = mMinute;
        seconds = mSeconds;
    }


    public void createNewSelfImprovementChallenge(final String description, int days) {
        final String type_id = "567d51c48322f85870fd931a";
        CurrentUserHelper user = new CurrentUserHelper(context);
        String token = user.getToken();
        final String duration = "" + (days * 86400);
        final String API_URL = "http://46.101.213.24:3007";
        final String challenge_name = "User_Challenge";
        final String detail = description + " during this period";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);

        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call = createChallenge.createChallenge(challenge_name, type_id, description, detail, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForSelf(challengeId);
                    Log.i(TAG, "createNewSelfImprovementChallenge Status: VSE OK"
                            + "\n challenge_name = " + challenge_name
                            + "\n type_id        = " + type_id
                            + "\n description    = " + description
                            + "\n detail         = " + detail
                            + "\n duration       = " + duration);
                } else Log.i(TAG, "createNewSelfImprovementChallenge Status: Failed " + response.message());
            }

            @Override
            public void onFailure(Throwable t) {}
        });

    }

    public void createNewDuelChallenge(final String description, int days, final String friend_id) {
        final String type_id = "567d51c48322f85870fd931b";
        CurrentUserHelper user = new CurrentUserHelper(context);
        final String token = user.getToken();
        final String duration = "" + (days * 86400);
        final String challenge_name = "User_Challenge";
        final String detail = description + " during this period";
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);

        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call = createChallenge.createChallenge(challenge_name, type_id, description, detail, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForDuel(challengeId, friend_id);
                    Log.i(TAG, "createNewDuelChallenge OnResponse: VSE OK" +
                            "\n challenge_name = " + challenge_name +
                            "\n type_id        = " + type_id +
                            "\n description    = " + description +
                            "\n detail         = " + detail +
                            "\n duration       = " + duration +
                            "\n challenge_id   = " + challengeId +
                            "\n friend_id      = " + friend_id);
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

        final String duration = "" + (days * 86400);
        final String challenge_name = "Wake Up";
        String sHour = "" + hour;
        String sMinute = "" + minute;
        if (hour < 10) sHour = "0" + sHour;
        if (minute < 10) sMinute = "0" + sMinute;
        long currentTime = System.currentTimeMillis() / 1000;
        final String description = "Wake up at "+ sHour +":"+ sMinute;
        Date date = new Date();
        Calendar myCalendar = GregorianCalendar.getInstance();
        myCalendar.setTime(date);
        myCalendar.get(Calendar.HOUR_OF_DAY);
        myCalendar.get(Calendar.HOUR);
        myCalendar.get(Calendar.MONTH);

        final long currentMidnight = currentTime - (myCalendar.get(Calendar.HOUR_OF_DAY) * 60 * 60) - (myCalendar.get(Calendar.MINUTE) * 60) - (myCalendar.get(Calendar.SECOND));
        Log.i(TAG, "createNewWakeUpChallenge CurrentMidNight: " + currentMidnight);

//        final String[] details = new String[21];
//        for (int i = 0; i <= 20; i++) {
//            details[i] = String.valueOf(minute * 60 + hour * 60 * 60 + i*(24*60*60) + currentMidnight);
//        }

//        final String myDetails = Arrays.toString(details);
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        final int intentId = Integer.parseInt(sHour + sMinute);
        final String myDetail = String.valueOf(Integer.parseInt(sHour + sMinute));

//        boolean ok = checkActive(sHour + sMinute);
//        if (!ok) {
//            Toast.makeText(context, "Already Exist!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // change stringIntentId for myDetails
        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call = createChallenge.createChallenge(challenge_name, type_id, description, myDetail, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForWakeUp(challengeId, intentId, currentMidnight);
                    Log.i(TAG, "createNewWakeUpChallenge Status: OK"
                            + "\n Intent_ID   = " + intentId
                            + "\n CHALL_NAME  = " + challenge_name
                            + "\n TYPE_ID     = " + type_id
                            + "\n DESCRIPTION = " + description
                            + "\n DETAILS     = " + myDetail // change for array
                            + "\n DURATION    = " + duration + " (21 day in seconds)");
                } else Log.i(TAG, "createNewWakeUpChallenge Status: Failed");
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }




    public void sendSingleInProgressForSelf(String challenge) {
        CurrentUserHelper user = new CurrentUserHelper(context);
        final String token = user.getToken();
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);

        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = singleinprogress.start_single_in_progress(challenge, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    com.example.ivan.champy_v2.single_inprogress.SingleInProgress data = response.body();
                    String inProgressId = data.getData().get_id();
                    Log.i("sendSingleInProgress", "InProgressId: " + inProgressId);
                    generateCardsForMainActivity();
                } else Log.i("sendSingleInProgress", "Status: FAILED: " + response.code());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void sendSingleInProgressForDuel(final String challenge, final String friend_id) {
        CurrentUserHelper user = new CurrentUserHelper(context);
        final String token = user.getToken();
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.duel.Duel> call = singleinprogress.Start_duel(friend_id, challenge, token);
        call.enqueue(new Callback<Duel>() {
            @Override
            public void onResponse(Response<Duel> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    ContentValues cv = new ContentValues();
                    Duel duel = response.body();
                    String inProgressId = duel.getData().getId();
                    cv.put("challenge_id", inProgressId);
                    cv.put("updated", "false");
                    DBHelper dbHelper = new DBHelper(firstActivity);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.insert("updated", null, cv);
                    Intent goToFriends = new Intent(firstActivity, FriendsActivity.class);
                    firstActivity.startActivity(goToFriends);
                    //firstActivity.finish();
                    Log.i("startDuelInProgress", "Status: VSE OK");
                } else Log.i("startDuelInProgress", "Status: FAILED" + response.code() + response.message());
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    public void sendSingleInProgressForWakeUp(String challenge, final int intentId, final long currentMidnight) {
        CurrentUserHelper user = new CurrentUserHelper(context);
        final String token = user.getToken();
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);

        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = singleinprogress.start_single_in_progress(challenge, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    com.example.ivan.champy_v2.single_inprogress.SingleInProgress data = response.body();
                    String inProgressId = data.getData().get_id();

                    long currentTime = System.currentTimeMillis() / 1000;
                    Date date = new Date();
                    Calendar myCalendar = Calendar.getInstance();

                    //long currentMidnight = currentTime - (myCalendar.get(Calendar.HOUR_OF_DAY) * 60 * 60) - (myCalendar.get(Calendar.MINUTE) * 60) - (myCalendar.get(Calendar.SECOND));

                    date.setTime(((minute * 60) + (hour * 60 * 60) + currentMidnight) * 1000);
                    myCalendar.setTime(date);
                    myCalendar.set(Calendar.SECOND, 0);
                    long current = Calendar.getInstance().getTimeInMillis();
                    long userInputTime = myCalendar.getTimeInMillis();

                    if (current > userInputTime) {
                        myCalendar.add(Calendar.DATE, 1);
                    }

                    Log.i("WakeUpActivity", "CurrentTime     = " + currentTime);
                    Log.i("WakeUpActivity", "CurrentMidnight = " + currentMidnight);
                    Log.i("WakeUpActivity", "UserInputTime   = " + userInputTime);
                    Log.i("WakeUpActivity", "Current         = " + current);
                    Log.i("WakeUpActivity", "Current - UserInputTime = " + (current - userInputTime));

                    Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);
                    myIntent.putExtra("inProgressId", inProgressId);
                    myIntent.putExtra("intentId", intentId);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(firstActivity, intentId, myIntent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, userInputTime, AlarmManager.INTERVAL_DAY, pendingIntent);

                    generateCardsForMainActivity();

                    Log.i("sendSingleInProgress", "IntentId: " + intentId);
                    Log.i("sendSingleInProgress", "InProgressId: " + inProgressId);
                } else Log.i("sendSingleInProgress", "Status: FAILED: " + response.code());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }



    public void joinToChallenge(final String inProgressId) {
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress singleInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = singleInProgress.Join(inProgressId, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("JoinToChallenge", "onResponse: VSE OK");
                    refreshCardsForPendingDuel();
                } else Log.i("JoinToChallenge", "onResponse: WTF" + " | ERROR = " + response.code());
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    public void rejectInviteForPendingDuel(String inProgressId) throws IOException {
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);

        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = activeInProgress.Reject(inProgressId, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()){
                    refreshCardsForPendingDuel();
                       Log.i(TAG, "RejectInviteForDuel onResponse: VSE OK");
                } else Log.i(TAG, "RejectInviteForDuel onResponse: FAILED" + " | ERROR: " + response.code() + " " + response.message());
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }



    public void doneForToday(String inProgressId) throws IOException {
        CurrentUserHelper user = new CurrentUserHelper(context);
        String token = user.getToken();
        //String userId = user.getUserObjectId();
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = activeInProgress.CheckChallenge(inProgressId, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i(TAG, "doneForToday onResponse: VSE OK");


                } else {
                    Log.i(TAG, "doneForToday onResponse: FAILED " + response.code() + response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    public void give_up(final String id, final int intentId) throws IOException {
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

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
                    generateCardsForMainActivity();
                    Log.i(TAG, "GiveUp onResponse: VSE OK");
                } else Log.i(TAG, "GiveUp onResponse: FAILED: " + response.code());
            }

            @Override
            public void onFailure(Throwable t) { }
        });

    }




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
                    generateCardsForMainActivity();
                    Log.i(TAG, "RefreshPendingDuels onResponse: VSE OK");
                } else {
                    Log.i(TAG, "RefreshPendingDuels onResponse: FAILED: " + response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });



    }


    public void generateCardsForMainActivity() {
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("myChallenges", null, null);
        final ContentValues cv = new ContentValues();
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        final String id = user.get("id");
        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        final long unixTime = System.currentTimeMillis() / 1000L;
        final String update = "0"; //1457019726
        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(id, update, token);
        call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);
                        Challenge challenge = datum.getChallenge();
                        Recipient recipient = datum.getRecipient();
                        Sender sender = datum.getSender();

                        String challenge_name = challenge.getName();
                        String challenge_description = challenge.getDescription();
                        String challenge_detail = challenge.getDetails();
                        String challenge_status = datum.getStatus();
                        String challenge_id = datum.get_id();
                        String challenge_type = challenge.getType();
                        int challenge_updated = challenge.getUpdated();
                        String duration = "";

                        if (datum.getEnd() != null) {
                            int end = datum.getEnd();
                            int days = round((end - unixTime) / 86400);
                            duration = "" + days;
                        }

                        List<Object> senderProgress = datum.getSenderProgress();
                        String stringSenderProgress[] = new String[senderProgress.size()];
                        for (int j = 0; j < senderProgress.size(); j++) {
                            try {
                                JSONObject json = new JSONObject(senderProgress.get(j).toString());
                                long at = json.getLong("at");
                                Log.i(TAG, "json : " + at);
                                stringSenderProgress[j] = String.valueOf(at);
                            } catch (JSONException e) {
                                Log.i(TAG, "onCatch: " + e);
                                e.printStackTrace();
                            }
                        }
                        Log.i(TAG, "onResponse AFTER FOR: senderProgressString = " + stringSenderProgress);

                        if (challenge_name.equals("Wake Up")) {
                            cv.put("recipient", "false");
                        } else if (challenge_type.equals("567d51c48322f85870fd931a")) {
                            cv.put("recipient", "false");
                        } else if (challenge_type.equals("567d51c48322f85870fd931b")) {
                            if (id.equals(recipient.getId())) {
                                cv.put("recipient", "true");
                                cv.put("versus", sender.getName());
                            } else {
                                cv.put("recipient", "false");
                                cv.put("versus", recipient.getName());
                            }
                        }


                        cv.put("name", challenge_name);
                        cv.put("challengeName", challenge_name);
                        cv.put("description", challenge_detail);
                        cv.put("duration", duration);
                        cv.put("challenge_id", challenge_id);
                        cv.put("status", challenge_status);
                        //String updated = getChallengeUpdated(challenge_id);
                        cv.put("updated", update);
                        cv.put("senderProgress", Arrays.toString(stringSenderProgress));
                        db.insert("myChallenges", null, cv);

                        Log.i(TAG, "Challenge | Description: " + challenge_detail);
                        Log.i(TAG, "Challenge | Challenge_updated: " + challenge_updated);
                        Log.i(TAG, "Challenge | SenderProgress: " + Arrays.toString(stringSenderProgress));
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


    private String getChallengeUpdated(String challenge_id) {
        DBHelper dbHelper = new DBHelper(firstActivity);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("updated", null, null, null, null, null, null);
        String ok = "false";
        if (c.moveToFirst()) {
            int colchallenge_id = c.getColumnIndex("challenge_id");
            do {
                if (c.getString(colchallenge_id).equals(challenge_id)){
                    ok = c.getString(c.getColumnIndex("updated"));
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();
        return ok;
    }

    // method for check is active challenge for wake up
    public boolean isActive(String description) {
        DBHelper dbHelper = new DBHelper(firstActivity);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        boolean ok = false;
        if (c.moveToFirst()) {
            int coldescription = c.getColumnIndex("description");
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
