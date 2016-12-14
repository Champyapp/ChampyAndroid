package com.azinecllc.champy.controller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.helper.CHLoadUserProgressBarInfo;
import com.azinecllc.champy.interfaces.ActiveInProgress;
import com.azinecllc.champy.interfaces.CreateChallenge;
import com.azinecllc.champy.interfaces.SingleInProgress;
import com.azinecllc.champy.model.active_in_progress.Challenge;
import com.azinecllc.champy.model.active_in_progress.Datum;
import com.azinecllc.champy.model.active_in_progress.Recipient;
import com.azinecllc.champy.model.active_in_progress.Sender;
import com.azinecllc.champy.model.duel.Duel;
import com.azinecllc.champy.model.single_in_progress.Data;
import com.azinecllc.champy.receiver.AlarmReceiver;
import com.azinecllc.champy.utils.Constants;
import com.azinecllc.champy.utils.OfflineMode;

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

import static com.azinecllc.champy.utils.Constants.API_URL;
import static com.azinecllc.champy.utils.Constants.typeDuel;
import static com.azinecllc.champy.utils.Constants.typeSelf;
import static com.azinecllc.champy.utils.Constants.typeWake;
import static com.azinecllc.champy.utils.Constants.unixTime;
import static java.lang.Math.round;

public class ChallengeController {

    private final String TAG = "ChallengeController";
    private final String update = "0";
    private String duration, details;
    private String token, userId;
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
        duration = "" + (days * 86400);
        details = description + " during this period: " + days + " days";

        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.azinecllc.champy.model.create_challenge.CreateChallenge> call = createChallenge
                .createChallenge("User_Challenge", typeSelf, description, details, duration, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForSelf(challengeId);
                    Log.d(TAG, "createNewSelfImprovementChallenge Status: VSE OK");
                }
                else Log.d(TAG, "createNewSelfImprovementChallenge Status: Failed " + response.message());
            }

            @Override
            public void onFailure(Throwable t) {}
        });

    }

    public void sendSingleInProgressForSelf(String challenge) {
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = singleinprogress.startSingleInProgress(challenge, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    com.azinecllc.champy.model.single_in_progress.SingleInProgress data = response.body();
                    String inProgressId = data.getData().get_id();

//                    dbHelper = DBHelper.getInstance(context);
//                    db = dbHelper.getWritableDatabase();
//                    cv = new ContentValues();
//
//                    cv.put("challenge_id", inProgressId);
//                    cv.put("updated", "false");
//                    db.insert("updated", null, cv);

                    Log.d("sendSingleInProgress", "InProgressId: " + inProgressId);
                    generateCardsForMainActivity();
                }
                else { Log.d("sendSingleInProgress", "Status: FAILED: " + response.code() + response.message()); }
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }



    public void createNewDuelChallenge(final String description, int days, final String friend_id) {
        duration = "" + (days * 86400);
        details = description + " during this period: " + days + " days";
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.azinecllc.champy.model.create_challenge.CreateChallenge> call = createChallenge
                .createChallenge("User_Challenge", typeDuel, description, details, duration, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForDuel(challengeId, friend_id);
                }
                else Log.d(TAG, "createNewDuelChallenge OnResponse: Failed " + response.message());
            }

            @Override
            public void onFailure(Throwable t) { }
        });

    }

    public void sendSingleInProgressForDuel(final String challenge, final String friend_id) {
        dbHelper = DBHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();

        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<Duel> call = singleinprogress.startDuel(friend_id, challenge, token);
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
                }
                else Log.d("startDuelInProgress", "Status: FAILED " + response.code() + response.message());
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }



    public void createNewWakeUpChallenge(int days, String sHour, String sMinute) {
        duration = "" + (days * 86400);
        //String description = "Wake Up";

        final String wakeUpName = "Wake up at "+ sHour +":"+ sMinute;
        final String wakeUpTime = sHour + sMinute;
        final int intHour = Integer.parseInt(sHour); // this need for sending in progress method
        final int intMin  = Integer.parseInt(sMinute); // this need for sending in progress method
        final int alarmID = Integer.parseInt(sHour + sMinute); // our unique id for pending intent

        Calendar c = GregorianCalendar.getInstance();
        final long currentMidnight = unixTime - (c.get(Calendar.HOUR_OF_DAY)*60*60) - (c.get(Calendar.MINUTE)*60) - (c.get(Calendar.SECOND));

        Log.d(TAG, "createNewWakeUpChallenge : current  midnight : " + currentMidnight);


        Log.d(TAG, "createNewWakeUpChallenge: days = " + days);

        final String[] details = new String[days];
        for (int i = 0; i < days; i++) {
            details[i] = String.valueOf(intMin * 60 + intHour * 60 * 60 + i* (24 * 60 * 60) + currentMidnight);
        }

        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.azinecllc.champy.model.create_challenge.CreateChallenge> call = createChallenge
                .createChallenge(wakeUpName, typeWake, wakeUpTime, Arrays.toString(details), duration, token);

        call.enqueue(new Callback<com.azinecllc.champy.model.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForWakeUp(challengeId, alarmID, intMin, intHour);
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

    private void sendSingleInProgressForWakeUp(String challenge, final int alarmID, int minute, int hour) {
        Calendar c = GregorianCalendar.getInstance();
        final long currentMidnight = unixTime - (c.get(Calendar.HOUR_OF_DAY)*60*60) - (c.get(Calendar.MINUTE)*60) - (c.get(Calendar.SECOND));
//        Log.d(TAG, "\ncalendar hours    : " + c.get(Calendar.HOUR_OF_DAY) + " (in seconds: " + c.get(Calendar.HOUR_OF_DAY)*60*60 + ")"
//                 + "\ncalendar minutes  : " + c.get(Calendar.MINUTE) + "   (in seconds: " + + c.get(Calendar.MINUTE)*60 +")"
//                 + "\ncalendar seconds  : " + c.get(Calendar.SECOND)
//                 + "\ncurrent  midnight : " + currentMidnight
//        );

        // create date
        Date date = new Date();
        // set time for date from user's input time;
        date.setTime(((minute * 60) + (hour * 60 * 60) + currentMidnight) * 1000);
        // set date for calendar. now our calendar has a right time for ring
        c.setTime(date);


        // if user picked time which biggest than current we replace item_alarm for a next day in same time
        if (Calendar.getInstance().getTimeInMillis() > c.getTimeInMillis()) c.add(Calendar.DAY_OF_YEAR, 1);
        // first we check if current time > than alarmClockTime and after that we set the time for new variable;
        final long userInputTime = c.getTimeInMillis(); // must be in millis
        Log.d(TAG, "Final Time for ring: " + userInputTime);


        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = singleinprogress.startSingleInProgress(challenge, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    com.azinecllc.champy.model.single_in_progress.SingleInProgress data = response.body();
                    final String inProgressId = data.getData().get_id();

                    /** Creating intent for alarmReceiver and putting some data in local DB **/

                    Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);
                    myIntent.putExtra("inProgressID", inProgressId);
                    myIntent.putExtra("alarmID", String.valueOf(alarmID));

                    /** Scheduling item_alarm **/

                    // creating pending intent which will launch when our item_alarm clock must ring;
                    PendingIntent pi = PendingIntent.getBroadcast(firstActivity, alarmID, myIntent, 0);
                    // creating item_alarm manager which has a permission 'alarm_service' for invoke our item_alarm clock
                    AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    // setting repeating our item_alarm clock
                    aManager.setRepeating(AlarmManager.RTC_WAKEUP, userInputTime, 24*60*60*1000, pi);

                    /** Generate current card in DB and for MainActivity **/
                    generateCardsForMainActivity();

                }
                else Log.d("sendSingleInProgress", "Status: FAILED: " + response.code());
            }

            @Override
            public void onFailure(Throwable t) { }

        });

    }




    public void joinToChallenge(final String inProgressId) {
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress singleInProgress = retrofit.create(SingleInProgress.class);
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = singleInProgress.join(inProgressId, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
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
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = activeInProgress.reject(inProgressId, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
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
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = activeInProgress.checkChallenge(inProgressId, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
//                    dbHelper = DBHelper.getInstance(context);
//                    db = dbHelper.getWritableDatabase();
//                    cv = new ContentValues();
//                    cv.put("updated", "true");
//                    db.update("updated",      cv, "challenge_id = ?", new String[]{inProgressId});
//                    db.update("myChallenges", cv, "challenge_id = ?", new String[]{inProgressId});

                    
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
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = activeInProgress.surrender(id, token);

        OfflineMode offlineMode = OfflineMode.getInstance();
        if (offlineMode.isConnectedToRemoteAPI(firstActivity)) {
            call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
                @Override
                public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        Data data = response.body().getData();
                        String type = data.getChallenge().getType();
                        //if this is "wake up" challenge then stop item_alarm manager;
                        if (type.equals(typeWake)) {
                            Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(firstActivity, intentId, myIntent, 0);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            alarmManager.cancel(pendingIntent);
                            Log.d("GiveUp", "AlarmManager status: stopped " + alarmManager);
                        }
                        Log.d(TAG, "GiveUp onResponse: VSE OK");
                        generateCardsForMainActivity();
                    }
                    else Log.d(TAG, "GiveUp onResponse: FAILED: " + response.code());
                }

                @Override
                public void onFailure(Throwable t) { }
            });

        }

    }




    public void refreshCardsForPendingDuel() {
        cv = new ContentValues();
        dbHelper = DBHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();
        final int clearCount = db.delete("pending_duel", null, null);
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        Call<com.azinecllc.champy.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(userId, update, token);
        call1.enqueue(new Callback<com.azinecllc.champy.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        com.azinecllc.champy.model.active_in_progress.Datum datum = data.get(i);
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
        dbHelper = DBHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
        int clearCount = db.delete("myChallenges", null, null);
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        Call<com.azinecllc.champy.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(userId, update, token);
        call1.enqueue(new Callback<com.azinecllc.champy.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();

                    for (int i = 0; i < data.size(); i++) {
                        com.azinecllc.champy.model.active_in_progress.Datum datum = data.get(i);

                        Challenge challenge          = datum.getChallenge();
                        Recipient recipient          = datum.getRecipient();
                        Sender sender                = datum.getSender();

                        String challenge_description = challenge.getDescription();   // no smoking
                        String challenge_detail      = challenge.getDetails();       // no smoking + " during this period"
                        String challenge_status      = datum.getStatus();            // active or not
                        String challenge_id          = datum.get_id();               // im progress id
                        String challenge_type        = challenge.getType();          // 567d51c48322f85870fd931a / b / c
                        String challenge_name        = challenge.getName();          // wake up (time / self / duel
                        //String challenge_updated     = isUpdated(challenge_id);      // bool check method;
                        String challenge_duration = "";
                        String constDuration = "";
                        List<Object> progress;
                        String needsToCheck;
                        String challType = (challenge_type.equals(typeSelf)) ? "Self-Improvement" : (challenge_type.equals(typeDuel)) ? "Duel" : "Wake Up";
                        String versus = (challenge_type.equals(typeDuel)) ? (userId.equals(sender.get_id()) ? recipient.getName() : sender.getName()) : "notDuel";

                        if (userId.equals(sender.get_id())) {
                            progress = datum.getSenderProgress();
                            needsToCheck = datum.getNeedsToCheckSender();
                            cv.put("recipient", "false");
                        } else {
                            progress = datum.getRecipientProgress();
                            needsToCheck = datum.getNeedsToCheckRecipient();
                            cv.put("recipient", "true");
                        }

                        if (datum.getEnd() != null) {
                            int constDays = round((datum.getEnd() - datum.getBegin()) / 86400);
                            int duration = datum.getChallenge().getDuration();
                            int days = (duration - (progress.size() * Constants.oneDay)) / Constants.oneDay;
                            challenge_duration = String.valueOf(days);
                            constDuration = String.valueOf(constDays);
                        }

                        String stringSenderProgress[] = new String[progress.size()];
                        for (int j = 0; j < progress.size(); j++) {
                            try {
                                JSONObject json = new JSONObject(progress.get(j).toString());
                                long at = json.getLong("at");
                                stringSenderProgress[j] = String.valueOf(at);

                                // TODO: 12/13/16 Relocate last check-in time for auto surrender here.
                            } catch (JSONException e) { e.printStackTrace(); }
                        }

                        cv.put("name", challType); // Self-Improvement / Duel / Wake Up
                        cv.put("versus", versus); // if this is duel than versus = recipient / sender name
                        cv.put("wakeUpTime", challenge_detail); // our specific fimeld for delete wakeUp (example: 1448);
                        cv.put("challengeName", challenge_name); // default 'challenge'. this column only for wake up time
                        cv.put("description", challenge_description); // smoking free life or wake up at 14:48
                        cv.put("duration", challenge_duration); // duration of challenge
                        cv.put("challenge_id", challenge_id); // in progress id
                        cv.put("status", challenge_status); // active or not
                        //cv.put("updated", challenge_updated); // true or false (need to delete)
                        cv.put("myProgress", Arrays.toString(stringSenderProgress)); // last update time in millis
                        cv.put("constDuration", constDuration); // our constant value of challenge duration
                        cv.put("needsToCheck", needsToCheck); // method for check challenge for "needToCheck"
                        db.insert("myChallenges", null, cv); // db when we store all challenges and information about them
                    }
                    Intent intent = new Intent(firstActivity, MainActivity.class);
                    firstActivity.startActivity(intent);
                    Log.d(TAG, "Generate onResponse: VSE OK");
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });

        CHLoadUserProgressBarInfo loadData = new CHLoadUserProgressBarInfo(context);
        loadData.loadUserProgressBarInfo();

    }

    //
//    // method which returns our last update information (true or false);
//    private String isUpdated(String challenge_id) {
//        DBHelper dbHelper = new DBHelper(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor c = db.query("updated", null, null, null, null, null, null);
//        String lastUpdate = "false";
//        if (c.moveToFirst()) {
//            int colchallenge_id = c.getColumnIndex("challenge_id");
//            do {
//                // в методе "sendSingleForDuel мы засовываем challenge_id в колонку "challenge_id" в
//                // таблице "updated", а тут мы ее проверяем. если она есть, то вернуть true для
//                // дуелей, если её здесь нету, то возвращаем "false" - это для wake-up и
//                // self-improvement челенджей. Соответственно данные про update time для дуелей
//                // находятся в таблице "updated", а для отсального в таблице "myChallenges".
//                try {
//                    if (c.getString(colchallenge_id).equals(challenge_id)) {
//                        lastUpdate = c.getString(c.getColumnIndex("updated"));
//                        return lastUpdate;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.d(TAG, "isUpdated: vse xyuinja: " + e.getMessage());
//                }
//            } while (c.moveToNext());
//        }
//        c.close();
//        return lastUpdate;
//    }

    // method for check is active challenge self / duel
    public boolean isActive(String description) {
        DBHelper dbHelper = DBHelper.getInstance(context);
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
        DBHelper dbHelper = DBHelper.getInstance(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int colDescription = c.getColumnIndex("description");
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
