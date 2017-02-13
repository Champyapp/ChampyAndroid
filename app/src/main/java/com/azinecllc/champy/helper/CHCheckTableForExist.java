package com.azinecllc.champy.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * The method of checking some users in another table.
 * For example: while we load Friends Facebook in 'Other' table, we check each user in another table.
 * If we have current user in 'Pending Table' we just don't load this friend in 'Other' table
 */
public class CHCheckTableForExist {

    private SQLiteDatabase db;

    public CHCheckTableForExist(SQLiteDatabase db) {
        this.db = db;
    }

    public Boolean isInOtherTable(String id) {
        Boolean ok = false;
        String friendID;

        Cursor c = db.query("pending", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int i = c.getColumnIndex("user_id");
            do {
                friendID = c.getString(i);
                if (friendID.equals(id)) {
                    ok = true;
                    break;
                }
            } while (c.moveToNext());
        }

        c = db.query("friends", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int index = c.getColumnIndex("user_id");
            do {
                friendID = c.getString(index);
                if (friendID.equals(id)) {
                    ok = true;
                    break;
                }
            } while (c.moveToNext());
        }


        c.close();
        return ok;
    }

}
