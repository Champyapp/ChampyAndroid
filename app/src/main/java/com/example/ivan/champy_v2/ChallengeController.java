package com.example.ivan.champy_v2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.data.DBHelper;
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

/**
 * Created by ivan on 28.03.16.
 */
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

    public void Create_new_challenge(String description, int days, String type_id) {
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");

        String duration = "" + (days * 86400);
        String sHour = "" + hour;
        String sMinute = "" + minute;
        if (hour < 10) sHour = "0" + sHour;
        if (minute < 10) sMinute = "0" + sMinute;
        String details = sHour + sMinute;

        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call =
                createChallenge.createChallenge(
                        "User_Challenge",
                        type_id,
                        description,
                        details,
                        duration,
                        token
                );
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challenge = response.body().getData().get_id();
                    Log.i("stat", "Status: " + challenge);
                    StartSingleInProgress(challenge);
                    Log.i("stat", "Status: Challenge Created");

                } else Log.i("stat", "Status: Error Creating");
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }
    public void StartSingleInProgress(String challenge) {
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");

        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call =
                singleinprogress.start_single_in_progress(
                        challenge,
                        token
                );
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("stat", "Status: Starting OK");
                    generate();
                } else Log.i("stat", "Status: Starting WRONG" + response.code());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void generate() {
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("myChallenges", null, null);
        final ContentValues cv = new ContentValues();
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final SessionManager sessionManager = new SessionManager(context);

        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        String id = user.get("id");

        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);

        final long unixTime = System.currentTimeMillis() / 1000L;

        String update = "1457019726";

        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(id, update, token);
        call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {

                        com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);

                        Challenge challenge = datum.getChallenge();
                        String duration = "";
                        String description = challenge.getDetails();

                        if (datum.getEnd() != null) {
                            int end = datum.getEnd();
                            int days = round((end - unixTime) / 86400); // секунд в дні
                            duration = "" + days;
                        }

                        String challenge_id = datum.get_id();
                        Log.i("stat", "Wake Up: " + datum.getStatus());

                        if (challenge.getDescription().equals("Wake Up")) {
                            cv.put("name", "Wake Up");
                        }
                        else {
                            cv.put("name", "Self Improvement");
                        }
                        cv.put("description", description);
                        cv.put("duration", duration);
                        cv.put("challenge_id", challenge_id);
                        cv.put("status", datum.getStatus());
                        db.insert("myChallenges", null, cv);
                    }
                    Intent intent = new Intent(firstActivity, MainActivity.class);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }
    public void give_up(String id) throws IOException {
        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");

        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

       SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);

        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = activeInProgress.Surrender(id, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
           @Override
           public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
               if (response.isSuccess()){
                   Log.i("stat", "Surrender: Success");
                   Data data = response.body().getData();
                   Log.i("stat", "Give up: "+data.getChallenge().getType());
                   if (data.getChallenge().getType().equals("567d51c48322f85870fd931c")) {
                       String s = data.getChallenge().getDetails();
                       Log.i("stat", "Give up: "+s);

                       int i = Integer.parseInt(s);

                       Intent myIntent = new Intent(firstActivity, AlarmReceiver.class);

                       PendingIntent pendingIntent = PendingIntent.getBroadcast(firstActivity, i, myIntent, 0);

                       Log.i("stat", "Give up: "+i);

                       AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                       alarmManager.cancel(pendingIntent);
                   }

                   generate();

               } else Log.i("stat", "Surrender: Fail"+response.code());
           }

           @Override
           public void onFailure(Throwable t) {
           }

       });
    }
}
