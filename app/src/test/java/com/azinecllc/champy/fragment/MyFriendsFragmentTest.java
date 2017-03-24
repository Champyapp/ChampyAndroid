//package com.azinecllc.champy.fragment;
//
//import android.os.Build;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//
//import com.azinecllc.champy.BuildConfig;
//import com.azinecllc.champy.R;
//import com.azinecllc.champy.activity.FriendsActivity;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricGradleTestRunner;
//import org.robolectric.annotation.Config;
//
//import static junit.framework.Assert.assertTrue;
//import static org.junit.Assert.*;
//
///**
// * Created by SashaKhyzhun on 3/2/17.
// */
//@RunWith(RobolectricGradleTestRunner.class)
//@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
//public class MyFriendsFragmentTest {
//
//    private View view;
//    private MyFriendsFragment fragment;
//    private FriendsActivity activity;
//
//    @Before
//    public void setUp() throws Exception {
//        activity = Robolectric.buildActivity(FriendsActivity.class).create().get();
//        fragment = new MyFriendsFragment();
//        view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.item_recycler, null);
//    }
//
//    @Test
//    public void testForActivity() throws Exception {
//        assertNotNull(activity);
//        assertEquals("FriendsActivity", activity.getClass().getSimpleName());
//    }
//
//    @Test
//    public void testForFragment() throws Exception {
//        assertNotNull(fragment);
//        assertEquals("MyFriendsFragment", fragment.getClass().getSimpleName());
//    }
//
//    @Test
//    public void testForSwipeRefreshLayout() throws Exception {
//        SwipeRefreshLayout swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
//        assertNotNull(swipe);
//        assertTrue(R.id.swipe_to_refresh == swipe.getId());
//    }
//
//    @Test
//    public void onCreateView() throws Exception {
//        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        assertNotNull(recyclerView);
//        assertTrue(R.id.recycler_view == recyclerView.getId());
//    }
//
//
//}