package com.azinecllc.champy.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by SashaKhyzhun on 2/24/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class AlarmReceiverActivityTest {

    private AlarmReceiverActivity activity;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.buildActivity(AlarmReceiverActivity.class).create().get();
    }

    @Test
    public void testForActivity() throws Exception {
        assertNotNull(activity);
        assertEquals("AlarmReceiverActivity", activity.getClass().getSimpleName());
    }


    @Test
    public void testForWakeUpLogo() throws Exception {
        ImageView imageView = (ImageView) activity.findViewById(R.id.imageViewWakeUpLogo);
        assertNotNull(imageView);

        assertTrue(R.id.imageViewWakeUpLogo == imageView.getId());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        assertEquals(32, lp.topMargin);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Test
    public void testForTextWakeUpChallenge() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.tvWakeUpChallenge);
        assertNotNull(textView);

        assertEquals("wake up \\nchallenge", textView.getText());

        assertEquals(Color.WHITE, textView.getCurrentTextColor());
    }

    @Test
    public void testForTextWAKEUP() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.wakeup_text);
        assertNotNull(textView);

        assertEquals("wake up!", textView.getText());
        assertEquals(Color.parseColor("#339997"), textView.getCurrentTextColor());
        assertEquals(60, textView.getTextSize(), 0);
        assertTrue(Gravity.CENTER == textView.getGravity());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(64, lp.topMargin);
    }

    @Test
    public void testForButtonGiveUp() throws Exception {
        ImageButton imageButton = (ImageButton) activity.findViewById(R.id.buttonWakeUpSurrender);
        assertNotNull(imageButton);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageButton.getLayoutParams();
        assertEquals(60, lp.width);
        assertEquals(60, lp.height);

        assertEquals(64, lp.bottomMargin);
        assertEquals(64, lp.leftMargin);

        assertTrue(R.id.buttonWakeUpSurrender == imageButton.getId());
        assertTrue(imageButton.isClickable());

    }

    @Test
    public void testForButtonDoneForToday() throws Exception {
        ImageButton imageButton = (ImageButton) activity.findViewById(R.id.buttonWakeUpDoneForToday);
        assertNotNull(imageButton);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageButton.getLayoutParams();
        assertEquals(60, lp.width);
        assertEquals(60, lp.height);

        assertEquals(64, lp.bottomMargin);
        assertEquals(64, lp.rightMargin);

        assertTrue(R.id.buttonWakeUpDoneForToday == imageButton.getId());
        assertTrue(imageButton.isClickable());

    }

    @Test
    public void testForPlaySound() throws Exception {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager.getActualDefaultRingtoneUri(activity, RingtoneManager.TYPE_RINGTONE);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        assertNotNull(alert);
        assertTrue(mediaPlayer.isLooping());

        AudioManager audioManager = null;
        try {
//            try {
//                mediaPlayer.setDataSource(activity.getApplicationContext(), alert);
//            } catch (IllegalArgumentException e) {
//              e.printStackTrace();
//            }
            audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            assertNotNull(audioManager);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace(); // eat it
        }

        assertNotNull(audioManager);

    }

    @Test
    public void testForGetUri() throws Exception {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager.getActualDefaultRingtoneUri(activity, RingtoneManager.TYPE_RINGTONE);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        assertNotNull(alert);
    }

}