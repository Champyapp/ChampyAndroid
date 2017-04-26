package com.azinecllc.champy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;

import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
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
public class ChallengeCreateDetailsActivityTest {

    private Activity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(ChallengeCreateDetailsActivity.class).create().get();
    }

    @Test
    public void onCreate() throws Exception {
        assertNotNull(activity);
        assertEquals("ChallengeCreateDetailsActivity", activity.getClass().getSimpleName());
    }

    @Test
    public void testForCanonicalActivityPath() throws Exception {
        assertNotNull(activity);
        assertEquals("com.azinecllc.champy.activity.ChallengeCreateDetailsActivity",
                activity.getClass().getCanonicalName());
    }

    @Test
    public void testForAppBarLayout() throws Exception {
        AppBarLayout appBarLayout = (AppBarLayout) activity.findViewById(R.id.appbar_challenge_create_detail);
        Assert.assertNotNull(appBarLayout);
        assertTrue(View.VISIBLE == appBarLayout.getVisibility());
        Assert.assertEquals(R.id.appbar_challenge_create_detail, appBarLayout.getId());
    }

    @Test
    public void testForToolbar() throws Exception {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        Assert.assertNotNull(toolbar);
        Assert.assertEquals("New Challenge", toolbar.getTitle());
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
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageProfile.getLayoutParams();
//        Assert.assertEquals(100, lp.height);
//        Assert.assertEquals(100, lp.width);
//        assertFalse(imageProfile.isClickable());
    }

    @Test
    public void testForTextViewChallengeName() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_challenge_name);
        assertNotNull(textView);
        Assert.assertEquals(Color.WHITE, textView.getCurrentTextColor());
        assertEquals("", textView.getText());
        assertTrue(textView.getTextSize() == 30);

        assertEquals(16, textView.getPaddingLeft());
        assertEquals(16, textView.getPaddingRight());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(8, lp.topMargin);
    }

    @Test
    public void testForTextViewChallengeDescription() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_challenge_description);
        assertNotNull(textView);
        //Assert.assertEquals(Color.WHITE, textView.getCurrentTextColor());
        assertEquals("Get to bed before midnight to have full 7 to 8 hours of sleep.", textView.getText());
        assertTrue(textView.getTextSize() == 18);

        assertEquals(16, textView.getPaddingLeft());
        assertEquals(16, textView.getPaddingRight());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(8, lp.topMargin);
    }

    @Test
    public void testForTextViewChallengeRules() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_challenge_rules);
        assertNotNull(textView);
        assertEquals("Challenge Rules", textView.getText());
        assertTrue(textView.getTextSize() == 18);

        assertEquals(0, textView.getPaddingLeft());
        assertEquals(0, textView.getPaddingRight());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(8, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
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
    public void testForTextViewCChallengeAFriends() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_challenge_a_friend);
        assertNotNull(textView);
        Assert.assertEquals(Color.BLACK, textView.getCurrentTextColor());
        assertEquals("Challenge a Friend", textView.getText());

        assertEquals(2, textView.getPaddingBottom());
        assertEquals(2, textView.getPaddingTop());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
    }

    @Test
    public void testForLine3() throws Exception {
        View line = activity.findViewById(R.id.line3);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) line.getLayoutParams();
        Assert.assertEquals(1, lp.height);
        Assert.assertEquals(8, lp.topMargin);
        Assert.assertEquals(8, lp.bottomMargin);
    }

    @Test
    public void testForSwitchNewChallengeRequests() throws Exception {
        Switch aSwitch = (Switch) activity.findViewById(R.id.switch_reminder);
        assertNotNull(aSwitch);
        Assert.assertEquals("Daily Reminder", aSwitch.getText());
        assertTrue(aSwitch.isChecked());


        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) aSwitch.getLayoutParams();
        Assert.assertEquals(16, lp.leftMargin);
        Assert.assertEquals(16, lp.rightMargin);
    }

    @Test
    public void testForLine4() throws Exception {
        View line = activity.findViewById(R.id.line4);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) line.getLayoutParams();
        Assert.assertEquals(1, lp.height);
        Assert.assertEquals(8, lp.topMargin);
        Assert.assertEquals(8, lp.bottomMargin);
    }

    @Test
    public void testForButtonCreateChallenge() throws Exception {
        Button button = (Button) activity.findViewById(R.id.button_create_challenge);
        assertNotNull(button);

        assertEquals("Create Challenge", button.getText());
        Assert.assertEquals(Color.WHITE, button.getCurrentTextColor());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) button.getLayoutParams();
        Assert.assertEquals(8, lp.topMargin);
        Assert.assertEquals(16, lp.bottomMargin);
        Assert.assertEquals(36, lp.leftMargin);
        Assert.assertEquals(36, lp.rightMargin);

    }

    @Test
    public void testForImageViewBackground() throws Exception {
        ImageView imageView = (ImageView) activity.findViewById(R.id.image_view_background);
        assertNotNull(imageView);
        assertEquals(0.8f, imageView.getAlpha());
    }

    @Test
    public void testForImageViewChallengeIcon() throws Exception {
        ImageView imageView = (ImageView) activity.findViewById(R.id.image_view_challenge_icon);
        assertNotNull(imageView);

//        assertEquals(200, imageView.getWidth());
        assertEquals(1f, imageView.getAlpha());
        assertEquals(1f, imageView.getScaleX());
        assertEquals(1f, imageView.getScaleY());
    }

    @Test
    public void testForTextViewNice() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_nice);
        assertNotNull(textView);
        Assert.assertEquals(Color.WHITE, textView.getCurrentTextColor());
        assertEquals("Nice!", textView.getText());

        assertEquals(36.0f, textView.getTextSize());

        assertEquals(16, textView.getPaddingLeft());
        assertEquals(16, textView.getPaddingRight());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(0, lp.leftMargin);
        assertEquals(0, lp.rightMargin);
    }

    @Test
    public void testForTextViewCreateChallengeLargeText() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_create_challenge_large_text);
        assertNotNull(textView);
        Assert.assertEquals(Color.WHITE, textView.getCurrentTextColor());
        assertEquals(activity.getResources().getString(R.string.challenge_created_large_text), textView.getText());

        assertEquals(20.0f, textView.getTextSize());

        assertEquals(16, textView.getPaddingLeft());
        assertEquals(16, textView.getPaddingRight());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(0, lp.leftMargin);
        assertEquals(0, lp.rightMargin);
        assertEquals(20, lp.topMargin);
    }

    @Test
    public void testForTextViewGotIt() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_got_it);
        assertNotNull(textView);
        Assert.assertEquals(Color.WHITE, textView.getCurrentTextColor());
        assertEquals("Got it", textView.getText());

        assertEquals(22.0f, textView.getTextSize());

        assertEquals(16, textView.getPaddingLeft());
        assertEquals(16, textView.getPaddingRight());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(0, lp.leftMargin);
        assertEquals(0, lp.rightMargin);
        assertEquals(0, lp.topMargin);
        assertEquals(36, lp.bottomMargin);
    }

    @Test
    public void testForTextViewShare() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_share);
        assertNotNull(textView);
        Assert.assertEquals(Color.WHITE, textView.getCurrentTextColor());
        assertEquals("Share", textView.getText());
        assertEquals(0, textView.getHeight());

        assertEquals(22.0f, textView.getTextSize());

        assertEquals(0, textView.getPaddingLeft());
        assertEquals(0, textView.getPaddingRight());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(0, lp.leftMargin);
        assertEquals(0, lp.rightMargin);
        assertEquals(0, lp.topMargin);
        assertEquals(16, lp.bottomMargin);
    }

//    @Test
//    public void onClickChallengeRules() throws Exception {
//        Intent intent = new Intent(activity, ChallengeRulesActivity.class);
//        assertNotNull(intent);
//        // ????!?!?!?!?!/!/!!@E!}@LE>D}>{!L>F:"Q
//        activity.startActivity(intent);
//    }
//
//    @Test
//    public void onClickChallengeAFriend() throws Exception {
//        Intent intent = new Intent(activity, ChallengesActivity.class);
//        System.out.println(intent.getExtras());
//        System.out.println(activity.getIntent().getExtras());
//    }

    @Test
    public void onClickGotIt() throws Exception {
        enableClicks();
        Switch aSwitch = (Switch) activity.findViewById(R.id.switch_reminder);
        TextView tvChallengeRules = (TextView) activity.findViewById(R.id.text_view_challenge_rules);
        TextView tvChallengeAFriend = (TextView) activity.findViewById(R.id.text_view_challenge_a_friend);

        assertTrue(aSwitch.isEnabled());
        assertTrue(tvChallengeRules.isEnabled());
        assertTrue(tvChallengeAFriend.isEnabled());
    }

    @Test
    public void onClickShare() throws Exception {
        enableClicks();
        Switch aSwitch = (Switch) activity.findViewById(R.id.switch_reminder);
        TextView tvChallengeRules = (TextView) activity.findViewById(R.id.text_view_challenge_rules);
        TextView tvChallengeAFriend = (TextView) activity.findViewById(R.id.text_view_challenge_a_friend);

        assertTrue(aSwitch.isEnabled());
        assertTrue(tvChallengeRules.isEnabled());
        assertTrue(tvChallengeAFriend.isEnabled());
    }

    @Test
    public void onClickCreateChallenge() throws Exception {
        disableClick();
        Switch aSwitch = (Switch) activity.findViewById(R.id.switch_reminder);
        TextView tvChallengeRules = (TextView) activity.findViewById(R.id.text_view_challenge_rules);
        TextView tvChallengeAFriend = (TextView) activity.findViewById(R.id.text_view_challenge_a_friend);

        assertFalse(aSwitch.isEnabled());
        assertFalse(tvChallengeRules.isEnabled());
        assertFalse(tvChallengeAFriend.isEnabled());
    }

    @Test
    public void onBackPressed() throws Exception {
        activity.finish();
        assertTrue(activity.isFinishing());
    }

    @Test
    public void enableChildClick() throws Exception {
        RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.layout_create_challenge);
        assertNotNull(layout);

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(true);
            assertTrue(child.isEnabled());
        }

        RelativeLayout layoutCreated = (RelativeLayout) activity.findViewById(R.id.layout_challenge_created);
        assertNotNull(layoutCreated);

    }

    @Test
    public void disableChildClick() throws Exception {
        RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.layout_create_challenge);
        assertNotNull(layout);

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(false);
            assertFalse(child.isEnabled());
        }

        RelativeLayout layoutCreated = (RelativeLayout) activity.findViewById(R.id.layout_challenge_created);
        assertNotNull(layoutCreated);
    }


    private void enableClicks() {
        RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.layout_create_challenge);
        assertNotNull(layout);

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(true);
            assertTrue(child.isEnabled());
        }

        RelativeLayout layoutCreated = (RelativeLayout) activity.findViewById(R.id.layout_challenge_created);
        assertNotNull(layoutCreated);

    }

    private void disableClick() {
        RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.layout_create_challenge);
        assertNotNull(layout);

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(false);
            assertFalse(child.isEnabled());
        }

        RelativeLayout layoutCreated = (RelativeLayout) activity.findViewById(R.id.layout_challenge_created);
        assertNotNull(layoutCreated);
    }

}