package com.azinecllc.champy.activity;

import android.os.Build;

import com.azinecllc.champy.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 2/24/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class AlarmReceiverActivityTest {

    private AlarmReceiverActivity activity;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.buildActivity(AlarmReceiverActivity.class).create().get();
    }

    @Test
    public void onCreate() throws Exception {

    }

    @Test
    public void onDestroy() throws Exception {

    }

    @Test
    public void onClick() throws Exception {

    }

}