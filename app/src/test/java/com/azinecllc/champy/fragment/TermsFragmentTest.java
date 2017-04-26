package com.azinecllc.champy.fragment;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.ChallengesActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 3/2/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class TermsFragmentTest {

    private View view;
    private TermsFragment fragment;
    private ChallengesActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(ChallengesActivity.class).create().get();
        fragment = new TermsFragment();
        view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.fragment_privacy, null);
    }

    @Test
    public void testForActivity() throws Exception {
        assertNotNull(activity);
        assertEquals("ChallengesActivity", activity.getClass().getSimpleName());
    }

    @Test
    public void testForFragment() throws Exception {
        assertNotNull(fragment);
        assertEquals("TermsFragment", fragment.getClass().getSimpleName());
    }

    @Test
    public void testForLayout() throws Exception {
        assertNotNull(view);
        assertEquals(16, view.getPaddingRight());
        assertEquals(16, view.getPaddingLeft());

        assertEquals(16, view.getPaddingTop());
        assertEquals(16, view.getPaddingBottom());
    }

    @Test
    public void testForScrollView() throws Exception {
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView1);
        assertNotNull(scrollView);
    }

    @Test
    public void testForTextViewTerms() throws Exception {
        TextView textView = (TextView) view.findViewById(R.id.textViewPrivacyPolice);
        assertNotNull(textView);
        assertTrue(R.id.textViewPrivacyPolice == textView.getId());
        assertTrue(View.VISIBLE == textView.getVisibility());
        assertEquals(Color.BLACK, textView.getCurrentTextColor());
    }

    @Test
    public void testForProgressBar() throws Exception {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        assertNotNull(progressBar);
    }

}