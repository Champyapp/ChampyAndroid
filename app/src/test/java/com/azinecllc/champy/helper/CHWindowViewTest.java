package com.azinecllc.champy.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.utils.CustomPagerBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;

import static android.content.Context.WINDOW_SERVICE;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by SashaKhyzhun on 12/29/16.
 */
public class CHWindowViewTest {

    private static Point screenSize;

    @Test
    public void getWindowWidth() throws Exception {

        // SETUP
        Context context = mock(Context.class); //spy(RuntimeEnvironment.application);

        // STUB
        WindowManager mockWM = mock(WindowManager.class);
        Display mockDisplay  = mock(Display.class);
        //DisplayMetrics metrics = mock(DisplayMetrics.class);

        when(context.getSystemService(Context.WINDOW_SERVICE)).thenReturn(mockWM);
        when(mockWM.getDefaultDisplay()).thenReturn(mockDisplay);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                System.out.println("ANSWER");
                DisplayMetrics metrics = (DisplayMetrics) invocation.getArguments()[0];
                metrics.scaledDensity = 3.25f;
                metrics.widthPixels   = 1080;
                metrics.heightPixels  = 1920;
                return metrics;
            }
        }).when(mockDisplay).getMetrics(any(DisplayMetrics.class));

        context.getSystemService(Context.WINDOW_SERVICE);
        mockWM.getDefaultDisplay();

        // RUN
        //CHWindowView.getWindowWidth(context);

        System.out.println("expect: " + CHWindowView.getWindowWidth(context)
                      + " | actual: " + mockDisplay.getWidth()); //mockWM.getDefaultDisplay().getWidth());


        assertEquals (CHWindowView.getWindowWidth(context), mockDisplay.getWidth()); //mockDisplay.getMetrics(any(DisplayMetrics.class)));
        assertNotNull(CHWindowView.getWindowWidth(context));

    }

    @Test
    public void getWindowHeight() throws Exception {
        // SETUP
        Context mockContext  = mock(Context.class);
        // STUB
        WindowManager mockWM = mock(WindowManager.class);
        Display mockDisplay  = mock(Display.class); //mockWM.getDefaultDisplay();
        Point mockScreenSize = mock(Point.class);
        DisplayMetrics metrics = mock(DisplayMetrics.class);

        when(mockContext.getSystemService(Context.WINDOW_SERVICE)).thenReturn(mockWM);
        when(mockWM.getDefaultDisplay()).thenReturn(mockDisplay);
        when(mockDisplay.getHeight()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                //DisplayMetrics metrics = (DisplayMetrics) invocation.getArguments()[0];

                metrics.scaledDensity = 3.25f;
                metrics.widthPixels   = 1080;
                metrics.heightPixels  = 1920;
                return metrics.heightPixels;
            }
        });


        System.out.println("expect: " + CHWindowView.getWindowHeight(mockContext) + " | actual: " + metrics.heightPixels); //mockWM.getDefaultDisplay().getWidth());

        assertEquals(CHWindowView.getWindowHeight(mockContext), metrics.heightPixels);
    }

    @Test
    public void getCurrentCardPositionX() throws Exception {
        float cardWidth = 500 / 100 * 65;

        System.out.println( (500/2) - (cardWidth/2) );
    }


}