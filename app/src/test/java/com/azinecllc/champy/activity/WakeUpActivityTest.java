package com.azinecllc.champy.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.text.Layout;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 1/27/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class WakeUpActivityTest {

    private Activity activity;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.buildActivity(WakeUpActivity.class).create().get();
    }

    @Test
    public void testActivityForNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void testImageViewLogo() throws Exception {
        ImageView imageViewLogo = (ImageView) activity.findViewById(R.id.imageViewLogo);
        assertNotNull(imageViewLogo);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageViewLogo.getLayoutParams();
        assertEquals(50, lp.width);
        assertEquals(50, lp.height);
        System.out.println("imageViewLogo | Expected width: 50,  Actual width: " + lp.width);
        System.out.println("imageViewLogo | Expected height: 50, Actual height: " + lp.height);
        assertEquals(R.id.imageViewLogo, imageViewLogo.getId());
        assertEquals(24, lp.topMargin);
    }

    @Test
    public void testTextViewIChallengeMySelfTo() throws Exception {
        TextView tvWakeUpChallenge = (TextView) activity.findViewById(R.id.tvChallengeToMySelf);
        assertNotNull(tvWakeUpChallenge);

        assertEquals(22, tvWakeUpChallenge.getTextSize(), 0);

        assertEquals(Color.WHITE, tvWakeUpChallenge.getTextColors().getDefaultColor());

        assertEquals("I challenge myself to", tvWakeUpChallenge.getText());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvWakeUpChallenge.getLayoutParams();
        assertEquals(10, lp.topMargin);
        assertEquals(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, tvWakeUpChallenge.getGravity());
    }

    @Test
    public void testTextViewWakeUpChallenge() throws Exception {
        TextView tvChallengeToMySelf = (TextView) activity.findViewById(R.id.tvWakeUpChallenge);
        assertNotNull(tvChallengeToMySelf);

        assertEquals(22d, tvChallengeToMySelf.getTextSize(), 0);

        assertEquals(Color.WHITE, tvChallengeToMySelf.getTextColors().getDefaultColor());

        assertEquals("wake up \\nchallenge", tvChallengeToMySelf.getText());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvChallengeToMySelf.getLayoutParams();
        assertEquals(16, lp.topMargin);

    }

    @Test
    public void testTextViewWakeUpAt() throws Exception {
        TextView goalText = (TextView) activity.findViewById(R.id.goal_text);
        assertNotNull(goalText);

        assertEquals(22, goalText.getTextSize(), 0);

        //assertEquals(R.color.colorTextAqua, goalText.getTextColors().getDefaultColor());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) goalText.getLayoutParams();
        assertEquals("WAKE UP AT", goalText.getText());

        assertEquals(15, lp.topMargin);
        //assertEquals(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, goalText.getGravity());
    }

    @Test
    public void testForTimePicker() throws Exception {
        TimePicker alarmTimePicker = (TimePicker) activity.findViewById(R.id.timePicker);
        assertNotNull(alarmTimePicker);
    }


}