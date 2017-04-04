package com.azinecllc.champy.activity;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.azinecllc.champy.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RoleControllerActivityTest {

    @Rule
    public ActivityTestRule<RoleControllerActivity> mActivityTestRule = new ActivityTestRule<>(RoleControllerActivity.class);

    @Test
    public void roleControllerActivityTest() {
        ViewInteraction loginButton = onView(
                allOf(withId(R.id.login_button),
                        withText("Log in with Facebook"),
                        withContentDescription("Logged in as: %1$s"),
                        isDisplayed()));

        loginButton.perform(click());

        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Open"),
                        withParent(allOf(withId(R.id.toolbar),
                                withParent(withId(R.id.appbar_challenge_detail)))),
                        isDisplayed()));

        imageButton.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text),
                        withText("Settings"),
                        isDisplayed()));

        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.textViewLogout),
                        withText("Logout"),
                        withParent(allOf(withId(R.id.settings_layout),
                                withParent(withId(R.id.scrollView))))));

        appCompatTextView.perform(scrollTo(), click());

    }

}
