package com.azinecllc.champy.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.SelfImprovementPagerAdapter;
import com.azinecllc.champy.data.DBHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;
import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 1/27/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class SelfImprovementActivityTest {

    private Activity activity;
    private DBHelper dbHelper;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.buildActivity(SelfImprovementActivity.class).create().get();
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
        TextView tvSIC = (TextView) activity.findViewById(R.id.tvSIC);
        assertNotNull(tvSIC);

        assertEquals(16d, tvSIC.getTextSize(), 0);

        assertEquals(Color.WHITE, tvSIC.getTextColors().getDefaultColor());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvSIC.getLayoutParams();
        assertEquals(16, lp.topMargin);
    }

    @Test
    public void testTextViewIChallengeMySelfTo() throws Exception {
        TextView tvChallengeToMySelf = (TextView) activity.findViewById(R.id.tvChallengeToMySelf);
        assertNotNull(tvChallengeToMySelf);

        assertEquals(22d, tvChallengeToMySelf.getTextSize(), 0);

        assertEquals(Color.WHITE, tvChallengeToMySelf.getTextColors().getDefaultColor());

        assertEquals("I challenge myself to", tvChallengeToMySelf.getText());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvChallengeToMySelf.getLayoutParams();
        assertEquals(32, lp.topMargin);

        assertEquals(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, tvChallengeToMySelf.getGravity());
    }

    @Test
    public void testProgressBar() throws Exception {
        View spinner = activity.findViewById(R.id.loadingPanel);
        assertNotNull(spinner);
        assertEquals(View.VISIBLE, spinner.getVisibility());
    }

    @Test
    public void testForViewPager() throws Exception {
        ViewPager viewPager = (ViewPager) activity.findViewById(R.id.pager);
        assertNotNull(viewPager);

        assertEquals(1, viewPager.getOffscreenPageLimit());
        assertEquals(90, viewPager.getPaddingLeft());
        assertEquals(90, viewPager.getPaddingRight());
        assertEquals(0, viewPager.getPaddingTop());
        assertEquals(0, viewPager.getPaddingBottom());
    }

    @Test
    public void testForImageButtonAccept() throws Exception {
        ImageButton imageButton = (ImageButton) activity.findViewById(R.id.imageButtonAccept);
        assertNotNull(imageButton);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageButton.getLayoutParams();
        assertEquals(50, lp.width);
        assertEquals(50, lp.height);
        System.out.println("imageButton | Expected width: 50,  Actual width: " + lp.width);
        System.out.println("imageButton | Expected height: 50, Actual height: " + lp.height);

        assertEquals(8, lp.bottomMargin);

    }


    @After
    public void killSingleton() throws Exception {
        dbHelper.close();
        System.out.println("Killed singleton!");
    }


}