package com.example.ivan.champy_v2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ivan on 04.03.16.
 */
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
                + "fb_id text"
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
                + "challenge_id text"
                + ");");

        db.execSQL("create table myChallenges("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "description text,"
                + "duration text,"
                + "challenge_id"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

