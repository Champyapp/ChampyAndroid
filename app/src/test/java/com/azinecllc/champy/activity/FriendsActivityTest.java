package com.azinecllc.champy.activity;

import android.app.Activity;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ImageView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

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

    @Before
    public void setup() throws Exception {
        activity = Robolectric.buildActivity(FriendsActivity.class).create().get();
    }

    @Test
    public void testForActivity() throws Exception {
        assertNotNull(activity);
    }

//    @Test
//    public void testFriendsBackground() throws Exception {
//        background = (ImageView)activity.findViewById(R.id.friends_background);
//        assertNotNull(background);
//        assertEquals(R.id.friends_background, background.getId());
//        assertEquals(View.VISIBLE, background.getVisibility());
//    }

//    @Test
//    public void testTabLayout() throws Exception {
//        tabLayout = (TabLayout)activity.findViewById(R.id.sliding_tabs_friends);
//        assertNotNull(tabLayout);
//        assertEquals(R.id.sliding_tabs_friends, tabLayout.getId());
//
//        assertEquals("#FFE0E0E0", String.valueOf(tabLayout.getTabTextColors()));
//
//
//    }


}