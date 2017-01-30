package com.azinecllc.champy.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.azinecllc.champy.data.DBHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 1/30/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class PendingDuelActivityTest {

    private Activity activity;
    private DBHelper dbHelper;


    @Before
    public void setup() throws Exception {
        activity = Robolectric.buildActivity(PendingDuelActivity.class).create().get();
        dbHelper = DBHelper.getInstance(activity);
    }

    @Test
    public void testNotNullActivity() throws Exception {
        assertNotNull(activity);
    }


    @Test
    public void testImageViewLogo() throws Exception {
        ImageView imageViewLogo = (ImageView) activity.findViewById(R.id.imageViewLogo);
        assertNotNull(imageViewLogo);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageViewLogo.getLayoutParams();

        assertEquals(50, lp.width);
        assertEquals(50, lp.height);
        System.out.println("imageViewLogo | Expected width: 50,  Actual width: " + lp.width);
        System.out.println("imageViewLogo | Expected height: 50, Actual height: " + lp.height);

        assertEquals(R.id.imageViewLogo, imageViewLogo.getId());

        assertEquals(24, lp.topMargin);

    }

    @Test // SIC = self-improvement challenge
    public void testTextViewSIC() throws Exception {
        TextView tvPendingDuels = (TextView) activity.findViewById(R.id.tvChallengeToMySelf);
        assertNotNull(tvPendingDuels);

        assertEquals(22d, tvPendingDuels.getTextSize(), 0);
        System.out.println("tvPendingDuels | TextSize expected: 22d, actual: " + tvPendingDuels.getTextSize());

        assertEquals(Color.WHITE, tvPendingDuels.getTextColors().getDefaultColor());
        System.out.println("tvPendingDuels | Color expected: WHITE, actual: " + tvPendingDuels.getTextColors().getDefaultColor());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvPendingDuels.getLayoutParams();
        assertEquals(32, lp.topMargin);
        System.out.println("tvPendingDuels | TopMargin expected: 32, actual: " + lp.topMargin);
    }

    @Test
    public void testCheckSpinner() throws Exception {
        View spinner = activity.findViewById(R.id.loadingPanel);
        assertNotNull(spinner);
        assertEquals(View.INVISIBLE, spinner.getVisibility()); // after loading data

    }

    @Test
    public void testForViewPager() throws Exception {
        ViewPager viewPager = (ViewPager) activity.findViewById(R.id.pager_pending_duel);
        assertNotNull(viewPager);

        assertEquals(1, viewPager.getOffscreenPageLimit());
        assertEquals(90, viewPager.getPaddingLeft());
        assertEquals(90, viewPager.getPaddingRight());
        assertEquals(0, viewPager.getPaddingTop());
        assertEquals(0, viewPager.getPaddingBottom());
    }

    @After
    public void killSingleton() throws Exception {
        dbHelper.close();
        System.out.println("Killed singleton!");
    }

}