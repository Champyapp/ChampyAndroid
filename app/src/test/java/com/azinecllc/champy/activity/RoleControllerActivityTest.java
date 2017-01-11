package com.azinecllc.champy.activity;

import android.os.Build;
import android.test.suitebuilder.annotation.SmallTest;

import com.azinecllc.champy.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 1/12/17.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class RoleControllerActivityTest {

    private RoleControllerActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(RoleControllerActivity.class).create().get();
    }


    @SmallTest
    public void testForActivity() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void onCreate() throws Exception {

    }

    @Test
    public void onClick() throws Exception {

    }

}