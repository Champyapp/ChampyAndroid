package com.example.ivan.champy_v2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ivan.champy_v2.activity.FriendsActivity;
import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.duel.Duel;
import com.example.ivan.champy_v2.helper.AppSync;
import com.example.ivan.champy_v2.helper.CHLoadUserProgressBarInfo;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.helper.DateValidator;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.CreateChallenge;
import com.example.ivan.champy_v2.interfaces.SingleInProgress;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;
import com.example.ivan.champy_v2.model.active_in_progress.Datum;
import com.example.ivan.champy_v2.model.active_in_progress.Recipient;
import com.example.ivan.champy_v2.model.active_in_progress.Sender;
import com.example.ivan.champy_v2.single_inprogress.Data;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
        final String details = description + " during this period";
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);

        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call = createChallenge.createChallenge("User_Challenge", type_id, description, details, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForSelf(challengeId);
                    Log.i("createNewSIC", "CreateNewChallenge Status: VSE OK"
                            + "\n CHALL_ID    = " + challengeId
                            + "\n TYPE_ID     = " + type_id
                            + "\n DESCRIPTION = " + description
                            + "\n DETAILS     = " + details
                            + "\n DURATION    = " + duration);
                } else Log.i("createNewSIC", "CreateNewChallenge Status: Failed");
            }

            @Override
            public void onFailure(Throwable t) {}
        });

    }

    public void createNewDuelChallenge(String description, int days, final String friend_id) {
        String type_id = "567d51c48322f85870fd931b";
        CurrentUserHelper user = new CurrentUserHelper(context);
        String token = user.getToken();
        String duration = "" + (days * 86400);
        String details = description + " during this period";
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);

        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call = createChallenge.createChallenge("User_Challenge", type_id, description, details, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForDuel(challengeId, friend_id);
                    Log.i("createNewDuelChallenge", "OnResponse: VSE OK" +
                            "\n ChallengeId = " + challengeId +
                            "\n recipientId = " + friend_id);
                } else Log.i("createNewDuelChallenge", "OnResponse: Failed");
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
        final String description = "Wake Up";
        String sHour = "" + hour;
        String sMinute = "" + minute;
        if (hour < 10) sHour = "0" + sHour;
        if (minute < 10) sMinute = "0" + sMinute;
        long currentTime = System.currentTimeMillis() / 1000;
        String wakeUpName = "Wake up at "+ sHour +":"+ sMinute +" during this period";
        Date date = new Date();
        Calendar myCalendar = GregorianCalendar.getInstance();
        myCalendar.setTime(date);
        myCalendar.get(Calendar.HOUR_OF_DAY);
        myCalendar.get(Calendar.HOUR);
        myCalendar.get(Calendar.MONTH);

        final long currentMidnight = currentTime - (myCalendar.get(Calendar.HOUR_OF_DAY) * 60 * 60) - (myCalendar.get(Calendar.MINUTE) * 60) - (myCalendar.get(Calendar.SECOND));
        Log.i("ChallengeController", "CurrentMidNight: " + currentMidnight);

//        final String[] details = new String[21];
//        for (int i = 0; i <= 20; i++) {
//            details[i] = String.valueOf(minute * 60 + hour * 60 * 60 + i*(24*60*60) + currentMidnight);
//        }

//        final String myDetails = Arrays.toString(details);
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        final int intentId = Integer.parseInt(sHour + sMinute);
        final String stringIntentId = String.valueOf(Integer.parseInt(sHour + sMinute));

//        boolean ok = check(sHour + sMinute);
//        if (!ok) {
//            Toast.makeText(context, "Already Exist!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // change stringIntentId for myDetails
        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call = createChallenge.createChallenge(wakeUpName, type_id, description, stringIntentId, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForWakeUp(challengeId, intentId, currentMidnight);
                    Log.i("makeNewWakeUpChallenge", "createNewWakeUpChallenge Status: OK"
                            + "\n Intent_ID   = " + intentId
                            + "\n _ID         = " + challengeId
                            + "\n TYPE_ID     = " + type_id
                            + "\n DESCRIPTION = " + description
                            + "\n DETAILS     = " + stringIntentId // change for myDetails
                            + "\n DURATION    = " + duration + " (21 day in seconds)");
                } else Log.i("makeNewWakeUpChallenge", "CreateNewChallenge Status: Failed");
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

                    List<Object> senderProgress = response.body().getData().getSenderProgress();

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
                    Log.i("RejectInviteForDuel", "onResponse: VSE OK");
                    refreshCardsForPendingDuel();
                } else Log.i("RejectInviteForDuel", "onResponse: FAILED" + " | ERROR: " + response.code() + " " + response.message());
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
                    Log.i("DoneForToday", "onResponse: VSE OK");
//                    DateValidator dateValidator = new DateValidator();
//                    Calendar c = Calendar.getInstance();
//                    int seconds = c.get(Calendar.SECOND);
//                    SimpleDateFormat sdf6 = new SimpleDateFormat("H:mm:ss:SSS");
//                    String currentDateAndTime = sdf6.format(new Date());
//                    String stringSeconds = String.valueOf(seconds);
//                    dateValidator.isThisDateWithin3MonthsRange(stringSeconds, currentDateAndTime);
//
//                    SessionManager session = new SessionManager(context);
//                    String facebookId = session.getFacebookId();
//                    String gcm = session.getGCM();
//                    String path_to_pic = session.getPathToPic();
//                    try {
//                        AppSync appSync = new AppSync(facebookId, gcm, path_to_pic, context);
//                        appSync.getUserInProgressChallenges(userId);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                } else {
                    Log.i("DoneForToday", "onResponse: FAILED " + response.code() + response.message());
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
                if (response.isSuccess()){
                    Data data = response.body().getData();
                    String type = data.getChallenge().getType();
                    List<Object> senderProgress = response.body().getData().getSenderProgress();

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
                            Log.i("GiveUp", "Challenge = Wake Up");
                            Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(firstActivity, intentId, myIntent, 0);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            alarmManager.cancel(pendingIntent);
                            Log.i("GiveUp", "AlarmManager status: " + alarmManager);
                        }
                    }
                    Log.i("GiveUp", "onResponse: VSE OK");
                } else Log.i("GiveUp", "onResponse: FAILED: " + response.code());



            }

            @Override
            public void onFailure(Throwable t) { }
        });

        generateCardsForMainActivity();

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

                    Log.i("RefreshPendingDuels", "onResponse: VSE OK");
                } else {
                    Log.i("RefreshPendingDuels", "onResponse: FAILED: " + response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });

        generateCardsForMainActivity();

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
                        String challenge_description = challenge.getDescription(); // bla-bla
                        String challenge_detail = challenge.getDetails(); // bla-bla + " during this period"
                        String challenge_status = datum.getStatus();      // active or not
                        String challenge_id = datum.get_id();
                        String challenge_type = challenge.getType(); // self, duel or wake up
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

                            if (id.equals(recipient.getId())) {
                                cv.put("recipient", "true");
                                cv.put("versus", sender.getName());
                            } else {
                                cv.put("recipient", "false");
                                cv.put("versus", recipient.getName());
                            }
                        }

                        cv.put("challengeName", challenge_name);
                        cv.put("description", challenge_detail);
                        cv.put("duration", duration);
                        cv.put("challenge_id", challenge_id);
                        cv.put("status", challenge_status);
                        String updated = find(challenge_id);
                        cv.put("updated", updated);
                        db.insert("myChallenges", null, cv);
                    }

                    Log.i("GenerateMethod", "onResponse: VSE OK");
                    Intent intent = new Intent(firstActivity, MainActivity.class);
                    firstActivity.startActivity(intent);

                } else {
                    Log.i("GenerateMethod", "onResponse: FAILED: " + response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });

        CHLoadUserProgressBarInfo loadData = new CHLoadUserProgressBarInfo(firstActivity);
        loadData.loadUserProgressBarInfo();

    }



    private String find(String challenge_id) {
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

    private boolean check(String time) {
        boolean ok = true;
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int colDescription = c.getColumnIndex("description");
            int status = c.getColumnIndex("status");
            do {
                if (c.getString(status).equals("started")) {
                    if (c.getString(colDescription).equals(time)) {
                        ok = false;
                        break;
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        return ok;
    }

    public boolean isActive(String description) {
        DBHelper dbHelper = new DBHelper(firstActivity);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        description = description + " during this period";
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
