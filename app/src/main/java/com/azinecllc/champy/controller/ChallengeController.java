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
import com.azinecllc.champy.receiver.CustomAlarmReceiver;

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
import static com.azinecllc.champy.utils.Constants.oneDay;
import static com.azinecllc.champy.utils.Constants.twoDays;
import static com.azinecllc.champy.utils.Constants.typeDuel;
import static com.azinecllc.champy.utils.Constants.typeSelf;
import static com.azinecllc.champy.utils.Constants.typeWake;
import static com.azinecllc.champy.utils.Constants.update;
import static java.lang.Math.round;

public class ChallengeController {

    private static final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
    private static final String TAG = "ChallengeController";
    private String token, userID;
    private Activity activity;
    private Context context;


    public ChallengeController(Context mContext, Activity activity, String uToken, String uID) {
        this.activity = activity;
        this.context = mContext;
        this.token = uToken;
        this.userID = uID;
    }

    public ChallengeController() {}


    /**
     * Method for create new Self-Improvement challenge and send call to API with Retrofit2.
     * We create new challenge and try to send it 'in progress'. This is only for custom cards.
     * @param description - this is value of challenge description. We get it from EditText in
     *                    SelfImprovementFragment.class Of course we check if this value
     *                    isActive or isEmpty
     * @param days - this is count of duration by challenge. Like with 'description': we get value
     *             from EditText and pass value here. After that we convert current value of days
     *             to UnixTime because API works only with it and push it up.
     */
    public void createNewSelfImprovementChallenge(String description, int days) {
        final String duration = "" + (days * 86400);
        final String details = description + " during this period: " + days + " days";

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.azinecllc.champy.model.create_challenge.CreateChallenge> call = createChallenge
                .createChallenge(
                        "User_Challenge",
                        typeSelf,
                        description,
                        details,
                        duration,
                        token
                );

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
                Toast.makeText(activity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Method which get data from 'createNewSelfImprovementChallenge' in case if this is custom card
     * and sending it 'in progress'. In others case we just try to send ID from standard card with
     * standard values 'in progress'.
     * @param inProgressID - this is unique challenge ID. In case when we create standard challenge
     *                     we just put in unique inProgressID after this we can make call to API
     *                     for create this challenge. In other case when we create custom card we
     *                     need to create new self-improvement challenge with user description and
     *                     count of days for challenge. After this operation we can get 'inProgressID'
     *                     and with that we can sent it to API
     */
    public void sendSingleInProgressForSelf(String inProgressID) {
        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);

        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = singleinprogress.startSingleInProgress(inProgressID, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    generateCardsForMainActivity(new Intent(activity, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }



    /**
     * Method for create new Duel challenge and make call to API with Retrofit2.
     * We create new challenge and try to sent it 'in progress'. This is only for custom cards.
     * @param description - this is value of challenge description. We get it from EditText in
     *                    DuelFragment.class, before sending we check if this value isActive or Empty
     * @param days - this is count of duration by challenge. Like with 'description': we get value
     *             from EditText and pass value here. After that we convert current value of days
     *             to UnixTime because API works only with it and push it up.
     * @param friend_id - this is userID with whom we want to make a duel. this is ID has generated
     *                  when user create his account. (this is friend from Facebook)
     */
    public void createNewDuelChallenge(String description, int days, String friend_id) {
        final String duration = String.valueOf(days * 86400);
        final String details = description + " during this period: " + days + " days";

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.azinecllc.champy.model.create_challenge.CreateChallenge> call = createChallenge
                .createChallenge(description, typeDuel, description, details, duration, token);

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
                Toast.makeText(activity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Method which get data from 'createNewDuelChallenge' in case if this is custom card and sending
     * it 'in progress'. In others case we just try to send ID from standard card with standard values.
     * @param inProgressID - this is unique challenge ID. in case when we create standard cahllenge
     *                     we just put in unique inProgressID after this we can make call to API
     *                     for create this challenge. In others case when we create custom card we
     *                     need to create new 'Duel challenge' with description, count of days and
     *                     friends id. After this operation we can get 'inProgressID' and make call.
     * @param friend_id - this is userID with whom we want to make a duel. this is ID has generated
     *                  when user create his account. (this is friend from Facebook)
     */
    public void sendSingleInProgressForDuel(String inProgressID, String friend_id) {

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<Duel> call = singleinprogress.startDuel(friend_id, inProgressID, token);
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

                    refreshCardsForPendingDuel(new Intent(activity, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }



    /**
     * Method for create new Wake-Up challenge, send this challenge to API after check, and transmit
     * needed values to method 'sendSingleInProgressForWakeUp'. We get data from TimePicker and calculate
     * current Midnight, create calendar we needed time for ring and create an array with time for ring
     * @param days - this is custom value of challenge duration which user has chosen. This is simple
     *             integer which we use like a range for array. Also we convert this count in UnixTime
     *             for APi
     * @param sHour - we get this value from time picker and convert from 1:8 to 01:08 if value lower
     *              than 10. Also we use this value for alarmID and calendar.
     * @param sMinute - all description is equal to @sHour
     */
    public void createNewWakeUpChallenge(int days, String sHour, String sMinute) {
        final String duration   = String.format("%s", days * oneDay);
        final String wakeUpName = "Wake up at " + sHour + ":" + sMinute;
        final String wakeUpTime = sHour + sMinute;
        final int intHour = Integer.parseInt(sHour);
        final int intMin  = Integer.parseInt(sMinute);
        final int alarmID = Integer.parseInt(wakeUpTime);

        Calendar c = GregorianCalendar.getInstance();
        Log.d(TAG, "createNewWakeUpChallenge: calendar: " + c);
        final long currentMidnight = System.currentTimeMillis() / 1000
                - (c.get(Calendar.HOUR_OF_DAY) * 60 * 60)
                - (c.get(Calendar.MINUTE) * 60)
                - (c.get(Calendar.SECOND));
        Log.d(TAG, "currentMidnight: " + currentMidnight);

        final String[] details = new String[days];
        for (int i = 0; i < days; i++) {
            details[i] = String.valueOf(
                    currentMidnight
                            + (intMin  * 60)
                            + (intHour * 60 * 60)
                            + (i * (24 * 60 * 60)));
        }

        Log.d(TAG, "details: " + Arrays.toString(details));

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.azinecllc.champy.model.create_challenge.CreateChallenge> call = createChallenge
                .createChallenge(wakeUpName, typeWake, wakeUpTime, Arrays.toString(details), duration, token);

        call.enqueue(new Callback<com.azinecllc.champy.model.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challengeId = response.body().getData().get_id();
                    sendSingleInProgressForWakeUp(challengeId, alarmID, intMin, intHour, details);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Private Method for send Wake-Up challenge 'in progress'. Here we get data from our method
     * 'createNewWakeUpChallenge', create AlarmManager for daily ring, convert time for API, and
     * sending extras for CustomAlarmReceiver. Next we generate cards for MainActivity.
     * @param inProgressID - our unique 'ID' for create new challenge, we get this value from
     *                     'createNewWakeUpChallenge' and transit here.
     * @param alarmID - values from time picker: minutes and hour. To start we get this, convert to
     *                normal view (means from 1:8 to 01:08) and put inside alarmManager like ID.
     * @param min - minutes from time picker. We transmit this from last method.
     * @param hour - hours from time picker. We transmit this from last method.
     * @param det - an array with time for alarm (in seconds). Size of array has generated by day count.
     */
    private void sendSingleInProgressForWakeUp(String inProgressID, int alarmID, int min, int hour, String[] det) {
        Calendar c = GregorianCalendar.getInstance();
        final long currentMidnight = System.currentTimeMillis() / 1000
                - (c.get(Calendar.HOUR_OF_DAY) * 60 * 60)
                - (c.get(Calendar.MINUTE) * 60)
                - (c.get(Calendar.SECOND));
        Log.d(TAG, "sendSingleInProgressForWakeUp: currentMidnight: " + currentMidnight);

        Date date = new Date();
        date.setTime(((min  * 60) + (hour * 60 * 60) + currentMidnight) * 1000);
        c.setTime(date); // set date for calendar. now our calendar has a right time for ring

        if (System.currentTimeMillis() > c.getTimeInMillis()) {
            Log.d(TAG, "sendSingleInProgressForWakeUp: now > input. need to add one day");
            Log.d(TAG, "sendSingleInProgressForWakeUp: " + System.currentTimeMillis() + " > " + c.getTimeInMillis());
            c.add(Calendar.DAY_OF_YEAR, 1);
            Log.d(TAG, "sendSingleInProgressForWakeUp: new date for ring: " + c.getTime());
        }

        final long userInputTime = c.getTimeInMillis(); // must be in millis
        Log.d(TAG, "sendSingleInProgressForWakeUp: time for ring: " + userInputTime);

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = singleinprogress.startSingleInProgress(inProgressID, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    com.azinecllc.champy.model.single_in_progress.SingleInProgress data = response.body();
                    final String inProgressId = data.getData().get_id();

                    Intent myIntent = new Intent(activity, CustomAlarmReceiver.class);
                    myIntent.putExtra("inProgressID", inProgressId);
                    myIntent.putExtra("alarmID", String.valueOf(alarmID));
                    myIntent.putExtra("details", Arrays.toString(det));


                    PendingIntent pi = PendingIntent.getBroadcast(activity, alarmID, myIntent, 0);
                    AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    aManager.set(AlarmManager.RTC_WAKEUP, userInputTime, pi);


                    generateCardsForMainActivity(new Intent(activity, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }

        });

    }




    public void joinToChallenge(String inProgressId) {
        SingleInProgress singleInProgress = retrofit.create(SingleInProgress.class);

        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = singleInProgress.join(inProgressId, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    refreshCardsForPendingDuel(new Intent(activity, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, R.string.service_not_available, Toast.LENGTH_LONG).show();
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
                    refreshCardsForPendingDuel(new Intent(activity, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }




    public void doneForToday(String inProgressId, String details, String alarmID) throws IOException {
        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);

        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = activeInProgress.checkChallenge(inProgressId, token);
        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String type = response.body().getData().getChallenge().getType();
                    Log.i(TAG, "onResponse: Done for today is success");
                    if (type.equals(typeWake)) {
                        setNewAlarmClock(details, alarmID);
                    }

                    generateCardsForMainActivity(new Intent(activity, MainActivity.class));

                } else {
                    Log.i(TAG, "onResponse: Done for today failed: " + response.message() + " " + response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void give_up(String id, int alarmID, Intent intent) throws IOException {
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
                        Intent myIntent = new Intent(activity, CustomAlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, alarmID, myIntent, 0);
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);
                    }

                    if (intent != null) { generateCardsForMainActivity(intent); }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });

    }




    public void refreshCardsForPendingDuel(Intent intent) {
        ContentValues cv  = new ContentValues();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("pending_duel", null, null);

        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);

        Call<com.azinecllc.champy.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(userID, update, token);
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
                            String versus = (userID.equals(recipient.getId())) ? sender.getName() : recipient.getName();
                            String mRecipient = (userID.equals(recipient.getId())) ? "true" : "false";

                            cv.put("versus",       versus);
                            cv.put("recipient",    mRecipient);
                            cv.put("challenge_id", inProgressId);
                            cv.put("description",  challengeDescription);
                            cv.put("duration",     challengeDuration);
                            db.insert("pending_duel", null, cv);
                        }
                    }

                    //if (intent != null) {
                        generateCardsForMainActivity(intent);
                    //}

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(context, context.getString(R.string.service_not_available), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void generateCardsForMainActivity(Intent intent) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv  = new ContentValues();
        db.delete("myChallenges", null, null);
        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        Call<com.azinecllc.champy.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(userID, update, token);
        call1.enqueue(new Callback<com.azinecllc.champy.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        com.azinecllc.champy.model.active_in_progress.Datum datum = data.get(i);

                        Challenge challenge     = datum.getChallenge();
                        Recipient recipient     = datum.getRecipient();
                        Sender sender           = datum.getSender();

                        String challenge_desc   = challenge.getDescription();   // no tv
                        String challenge_detail = challenge.getDetails();       // ... + " during.."
                        String challenge_type   = challenge.getType();          // ....a / b / c
                        String challenge_name   = challenge.getName();          // wake up
                        String challenge_status = datum.getStatus();            // active or not
                        String challenge_id     = datum.get_id();               // im progress id
                        String challType        = (challenge_type.equals(typeSelf)) ? "Self-Improvement"
                                                : (challenge_type.equals(typeDuel)) ? "Duel" : "Wake Up";
                        String challenge_dur    = "";
                        String constDuration    = "";
                        List<Object> progress;
                        String needsToCheck;
                        String isRecipient;
                        String versus;

                        //String isRecipient      = (userID.equals(sender.getID())) ? "false" : "true";
                        //List<Object> progress   = (userID.equals(sender.getID()))
                        //                           ? datum.getSenderProgress()
                        //                           : datum.getRecipientProgress();
                        //String needsToCheck     = (userID.equals(sender.getID()))
                        //                           ? datum.getNeedsToCheckSender()
                        //                           : datum.getNeedsToCheckRecipient();
                        //String versus           = (challenge_type.equals(typeDuel))
                        //                           ? (userID.equals(sender.getID())
                        //                                            ? recipient.getName()
                        //                                            : sender.getName())
                        //                           : "notDuel";

                        if (userID.equals(sender.getID())) {
                            progress = datum.getSenderProgress();
                            needsToCheck = datum.getNeedsToCheckSender();
                            cv.put("recipient", "false");
                            versus = (challenge_type.equals(typeDuel)) ? recipient.getName() : "notDuel";
                            isRecipient = "false";
                        } else {
                            progress = datum.getRecipientProgress();
                            needsToCheck = datum.getNeedsToCheckRecipient();
                            cv.put("recipient", "true");
                            versus = (challenge_type.equals(typeDuel)) ? sender.getName() : "notDuel";
                            isRecipient = "true";
                        }

                        if (datum.getEnd() != null) {
                            int constDays = round((datum.getEnd() - datum.getBegin()) / 86400);
                            int duration  = datum.getChallenge().getDuration();
                            int days      = (duration - (progress.size() * oneDay)) / oneDay;
                            challenge_dur = String.valueOf(days);
                            constDuration = String.valueOf(constDays);
                        }

                        /********************** last check-in & auto surrender ********************/

                        long lastCheck = 0;
                        String prog[] = new String[progress.size()];

                        if (challenge_status.equals("started")) {
                            for (int j = 0; j < progress.size(); j++) {
                                try {
                                    JSONObject json = new JSONObject(progress.get(j).toString());
                                    long at = json.getLong("at");
                                    prog[j] = String.valueOf(at);
                                } catch (JSONException e) { e.printStackTrace(); }
                            }

                            if (prog.length != 0) {
                                lastCheck = Long.parseLong(prog[prog.length - 1]);
                            }

                            if (lastCheck != 0) {
                                Date date = new Date(lastCheck * 1000);
                                long now = System.currentTimeMillis() / 1000;
                                long lastCheckMidnight = lastCheck
                                        - (date.getHours()  * 60 * 60)
                                        - (date.getMinutes()* 60)
                                        - (date.getSeconds());
                                if (now > lastCheckMidnight + twoDays) {
                                    try {
                                        int alarmID = (challenge_type.equals("Wake Up"))
                                                ? Integer.parseInt(challenge_desc) : 0;
                                        give_up(challenge_id, alarmID, new Intent(context, MainActivity.class));
                                    } catch (Exception e) { e.printStackTrace(); }
                                }
                            }
                        }

                        cv.put("name",          challType);             // Self-Improvement / Duel / Wake Up
                        cv.put("versus",        versus);                // if this is duel than versus = recipient / sender name
                        cv.put("wakeUpTime",    challenge_detail);      // our specific time id for delete wakeUp (example: 1448);
                        cv.put("challengeName", challenge_name);        // default 'challenge'. this column only for wake up time
                        cv.put("description",   challenge_desc);        // smoking free life or wake up at 14:48
                        cv.put("duration",      challenge_dur);         // duration of challenge
                        cv.put("challenge_id",  challenge_id);          // in progress id
                        cv.put("status",        challenge_status);      // active or not
                        cv.put("recipient",     isRecipient);           // i'm recipient? true / false
                        cv.put("myProgress",    Arrays.toString(prog)); // last update time in millis
                        cv.put("constDuration", constDuration);         // our constant value of challenge duration
                        cv.put("needsToCheck",  needsToCheck);          // method for check challenge for "needToCheck"
                        db.insert("myChallenges", null, cv);            // db when we store all in progress and information about them

                    }

                    if (intent != null) {
                        activity.startActivity(intent);
                        // progressBar.loadUserProgressBarInfo(); mb?
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });

        CHLoadUserProgressBarInfo progressBar = new CHLoadUserProgressBarInfo(context);
        progressBar.loadUserProgressBarInfo();

        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();

    }


    /**
     * Method for compare current time with 'i-element' in array of alarm time.
     * @param arrayDetails - this is our array with time for alarm in seconds. When user has
     *                     selected time and count of days for Wake-Up challenge then we take this
     *                     data and convert this time in UnixTime and multiply for N-days.
     *                     After this operation we have an array with time for alarm. We had
     *                     named it 'description' but only for Wake-Up (be care with it). In this
     *                     method we put in our array 'for' to compare current time with elements
     *                     inside array. If current time is more than 'i[0]' then we get next 'i'
     *                     and do it while current time will be smaller than i[n];
     *
     * @value details - cleaned up our array from '[1, 2, 3]' to '1, 2, 3'
     * @value now     - current time on the device in seconds
     */
    public void setNewAlarmClock(String arrayDetails, String alarmID) {
        String[] details = arrayDetails.replace("[", "").replace("]", "").split(", ");

        long now = System.currentTimeMillis() / 1000;

        Log.i(TAG, "~~~~~~~~~~~~~~~ START ~~~~~~~~~~~~~~~");
        Log.i(TAG, "details.length = " + details.length);
        Log.i(TAG, "details.full   = " + Arrays.toString(details));
        Log.i(TAG, "~~~~~~~~~~~~~ START IF ~~~~~~~~~~~~~~");
        for (int i = 0; i <= details.length - 1; i++) {
            Log.d(TAG, "i = " + i + " | d[i]: " + details[i]);

            if (now < Integer.parseInt(details[i])) {
                Log.i(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                Log.i(TAG, "Woo-hoo");
                Log.i(TAG, "now < d[i]");
                Log.i(TAG, now + " < " + Integer.parseInt(details[i]));
                Log.i(TAG, "next alarm will be at: " + Long.parseLong(details[i]) * 1000);
                Log.i(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent operation = PendingIntent.getBroadcast(context, Integer.parseInt(alarmID), intent, 0);
                AlarmManager aManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                aManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(details[i]) * 1000, operation);
                break;
            }

            Log.i(TAG, "i-element < needed time");
            Log.i(TAG, "i++");

        }

    }


    /**
     * @param description - this is 'description' of challenges like 'No TV' or 'Wake Up at 9:00'.
     *                    To start we should to connect DataBase which was named 'myDB', next
     *                    need to select table 'myChallenges' and set Cursor at needed row, in our
     *                    situation this is 'description'. After that we can check row for existing
     *                    name. We use 'do-while' for check all description which has active.
     *                    P.S.: value of Wake-Up challenge has generated in 'createWakeUpChallenge'.
     *                    When user has picked time for Wake-Up challenge then we get hours & minutes
     *                    from time picker and put together like one value 12:45 = 1245. It's easy
     *                    way to avoid so much if-statements for only one type of challenges.
     * @return - if we have some description in our DataBase then we return true. It's means
     *           like is 'Yes, this challenge is exist. In any other situation we return 'false'
     */
    public boolean isActive(String description) {
        // method for check is active challenge
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


}
