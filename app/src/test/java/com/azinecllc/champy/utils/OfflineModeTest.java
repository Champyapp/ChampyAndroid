package com.azinecllc.champy.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by SashaKhyzhun on 3/2/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class OfflineModeTest {

    private static OfflineMode instance = null;

    @Test
    public void getInstance() throws Exception {
        assertNull(instance);

        if (instance == null) {
            instance = OfflineMode.getInstance();
        }

        assertNotNull(instance);
    }

    @Test
    public void isConnectedToRemoteAPI() throws Exception {
        ConnectivityManager cm = Mockito.mock(ConnectivityManager.class);
        NetworkInfo info = Mockito.mock(NetworkInfo.class);

        when(cm.getActiveNetworkInfo()).thenReturn(info);

        info = cm.getActiveNetworkInfo();

        System.out.println(info);
        System.out.println(cm.getActiveNetworkInfo());


    }

}