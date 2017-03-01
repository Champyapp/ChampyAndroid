package com.azinecllc.champy.fragment;

import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.DuelActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static com.facebook.FacebookSdk.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by SashaKhyzhun on 2/28/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class ChallengeDuelFragmentTest {

    private View view;
    private ChallengeDuelFragment fragment;
    private DuelActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(DuelActivity.class).create().get();
        fragment = new ChallengeDuelFragment();
        view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_card_self_and_duel, null);
    }

    @Test
    public void testForActivity() throws Exception {
        assertNotNull(activity);
        assertEquals("DuelActivity", activity.getClass().getSimpleName());
    }

    @Test
    public void testForFragment() throws Exception {
        assertNotNull(fragment);
        assertEquals("ChallengeDuelFragment", fragment.getClass().getSimpleName());
    }

    @Test
    public void testForGoalText() throws Exception {
        TextView goal = (TextView) view.findViewById(R.id.goal_text);
        assertNotNull(goal);

        assertTrue(View.VISIBLE == goal.getVisibility());
        assertEquals(20, goal.getPaddingTop());
        assertEquals(Gravity.CENTER, goal.getGravity());
        assertTrue(R.id.goal_text == goal.getId());
        assertEquals(Color.parseColor("#339997"), goal.getCurrentTextColor());
    }

    @Test
    public void testForEditTextGoal() throws Exception {
        EditText editText = (EditText) view.findViewById(R.id.et_goal);
        assertNotNull(editText);
        assertEquals(Color.parseColor("#339997"), editText.getCurrentTextColor());
        assertEquals(Color.parseColor("#d7d7d7"), editText.getCurrentHintTextColor());
        assertTrue(editText.getHint().equals("Enter your challenge"));
        assertEquals(22, editText.getPaddingTop());
        assertEquals(Gravity.CENTER, editText.getGravity());
        assertTrue(View.INVISIBLE == editText.getVisibility());
    }

    @Test
    public void testForViewLine() throws Exception {
        View line = view.findViewById(R.id.line);
        assertNotNull(line);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) line.getLayoutParams();

        assertEquals(5, lp.topMargin);
        assertEquals(5, lp.leftMargin);
        assertEquals(5, lp.rightMargin);

        assertEquals(1, lp.height);


        assertTrue(View.VISIBLE == line.getVisibility());
    }

    @Test
    public void testForTextViewEveryDayForTheNext() throws Exception {
        TextView textView = (TextView) view.findViewById(R.id.tvEveryDaySelf);
        assertNotNull(textView);
        assertEquals("everyday for the next", textView.getText());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(25, lp.topMargin);
        assertTrue(R.id.tvEveryDaySelf == textView.getId());
    }

    @Test
    public void testForTextView21Days() throws Exception {
        TextView textView = (TextView) view.findViewById(R.id.days_text);
        assertNotNull(textView);
        assertEquals("21 DAYS", textView.getText());
        assertEquals(Color.parseColor("#339997"), textView.getCurrentTextColor());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(15, lp.leftMargin);
        assertEquals(15, lp.rightMargin);
        assertEquals(25, textView.getPaddingTop());
        assertEquals(100, textView.getLayoutParams().width);

        assertTrue(R.id.days_text == textView.getId());
    }

    @Test
    public void testForImageButtonPlus() throws Exception {
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.imageButtonPlus);
        assertNotNull(imageButton);
        assertTrue(View.INVISIBLE == imageButton.getVisibility());
        assertTrue(imageButton.isClickable());
        assertTrue(R.id.imageButtonPlus == imageButton.getId());
        assertEquals(35, imageButton.getLayoutParams().height);
        assertEquals(35, imageButton.getLayoutParams().width);
    }

    @Test
    public void testForImageButtonMinus() throws Exception {
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.imageButtonMinus);
        assertNotNull(imageButton);
        assertTrue(View.INVISIBLE == imageButton.getVisibility());
        assertTrue(imageButton.isClickable());
        assertTrue(R.id.imageButtonMinus == imageButton.getId());
        assertEquals(35, imageButton.getLayoutParams().height);
        assertEquals(35, imageButton.getLayoutParams().width);
    }

    @Test
    public void testForTextViewDays() throws Exception {
        TextView textView = (TextView) view.findViewById(R.id.textDays);
        assertNotNull(textView);
        assertEquals("Days", textView.getText());
        assertEquals(Color.parseColor("#339997"), textView.getCurrentTextColor());
        assertTrue(Gravity.CENTER == textView.getGravity());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(50, textView.getLayoutParams().width);

        assertTrue(R.id.textDays == textView.getId());
    }

    @Test
    public void testForTextView21() throws Exception {
        TextView textView = (TextView) view.findViewById(R.id.et_days);
        assertNotNull(textView);
        assertEquals("21", textView.getText());
        assertEquals(Color.parseColor("#339997"), textView.getCurrentTextColor());
        assertTrue(Gravity.CENTER == textView.getGravity());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(50, textView.getLayoutParams().width);

        assertTrue(R.id.et_days == textView.getId());
    }


}