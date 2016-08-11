package com.example.ivan.champy_v2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.ivan.champy_v2.data.DBHelper;

public class AlarmSchedule extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String newString;
        //Log.d("myLogs", "Updated schedule");
        Bundle extras = intent.getExtras();
        if(extras == null) {
            newString = null;
        } else {
            newString = extras.getString("alarm");
        }

        //newString.equals("reset");


        if ("reset".equals(newString)) {
            //Log.d("myLogs", "Updated schedule");
            DBHelper dbHelper = new DBHelper(context);
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            final ContentValues cv = new ContentValues();
            Cursor c = db.query("myChallenges", null, null, null, null, null, null);
            int o = 0;
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
