package com.azinecllc.champy.helper;

import android.app.RobolectricActivityManager;
import android.widget.ImageView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static java.lang.Math.round;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by SashaKhyzhun on 12/27/16.
 */
@RunWith (RobolectricTestRunner.class)
@Config  (manifest = Config.NONE)
public class CHMakeResponsiveScoreTest2 {

    //@InjectView(R.id.imageView_challenges_animation) ImageView circleInProgress;

    private CHMakeResponsiveScore makeResponsiveScore;
    private MainActivity mainActivity;

    @Before
    public void setup() throws Exception {
        makeResponsiveScore = CHMakeResponsiveScore.getInstance();
        mainActivity        = Robolectric.buildActivity(MainActivity.class).get();
    }

    @Test
    public void getInstance() throws Exception {
        assertNotNull(makeResponsiveScore);
        assertNotNull(mainActivity);

        System.out.println(makeResponsiveScore);
        System.out.println(mainActivity);

    }


    @Test
    public void makeResponsiveScore() throws Exception {
        //int width = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
        //int x = round(width / 100);

        ImageView circleInProgress = (ImageView)mainActivity.findViewById(R.id.imageView_challenges_animation);
        assertNull(circleInProgress);
    }

}