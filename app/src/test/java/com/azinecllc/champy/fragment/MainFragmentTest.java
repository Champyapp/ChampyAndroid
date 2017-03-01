package com.azinecllc.champy.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.utils.SessionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import static com.facebook.FacebookSdk.getApplicationContext;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by SashaKhyzhun on 3/1/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MainFragmentTest {

    private View view;
    private MainFragment fragment;
    private MainActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
        fragment = new MainFragment();
        view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.fragment_main, null);
    }

    @After
    public void finishComponentTesting() throws Exception {
        resetSingleton(DBHelper.class, "instance");
        resetSingleton(SessionManager.class, "instance");
        resetSingleton(CHCheckPendingDuels.class, "instance");
    }

    @Test
    public void testForActivity() throws Exception {
        assertNotNull(activity);
        assertEquals("MainActivity", activity.getClass().getSimpleName());
    }

    @Test
    public void testForFragment() throws Exception {
        assertNotNull(fragment);
        assertEquals("MainFragment", fragment.getClass().getSimpleName());
    }

    @Test
    public void testForSessionManagerData() throws Exception {
        SessionManager sessionManager = SessionManager.getInstance(fragment.getContext());
        assertNotNull(sessionManager);

        System.out.println("session manager is not null");

        String challenges = sessionManager.getChampyOptions().get("challenges");
        String wins = sessionManager.getChampyOptions().get("wins");
        String total = sessionManager.getChampyOptions().get("total");

        int challengesInteger = (!challenges.equals("")) ? Integer.parseInt(challenges) : 0;
        int winsInteger = (!wins.equals("")) ? Integer.parseInt(wins) : 0;
        int totalInteger = (!total.equals("")) ? Integer.parseInt(total) : 0;

        System.out.println("challenges : " + challengesInteger
                + "\nwins       : " + winsInteger
                + "\ntotal      : " + totalInteger);

    }

    @Test
    public void testForTypeface() throws Exception {
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/bebasneue.ttf");
        assertNotNull(typeface);
        System.out.println("typeface is not null");
    }

    @Test
    public void testForWelcomeText() throws Exception {
        TextView textViewWelcome = (TextView) view.findViewById(R.id.welcomeUserName);
        assertNotNull(textViewWelcome);

        assertEquals(20, textViewWelcome.getTextSize(), 0);

        assertEquals(10, textViewWelcome.getPaddingTop());
        assertEquals(10, textViewWelcome.getPaddingBottom());

        assertEquals(Gravity.CENTER, textViewWelcome.getGravity());

        assertTrue(R.id.welcomeUserName == textViewWelcome.getId());
        assertTrue(View.VISIBLE == textViewWelcome.getVisibility());

        assertEquals("", textViewWelcome.getText());
    }

    @Test
    public void testForCircleInProgress() throws Exception {
        ImageView ivInProgress = (ImageView) view.findViewById(R.id.imageView_challenges_animation);
        assertNotNull(ivInProgress);
        System.out.println(ivInProgress);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivInProgress.getLayoutParams();
        assertNotNull(lp);

        assertEquals(90, lp.height);
        assertEquals(90, lp.width);


        assertEquals(16, lp.leftMargin);

        assertTrue(R.id.imageView_challenges_animation == ivInProgress.getId());
    }

    @Test
    public void testForCircleWins() throws Exception {
        ImageView ivWins = (ImageView) view.findViewById(R.id.imageView_wins_animation);
        assertNotNull(ivWins);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivWins.getLayoutParams();
        assertNotNull(lp);

        assertEquals(90, lp.height);
        assertEquals(90, lp.width);

        assertTrue(R.id.imageView_wins_animation == ivWins.getId());
        //assertEquals(Gravity.CENTER_HORIZONTAL, ivWins.getLayoutParams());
    }

    @Test
    public void testForCircleTotal() throws Exception {
        ImageView ivTotal = (ImageView) view.findViewById(R.id.imageView_total_animation);
        assertNotNull(ivTotal);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivTotal.getLayoutParams();
        assertNotNull(lp);

        assertEquals(90, lp.height);
        assertEquals(90, lp.width);

        assertEquals(35, lp.leftMargin);

        assertTrue(R.id.imageView_total_animation == ivTotal.getId());
    }

    @Test
    public void testForCounterInProgress() throws Exception {
        TextView tvCounterInProgress = (TextView) view.findViewById(R.id.textViewChallengesCounter);
        assertNotNull(tvCounterInProgress);

        assertEquals(28, tvCounterInProgress.getTextSize(), 0);

        assertEquals(Color.WHITE, tvCounterInProgress.getCurrentTextColor());
        assertEquals(Color.WHITE, tvCounterInProgress.getTextColors().getDefaultColor());
        assertTrue(R.id.textViewChallengesCounter == tvCounterInProgress.getId());
        assertEquals(Gravity.CENTER, tvCounterInProgress.getGravity());
    }

    @Test
    public void testForCounterWins() throws Exception {
        TextView tvCounterWins = (TextView) view.findViewById(R.id.textViewWinsCounter);
        assertNotNull(tvCounterWins);

        assertEquals(28, tvCounterWins.getTextSize(), 0);

        assertEquals(Color.WHITE, tvCounterWins.getCurrentTextColor());
        assertEquals(Color.WHITE, tvCounterWins.getTextColors().getDefaultColor());
        assertTrue(R.id.textViewWinsCounter == tvCounterWins.getId());
        assertEquals(Gravity.CENTER, tvCounterWins.getGravity());
    }

    @Test
    public void testForCounterTotal() throws Exception {
        TextView tvCounterTotal = (TextView) view.findViewById(R.id.textViewTotalCounter);
        assertNotNull(tvCounterTotal);

        assertEquals(28, tvCounterTotal.getTextSize(), 0);

        assertEquals(Color.WHITE, tvCounterTotal.getCurrentTextColor());
        assertEquals(Color.WHITE, tvCounterTotal.getTextColors().getDefaultColor());
        assertTrue(R.id.textViewTotalCounter == tvCounterTotal.getId());
        assertEquals(Gravity.CENTER, tvCounterTotal.getGravity());
    }

    @Test
    public void testForLogoInProgress() throws Exception {
        ImageView ivInProgress = (ImageView) view.findViewById(R.id.imageView_challenges_logo);
        assertNotNull(ivInProgress);
        assertTrue(R.id.imageView_challenges_logo == ivInProgress.getId());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivInProgress.getLayoutParams();
        assertEquals(20, lp.height);
        assertEquals(20, lp.width);

        assertEquals(10, lp.bottomMargin);
    }

    @Test
    public void testForLogoWins() throws Exception {
        ImageView ivWins = (ImageView) view.findViewById(R.id.imageView_wins_logo);
        assertNotNull(ivWins);
        assertTrue(R.id.imageView_wins_logo == ivWins.getId());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivWins.getLayoutParams();
        assertEquals(20, lp.height);
        assertEquals(20, lp.width);
    }

    @Test
    public void testForLogoTotal() throws Exception {
        ImageView ivTotal = (ImageView) view.findViewById(R.id.imageView_total_logo);
        assertNotNull(ivTotal);
        assertTrue(R.id.imageView_total_logo == ivTotal.getId());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivTotal.getLayoutParams();
        assertEquals(20, lp.height);
        assertEquals(20, lp.width);
    }

    @Test
    public void testForTextViewInProgress() throws Exception {
        TextView textViewInProgress = (TextView) view.findViewById(R.id.textViewChallenges);
        assertNotNull(textViewInProgress);
        assertTrue(R.id.textViewChallenges == textViewInProgress.getId());

        assertEquals(16, textViewInProgress.getTextSize(), 0);

        assertEquals(Color.WHITE, textViewInProgress.getCurrentTextColor());
        assertEquals(Color.WHITE, textViewInProgress.getTextColors().getDefaultColor());
        assertEquals(5, textViewInProgress.getPaddingTop());
        assertEquals(5, textViewInProgress.getPaddingBottom());
        assertEquals(Gravity.CENTER, textViewInProgress.getGravity());

    }

    @Test
    public void testForTextViewWins() throws Exception {
        TextView textViewWins = (TextView) view.findViewById(R.id.textViewWins);
        assertNotNull(textViewWins);
        assertTrue(R.id.textViewWins == textViewWins.getId());

        assertEquals(16, textViewWins.getTextSize(), 0);

        assertEquals(Color.WHITE, textViewWins.getCurrentTextColor());
        assertEquals(Color.WHITE, textViewWins.getTextColors().getDefaultColor());
        assertEquals(5, textViewWins.getPaddingTop());
        assertEquals(5, textViewWins.getPaddingBottom());
        assertEquals(Gravity.CENTER, textViewWins.getGravity());
    }

    @Test
    public void testForTextViewTotal() throws Exception {
        TextView textViewTotal = (TextView) view.findViewById(R.id.textViewTotal);
        assertNotNull(textViewTotal);
        assertTrue(R.id.textViewTotal == textViewTotal.getId());

        assertEquals(16, textViewTotal.getTextSize(), 0);

        assertEquals(Color.WHITE, textViewTotal.getCurrentTextColor());
        assertEquals(Color.WHITE, textViewTotal.getTextColors().getDefaultColor());
        assertEquals(5, textViewTotal.getPaddingTop());
        assertEquals(5, textViewTotal.getPaddingBottom());
        assertEquals(Gravity.CENTER, textViewTotal.getGravity());
    }

    @Test
    public void testForCardsLayout() throws Exception {
        RelativeLayout cards = (RelativeLayout) view.findViewById(R.id.cards);
        assertNotNull(cards);
        assertEquals(10, cards.getPaddingTop());
        assertTrue(R.id.cards == cards.getId());
    }

    @Test
    public void testForFabSelf() throws Exception {
        FloatingActionButton fabSelf = (FloatingActionButton) view.findViewById(R.id.fabSelf);
        assertNotNull(fabSelf);
        assertTrue(View.INVISIBLE == fabSelf.getVisibility());
        assertTrue(R.id.fabSelf == fabSelf.getId());
        assertEquals(ImageView.ScaleType.CENTER, fabSelf.getScaleType());
    }

    @Test
    public void testForFabDuel() throws Exception {
        FloatingActionButton fabDuel = (FloatingActionButton) view.findViewById(R.id.fabDuel);
        assertNotNull(fabDuel);
        assertTrue(View.INVISIBLE == fabDuel.getVisibility());
        assertTrue(R.id.fabDuel == fabDuel.getId());
        assertEquals(ImageView.ScaleType.CENTER, fabDuel.getScaleType());
    }

    @Test
    public void testForFabWake() throws Exception {
        FloatingActionButton fabWake = (FloatingActionButton) view.findViewById(R.id.fabWake);
        assertNotNull(fabWake);
        assertTrue(View.INVISIBLE == fabWake.getVisibility());
        assertTrue(R.id.fabWake == fabWake.getId());
        assertEquals(ImageView.ScaleType.CENTER, fabWake.getScaleType());
    }

    @Test
    public void testForFabPlus() throws Exception {
        FloatingActionButton fabPlus = (FloatingActionButton) view.findViewById(R.id.fabPlus);
        assertNotNull(fabPlus);
        assertTrue(View.VISIBLE == fabPlus.getVisibility());
        assertTrue(R.id.fabPlus == fabPlus.getId());
        assertEquals(ImageView.ScaleType.CENTER, fabPlus.getScaleType());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fabPlus.getLayoutParams();
        System.out.println(lp.leftMargin + " left");
        System.out.println(lp.rightMargin + " right");
        System.out.println(lp.topMargin + " top");
        System.out.println(lp.bottomMargin + " bot");
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
        assertEquals(16, lp.bottomMargin);
    }

    // after each test we need to destroy singletons
    public void resetSingleton(Class clazz, String fieldName) {
        Field instance;
        try {
            instance = clazz.getDeclaredField(fieldName);
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }


}
