package com.example.ivan.champy_v2.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.data.DBHelper;

public class CHCheckPendingDuels {

    Context context;
    View view;

    public CHCheckPendingDuels(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public int checkPending() {
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        Cursor c = db.query("pending_duel", null, null, null, null, null, null);
        int countOfPendingDuel = 0;
        if (c.moveToFirst()) {
            do {
                countOfPendingDuel++;
            } while (c.moveToNext());
        }
        c.close();
        SessionManager sessionManager = new SessionManager(context);
        sessionManager.set_duel_pending("" + countOfPendingDuel);
        Log.i("CheckPending", "           Pend_Count: " + countOfPendingDuel);
        Log.i("RefreshPendingDuels", "    onResponse: VSE OK");
        return countOfPendingDuel;
    }

    public void hideItem() {
        NavigationView navigationView = (NavigationView)view.findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.pending_duels).setVisible(false);
        Log.i("CheckPending", "             hideItem: VSE OK");
    }

}
