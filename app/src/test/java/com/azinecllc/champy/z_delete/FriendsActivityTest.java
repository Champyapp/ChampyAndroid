//package com.azinecllc.champy.activity;
//
//import android.app.Activity;
//import android.os.Build;
//import android.support.design.widget.AppBarLayout;
//import android.support.design.widget.NavigationView;
//import android.support.design.widget.TabLayout;
//import android.support.v4.view.ViewPager;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.azinecllc.champy.BuildConfig;
//import com.azinecllc.champy.R;
//import com.azinecllc.champy.data.DBHelper;
//import com.azinecllc.champy.helper.CHCheckPendingDuels;
//import com.azinecllc.champy.utils.SessionManager;
//
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricGradleTestRunner;
//import org.robolectric.annotation.Config;
//import org.robolectric.util.ActivityController;
//
//import java.lang.reflect.Field;
//import java.util.Random;
//
//import static junit.framework.Assert.assertEquals;
//import static junit.framework.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
///**
// * Created by SashaKhyzhun on 1/13/17.
// */
//@RunWith(RobolectricGradleTestRunner.class)
//@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
//public class FriendsActivityTest {
//
//    private Activity activity;
//    private DBHelper dbHelper;
//    private SessionManager sessionManager;
//
//    @Before
//    public void setup() throws Exception {
//        activity = Robolectric.buildActivity(FriendsActivity.class).create().get();
//        dbHelper = DBHelper.getInstance(activity);
//        sessionManager = SessionManager.getInstance(activity);
//    }
//
//    @Test
//    public void testForActivity() throws Exception {
//        assertNotNull(activity);
//    }
//
//    @After
//    public void finishComponentTesting() throws Exception {
//        resetSingleton(DBHelper.class, "instance");
//        resetSingleton(SessionManager.class, "instance");
//    }
//
//    @Test
//    public void testForAppBarLayout() throws Exception {
//        AppBarLayout appBarLayout = (AppBarLayout) activity.findViewById(R.id.appbar_friends);
//        Assert.assertNotNull(appBarLayout);
//        assertTrue(View.VISIBLE == appBarLayout.getVisibility());
//        Assert.assertEquals(R.id.appbar_friends, appBarLayout.getId());
//    }
//
//    @Test
//    public void testForToolbar() throws Exception {
//        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
//        Assert.assertNotNull(toolbar);
//        Assert.assertEquals("Friends", toolbar.getTitle());
//        System.out.println("Expected: Friends | actual: " + toolbar.getTitle());
//        assertTrue(R.id.toolbar == toolbar.getId());
//        System.out.println("Expected: R.id.toolbar | actual: " + toolbar.getId());
//    }
//
//    @Test
//    public void testForToolbarDrawerAndActionBar() throws Exception {
//        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
//        assertNotNull(toolbar);
//        assertTrue(toolbar.getTitle().equals("Friends"));
//        System.out.println("Toolbar.text = 'Friends'. Success");
//
//        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
//        assertNotNull(drawer);
//        assertEquals(R.id.drawer_layout, drawer.getId());
//
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        assertNotNull(toggle);
//
//    }
//
//    @Test
//    public void testForNavigationView() throws Exception {
//        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
//        assertNotNull(navigationView);
//        assertEquals(navigationView.getId(), R.id.nav_view);
//
//        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
//        assertNotNull(headerLayout);
//        assertEquals(View.VISIBLE, headerLayout.getVisibility());
//
//        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.drawer_user_photo);
//        assertNotNull(drawerImageProfile);
//        assertEquals(drawerImageProfile.getId(), R.id.drawer_user_photo);
//        assertEquals(View.VISIBLE, drawerImageProfile.getVisibility());
//
//        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.drawer_background);
//        assertNotNull(drawerBackground);
//        assertEquals(drawerBackground.getId(), R.id.drawer_background);
//        assertEquals(View.VISIBLE, drawerBackground.getVisibility());
//
//        TextView drawerUserEmail = (TextView) headerLayout.findViewById(R.id.drawer_tv_user_email);
//        assertNotNull(drawerUserEmail);
//        assertEquals(drawerUserEmail.getId(), R.id.drawer_tv_user_email);
//        assertEquals(View.VISIBLE, drawerUserEmail.getVisibility());
//
//        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.drawer_tv_user_name);
//        assertNotNull(drawerUserName);
//        assertEquals(drawerUserName.getId(), R.id.drawer_tv_user_name);
//        assertEquals(View.VISIBLE, drawerUserName.getVisibility());
//        System.out.println("navigation view tests: Success");
//
//
//    }
//
//    @Test
//    public void testFriendsBackground() throws Exception {
//        ImageView background = (ImageView) activity.findViewById(R.id.friends_background);
//        assertNotNull(background);
//        assertEquals(R.id.friends_background, background.getId());
//        assertEquals(View.VISIBLE, background.getVisibility());
//        assertTrue(ImageView.ScaleType.CENTER_CROP == background.getScaleType());
//    }
//
//    @Test
//    public void testItemBlurIsNotNull() throws Exception {
//        View view = activity.findViewById(R.id.item_blur);
//        assertNotNull(view);
//        assertEquals(View.VISIBLE, view.getVisibility());
//    }
//
//    @Test
//    public void testTabLayout() throws Exception {
//        TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.sliding_tabs_friends);
//        assertNotNull(tabLayout);
//        assertEquals(R.id.sliding_tabs_friends, tabLayout.getId());
//        //assertEquals(R.color.color_white, tabLayout.getTabTextColors());
//        assertEquals(3, tabLayout.getTabCount());
//    }
//
//    @Test
//    public void testCheckSpinner() throws Exception {
//        View spinner = activity.findViewById(R.id.loadingPanel);
//        assertNotNull(spinner);
//        assertEquals(View.VISIBLE, spinner.getVisibility());
//    }
//
//    @Test
//    public void testViewPager() throws Exception {
//        ViewPager viewPager = (ViewPager) activity.findViewById(R.id.viewpager);
//        assertNotNull(viewPager);
//
//        assertEquals(View.VISIBLE, viewPager.getVisibility());
//
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewPager.getLayoutParams();
//        assertEquals(false, lp.alignWithParent);
//        assertEquals(2, viewPager.getOffscreenPageLimit());
//        assertNotNull(viewPager.getAdapter());
//
//    }
//
//    @Test
//    public void testForCHCheckPendingDuels() throws Exception {
//        int min = 0;
//        int max = 10;
//        Random random = new Random();
//        int count = random.nextInt(max - min + 1) + min;
//        assertNotNull(count);
//
//        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
//        assertNotNull(navigationView);
//
//        TextView view = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_duels).getActionView();
//        assertNotNull(navigationView);
//        System.out.println("PendingDuels Count: " + count);
//
//        view.setText(count > 0 ? String.valueOf(count) : "");
//
//        assertTrue(view.getText().equals((count > 0) ? String.valueOf(count) : ""));
//        assertNotNull(view.getText());
//        assertNotNull(view);
//
//    }
//
//    // after each test we need to destroy singletons
//    public void resetSingleton(Class clazz, String fieldName) {
//        Field instance;
//        try {
//            instance = clazz.getDeclaredField(fieldName);
//            instance.setAccessible(true);
//            instance.set(null, null);
//        } catch (Exception e) {
//            throw new RuntimeException();
//        }
//    }
//
//}