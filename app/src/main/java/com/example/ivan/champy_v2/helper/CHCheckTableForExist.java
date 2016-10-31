package com.example.ivan.champy_v2.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ivan.champy_v2.data.DBHelper;

/**
 * Ultra stupid solution for checking other table.
 */
public class CHCheckTableForExist {

    Context context;

    public CHCheckTableForExist(Context context) {
        this.context = context;
    }

    public Boolean isInOtherTable(String id) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Boolean ok = false;
        Cursor c = db.query("pending", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int index = c.getColumnIndex("user_id");
            do {
                String user_id = c.getString(index);
                if (user_id.equals(id)) {
                    ok = true;
                    break;
                }
            } while (c.moveToNext());
        }
        c = db.query("friends", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int index = c.getColumnIndex("user_id");
            do {
                String user_id = c.getString(index);
                if (user_id.equals(id)) {
                    ok = true;
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();
        return ok;
    }

}
