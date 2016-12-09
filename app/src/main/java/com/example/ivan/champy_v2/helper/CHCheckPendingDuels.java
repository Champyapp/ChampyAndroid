package com.example.ivan.champy_v2.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.View;

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.utils.SessionManager;

/**
 * this class helps us to check pending duels and if count > 0 then we create new menu
 * "pending duels" in navigation drawer. Else we hide this menu.
 */
public class CHCheckPendingDuels {

    private Context context;
    private SessionManager sessionManager;
    private View view;

    public CHCheckPendingDuels(Context context, View view, SessionManager sessionManager) {
        this.context = context;
        this.view = view;
        this.sessionManager = sessionManager;
    }

    public int getPendingCount() {
        DBHelper dbHelper = DBHelper.getInstance(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("pending_duel", null, null, null, null, null, null);
        int countOfPendingDuel = 0;
        if (c.moveToFirst()) {
            do {
                countOfPendingDuel++;
            } while (c.moveToNext());
        }
        c.close();
        sessionManager.set_duel_pending(String.valueOf(countOfPendingDuel));
        return countOfPendingDuel;
    }

    public void hideItem() {
        NavigationView navigationView = (NavigationView)view.findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.pending_duels).setVisible(false);
    }

}
