package com.azinecllc.champy.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.View;

import com.azinecllc.champy.R;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.utils.SessionManager;

/**
 * this class helps us to check pending duels and if count > 0 then we create new menu
 * "pending duels" in navigation drawer. Else we hide this menu.
 */
public class CHCheckPendingDuels {

    private static CHCheckPendingDuels instance = null;

    private CHCheckPendingDuels() {}

    public static CHCheckPendingDuels getInstance() {
        if (instance == null) {
            instance = new CHCheckPendingDuels();
        }
        return instance;
    }

    public int getPendingCount(Context context) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("pending_duel", null, null, null, null, null, null);
        int countOfPendingDuel = 0;
        if (c.moveToFirst()) {
            do {
                countOfPendingDuel++;
            } while (c.moveToNext());
        }
        c.close();
        SessionManager sessionManager = SessionManager.getInstance(context);
        sessionManager.set_duel_pending(""+ countOfPendingDuel);
        return countOfPendingDuel;
    }

    public void hideItem(View view) {
        NavigationView navigationView = (NavigationView)view.findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_pending_duels).setVisible(false);
    }

}
