package com.example.ivan.champy_v2.model;

/**
 * Created by ivan on 05.02.16.
 */
public class Pending_friend {

    private String mName;
    private String mPicture;
    private String mID;
    private String mOwner;
    private String mTotal;
    private String mChallenges;
    private String mWins;

    public Pending_friend(String name, String picture, String ID, String owner, String wins, String total, String challenges) {
        mName = name;
        mPicture = picture;
        mID = ID;
        mOwner = owner;
        mWins = wins;
        mTotal = total;
        mChallenges = challenges;
    }

    public String getName() {
        return mName;
    }

    public String getPicture() {
        return mPicture;
    }

    public String  getID(){ return  mID; }

    public void setID(String id) { mID = id; }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String mowner) {
        this.mOwner = mowner;
    }

    private static int lastFriendId = 0;

    public String getmWins() {
        return mWins;
    }

    public String getmChallenges() {
        return mChallenges;
    }

    public String getmTotal() {
        return mTotal;
    }

    public void setmWins(String mWins) {
        this.mWins = mWins;
    }

    public void setmTotal(String mTotal) {
        this.mTotal = mTotal;
    }

    public void setmChallenges(String mChallenges) {
        this.mChallenges = mChallenges;
    }
}
