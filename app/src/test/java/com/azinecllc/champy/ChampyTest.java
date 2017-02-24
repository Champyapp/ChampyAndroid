package com.azinecllc.champy;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 2/24/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class ChampyTest {

    private Champy champy;

    @Test
    public void getInstance() throws Exception {
        champy = Champy.getInstance();
        assertNull(champy);
        System.out.println("Expected: null | actual: " + champy);
    }

    @Test
    public void onCreate() throws Exception {
        System.out.println(champy);
    }

    @Test
    public void getContext() throws Exception {

    }

}