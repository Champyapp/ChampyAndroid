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
import com.example.ivan.champy_v2.duel.Duel;
import com.example.ivan.champy_v2.helper.CHLoadUserProgressBarInfo;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.CreateChallenge;
import com.example.ivan.champy_v2.interfaces.SingleInProgress;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;
import com.example.ivan.champy_v2.model.active_in_progress.Datum;
import com.example.ivan.champy_v2.single_inprogress.Data;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static java.lang.Math.round;

// эта какаха относится к "wake up" и наследовать я ёё не могу
public class ChallengeController {

    Context context;
    Activity firstActivity;
    int hour, minute;
    public ChallengeController(Context mContext, Activity activity, int mHour, int mMinute) {
        context = mContext;
        firstActivity = activity;
        hour = mHour;
        minute = mMinute;
    }


    public void createNewSelfImprovementChallenge(final String description, int days) {
        final String type_id = "567d51c48322f85870fd931a";
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
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
                    String challenge = response.body().getData().get_id();
                    sendSingleInProgressForSelfOrWakeUp(challenge);
                    Log.i("createNewSIC", "CreateNewChallenge Status: VSE OK"
                            + "\n create_challenge.CreateChallenge data = " + challenge
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

    public void createNewWakeUpChallenge(final String description, int days, final String type_id) {
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");

        final String duration = "" + (days * 86400);
        String sHour = "" + hour;
        String sMinute = "" + minute;
        if (hour < 10) sHour = "0" + sHour;
        if (minute < 10) sMinute = "0" + sMinute;
        final String details = sHour + sMinute;
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);

        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call = createChallenge.createChallenge("User_Challenge", type_id, description, details, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    Log.i("stat", "Status: " + challengeId);
                    sendSingleInProgressForSelfOrWakeUp(challengeId);
                    Log.i("makeNewWakeUpChallenge", "createNewWakeUpChallenge Status: OK"
                            + "\n _ID         = " + challengeId
                            + "\n TYPE_ID     = " + type_id
                            + "\n DESCRIPTION = " + description
                            + "\n DETAILS     = " + details
                            + "\n DURATION    = " + duration);
                } else Log.i("makeNewWakeUpChallenge", "CreateNewChallenge Status: Failed");
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    public void createNewDuelChallenge(String description, int days, final String friend_id) {
        String type_id = "567d51c48322f85870fd931b";
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
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



    public void sendSingleInProgressForSelfOrWakeUp(String challenge) {
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);

        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = singleinprogress.start_single_in_progress(challenge, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    com.example.ivan.champy_v2.single_inprogress.SingleInProgress data = response.body();
                    String _id = data.getData().get_id();
                    Log.i("startSingleInProgress", "Status: VSE OK" + "\n_ID! = " + _id);
                    generateCardsForMainActivity();
                } else Log.i("startSingleInProgress", "Status: FAILED");
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void sendSingleInProgressForDuel(final String challenge, final String friend_id) {
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
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
                    //myRetrieveInProgressChallengeById(_id);
                    //generateCardsForMainActivity();
                    Log.i("startDuelInProgress", "startDuelInProgress: " + "\n DUEL_ID = " + inProgressId + "\n friend_id = " + friend_id);
                } else Log.i("startDuelInProgress", "Status: FAILED" + response.code() + response.message());
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }




    public void myRetrieveInProgressChallengeById(final String inProgressId) {
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        final String update = "0";
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call = activeInProgress.getActiveInProgress(inProgressId, update, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("RetrieveInProgress", "onResponse: VSE OK \n inProgressId = " + inProgressId);

                } else {
                    Log.i("RetrieveInProgress", "onResponse: FAILED");
                }
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
        final String mToken = token;
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SingleInProgress singleInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = singleInProgress.Join(inProgressId, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("JoinToChallenge", "Status: VSE OK"
                            + "\n     DataId      = "
                            + "\n     TOKEN       = " + mToken);

                    generateCardsForMainActivity();
                } else {
                    Log.i("JoinToChallenge", "Status: WTF"
                            + "\n    ERROR        = " + response.code() + response.message()
                            + "\n    DataId       = "
                            + "\n    TOKEN        = " + mToken);
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }










    private void generateCardsForMainActivity() {
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
        String id = user.get("id");
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
                        } else if (challenge_type.equals("567d51c48322f85870fd931a")) {
                            cv.put("name", "Self-Improvement");
                        } else if (challenge_type.equals("567d51c48322f85870fd931b")) {
                            cv.put("name", "Duel");
                        }

                        cv.put("description", challenge_detail);
                        cv.put("duration", duration);
                        cv.put("challenge_id", challenge_id);
                        cv.put("status", challenge_status);
                        String updated = find(challenge_id);
                        cv.put("updated", updated);
                        db.insert("myChallenges", null, cv);
                    }
                    Log.i("Generate Method:", "Status: VSE OK");
                    Intent intent = new Intent(firstActivity, MainActivity.class);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });


        CHLoadUserProgressBarInfo loadData = new CHLoadUserProgressBarInfo(firstActivity);
        loadData.loadUserProgressBarInfo();
    }

    public void give_up(String id) throws IOException {
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
                   //если это wake up, то отключаем будильник, если нет, то call мы и так уже скинули и все ок
                   if (type.equals("567d51c48322f85870fd931c")) {
                       String s = data.getChallenge().getDetails();
                       int i = Integer.parseInt(s);
                       Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);
                       PendingIntent pendingIntent = PendingIntent.getBroadcast(firstActivity, i, myIntent, 0);
                       AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                       alarmManager.cancel(pendingIntent);
                   }

                   generateCardsForMainActivity();
               }
           }

           @Override
           public void onFailure(Throwable t) { }
       });
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




}
