package com.azinecllc.champy.activity;

import android.app.Activity;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by SashaKhyzhun on 2/24/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class AboutActivityTest {

    private Activity activity;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.buildActivity(AboutActivity.class).create().get();
    }

    @Test
    public void onCreate() throws Exception {
        assertNotNull(activity);
        assertEquals("AboutActivity", activity.getClass().getSimpleName());
    }

    @Test
    public void testForAppBarLayout() throws Exception {
        AppBarLayout appBarLayout = (AppBarLayout) activity.findViewById(R.id.appbar_about);
        assertNotNull(appBarLayout);
        assertTrue(View.VISIBLE == appBarLayout.getVisibility());
        assertEquals(R.id.appbar_about, appBarLayout.getId());
    }

    @Test
    public void testForToolbar() throws Exception {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        assertNotNull(toolbar);
        assertEquals("About", toolbar.getTitle());
        System.out.println("Expected: About | actual: " + toolbar.getTitle());
        assertTrue(R.id.toolbar == toolbar.getId());
        System.out.println("Expected: R.id.toolbar | actual: " + toolbar.getId());
    }

//    @Test
//    public void testForWebView() throws Exception {
//        WebView webView = (WebView) activity.findViewById(R.id.webView);
//        assertNotNull(webView);
//        assertTrue(R.id.webView == webView.getId());
//        System.out.println("Expected: R.id.webView | actual: " + webView.getId());
//        assertTrue(webView.getSettings().getJavaScriptEnabled());
//        System.out.println("Expected: true | actual: " + webView.getSettings().getJavaScriptEnabled());
//        assertEquals(View.VISIBLE, webView.getVisibility());
//        System.out.println("Expected: visible (0) | actual: " + webView.getVisibility());
//
//    }

//    @Test
//    public void testProgressBar() throws Exception {
//        View spinner = activity.findViewById(R.id.loadingPanel);
//        assertNotNull(spinner);
//        assertEquals(View.VISIBLE, spinner.getVisibility());
//    }


}