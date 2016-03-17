package com.example.ivan.champy_v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by ivan on 14.03.16.
 */
public class SelfImprovement_model {

    String goal;
    String days;
    private Context context;
    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }


    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SelfImprovement_model( String mgoal, String mdays, String mtype)
    {
        this.goal = mgoal;
        this.days = mdays;
        this.type = mtype;
    }

    public static ArrayList<SelfImprovement_model> generate(Context context)
    {
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        final ContentValues cv = new ContentValues();

        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        final SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        String id = user.get("id");
        ArrayList<SelfImprovement_model> arrayList = new ArrayList<>();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        int o = 0;
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int coldescription = c.getColumnIndex("description");
            int colduration = c.getColumnIndex("duration");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            Log.i("stat", "Statuskwo: o="+o);
            do {
              arrayList.add (new SelfImprovement_model(c.getString(coldescription), c.getString(colduration), c.getString(nameColIndex)));
            } while (c.moveToNext());
        } else
            Log.i("stat", "kwo0 rows");
        c.close();
        return  arrayList;
    }

}
