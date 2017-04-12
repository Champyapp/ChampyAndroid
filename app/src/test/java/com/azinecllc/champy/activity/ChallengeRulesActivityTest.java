package com.azinecllc.champy.activity;

import android.app.Activity;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;

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
 * Created on 4/12/17.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class ChallengeRulesActivityTest {

    private Activity activity;


    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(ChallengeRulesActivity.class).create().get();
    }

    @Test
    public void onCreate() throws Exception {
        assertNotNull(activity);
        assertEquals("ChallengeRulesActivity", activity.getClass().getSimpleName());
    }

    @Test
    public void testForCanonicalActivityPath() throws Exception {
        assertNotNull(activity);
        assertEquals("com.azinecllc.champy.activity.ChallengeRulesActivity",
                activity.getClass().getCanonicalName());
    }

    @Test
    public void testForAppBarLayout() throws Exception {
        AppBarLayout appBarLayout = (AppBarLayout) activity.findViewById(R.id.appbar_challenge_rules);
        Assert.assertNotNull(appBarLayout);
        assertTrue(View.VISIBLE == appBarLayout.getVisibility());
        Assert.assertEquals(R.id.appbar_challenge_rules, appBarLayout.getId());
    }

    @Test
    public void testForToolbar() throws Exception {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        Assert.assertNotNull(toolbar);
        Assert.assertEquals("Challenge Rules", toolbar.getTitle());
        System.out.println("Expected: Challenge Rules | actual: " + toolbar.getTitle());
        assertTrue(R.id.toolbar == toolbar.getId());
        System.out.println("Expected: R.id.toolbar | actual: " + toolbar.getId());
    }

    @Test
    public void testForTextViewChallengeRules() throws Exception {
        TextView textView = (TextView) activity.findViewById(R.id.text_view_challenge_rules_large_text);
        assertNotNull(textView);
        assertEquals(activity.getResources().getString(R.string.challenge_rules), textView.getText());
        assertEquals(16.0f, textView.getTextSize());
        assertEquals(16, textView.getPaddingBottom());
        assertEquals(16, textView.getPaddingTop());
        assertEquals(16, textView.getPaddingLeft());
        assertEquals(16, textView.getPaddingRight());
    }


}