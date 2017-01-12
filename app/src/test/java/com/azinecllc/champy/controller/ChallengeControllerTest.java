package com.azinecllc.champy.controller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.data.DBHelper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;

/**
 * Created by SashaKhyzhun on 12/27/16.
 */
public class ChallengeControllerTest {
    @Mock
    Context mockContext;
    @Mock
    DBHelper dbHelper;
    @Mock
    Cursor c;
    @Mock
    SQLiteDatabase db;
    @Mock
    AlarmManager aManager;
    @Mock
    Intent mockIntent;
    @Mock
    PendingIntent mockPI;
    @Mock
    Activity mockActivity;


    @Before
    public void setup() throws Exception {
        mockContext = mock(Context.class);
        dbHelper = DBHelper.getInstance(mockContext);
        c = mock(Cursor.class);
        db = mock(SQLiteDatabase.class);
        aManager = mock(AlarmManager.class);
        mockIntent = mock(Intent.class);
        mockPI = mock(PendingIntent.class);
        mockActivity = mock(MainActivity.class);

    }

    @Test
    public void setNewAlarmClock() throws Exception {

        Calendar c = GregorianCalendar.getInstance();
        int days  = c.get(Calendar.DAY_OF_YEAR);
        int hour  = c.get(Calendar.HOUR_OF_DAY);
        int min   = c.get(Calendar.MINUTE);

        Assert.assertNotNull(c);
        Assert.assertNotNull(days);
        Assert.assertNotNull(hour);
        Assert.assertNotNull(min);

        final long currentMidnight = System.currentTimeMillis() / 1000
                - (c.get(Calendar.HOUR_OF_DAY) * 60 * 60)
                - (c.get(Calendar.MINUTE) * 60)
                - (c.get(Calendar.SECOND));

        Assert.assertNotNull(currentMidnight);

        final String[] details = new String[days];
        for (int i = 0; i < days; i++) {
            details[i] = String.valueOf(
                    currentMidnight
                            + (min  * 60)
                            + (hour * 60 * 60)
                            + (i * (24 * 60 * 60))
            );
        }

        Assert.assertEquals(notNull(), Arrays.toString(details));

        for (int i = 0; i <= details.length - 1; i++) {
            if (System.currentTimeMillis() / 1000 < Integer.parseInt(details[i])) {
                mockPI = PendingIntent.getBroadcast(mockContext, 0, mockIntent, 0);
                aManager = (AlarmManager)mockContext.getSystemService(Context.ALARM_SERVICE);
                aManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(details[i]) * 1000, mockPI);
                Assert.assertEquals("OK", "OK", "OK");
                break;
            }

        }



    }

    @Test
    public void isActive() throws Exception {
        System.out.println(mockContext);
        System.out.println(dbHelper);
        System.out.println(c);
        System.out.println(db);

        //db = dbHelper.getWritableDatabase();

    }


}