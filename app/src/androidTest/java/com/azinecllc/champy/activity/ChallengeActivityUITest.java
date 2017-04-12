package com.azinecllc.champy.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.azinecllc.champy.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ChallengeActivityUITest {

    @Rule
    public ActivityTestRule<ChallengeActivity> mActivityTestRule = new ActivityTestRule<>(ChallengeActivity.class);

    @Test
    public void challengeActivityTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        ViewInteraction textView = onView(
                allOf(withText("Challenges"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.appbar_challenge_detail),
                                                0)),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("Challenges")));

        ViewInteraction textView2 = onView(
                allOf(withText("Challenges"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.appbar_challenge_detail),
                                                0)),
                                1),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Open"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.appbar_challenge_detail),
                                                0)),
                                0),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction viewGroup = onView(
                allOf(withId(R.id.toolbar),
                        childAtPosition(
                                allOf(withId(R.id.appbar_challenge_detail),
                                        childAtPosition(
                                                IsInstanceOf.instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                0),
                        isDisplayed()));
        viewGroup.check(matches(isDisplayed()));

        ViewInteraction actionBar$Tab = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.tab_layout_main),
                                0),
                        0),
                        isDisplayed()));
        actionBar$Tab.check(matches(isDisplayed()));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.tab),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.tab),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        textView4.check(matches(isDisplayed()));

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.item_parent_layout),
                        childAtPosition(
                                allOf(withId(R.id.recycler_view),
                                        childAtPosition(
                                                withId(R.id.swipe_to_refresh),
                                                0)),
                                0),
                        isDisplayed()));
        relativeLayout.check(matches(isDisplayed()));

        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.card_layout),
                        childAtPosition(
                                allOf(withId(R.id.item_parent_layout),
                                        childAtPosition(
                                                withId(R.id.recycler_view),
                                                0)),
                                0),
                        isDisplayed()));
        relativeLayout2.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.tv_challenge_name), withText("Drink a beer with Chuck Norris"),
                        childAtPosition(
                                allOf(withId(R.id.card_layout),
                                        childAtPosition(
                                                withId(R.id.item_parent_layout),
                                                0)),
                                0),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

        ViewInteraction imageButton2 = onView(
                allOf(withId(R.id.fabPlus),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.drawer_layout),
                                        0),
                                2),
                        isDisplayed()));
        imageButton2.check(matches(isDisplayed()));

        ViewInteraction imageButton3 = onView(
                allOf(withId(R.id.fabPlus),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.drawer_layout),
                                        0),
                                2),
                        isDisplayed()));
        imageButton3.check(matches(isDisplayed()));


        ViewInteraction viewPager = onView(
                allOf(withId(R.id.view_pager_main), isDisplayed()));
        viewPager.perform(swipeLeft());

        ViewInteraction viewPager2 = onView(
                allOf(withId(R.id.view_pager_main), isDisplayed()));
        viewPager2.perform(swipeRight());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open"),
                        withParent(allOf(withId(R.id.toolbar),
                                withParent(withId(R.id.appbar_challenge_detail)))),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Open"),
                        withParent(allOf(withId(R.id.toolbar),
                                withParent(withId(R.id.appbar_challenge_detail)))),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        pressBack();

        ViewInteraction appCompatTextView = onView(
                allOf(withText("Friends"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction viewPager3 = onView(
                allOf(withId(R.id.view_pager_main), isDisplayed()));
        viewPager3.perform(swipeLeft());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withText("Challenges"), isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction viewPager4 = onView(
                allOf(withId(R.id.view_pager_main), isDisplayed()));
        viewPager4.perform(swipeRight());

    }


    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
