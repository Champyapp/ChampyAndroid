package com.azinecllc.champy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.utils.OfflineMode;
import com.facebook.FacebookSdk;

import java.io.IOException;

/**
 * This is Wake-Up activity when our Alarm Receiver manager starts to ring
 */
public class AlarmReceiverActivity extends Activity implements View.OnClickListener {

    private MediaPlayer mMediaPlayer;
    private String inProgressID;
    private int requestCode;
    public Context context;
    public Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            FacebookSdk.sdkInitialize(getApplicationContext());
        } catch (RuntimeException r) {
            Log.e("AlarmReceiverActivity", "I hate facebook: " + r);
            //eat it, bitch;
        }

        requestCode = getIntent().getIntExtra("finalRequestCode", 0);
        inProgressID = getIntent().getStringExtra("finalInProgressID");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alarm_receiver);
        try {
            playSound(this, getAlarmUri());
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView tvWakeUpChallenge = (TextView) findViewById(R.id.tvWakeUpChallenge);
        TextView tvWakeUp = (TextView) findViewById(R.id.wakeup_text);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        ChallengeController cc = new ChallengeController(getApplicationContext(), AlarmReceiverActivity.this);

        switch (v.getId()) {
            case R.id.buttonWakeUpDoneForToday:
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                if (offlineMode.isConnectedToRemoteAPI(this)) {
                    try {
                        cc.doneForToday(inProgressID, String.valueOf(requestCode), v, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, (R.string.lost_inet_wake_up), Toast.LENGTH_LONG).show();
                    Toast.makeText(this, (R.string.lost_inet_wake_up), Toast.LENGTH_LONG).show();
                }

                finish();
                break;
            case R.id.buttonWakeUpSurrender:
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                if (offlineMode.isConnectedToRemoteAPI(this)) {
                    try {
                        cc.give_up(inProgressID, requestCode, intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, (R.string.lost_inet_wake_up), Toast.LENGTH_LONG).show();
                    Toast.makeText(this, (R.string.lost_inet_wake_up), Toast.LENGTH_LONG).show();
                }
                // so, we take 0 updates and then this challenge will auto surrender.
                finish();
                break;
        }
    }


    private void playSound(Context context, Uri alert) throws Exception {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setLooping(true);
        try {
            mMediaPlayer.setDataSource(context, alert);
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException | IllegalArgumentException e) {
            //System.out.println("OOPS"); //eat it, bitch
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


