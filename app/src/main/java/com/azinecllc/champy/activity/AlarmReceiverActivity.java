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
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.FacebookSdk;

import java.io.IOException;

/**
 * This is Wake-Up activity when our item_alarm manager starts ring
 */
public class AlarmReceiverActivity extends Activity implements View.OnClickListener {

    public final String TAG = "AlarmReceiverActivity";
    private MediaPlayer mMediaPlayer;
    private ChallengeController cc;
    private String progressID, alarmID, details;
    public Context context;
    public Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        alarmID = getIntent().getStringExtra("finalAlarmID");
        details = getIntent().getStringExtra("finalDetails");
        progressID = getIntent().getStringExtra("finalInProgressID");

        Log.d(TAG, "\nalarmID: " + alarmID + "\ndetails: " + details + "\nprogressID: " + progressID);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.item_alarm);
        playSound(this, getAlarmUri());

        final TextView tvWakeUpChallenge = (TextView) findViewById(R.id.tvWakeUpChallenge);
        final TextView tvWakeUp = (TextView) findViewById(R.id.wakeup_text);
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

        ImageButton buttonDoneForToday = (ImageButton) findViewById(R.id.buttonWakeUpDoneForToday);
        ImageButton buttonSurrender = (ImageButton) findViewById(R.id.buttonWakeUpSurrender);

        buttonDoneForToday.setOnClickListener(this);
        buttonSurrender.setOnClickListener(this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, RoleControllerActivity.class);
        OfflineMode offlineMode = OfflineMode.getInstance();
        switch (v.getId()) {
            case R.id.buttonWakeUpDoneForToday:
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }

                if (offlineMode.isConnectedToRemoteAPI(this)) {
                    try {
                        cc.doneForToday(progressID, details, alarmID, intent);
                        cc.setNewAlarmClock(details, alarmID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                finish();
                break;

            case R.id.buttonWakeUpSurrender:
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                if (offlineMode.isConnectedToRemoteAPI(this)) {
                    try {
                        cc.give_up(progressID, Integer.parseInt(alarmID), intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
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
            alert = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        return alert;
    }

}


