package com.azinecllc.champy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Our Local DataBase which was named "myDB"
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance = null;
    private static final String DB_NAME = "myDB";
    private static final int DB_VERSION = 1;

    private DBHelper(Context context) {
        // constructor for superclass
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // table for save info about "other" page
        db.execSQL("create table mytable ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "photo text,"
                + "user_id text,"
                + "fb_id text,"
                + "challenges text,"
                + "wins text,"
                + "total text,"
                + "level text"
                + ");");

        // table for save info about "friends" page
        db.execSQL("create table friends ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "photo text,"
                + "user_id text,"
                + "fb_id text,"
                + "inProgressChallengesCount text DEFAULT '0',"
                + "successChallenges text DEFAULT '0',"
                + "allChallengesCount text DEFAULT '0',"
                + "level text DEFAULT '0'"
                + ");");

        // table for save info about "pending" page
        db.execSQL("create table pending ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "photo text,"
                + "user_id text,"
                + "owner text,"
                + "inProgressChallengesCount text DEFAULT '0',"
                + "successChallenges text DEFAULT '0',"
                + "allChallengesCount text DEFAULT '0',"
                + "level text DEFAULT '0'"
                + ");");

        // table for store cards for self-improvement challenges
        db.execSQL("create table selfimprovement("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "description text,"
                + "duration text,"
                + "challenge_id text,"
                + "status text"
                + ");");

        // table for store cards for duel challenges
        db.execSQL("create table duel("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "description text,"
                + "duration text,"
                + "challenge_id text,"
                + "status text"
                + ");");

        // table for store cards in pending_duel activity
        db.execSQL("create table pending_duel("
                + "id integer primary key autoincrement,"
                + "versus text,"
                + "description text,"
                + "duration text,"
                + "challenge_id text,"
                + "recipient text"
                + ");");


        // table for store MY(!) in progress challenges and other data
        db.execSQL("create table myChallenges("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "challengeName text DEFAULT 'challenge',"
                + "recipient text DEFAULT 'false',"
                + "wakeUpTime text DEFAULT '',"
                + "versus text,"
                + "description text,"
                + "duration text,"
                + "challenge_id text,"
                + "status text,"
                + "updated text,"
                + "myProgress text DEFAULT [],"
                + "constDuration text DEFAULT '',"
                + "needsToCheck text DEFAULT ''"
                + ");");

        // table-helper for "myChallenges". This table store inProgressId and last check-in time
        // because when we create or delete some challenge we rewrite "myChallenge" and we need to
        // save important data like this:
        db.execSQL("create table updated("
                + "id integer primary key autoincrement,"
                + "challenge_id text,"
                + "updated text,"
                + "myProgress text DEFAULT [],"
                + "dailyRemind text DEFAULT 'true'"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // we don't need to update our data base if project not in production
    }
}

