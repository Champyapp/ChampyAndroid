package com.azinecllc.champy.utils;

import android.os.Build;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.Champy;
import com.azinecllc.champy.SingletonHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by SashaKhyzhun on 3/3/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class SessionManagerTest {

    private SessionManager sessionManager;

    @Before
    public void setUp() throws Exception {
        sessionManager = Mockito.mock(SessionManager.class);
    }

    @After
    public void tearDown() throws Exception {
        SingletonHelper.resetSingleton(SessionManager.class, "instance");
    }

    @Test
    public void getInstance() throws Exception {
        assertNotNull(sessionManager);
        if (sessionManager == null) {
            System.out.println("sessionManager == null");
            sessionManager = SessionManager.getInstance(Champy.getContext());
        }
    }

    @Test
    public void createUserLoginSession() throws Exception {
        when(sessionManager.getGCM()).thenReturn("MY GCM OMG");

        //sessionManager.createUserLoginSession("", "", "", "", "", "", "", "", "", "", "", "", "MY GCM OMG", "");
        //verify(sessionManager).getGCM();

        assertEquals("MY GCM OMG", sessionManager.getGCM());
        System.out.println(sessionManager.getGCM());
    }

    @Test
    public void setChampyOptions() throws Exception {

    }

    @Test
    public void toggleNewChallengeRequest() throws Exception {

    }

    @Test
    public void toggleAcceptYourChallenge() throws Exception {

    }

    @Test
    public void toggleChallengesForToday() throws Exception {

    }

    @Test
    public void togglePushNotification() throws Exception {

    }

    @Test
    public void toggleChallengeEnd() throws Exception {

    }

    @Test
    public void setRefreshPending() throws Exception {

    }

    @Test
    public void setRefreshFriends() throws Exception {

    }

    @Test
    public void setRefreshOthers() throws Exception {

    }

    @Test
    public void setDuelPending() throws Exception {

    }

    @Test
    public void setUserPicture() throws Exception {

    }

    @Test
    public void setUserName() throws Exception {

    }

    @Test
    public void setSelfSize() throws Exception {

    }

    @Test
    public void logout() throws Exception {

    }

    @Test
    public void isUserLoggedIn() throws Exception {

    }

    @Test
    public void getDuelPending() throws Exception {

    }

    @Test
    public void getRefreshPending() throws Exception {

    }

    @Test
    public void getRefreshFriends() throws Exception {

    }

    @Test
    public void getRefreshOthers() throws Exception {

    }

    @Test
    public void getTokenAndroid() throws Exception {

    }

    @Test
    public void getFacebookId() throws Exception {

    }

    @Test
    public void getUserPicture() throws Exception {

    }

    @Test
    public void getUserEmail() throws Exception {

    }

    @Test
    public void getUserName() throws Exception {

    }

    @Test
    public void getToken() throws Exception {

    }

    @Test
    public void getUserId() throws Exception {

    }

    @Test
    public void getGCM() throws Exception {

    }

    @Test
    public void getSelfSize() throws Exception {

    }

    @Test
    public void getChampyOptions() throws Exception {

    }

    @Test
    public void getUserDetails() throws Exception {

    }

}