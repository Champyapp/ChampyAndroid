package com.example.ivan.champy_v2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by ivan on 04.04.16.
 */
public class AlarmSchedule extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String newString;
        Bundle extras = intent.getExtras();
        if(extras == null) {
            newString= null;
        } else {
            newString= extras.getString("alarm");
        }
        if (newString.equals("reset")){
            Log.d("myLogs", "Updated schedule");;
            DBHelper dbHelper = new DBHelper(context);
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            final ContentValues cv = new ContentValues();
            Cursor c = db.query("myChallenge", null, null, null, null, null, null);
            int o = 0;
            if (c.moveToFirst()) {
                int colchallenge_id = c.getColumnIndex("challenge_id");
                do {
                    cv.put("updated", "false");
                    String id = c.getString(colchallenge_id);
                    db.update("myChallenges", cv, "challenge_id = ?", new String[]{id});
                    db.update("updated", cv, "challenge_id = ?", new String[] {id});
                } while (c.moveToNext());
            } else
                Log.i("stat", "0 rows");
            c.close();

        }

    }
}
