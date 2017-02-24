package com.azinecllc.champy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.utils.SessionManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import static com.azinecllc.champy.utils.Constants.TAG_CHALLENGES;
import static com.azinecllc.champy.utils.Constants.TAG_PRIVACY_POLICE;
import static com.azinecllc.champy.utils.Constants.TAG_SETTINGS;
import static com.azinecllc.champy.utils.Constants.TAG_TERMS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 2/24/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MainActivityTest {

    private Activity activity;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.setupActivity(MainActivity.class);
    }

    @After
    public void finishComponentTesting() throws Exception {
        resetSingleton(DBHelper.class, "instance");
        resetSingleton(SessionManager.class, "instance");
        resetSingleton(CHCheckPendingDuels.class, "instance");
    }

    @Test
    public void testForNotNullActivity() throws Exception {
        assertNotNull(activity);
        assertEquals("MainActivity", activity.getClass().getSimpleName());
    }

    @Test
    public void testForAppBarLayout() throws Exception {
        AppBarLayout appBarLayout = (AppBarLayout) activity.findViewById(R.id.appbar_main);
        Assert.assertNotNull(appBarLayout);
        assertTrue(View.VISIBLE == appBarLayout.getVisibility());
        Assert.assertEquals(R.id.appbar_main, appBarLayout.getId());
    }

    @Test
    public void testForToolbar() throws Exception {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        Assert.assertNotNull(toolbar);
        Assert.assertEquals("Challenges", toolbar.getTitle());
        System.out.println("Expected: Challenges | actual: " + toolbar.getTitle());
        assertTrue(R.id.toolbar == toolbar.getId());
        System.out.println("Expected: R.id.toolbar | actual: " + toolbar.getId());
    }

    @Test
    public void testFriendsBackground() throws Exception {
        ImageView background = (ImageView) activity.findViewById(R.id.main_background);
        assertNotNull(background);
        assertEquals(R.id.main_background, background.getId());
        assertEquals(View.VISIBLE, background.getVisibility());
        assertTrue(ImageView.ScaleType.CENTER_CROP == background.getScaleType());
    }

    @Test
    public void testItemBlurIsNotNull() throws Exception {
        View view = activity.findViewById(R.id.item_blur);
        assertNotNull(view);
        assertEquals(View.VISIBLE, view.getVisibility());
    }

    @Test
    public void testForFragmentHolder() throws Exception {
        RelativeLayout fragmentHolder = (RelativeLayout) activity.findViewById(R.id.frame);
        assertNotNull(fragmentHolder);
    }

    @Test
    public void testForNavigationView() throws Exception {
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        assertNotNull(navigationView);
        assertEquals(navigationView.getId(), R.id.nav_view);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        assertNotNull(headerLayout);
        assertEquals(View.VISIBLE, headerLayout.getVisibility());

        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.drawer_user_photo);
        assertNotNull(drawerImageProfile);
        assertEquals(drawerImageProfile.getId(), R.id.drawer_user_photo);
        assertEquals(View.VISIBLE, drawerImageProfile.getVisibility());

        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.drawer_background);
        assertNotNull(drawerBackground);
        assertEquals(drawerBackground.getId(), R.id.drawer_background);
        assertEquals(View.VISIBLE, drawerBackground.getVisibility());

        TextView drawerUserEmail = (TextView) headerLayout.findViewById(R.id.drawer_tv_user_email);
        assertNotNull(drawerUserEmail);
        assertEquals(drawerUserEmail.getId(), R.id.drawer_tv_user_email);
        assertEquals(View.VISIBLE, drawerUserEmail.getVisibility());

        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.drawer_tv_user_name);
        assertNotNull(drawerUserName);
        assertEquals(drawerUserName.getId(), R.id.drawer_tv_user_name);
        assertEquals(View.VISIBLE, drawerUserName.getVisibility());
        System.out.println("navigation view tests: Success");

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