package com.example.ivan.champy_v2.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class SelfImprovement_model {

    String goal;
    String days;
    String id;
    String updated;
    String status;
    String type;
    String name;
    String challengeName;
    String versus;
    String recipient;
    String senderProgress;

    public String getSenderProgress() {
        return senderProgress;
    }

    public void setSenderProgress(String senderProgress) {
        this.senderProgress = senderProgress;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getVersus() {
        return versus;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdated() {
        return updated;
    }

    public String getStatus() {
        return status;
    }

    public String getGoal() {
        return goal;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }


    public SelfImprovement_model( String mGoal, String mDays, String mType, String mid ,String mStatus, String mUpdated, String mChallengeName, String versus, String recipient, String senderProgress) {
        this.goal = mGoal;
        this.days = mDays;
        this.type = mType;
        this.id = mid;
        this.status = mStatus;
        this.updated = mUpdated;
        this.challengeName = mChallengeName;
        this.versus = versus;
        this.recipient = recipient;
        this.senderProgress = senderProgress;
    }


    //генерирует InProgress в History и с этими же данными генерирует карточки для MainActivity
    public static ArrayList<SelfImprovement_model> generate(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<SelfImprovement_model> arrayList = new ArrayList<>();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int idColIndex        = c.getColumnIndex("id");
            int nameColIndex      = c.getColumnIndex("name");
            int coldescription    = c.getColumnIndex("description");
            int colduration       = c.getColumnIndex("duration");
            int colchallenge_id   = c.getColumnIndex("challenge_id");
            int status            = c.getColumnIndex("status");
            int updated           = c.getColumnIndex("updated");
            int challengeName     = c.getColumnIndex("challengeName");
            int colversus         = c.getColumnIndex("versus");
            int colrecipient      = c.getColumnIndex("recipient");
            int colsencerprogress = c.getColumnIndex("senderProgress");
            do {
                if (c.getString(status).equals("started")) arrayList.add (new SelfImprovement_model(
                        c.getString(coldescription),
                        c.getString(colduration),
                        c.getString(nameColIndex),
                        c.getString(colchallenge_id),
                        "started",
                        c.getString(updated),
                        c.getString(challengeName),
                        c.getString(colversus),
                        c.getString(colrecipient),
                        c.getString(colsencerprogress)));
            } while (c.moveToNext());
        }
        c.close();
        return  arrayList;
    }

    //генерирует Wins в History
    public static ArrayList<SelfImprovement_model> generateWins(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<SelfImprovement_model> arrayList = new ArrayList<>();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int idColIndex        = c.getColumnIndex("id");
            int nameColIndex      = c.getColumnIndex("name");
            int coldescription    = c.getColumnIndex("description");
            int colduration       = c.getColumnIndex("duration");
            int colchallenge_id   = c.getColumnIndex("challenge_id");
            int status            = c.getColumnIndex("status");
            int updated           = c.getColumnIndex("updated");
            int challengeName     = c.getColumnIndex("challengeName");
            int colversus         = c.getColumnIndex("versus");
            int colrecipient      = c.getColumnIndex("recipient");
            int colsencerprogress = c.getColumnIndex("senderProgress");
            do {
                if (c.getString(colrecipient).equals("true")) {
                    if (c.getString(status).equals("failedBySender")) {
                        arrayList.add(new SelfImprovement_model(
                                c.getString(coldescription),
                                c.getString(colduration),
                                c.getString(nameColIndex),
                                c.getString(colchallenge_id),
                                "finished",
                                c.getString(updated),
                                c.getString(challengeName),
                                c.getString(colversus),
                                c.getString(colrecipient),
                                c.getString(colsencerprogress)));
                    }
                }
                if (c.getString(colrecipient).equals("false")) {
                    if (c.getString(status).equals("failedByRecipient")) {
                        arrayList.add(new SelfImprovement_model(
                                c.getString(coldescription),
                                c.getString(colduration),
                                c.getString(nameColIndex),
                                c.getString(colchallenge_id),
                                "finished",
                                c.getString(updated),
                                c.getString(challengeName),
                                c.getString(colversus),
                                c.getString(colrecipient),
                                c.getString(colsencerprogress)));
                    }
                    if (c.getString(status).equals("finished")) {
                        arrayList.add(new SelfImprovement_model(
                                c.getString(coldescription),
                                c.getString(colduration),
                                c.getString(nameColIndex),
                                c.getString(colchallenge_id),
                                "finished",
                                c.getString(updated),
                                c.getString(challengeName),
                                c.getString(colversus),
                                c.getString(colrecipient),
                                c.getString(colsencerprogress)));
                    }
                }

            } while (c.moveToNext());
        }
        c.close();
        return arrayList;
    }

    //генерирует Fails в History
    public static ArrayList<SelfImprovement_model> generateFailed(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<SelfImprovement_model> arrayList = new ArrayList<>();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int idColIndex        = c.getColumnIndex("id");
            int nameColIndex      = c.getColumnIndex("name");
            int coldescription    = c.getColumnIndex("description");
            int colduration       = c.getColumnIndex("duration");
            int colchallenge_id   = c.getColumnIndex("challenge_id");
            int status            = c.getColumnIndex("status");
            int updated           = c.getColumnIndex("updated");
            int challengeName     = c.getColumnIndex("challengeName");
            int colversus         = c.getColumnIndex("versus");
            int colrecipient      = c.getColumnIndex("recipient"); // check recipient
            int colsencerprogress = c.getColumnIndex("senderProgress");
            do {
                if (c.getString(colrecipient).equals("true")) {
                    if (c.getString(status).equals("failedByRecipient")) {
                        arrayList.add(new SelfImprovement_model(
                                c.getString(coldescription),
                                c.getString(colduration),
                                c.getString(nameColIndex),
                                c.getString(colchallenge_id),
                                "failed",
                                c.getString(updated),
                                c.getString(challengeName),
                                c.getString(colversus),
                                c.getString(colrecipient),
                                c.getString(colsencerprogress)));
                    }
                }
                if (c.getString(colrecipient).equals("false")) {
                    if (c.getString(status).equals("failedBySender")) {
                        arrayList.add(new SelfImprovement_model(
                                c.getString(coldescription),
                                c.getString(colduration),
                                c.getString(nameColIndex),
                                c.getString(colchallenge_id),
                                "failed",
                                c.getString(updated),
                                c.getString(challengeName),
                                c.getString(colversus),
                                c.getString(colrecipient),
                                c.getString(colsencerprogress)));
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        return  arrayList;
    }


}
