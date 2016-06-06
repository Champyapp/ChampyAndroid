package com.example.ivan.champy_v2;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void testInitAll() {
        onView(withId(R.id.textViewChallenges)).check(matches(withText("Challenges")));
        onView(withId(R.id.textViewWins)).check(matches(withText("Wins")));
        onView(withId(R.id.textViewTotal)).check(matches(withText("Total")));

        onView(withId(R.id.imageView_challenges_logo)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.imageView_wins_logo)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.imageView_total_logo)).check(matches(ViewMatchers.isDisplayed()));

        onView(withId(R.id.fabPlus)).check(matches(isDisplayed()));

    }


    @Test // press the fab
    public void testTapFabForOpen() {
        onView(withId(R.id.fabPlus)).perform(click());
    }

    @Test // press the fab
    public void testTapFabForClose() {
        onView(withId(R.id.fabPlus)).perform(click());
        onView(withId(R.id.fabPlus)).perform(click());
    }



}