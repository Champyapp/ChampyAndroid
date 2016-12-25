package com.azinecllc.champy.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.FacebookSdk;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * This is Wake-Up activity when our item_alarm manager starts ring
 */
public class AlarmReceiverActivity extends Activity implements View.OnClickListener {

    public final String TAG = "AlarmReceiverActivity";
    private MediaPlayer mMediaPlayer;
    private ChallengeController cc;
    private String inProgressChallengeId, alarmID, details;
    public Context context;
    public Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        alarmID = getIntent().getStringExtra("finalAlarmID");
        details = getIntent().getStringExtra("finalDetails");
        inProgressChallengeId = getIntent().getStringExtra("finalInProgressID");

        Log.d(TAG,
                "\nalarmID: " + alarmID
              + "\ndetails: " + details
              + "\nprogressID: " + inProgressChallengeId);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.item_alarm);
        playSound(this, getAlarmUri());

        final TextView tvWakeUpChallenge = (TextView)findViewById(R.id.tvWakeUpChallenge);
        final TextView tvWakeUp = (TextView)findViewById(R.id.wakeup_text);
        final Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        tvWakeUpChallenge.setTypeface(typeface);
        tvWakeUp.setTypeface(typeface);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SessionManager sessionManager = SessionManager.getInstance(getApplicationContext());
        final String token = sessionManager.getToken();
        final String userId = sessionManager.getUserId();
        cc = new ChallengeController(getApplicationContext(), AlarmReceiverActivity.this, token, userId);

        ImageButton buttonWakeUpDoneForToday = (ImageButton) findViewById(R.id.buttonWakeUpDoneForToday);
        ImageButton buttonWakeUpSurrender = (ImageButton) findViewById(R.id.buttonWakeUpSurrender);

        buttonWakeUpDoneForToday.setOnClickListener(this);
        buttonWakeUpSurrender.setOnClickListener(this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonWakeUpDoneForToday:
                mMediaPlayer.stop();
                try {
                    cc.doneForToday(inProgressChallengeId);
                    setNewAlarmClock(details);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
                break;

            case R.id.buttonWakeUpSurrender:
                mMediaPlayer.stop();
                try {
                    cc.give_up(inProgressChallengeId, Integer.parseInt(alarmID), new Intent(this, MainActivity.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // so, we take 0 updates and then this challenge will auto surrender.
                finish();
                break;
        }
    }

    private void setNewAlarmClock(String arrayDetails) {
        String[] details = arrayDetails.replace("[", "").replace("]", "").split(", ");

        int now = (int) (System.currentTimeMillis() / 1000);
        int timeForNextRing = 0;

        for (int i = 0; i <= details.length; i++) {
            Log.d(TAG, "i: " + i + "\nnow: " + now  + "\ndetails[i]: " + Integer.valueOf(details[i]));

            if (now > Integer.valueOf(details[i])) {
                Log.d(TAG, "now > details[i].");
                Log.d(TAG, now + " > " + details[i]);

                String[] detailMinusFirst = Arrays.copyOfRange(details, 1, details.length);
                Log.d(TAG, "deleting first element...");
                timeForNextRing = Integer.valueOf(detailMinusFirst[0]);
            } else {
                Log.d(TAG, "Vse ok: now < details[i]. can set alarm manager");
            }

            if (now < timeForNextRing) {
                Log.d(TAG, "~~~~~THE WIN~~~~~");
                Log.d(TAG, "now < timeForNextRing");
                Log.d(TAG, now + " < " + timeForNextRing);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent operation = PendingIntent.getBroadcast(getApplicationContext(), 228, intent, 0);
                AlarmManager aManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Log.d(TAG, "setting alarm for next day at: " + timeForNextRing);
                aManager.setRepeating(AlarmManager.RTC_WAKEUP, timeForNextRing, 24 * 60 * 60 * 1000, operation);
                break;

            } else {
                Log.d(TAG, "i++\n\n");
            }

        }

//        if (System.currentTimeMillis() > Integer.valueOf(details[0])) {
//            Log.d(TAG, "setNewAlarmClock: now > details[0]. need to set alarm for next time");
//        } else {
//            Log.d(TAG, "setNewAlarmClock: now < details[0]. wtf");
//        }

    }


    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setLooping(true);
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }


    private Uri getAlarmUri() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }


}