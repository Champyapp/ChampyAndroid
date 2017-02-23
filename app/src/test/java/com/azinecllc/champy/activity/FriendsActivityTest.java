package com.azinecllc.champy.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 1/13/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class FriendsActivityTest {

    private Activity activity;
    private ImageView background;
    private TabLayout tabLayout;
    private DBHelper dbHelper;
    private Context context;
    private CHCheckPendingDuels chCheckPendingDuels;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.setupActivity(FriendsActivity.class);
        dbHelper = DBHelper.getInstance(activity);
        chCheckPendingDuels = CHCheckPendingDuels.getInstance();
        //sessionManager = SessionManager.getInstance(context);
    }

    @After
    public void killSingleton() throws Exception {
        dbHelper.close();
        chCheckPendingDuels = null;
        System.out.println("Killed singletons!");
    }

    @Test
    public void testForActivity() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void testForToolbar() throws Exception {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        assertNotNull(toolbar);
    }

    @Test
    public void testFriendsBackground() throws Exception {
        background = (ImageView) activity.findViewById(R.id.friends_background);
        assertNotNull(background);
        assertEquals(R.id.friends_background, background.getId());
        assertEquals(View.VISIBLE, background.getVisibility());
    }

    @Test
    public void testItemBlurIsNotNull() throws Exception {
        View view = activity.findViewById(R.id.item_blur);
        assertNotNull(view);
        assertEquals(View.VISIBLE, view.getVisibility());
    }

    @Test
    public void testTabLayout() throws Exception {
        tabLayout = (TabLayout) activity.findViewById(R.id.sliding_tabs_friends);
        assertNotNull(tabLayout);
        assertEquals(R.id.sliding_tabs_friends, tabLayout.getId());
        //assertEquals(R.color.color_white, tabLayout.getTabTextColors());
        assertEquals(3, tabLayout.getTabCount());
    }

    @Test
    public void testCheckSpinner() throws Exception {
        View spinner = activity.findViewById(R.id.loadingPanel);
        assertNotNull(spinner);
        assertEquals(View.INVISIBLE, spinner.getVisibility());
    }

    @Test
    public void testViewPager() throws Exception {
        ViewPager viewPager = (ViewPager) activity.findViewById(R.id.viewpager);
        assertNotNull(viewPager);

        assertEquals(View.VISIBLE, viewPager.getVisibility());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewPager.getLayoutParams();
        assertEquals(false, lp.alignWithParent);
    }




}