package com.azinecllc.champy.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Ultra stupid solution for checking other table.
 */
public class CHCheckTableForExist {

    private SQLiteDatabase db;

    public CHCheckTableForExist(SQLiteDatabase db) {
        this.db = db;
    }

    public Boolean isInOtherTable(String id) {
        Boolean ok = false;
        String user_id;

        Cursor c = db.query("pending", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int index = c.getColumnIndex("user_id");
            do {
                user_id = c.getString(index);
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
                user_id = c.getString(index);
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
