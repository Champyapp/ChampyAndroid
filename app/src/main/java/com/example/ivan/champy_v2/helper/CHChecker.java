package com.example.ivan.champy_v2.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.data.DBHelper;

public class CHChecker {

    public int checkPending(Activity activity) {
        DBHelper dbHelper = new DBHelper(activity);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        Cursor c = db.query("pending_duel", null, null, null, null, null, null);
        int o = 0;
        if (c.moveToFirst()) {
            do {
                o++;
            } while (c.moveToNext());
        }
        c.close();
        SessionManager sessionManager = new SessionManager(activity);
        sessionManager.set_duel_pending("" + o);
        Log.d("TAG", "O: " + o);
        return o;
    }

    public boolean checkChellenges(String id, Activity activity) {
        boolean ok = true;
        DBHelper dbHelper = new DBHelper(activity);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        int o = 0;
        if (c.moveToFirst()) {
            int colchallenge_id = c.getColumnIndex("challenge_id");
            do {
                String checked = c.getString(colchallenge_id);
                if (checked.equals(id)) {
                    Log.i("stat", "Checked");
                    ok = false;
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();
        return ok;
    }


}





