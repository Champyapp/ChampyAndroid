package com.azinecllc.champy.helper;

import android.os.Build;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static java.lang.Math.round;
import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 1/16/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class CHMakeResponsiveScoreTest {

    private CHMakeResponsiveScore instance = null;
    private MainActivity activity;

    @Before
    public void setup() throws Exception {
        instance = CHMakeResponsiveScore.getInstance();
        activity = Mockito.mock(MainActivity.class);  //Robolectric.buildActivity(Mockito.mock(MainActivity.class)).create().get();
    }

    @Test
    public void getInstance() throws Exception {
        assertNotNull(instance);
        if (instance == null) {
            assertNotNull(instance);
        }
    }

    @Test
    public void makeResponsiveScore() throws Exception {
        //int x = round(width/100);
        int x = 10;

        //-------------------------- Circles ---------------------------//
        ImageView imageView = (ImageView) activity.findViewById(R.id.imageView_challenges_animation);
        imageView.getLayoutParams().width = x * 25;
        imageView.getLayoutParams().height = x * 25;

        imageView = (ImageView) activity.findViewById(R.id.imageView_wins_animation);
        imageView.getLayoutParams().width = x * 25;
        imageView.getLayoutParams().height = x * 25;

        imageView = (ImageView) activity.findViewById(R.id.imageView_total_animation);
        imageView.getLayoutParams().width = x * 25;
        imageView.getLayoutParams().height = x * 25;

        //---------------------------- Logo -----------------------------//
        imageView = (ImageView) activity.findViewById(R.id.imageView_wins_logo);
        imageView.getLayoutParams().width = x * 5;
        imageView.getLayoutParams().height = x * 5;

        imageView = (ImageView) activity.findViewById(R.id.imageView_total_logo);
        imageView.getLayoutParams().width = x * 5;
        imageView.getLayoutParams().height = x * 5;

        imageView = (ImageView) activity.findViewById(R.id.imageView_challenges_logo);
        imageView.getLayoutParams().width = x * 5;
        imageView.getLayoutParams().height = x * 5;

        //---------------------------- Fab -----------------------------//
        ImageButton fab = (ImageButton) activity.findViewById(R.id.fabPlus);
        fab.getLayoutParams().width = x * 20;
        fab.getLayoutParams().height = x * 20;
    }

}