package com.azinecllc.champy.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

/**
 * Created by SashaKhyzhun on 1/12/17.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class SplashActivityTest {

    private RoleControllerActivity activity;
    private Typeface typeface;
    private TextView lostInternet;
    private TextView tvChampy;
    private ImageView champyLogo;
    private ImageView imageReload;
    private boolean isLostInet;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(RoleControllerActivity.class).create().get();
        typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/bebasneue.ttf");

        lostInternet = (TextView)  activity.findViewById(R.id.tvLostInternetConnection);
        tvChampy     = (TextView)  activity.findViewById(R.id.tvChampy);
        champyLogo   = (ImageView) activity.findViewById(R.id.imageViewChampy);
        imageReload  = (ImageView) activity.findViewById(R.id.imageRetry);


    }

    @Test
    public void testCheckActivityForNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void testChampyLogo() throws Exception {
        assertNotNull(champyLogo);

        assertEquals(100, champyLogo.getLayoutParams().width);
        assertEquals(100, champyLogo.getLayoutParams().height);
        assertEquals(View.VISIBLE, champyLogo.getVisibility());
        assertEquals(R.id.imageViewChampy, champyLogo.getId());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)champyLogo.getLayoutParams();
        assertEquals(60, lp.topMargin);
    }


    @Test
    public void testCheckAppName() throws Exception {
        assertNotNull(tvChampy);
        assertEquals("Champy", tvChampy.getText());
        assertEquals(25, 25, tvChampy.getTextSize());
        assertEquals(R.id.tvChampy, tvChampy.getId());

        assertEquals(Color.WHITE, tvChampy.getCurrentTextColor());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)tvChampy.getLayoutParams();
        assertEquals(25, lp.topMargin);

        tvChampy.setTypeface(typeface);
        assertEquals(typeface, tvChampy.getTypeface());
    }

    @Test
    public void testCheckSpinner() throws Exception {
        View spinner = activity.findViewById(R.id.loadingPanel);
        assertNotNull(spinner);
        assertEquals(View.VISIBLE, spinner.getVisibility());

    }

    @Test
    public void testCheckTVLostInternetConnection() throws Exception {
        assertNotNull(lostInternet);
        assertEquals(View.INVISIBLE, lostInternet.getVisibility());

        lostInternet.setVisibility(View.VISIBLE);
        assertEquals("Lost internet connection", lostInternet.getText());

        assertEquals(Color.WHITE, lostInternet.getCurrentTextColor());

        lostInternet.setTypeface(typeface);
        assertEquals(typeface, lostInternet.getTypeface());

    }

    @Test
    public void testImageViewReload() throws Exception {
        assertNotNull(imageReload);
        assertEquals(View.INVISIBLE, imageReload.getVisibility());
        assertEquals(R.id.imageRetry, imageReload.getId());
    }








}