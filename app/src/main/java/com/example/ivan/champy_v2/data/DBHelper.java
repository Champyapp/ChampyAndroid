package com.example.ivan.champy_v2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создаем таблицу с полями
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

        db.execSQL("create table friends ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "photo text,"
                + "user_id text,"
                + "fb_id text"
                + ");");

        db.execSQL("create table pending ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "photo text,"
                + "user_id text,"
                + "owner text"
                + ");");

        db.execSQL("create table selfimprovement("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "description text,"
                + "duration text,"
                + "challenge_id text,"
                + "status text"
                + ");");

        db.execSQL("create table duel("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "description text,"
                + "duration text,"
                + "challenge_id text,"
                + "status text"
                + ");");

        db.execSQL("create table pending_duel("
                + "id integer primary key autoincrement,"
                + "versus text,"
                + "description text,"
                + "duration text,"
                + "challenge_id text,"
                + "recipient text"
                + ");");

        db.execSQL("create table myChallenges("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "challengeName text DEFAULT 'challenge',"
                + "recipient text,"
                + "versus text,"
                + "description text,"
                + "duration text,"
                + "challenge_id text,"
                + "status text,"
                + "updated text"
                + ");");

        db.execSQL("create table updated("
                + "id integer primary key autoincrement,"
                + "challenge_id text,"
                + "updated text"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

