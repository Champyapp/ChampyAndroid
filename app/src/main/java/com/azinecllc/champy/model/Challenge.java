package com.azinecllc.champy.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.azinecllc.champy.data.DBHelper;

import java.util.ArrayList;

public class Challenge {

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
    private String progress;
    private String wakeUpTime;
    private String needsToCheck;
    private String constDuration;

    /**
     * setters
     */
    public void setConstDuration(String constDuration) {
        this.constDuration = constDuration;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public void setNeedsToCheck(String needsToCheck) {
        this.needsToCheck = needsToCheck;
    }

    public void setWakeUpTime(String wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setProgress(String progress) {
        this.progress = progress;
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

    public void setId(String id) {
        this.id = id;
    }

    /**
     * getters
     */


    public String getConstDuration() {
        return constDuration;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public String getNeedsToCheck() {
        return needsToCheck;
    }

    public String getWakeUpTime() {
        return wakeUpTime;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getProgress() {
        return progress;
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
    private Challenge(String mGoal, String mDays, String mType, String mId, String mStatus,
                      String mChallengeName, String versus, String recipient, String senderProgress,
                      String wakeUpTime, String constDuration, String needsToCheck) {

        this.goal = mGoal;
        this.days = mDays;
        this.type = mType;
        this.id = mId;
        this.status = mStatus;
        this.challengeName = mChallengeName;
        this.versus = versus;
        this.recipient = recipient;
        this.progress = senderProgress;
        this.wakeUpTime = wakeUpTime;
        this.constDuration = constDuration;
        this.needsToCheck = needsToCheck;

    }


    // this method generates InProgress for History and for cards in MainActivity
    public static ArrayList<Challenge> generate(Context context) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<Challenge> arrayList = new ArrayList<>();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int idColIndex        = c.getColumnIndex("id");
            int nameColIndex      = c.getColumnIndex("name");
            int coldescription    = c.getColumnIndex("description");
            int colduration       = c.getColumnIndex("duration");
            int colchallenge_id   = c.getColumnIndex("challenge_id");
            int status            = c.getColumnIndex("status");
            int challengeName     = c.getColumnIndex("challengeName");
            int colversus         = c.getColumnIndex("versus");
            int colrecipient      = c.getColumnIndex("recipient");
            int colsencerprogress = c.getColumnIndex("myProgress");
            int wakeUpTimeCol     = c.getColumnIndex("wakeUpTime");
            int colConstDuration  = c.getColumnIndex("constDuration");
            int needsToCheckSender= c.getColumnIndex("needsToCheck");

            do {
                if (c.getString(status).equals("started")) arrayList.add(new Challenge(
                        c.getString(coldescription),
                        c.getString(colduration),
                        c.getString(nameColIndex),
                        c.getString(colchallenge_id),
                        "started",
                        c.getString(challengeName),
                        c.getString(colversus),
                        c.getString(colrecipient),
                        c.getString(colsencerprogress),
                        c.getString(wakeUpTimeCol),
                        c.getString(colConstDuration),
                        c.getString(needsToCheckSender)));
            } while (c.moveToNext());
        }
        c.close();
        return  arrayList;
    }

    // generate Wins in History
    public static ArrayList<Challenge> generateWins(Context context) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<Challenge> arrayList = new ArrayList<>();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int idColIndex        = c.getColumnIndex("id");
            int nameColIndex      = c.getColumnIndex("name");
            int coldescription    = c.getColumnIndex("description");
            int colduration       = c.getColumnIndex("duration");
            int colchallenge_id   = c.getColumnIndex("challenge_id");
            int status            = c.getColumnIndex("status");
            int challengeName     = c.getColumnIndex("challengeName");
            int colversus         = c.getColumnIndex("versus");
            int colrecipient      = c.getColumnIndex("recipient");
            int colsencerprogress = c.getColumnIndex("myProgress");
            int wakeUpTimeCol     = c.getColumnIndex("wakeUpTime");
            int colConstDuration  = c.getColumnIndex("constDuration");
            int needsToCheckSender= c.getColumnIndex("needsToCheck");

            do {
                if (c.getString(colrecipient).equals("true") && c.getString(status).equals("failedBySender")) {
                    arrayList.add(new Challenge(
                                c.getString(coldescription),
                                c.getString(colduration),
                                c.getString(nameColIndex),
                                c.getString(colchallenge_id),
                                "finished",
                                c.getString(challengeName),
                                c.getString(colversus),
                                c.getString(colrecipient),
                                c.getString(colsencerprogress),
                                c.getString(wakeUpTimeCol),
                                c.getString(colConstDuration),
                                c.getString(needsToCheckSender)));
                }
                if (c.getString(colrecipient).equals("false")) {
                    if (c.getString(status).equals("failedByRecipient")) {
                        arrayList.add(new Challenge(
                                c.getString(coldescription),
                                c.getString(colduration),
                                c.getString(nameColIndex),
                                c.getString(colchallenge_id),
                                "finished",
                                c.getString(challengeName),
                                c.getString(colversus),
                                c.getString(colrecipient),
                                c.getString(colsencerprogress),
                                c.getString(wakeUpTimeCol),
                                c.getString(colConstDuration),
                                c.getString(needsToCheckSender)));
                    }
                    else if (c.getString(status).equals("finished")) {
                        arrayList.add(new Challenge(
                                c.getString(coldescription),
                                c.getString(colduration),
                                c.getString(nameColIndex),
                                c.getString(colchallenge_id),
                                "finished",
                                c.getString(challengeName),
                                c.getString(colversus),
                                c.getString(colrecipient),
                                c.getString(colsencerprogress),
                                c.getString(wakeUpTimeCol),
                                c.getString(colConstDuration),
                                c.getString(needsToCheckSender)));
                    }
                }

            } while (c.moveToNext());
        }
        c.close();
        return arrayList;
    }

    // generate Fails in History
    public static ArrayList<Challenge> generateFailed(Context context) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<Challenge> arrayList = new ArrayList<>();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int idColIndex        = c.getColumnIndex("id");
            int nameColIndex      = c.getColumnIndex("name");
            int coldescription    = c.getColumnIndex("description");
            int colduration       = c.getColumnIndex("duration");
            int colchallenge_id   = c.getColumnIndex("challenge_id");
            int status            = c.getColumnIndex("status");
            int challengeName     = c.getColumnIndex("challengeName");
            int colversus         = c.getColumnIndex("versus");
            int colrecipient      = c.getColumnIndex("recipient"); // check recipient
            int colsencerprogress = c.getColumnIndex("myProgress");
            int wakeUpTimeCol     = c.getColumnIndex("wakeUpTime");
            int colConstDuration  = c.getColumnIndex("constDuration");
            int needsToCheckSender= c.getColumnIndex("needsToCheck");

            do {
                if (c.getString(colrecipient).equals("true")) {
                    if (c.getString(status).equals("failedByRecipient")) {
                        arrayList.add(new Challenge(
                                c.getString(coldescription),
                                c.getString(colduration),
                                c.getString(nameColIndex),
                                c.getString(colchallenge_id),
                                "failed",
                                c.getString(challengeName),
                                c.getString(colversus),
                                c.getString(colrecipient),
                                c.getString(colsencerprogress),
                                c.getString(wakeUpTimeCol),
                                c.getString(colConstDuration),
                                c.getString(needsToCheckSender)));
                    }
                }
                if (c.getString(colrecipient).equals("false")) {
                    if (c.getString(status).equals("failedBySender")) {
                        arrayList.add(new Challenge(
                                c.getString(coldescription),
                                c.getString(colduration),
                                c.getString(nameColIndex),
                                c.getString(colchallenge_id),
                                "failed",
                                c.getString(challengeName),
                                c.getString(colversus),
                                c.getString(colrecipient),
                                c.getString(colsencerprogress),
                                c.getString(wakeUpTimeCol),
                                c.getString(colConstDuration),
                                c.getString(needsToCheckSender)));
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        return  arrayList;
    }

}