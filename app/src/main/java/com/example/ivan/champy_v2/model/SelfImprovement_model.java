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

    private String goal;
    private String days;
    private String id;
    private String updated;
    private String status;
    private String type;
    private String name;
    private String challengeName;
    private String versus;
    private String recipient;
    private String senderProgress;
    private String wakeUpTime;

    public void setSenderProgress(String senderProgress) {
        this.senderProgress = senderProgress;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public void setWakeUpTime(String wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVersus(String versus) {
        this.versus = versus;
    }

    public String getSenderProgress() {
        return senderProgress;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWakeUpTime() {
        return wakeUpTime;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdated() {
        return updated;
    }

    public String getVersus() {
        return versus;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getGoal() {
        return goal;
    }

    public String getDays() {
        return days;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    // constructor :P
    private SelfImprovement_model(String mGoal, String mDays, String mType, String mid, String mStatus, String mUpdated,
                                  String mChallengeName, String versus, String recipient, String senderProgress, String wakeUpTime) {
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
        this.wakeUpTime = wakeUpTime;
    }


    //this method generates InProgress for History and for cards in MainActivity
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
            int wakeUpTimeCol     = c.getColumnIndex("wakeUpTime");
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
                        c.getString(colsencerprogress),
                        c.getString(wakeUpTimeCol)));
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
            int wakeUpTimeCol     = c.getColumnIndex("wakeUpTime");
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
                                c.getString(colsencerprogress),
                                c.getString(wakeUpTimeCol)));
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
                                c.getString(colsencerprogress),
                                c.getString(wakeUpTimeCol)));
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
                                c.getString(colsencerprogress),
                                c.getString(wakeUpTimeCol)));
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
            int wakeUpTimeCol     = c.getColumnIndex("wakeUpTime");
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
                                c.getString(colsencerprogress),
                                c.getString(wakeUpTimeCol)));
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
                                c.getString(colsencerprogress),
                                c.getString(wakeUpTimeCol)));
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        return  arrayList;
    }


    private void checkIfDoneForToday() {

    }


}
