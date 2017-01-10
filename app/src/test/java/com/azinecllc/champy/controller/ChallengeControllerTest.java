package com.azinecllc.champy.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.azinecllc.champy.data.DBHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

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


    @Before
    public void setup() throws Exception {
        mockContext = mock(Context.class);
        dbHelper = DBHelper.getInstance(mockContext);
        c = mock(Cursor.class);
        db = mock(SQLiteDatabase.class);

    }

    @Test
    public void setNewAlarmClock() throws Exception {
        Random random = new Random();
        int days  = random.nextInt(21);
        int min   = random.nextInt(60);
        int hour  = random.nextInt(24);

        Calendar c = GregorianCalendar.getInstance();
        final long currentMidnight = System.currentTimeMillis() / 1000
                - (c.get(Calendar.HOUR_OF_DAY) * 60 * 60)
                - (c.get(Calendar.MINUTE) * 60)
                - (c.get(Calendar.SECOND));

        final String[] details = new String[days];
        for (int i = 0; i < days; i++) {
            details[i] = String.valueOf(
                    currentMidnight
                            + (min  * 60)
                            + (hour * 60 * 60)
                            + (i * (24 * 60 * 60))
            );
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