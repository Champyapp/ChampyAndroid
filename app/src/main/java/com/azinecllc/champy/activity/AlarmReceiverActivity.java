package com.azinecllc.champy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.FacebookSdk;

import java.io.IOException;
import java.util.Arrays;

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

        Log.d(TAG, "AlarmReceiverActivity:"
                + "\nalarmID: " + alarmID
                + "\ndetails: " + details
                + "\ninProgressChallengeId: " + inProgressChallengeId);

        getFirstItemFromAlarmMassive(details);

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

    private int getFirstItemFromAlarmMassive(String arrayDetails) {
        String details = toArrayOfStrings(arrayDetails);
        Log.d(TAG, "getFirstItemFromAlarmMassive: details after convert: " + details);



        return 1;
    }

    private String toArrayOfStrings(String details) {
        String a = details.replace("[", "");
        return a.replace("]","");
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