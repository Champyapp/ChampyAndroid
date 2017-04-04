package com.azinecllc.champy.helper;

import android.content.Context;
import android.widget.ImageView;

import com.azinecllc.champy.activity.ChallengeActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static java.lang.Math.round;
import static org.junit.Assert.*;

import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;

import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by SashaKhyzhun on 12/27/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class CHMakeResponsiveScoreTest3 {

    private ChallengeActivity mockMainActivity;
    private Context context;
    @Mock
    ImageView circleInProgress;
    @Mock
    ImageView mockImageView;


    @Before
    public void setup() throws Exception {
        mockMainActivity = mock(ChallengeActivity.class);
        mockImageView = mock(ImageView.class);
        context = mock(Context.class);
    }

    @Test
    public void getInstance() throws Exception {
        if (context == null) {
            assertEquals("Error", false, true);
        } else {
            assertEquals("OK!", false, false);
            assertNotNull(context);
        }
        System.out.println(context);

    }

    @Test
    public void makeResponsiveScore() throws Exception {
        if (mockMainActivity == null) {
            assertEquals("Error", false, true);
        } else {
            assertEquals("OK!", false, false);
            assertNotNull(mockMainActivity);
        }
        System.out.println(mockMainActivity);

        if (mockImageView == null) {
            assertEquals("Error", false, true);
        } else {
            assertEquals("OK!", false, false);
            assertNotNull(mockImageView);
        }
        System.out.println(mockImageView);

    }

    @Test
    public void testForLinkedList() throws Exception {
        LinkedList mockedList = mock(LinkedList.class);

        //stub'инг
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException());

        //получим "first"
        System.out.println(mockedList.get(0));

        //получим RuntimeException
        //System.out.println(mockedList.get(1));

        //получим "null" ибо get(999) не был определен
        System.out.println(mockedList.get(999) + " // it's ok.");
    }

}