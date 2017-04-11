package com.azinecllc.champy.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.azinecllc.champy.utils.CustomScrollView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * @autor SashaKhyzhun
 * Created on 4/11/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class ChallengeDetailActivityTest {

    private Activity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(ChallengeDetailActivity.class).create().get();
    }

    @Test
    public void onCreate() throws Exception {
        assertNotNull(activity);
        assertEquals("ChallengeDetailActivity", activity.getClass().getSimpleName());
    }

    @Test
    public void testForCanonicalActivityPath() throws Exception {
        assertNotNull(activity);
        assertEquals("com.azinecllc.champy.activity.ChallengeDetailActivity",
                activity.getClass().getCanonicalName());
    }

    @Test
    public void testForAppBarLayout() throws Exception {
        AppBarLayout appBarLayout = (AppBarLayout) activity.findViewById(R.id.appbar_challenge_detail);
        Assert.assertNotNull(appBarLayout);
        assertTrue(View.VISIBLE == appBarLayout.getVisibility());
        Assert.assertEquals(R.id.appbar_challenge_detail, appBarLayout.getId());
    }

    @Test
    public void testForToolbar() throws Exception {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        Assert.assertNotNull(toolbar);
        Assert.assertEquals("Champy", toolbar.getTitle());
        System.out.println("Expected: Challenges | actual: " + toolbar.getTitle());
        assertTrue(R.id.toolbar == toolbar.getId());
        System.out.println("Expected: R.id.toolbar | actual: " + toolbar.getId());
    }

    @Test
    public void testForImageChallengeIcon() throws Exception {
        ImageView imageProfile = (ImageView) activity.findViewById(R.id.image_view_challenge_icon);
        assertNotNull(imageProfile);

        assertEquals(imageProfile.getBackground(),
                activity.getResources().getDrawable(R.drawable.ic_bg_sleep_before_midnight));

        assertEquals(imageProfile.getId(), R.id.image_view_challenge_icon);
    }

    @Test
    public void testForScrollView() throws Exception {
        CustomScrollView scrollView = (CustomScrollView) activity.findViewById(R.id.scroll_view);
        assertNotNull(scrollView);
    }

    @Test
    public void testForRelativeLayoutInsideScrollView() throws Exception {
        RelativeLayout relativeLayout = (RelativeLayout) activity.findViewById(R.id.layout_content);
        assertNotNull(relativeLayout);
    }

    @Test
    public void testForLayoutStatisticsInsideRelative() throws Exception {
        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.layout_statistics);
        assertNotNull(layout);
        assertEquals(layout.getOrientation(), LinearLayout.VERTICAL);
    }

    @Test
    public void testForTextViewStreak() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_streak);
        assertNotNull(textView);
        assertEquals("Streak", textView.getText());
        assertEquals(activity.getResources().getColor(R.color.gray), textView.getCurrentTextColor());
        assertTrue(textView.getTextSize() == 18);
        assertEquals(0, textView.getPaddingLeft());
        assertEquals(0, textView.getPaddingRight());
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(33.0f, lp.weight);
    }

    @Test
    public void testForTextViewCompletion() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_completion);
        assertNotNull(textView);
        assertEquals("Completion", textView.getText());
        assertEquals(activity.getResources().getColor(R.color.gray), textView.getCurrentTextColor());
        assertTrue(textView.getTextSize() == 18);
        assertEquals(0, textView.getPaddingLeft());
        assertEquals(0, textView.getPaddingRight());
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(33.0f, lp.weight);
    }

    @Test
    public void testForTextViewDay() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_day);
        assertNotNull(textView);
        assertEquals("Day", textView.getText());
        assertEquals(activity.getResources().getColor(R.color.gray), textView.getCurrentTextColor());
        assertTrue(textView.getTextSize() == 18);
        assertEquals(0, textView.getPaddingLeft());
        assertEquals(0, textView.getPaddingRight());
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(33.0f, lp.weight);
    }

    @Test
    public void testForTextViewStreakN() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_streak_n);
        assertNotNull(textView);
        assertEquals("", textView.getText());
        assertEquals(activity.getResources().getColor(R.color.gray), textView.getCurrentTextColor());
        assertTrue(textView.getTextSize() == 36);
        assertEquals(0, textView.getPaddingLeft());
        assertEquals(0, textView.getPaddingRight());
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(33.0f, lp.weight);
    }

    @Test
    public void testForTextViewCompletionN() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_completion_n);
        assertNotNull(textView);
        assertNotNull(textView.getText());
        assertEquals(activity.getResources().getColor(R.color.gray), textView.getCurrentTextColor());
        assertTrue(textView.getTextSize() == 36);
        assertEquals(0, textView.getPaddingLeft());
        assertEquals(0, textView.getPaddingRight());
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(33.0f, lp.weight);
    }

    @Test
    public void testForTextViewDayN() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_day_n);
        assertNotNull(textView);
        assertEquals("", textView.getText());
        assertEquals(activity.getResources().getColor(R.color.gray), textView.getCurrentTextColor());
        assertTrue(textView.getTextSize() == 36);
        assertEquals(0, textView.getPaddingLeft());
        assertEquals(0, textView.getPaddingRight());
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(33.0f, lp.weight);
    }

    @Test
    public void testForRecyclerView() throws Exception {
        RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.rv_streaks);
        assertNotNull(recyclerView);

        assertEquals(0, recyclerView.getHeight());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
        assertEquals(16, lp.topMargin);
    }

    @Test
    public void testForButtonCreateChallenge() throws Exception {
        Button button = (Button) activity.findViewById(R.id.button_check_in);
        assertNotNull(button);

        assertEquals("Check in", button.getText());
        Assert.assertEquals(Color.WHITE, button.getCurrentTextColor());

        assertEquals(0, button.getPaddingBottom());
        assertEquals(0, button.getPaddingTop());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) button.getLayoutParams();
        Assert.assertEquals(16, lp.topMargin);
        Assert.assertEquals(0, lp.bottomMargin);
        Assert.assertEquals(75, lp.leftMargin);
        Assert.assertEquals(75, lp.rightMargin);
    }

    @Test
    public void testForTextViewChallengeRules() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_challenge_rules);
        assertNotNull(textView);
        assertEquals("Challenge Rules", textView.getText());
        assertEquals(Color.BLACK, textView.getCurrentTextColor());
        assertEquals(0, textView.getPaddingLeft());
        assertEquals(0, textView.getPaddingRight());
        assertEquals(2, textView.getPaddingTop());
        assertEquals(2, textView.getPaddingBottom());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
        assertEquals(32, lp.topMargin);
    }

    @Test
    public void testForLine2() throws Exception {
        View line = activity.findViewById(R.id.line2);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) line.getLayoutParams();
        Assert.assertEquals(1, lp.height);
        Assert.assertEquals(8, lp.topMargin);
        Assert.assertEquals(8, lp.bottomMargin);
    }

    @Test
    public void testForSwitchDailyReminder() throws Exception {
        Switch aSwitch = (Switch) activity.findViewById(R.id.switch_reminder);
        assertNotNull(aSwitch);
        Assert.assertEquals("Daily Reminder", aSwitch.getText());
        assertTrue(aSwitch.isChecked());
        assertEquals(Color.BLACK, aSwitch.getCurrentTextColor());
        assertEquals(16, aSwitch.getPaddingBottom());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) aSwitch.getLayoutParams();
        Assert.assertEquals(16, lp.leftMargin);
        Assert.assertEquals(16, lp.rightMargin);
    }


}