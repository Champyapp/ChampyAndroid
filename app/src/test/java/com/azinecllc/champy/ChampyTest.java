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

    @Test
    public void getInstance() throws Exception {
        Champy champy = Champy.getInstance();
        assertNotNull(champy);
        System.out.println("Expected: notnull | actual: " + champy);
    }

    @Test
    public void getContext() throws Exception {
        Champy champy = Champy.getInstance();
        assertNotNull(champy.getApplicationContext());
        System.out.println(champy.getApplicationContext());

    }

}