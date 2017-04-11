package com.azinecllc.champy.activity;

import android.app.Activity;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.utils.SessionManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static com.azinecllc.champy.SingletonHelper.resetSingleton;
import static org.junit.Assert.*;

/**
 * @autor SashaKhyzhun
 * Created on 4/11/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class ChallengeCreateActivityTest {

    private Activity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(ChallengeCreateActivity.class).create().get();
    }

    @After
    public void finishComponentTesting() throws Exception {
        resetSingleton(DBHelper.class, "instance");
        resetSingleton(SessionManager.class, "instance");
    }

    @Test
    public void testForCreatingActivity() throws Exception {
        assertNotNull(activity);
        assertEquals("ChallengeCreateActivity", activity.getClass().getSimpleName());
        System.out.println(activity.getClass().getCanonicalName());
    }

    @Test
    public void testForCanonicalActivityPath() throws Exception {
        assertNotNull(activity);
        assertEquals("com.azinecllc.champy.activity.ChallengeCreateActivity", activity.getClass().getCanonicalName());
    }

    @Test
    public void testForAppBarLayout() throws Exception {
        AppBarLayout appBarLayout = (AppBarLayout) activity.findViewById(R.id.appbar_challenge_create);
        Assert.assertNotNull(appBarLayout);
        assertTrue(View.VISIBLE == appBarLayout.getVisibility());
        Assert.assertEquals(R.id.appbar_challenge_create, appBarLayout.getId());
    }

    @Test
    public void testForToolbar() throws Exception {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        Assert.assertNotNull(toolbar);
        Assert.assertEquals("New Challenge", toolbar.getTitle());
        System.out.println("Expected: New Challenge | actual: " + toolbar.getTitle());
        assertTrue(R.id.toolbar == toolbar.getId());
        System.out.println("Expected: R.id.toolbar  | actual: " + toolbar.getId());
    }

    @Test
    public void testForSwipeLayout() throws Exception {
        SwipeRefreshLayout swipe = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_to_refresh);
        assertNotNull(swipe);
        assertFalse(swipe.isEnabled());
    }

    @Test
    public void testForRecycleView() throws Exception {
        RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view);
        assertNotNull(recyclerView);
        assertTrue(recyclerView.getVisibility() == View.VISIBLE);
    }


}