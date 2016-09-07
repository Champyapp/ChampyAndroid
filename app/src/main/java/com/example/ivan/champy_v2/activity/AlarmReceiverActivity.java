package com.example.ivan.champy_v2.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.AlarmReceiver;
import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.interfaces.SingleInProgress;
import com.example.ivan.champy_v2.single_inprogress.Data;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class AlarmReceiverActivity extends Activity {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String TAG = "AlarmReceiverActivity";
    private MediaPlayer mMediaPlayer;
    public Context context;
    public Activity activity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String finalInProgressChallengeId = this.getIntent().getStringExtra("finalInProgressChallengeId");
        final int finalIntentId = this.getIntent().getIntExtra("finalIntentId", 0);
        final String stringFinalIntentId = String.valueOf(finalIntentId);

        Log.i(TAG, "finalInProgressChallengeId: " + finalInProgressChallengeId);
        Log.i(TAG, "finalIntentId: " + finalIntentId);
        Log.i(TAG, "stringFinalIntentId: " + stringFinalIntentId);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.alarm);
        playSound(this, getAlarmUri());
        Glide.with(this).load(R.drawable.wakeupwhite).override(130, 130).into((ImageView) findViewById(R.id.imageViewWakeUpLogo));
        Glide.with(this).load(R.drawable.wakeuptext).override(200, 170).into((ImageView) findViewById(R.id.imageViewWakeUpText));

        TextView textView = (TextView)findViewById(R.id.wakeup_text);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        textView.setTypeface(typeface); // закоментити
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ImageButton buttonWakeUpDoneForToday = (ImageButton) findViewById(R.id.buttonWakeUpDoneForToday);
        final ChallengeController cc = new ChallengeController(getApplicationContext(), AlarmReceiverActivity.this, 0, 0, 0);

        //final String finalChallengeId = challengeId;
        buttonWakeUpDoneForToday.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mMediaPlayer.stop();
                try {
                    cc.doneForToday(finalInProgressChallengeId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
                return false;
            }
        });

        //playSound(this, getAlarmUri());


        ImageButton buttonWakeUpSurrender = (ImageButton) findViewById(R.id.buttonWakeUpSurrender);
        buttonWakeUpSurrender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mMediaPlayer.stop();
                try {
                    cc.give_up(finalInProgressChallengeId, finalIntentId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
                return false;
            }
        });

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


    // берем звук для будильника
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