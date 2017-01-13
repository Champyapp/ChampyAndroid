package com.azinecllc.champy.controller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.data.DBHelper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by SashaKhyzhun on 12/27/16.
 */
public class ChallengeControllerTest {
    @Mock
    Context mockContext;
    @Mock
    DBHelper mockDBHelper;
    @Mock
    Cursor mockCursor;
    @Mock
    SQLiteDatabase mockDB;
    @Mock
    Activity mockActivity;


    @Before
    public void setup() throws Exception {
        mockContext  = mock(Context.class);
        mockDBHelper = mock(DBHelper.class);
        mockCursor   = mock(Cursor.class);
        mockDB       = mock(SQLiteDatabase.class);
        mockActivity = mock(MainActivity.class);

    }

    @Test
    public void testMethodForSetNewAlarmManager() throws Exception {

        Calendar c = GregorianCalendar.getInstance();
        int days  = c.get(Calendar.DAY_OF_YEAR);
        int hour  = c.get(Calendar.HOUR_OF_DAY);
        int min   = c.get(Calendar.MINUTE);

        System.out.println("days: " + days);
        System.out.println("hour: " + hour);
        System.out.println("min: " + min);

        assertNotNull(c);
        assertNotNull(days);
        assertNotNull(hour);
        assertNotNull(min);

        final long currentMidnight = System.currentTimeMillis() / 1000
                - (c.get(Calendar.HOUR_OF_DAY) * 60 * 60)
                - (c.get(Calendar.MINUTE) * 60)
                - (c.get(Calendar.SECOND));

        System.out.println("currentMidnight: " + currentMidnight);
        assertNotNull(currentMidnight);

        final String[] details = new String[days];
        for (int i = 0; i < days; i++) {
            details[i] = String.valueOf(
                    currentMidnight
                            + (min  * 60)
                            + (hour * 60 * 60)
                            + (i * (24 * 60 * 60))
            );
        }

        if (Arrays.toString(details).equals("[]")) {
            System.out.println("damn");
            Assert.assertEquals(String.valueOf(details.length), String.valueOf(1), "[]");
        } else {
            assertNotNull(Arrays.toString(details));
        }
        System.out.println("Array with time: " + Arrays.toString(details));

        for (int i = 0; i <= details.length - 1; i++) {
            if (System.currentTimeMillis() / 1000 < Integer.parseInt(details[i])) {
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("Woo-hoo");
                System.out.println("now < d[i]");
                System.out.println(System.currentTimeMillis()/1000 + " < " + Integer.parseInt(details[i]));
                System.out.println("next alarm will be at: " + Long.parseLong(details[i]) * 1000);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                Assert.assertEquals("GOOD", "OK", "OK");
                break;
            }

        }



    }



}