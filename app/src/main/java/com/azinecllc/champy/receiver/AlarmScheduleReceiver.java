package com.azinecllc.champy.receiver;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.azinecllc.champy.data.DBHelper;

/**
 * Created by SashaKhyzhun on 12/16/16.
 */

public class AlarmScheduleReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String newString;
        Bundle extras = intent.getExtras();
        newString = (extras == null) ? "" : extras.getString("alarm");

        if ("reset".equals(newString)) {
            DBHelper dbHelper = DBHelper.getInstance(context);
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            final ContentValues cv = new ContentValues();
            Cursor c = db.query("myChallenges", null, null, null, null, null, null);
            if (c.moveToFirst()) {
                int colchallenge_id = c.getColumnIndex("challenge_id");
                do {
                    cv.put("updated", "false");
                    String id = c.getString(colchallenge_id);
                    db.update("myChallenges", cv, "challenge_id = ?", new String[]{id});
                    db.update("updated", cv, "challenge_id = ?", new String[] {id});
                } while (c.moveToNext());
            }
            c.close();
        }
    }

}
