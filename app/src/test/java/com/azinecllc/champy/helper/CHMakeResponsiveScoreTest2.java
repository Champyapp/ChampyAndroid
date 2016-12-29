package com.azinecllc.champy.helper;

import android.app.RobolectricActivityManager;
import android.widget.ImageView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.lang.Math.round;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by SashaKhyzhun on 12/27/16.
 */
public class CHMakeResponsiveScoreTest2 {

    //@InjectView(R.id.imageView_challenges_animation) ImageView circleInProgress;

    private CHMakeResponsiveScore makeResponsiveScore;
    private MainActivity mainActivity;
    @Mock
    MainActivity mockMainActivity;

    @Before
    public void setup() throws Exception {
        makeResponsiveScore = CHMakeResponsiveScore.getInstance();
        mainActivity        = new MainActivity();
        mockMainActivity    = mock(MainActivity.class);
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

        ImageView circleInProgress = (ImageView)mockMainActivity.findViewById(R.id.imageView_challenges_animation);
        System.out.println(circleInProgress);

    }

}