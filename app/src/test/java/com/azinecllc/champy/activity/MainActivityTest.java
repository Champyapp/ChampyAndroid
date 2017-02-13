package com.azinecllc.champy.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.ImageView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 1/13/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MainActivityTest {

    private Activity mainActivity;
    private ImageView background;
    @Mock
    Context context;
    @Mock
    CHDownloadImageTask imageTask;


    @Before
    public void setup() throws Exception {
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
        imageTask = Mockito.mock(CHDownloadImageTask.class);
        context   = Mockito.mock(Context.class);
        imageTask = new CHDownloadImageTask(context, (MainActivity)mainActivity);

    }

    @Test
    public void testActivity() throws Exception {
        assertNotNull(mainActivity);
        assertEquals("MainActivity", mainActivity.getLocalClassName());
    }

    @Test
    public void testBackgroundImageView() throws Exception {
        background = (ImageView)mainActivity.findViewById(R.id.main_background);
        assertNotNull(background);
    }

//    @Test
//    public void onCreate() throws Exception {
//
//    }
//
//    @Test
//    public void onClick() throws Exception {
//
//    }

}