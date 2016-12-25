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
import android.widget.Toast;

import com.azinecllc.champy.R;
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
import java.util.ArrayList;
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
import static com.azinecllc.champy.utils.Constants.update;
import static java.lang.Math.round;

public class ChallengeController {

    private static final String TAG = "ChallengeController";
    private String token, userId;
    private Context context;
    private Activity firstActivity;
    private Retrofit retrofit;



    public ChallengeController(Context mContext, Activity activity, String uToken, String uID) {
        context = mContext;
        firstActivity = activity;
        token = uToken;
        userId = uID;
        this.retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }



    public void createNewSelfImprovementChallenge(final String description, int days) {
        final String duration = "" + (days * 86400);
        final String details = description + " during this period: " + days + " days";

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.azinecllc.champy.model.create_challenge.CreateChallenge> call = createChallenge
                .createChallenge("User_Challenge", typeSelf, description, details, duration, token);

        call.enqueue(new Callback<com.azinecllc.champy.model.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForSelf(challengeId);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(firstActivity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void sendSingleInProgressForSelf(String challenge) {
        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);

        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = singleinprogress.startSingleInProgress(challenge, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    generateCardsForMainActivity(new Intent(firstActivity, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(firstActivity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }



    public void createNewDuelChallenge(final String name, int days, final String friend_id) {
        final String duration = String.valueOf(days * 86400);
        final String details = name + " during this period: " + days + " days";

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.azinecllc.champy.model.create_challenge.CreateChallenge> call = createChallenge
                .createChallenge(name, typeDuel, name, details, duration, token);

        call.enqueue(new Callback<com.azinecllc.champy.model.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForDuel(challengeId, friend_id);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(firstActivity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void sendSingleInProgressForDuel(final String challenge, final String friend_id) {

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<Duel> call = singleinprogress.startDuel(friend_id, challenge, token);
        call.enqueue(new Callback<Duel>() {
            @Override
            public void onResponse(Response<Duel> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    //////////////////////////////////////////////////
                    ContentValues cv  = new ContentValues();
                    DBHelper dbHelper = DBHelper.getInstance(context);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Duel duel         = response.body();
                    String progressID = duel.getData().getId();
                    //String createdBy  = duel.getData().getCreated()

                    cv.put("challenge_id", progressID);
                    cv.put("updated", "false");
                    db.insert("updated", null, cv);
                    //////////////////////////////////////////////////

                    refreshCardsForPendingDuel(new Intent(firstActivity, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(firstActivity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }



    public void createNewWakeUpChallenge(int days, String sHour, String sMinute) {
        final String duration = "" + (days * 86400);
        final String wakeUpName = "Wake up at "+ sHour +":"+ sMinute;
        final String wakeUpTime = sHour + sMinute;
        final int intHour = Integer.parseInt(sHour);           // for sending in progress method
        final int intMin  = Integer.parseInt(sMinute);         // for sending in progress method
        final int alarmID = Integer.parseInt(sHour + sMinute); // unique  id for pending  intent

        Calendar c = GregorianCalendar.getInstance();
        final long currentMidnight = unixTime - (c.get(Calendar.HOUR_OF_DAY) * 60 * 60)
                                              - (c.get(Calendar.MINUTE) * 60)
                                              - (c.get(Calendar.SECOND));

        final String[] details = new String[days];
        for (int i = 0; i < days; i++) {
            details[i] = String.valueOf(
                      (intMin  * 60)
                    + (intHour * 60 * 60)
                    + (i * (24 * 60 * 60))
                    + currentMidnight
            );
        }


        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.azinecllc.champy.model.create_challenge.CreateChallenge> call = createChallenge
                .createChallenge(wakeUpName, typeWake, wakeUpTime, Arrays.toString(details), duration, token);

        call.enqueue(new Callback<com.azinecllc.champy.model.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForWakeUp(challengeId, alarmID, intMin, intHour, details);
                    Log.d(TAG, "createNewWakeUpChallenge: isSuccess");
                } else {
                    Log.d(TAG, "createNewWakeUpChallenge: Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(firstActivity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendSingleInProgressForWakeUp(String challenge, final int alarmID, int min, int hour, String[] det) {
        // TODO: 12/21/16 get current midnight from create new wake-up challenge method;
        Calendar c = GregorianCalendar.getInstance();
        final long currentMidnight = unixTime - (c.get(Calendar.HOUR_OF_DAY) * 60 * 60)
                                              - (c.get(Calendar.MINUTE) * 60)
                                              - (c.get(Calendar.SECOND));


        Date date = new Date(); // create date
        date.setTime(((min * 60) + (hour * 60 * 60) + currentMidnight) * 1000); // set time for date from user's input time;
        c.setTime(date); // set date for calendar. now our calendar has a right time for ring


        if (Calendar.getInstance().getTimeInMillis() > c.getTimeInMillis()) c.add(Calendar.DAY_OF_YEAR, 1);
        final long userInputTime = c.getTimeInMillis(); // must be in millis


        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = singleinprogress.startSingleInProgress(challenge, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.d(TAG, "SendSingleInProgress Wake-Up is Success");
                    com.azinecllc.champy.model.single_in_progress.SingleInProgress data = response.body();
                    final String inProgressId = data.getData().get_id();


                    Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);
                    myIntent.putExtra("inProgressID", inProgressId);
                    myIntent.putExtra("alarmID", String.valueOf(alarmID));
                    myIntent.putExtra("details", Arrays.toString(det));


                    PendingIntent pi = PendingIntent.getBroadcast(firstActivity, alarmID, myIntent, 0);
                    AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    aManager.setRepeating(AlarmManager.RTC_WAKEUP, userInputTime, 24*60*60*1000, pi);


                    generateCardsForMainActivity(new Intent(firstActivity, MainActivity.class));

                } else {
                    Log.d(TAG, "onResponse: SendSingleInProgress Wake-Up Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(firstActivity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }

        });

    }




    public void joinToChallenge(final String inProgressId) {
        SingleInProgress singleInProgress = retrofit.create(SingleInProgress.class);

        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = singleInProgress.join(inProgressId, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    refreshCardsForPendingDuel(new Intent(firstActivity, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(firstActivity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void rejectInviteForPendingDuel(String inProgressId) throws IOException {
        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);

        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = activeInProgress.reject(inProgressId, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()){
                    refreshCardsForPendingDuel(new Intent(firstActivity, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(firstActivity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }




    public void doneForToday(final String inProgressId) throws IOException {
        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);

        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = activeInProgress.checkChallenge(inProgressId, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    generateCardsForMainActivity(new Intent(firstActivity, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(firstActivity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void give_up(final String id, final int alarmID, Intent intent) throws IOException {
        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = activeInProgress.surrender(id, token);

        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Data data = response.body().getData();
                    String type = data.getChallenge().getType();

                    if (type.equals(typeWake)) {
                        //if this is "wake up" challenge then stop alarm manager;
                        Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(firstActivity, alarmID, myIntent, 0);
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);
                    }

                    generateCardsForMainActivity(intent);

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(firstActivity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });

    }




    public void refreshCardsForPendingDuel(Intent intent) {
        ContentValues cv  = new ContentValues();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("pending_duel", null, null);

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
                        Challenge challenge = datum.getChallenge();
                        Sender sender       = datum.getSender();

                        String challengeStatus = datum.getStatus();

                        if (challengeStatus.equals("pending")) {
                            String inProgressId = datum.get_id();
                            String challengeDescription = challenge.getDescription();
                            int challengeDuration = challenge.getDuration();
                            String versus = (userId.equals(recipient.getId())) ? sender.getName() : recipient.getName();
                            String mRecipient = (userId.equals(recipient.getId())) ? "true" : "false";

                            cv.put("versus",       versus);
                            cv.put("recipient",    mRecipient);
                            cv.put("challenge_id", inProgressId);
                            cv.put("description",  challengeDescription);
                            cv.put("duration",     challengeDuration);
                            db.insert("pending_duel", null, cv);
                        }
                    }

                    generateCardsForMainActivity(intent);

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(firstActivity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void generateCardsForMainActivity(Intent intent) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        db.delete("myChallenges", null, null);
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
                        String challenge_type        = challenge.getType();          // 567d51c48322f85870fd931a / b / c
                        String challenge_name        = challenge.getName();          // wake up (time / self / button_duel
                        String challenge_status      = datum.getStatus();            // active or not
                        String challenge_id          = datum.get_id();               // im progress id
                        String challenge_duration    = "";
                        String constDuration         = "";
                        List<Object> progress;
                        String needsToCheck;
                        String challType = (challenge_type.equals(typeSelf)) ? "Self-Improvement"
                                         : (challenge_type.equals(typeDuel)) ? "Duel" : "Wake Up";
                        String versus    = (challenge_type.equals(typeDuel))
                                         ? (userId.equals(sender.get_id())   ? recipient.getName()
                                         : sender.getName()) : "notDuel";


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

                        String prog[] = new String[progress.size()];
                        for (int j = 0; j < progress.size(); j++) {
                            try {
                                JSONObject json = new JSONObject(progress.get(j).toString());
                                long at = json.getLong("at");
                                prog[j] = String.valueOf(at);
                                // TODO: 12/13/16 Relocate last check-in time for auto surrender here.
                            } catch (JSONException e) { e.printStackTrace(); }
                        }



                        cv.put("name",          challType);             // Self-Improvement / Duel / Wake Up
                        cv.put("versus",        versus);                // if this is button_duel than versus = recipient / sender name
                        cv.put("wakeUpTime",    challenge_detail);      // our specific time id for delete wakeUp (example: 1448);
                        cv.put("challengeName", challenge_name);        // default 'challenge'. this column only for wake up time
                        cv.put("description",   challenge_description); // smoking free life or wake up at 14:48
                        cv.put("duration",      challenge_duration);    // duration of challenge
                        cv.put("challenge_id",  challenge_id);          // in progress id
                        cv.put("status",        challenge_status);      // active or not
                        cv.put("myProgress",    Arrays.toString(prog)); // last update time in millis
                        cv.put("constDuration", constDuration);         // our constant value of challenge duration
                        cv.put("needsToCheck",  needsToCheck);          // method for check challenge for "needToCheck"
                        db.insert("myChallenges", null, cv);            // db when we store all ic_score_progress and information about them
                    }
                    //Intent intent = new Intent(firstActivity, MainActivity.class);
                    if (intent != null) {
                        firstActivity.startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });

        CHLoadUserProgressBarInfo loadData = new CHLoadUserProgressBarInfo(context);
        loadData.loadUserProgressBarInfo();

        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();

    }





    // method for check is active challenge self / button_duel
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
                }
            } while (c.moveToNext());
        }
        c.close();
        return ok;
    }

}
