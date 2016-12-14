package com.azinecllc.champy.activity;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.ChallengeController;
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
    private String inProgressChallengeId, alarmID;
    public Context context;
    public Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        inProgressChallengeId = getIntent().getStringExtra("finalInProgressID");
        alarmID = getIntent().getStringExtra("finalAlarmID");

        Log.d(TAG, "AlarmReceiverActivity:" + "\ninProgressChallengeId: " + inProgressChallengeId + "\nalarmID: " + alarmID);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.item_alarm);
        playSound(this, getAlarmUri());
        Glide.with(this).load(R.drawable.wakeupwhite).override(130, 130).into((ImageView) findViewById(R.id.imageViewWakeUpLogo));
        Glide.with(this).load(R.drawable.wakeuptext).override(200, 170).into((ImageView) findViewById(R.id.imageViewWakeUpText));

        TextView textView = (TextView)findViewById(R.id.wakeup_text);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        textView.setTypeface(typeface);

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
                    cc.give_up(inProgressChallengeId, Integer.parseInt(alarmID));
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