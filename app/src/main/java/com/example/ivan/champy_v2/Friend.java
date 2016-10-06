package com.example.ivan.champy_v2;

/**
 * Don't replace this class.
 */
public class Friend {

    private String mChallenges;
    private String mPicture;
    private String mLevel;
    private String mTotal;
    private String mName;
    private String mWins;
    private String mID;
    //private List<Friend> friends;

    public Friend(String name, String picture, String ID, String challenges, String wins, String total, String level) {
        mName = name;
        mPicture = picture;
        mID = ID;
        mChallenges = challenges;
        mWins = wins;
        mTotal = total;
        mLevel = level;
    }

    public String getmChallenges() {
        return mChallenges;
    }

    public String getmWins() {
        return mWins;
    }

    public String getmTotal() {
        return mTotal;
    }

    public String getmLevel() {
        return mLevel;
    }

    public String getName() {
        return mName;
    }

    public String getPicture() {
        return mPicture;
    }

    public String getID() { return  mID; }

    // TODO: 06.10.2016 Delete Trash
    public void setmLevel(String mLevel) {
        this.mLevel = mLevel;
    }

    public void setmChallenges(String mChallenges) {
        this.mChallenges = mChallenges;
    }

    public void setmWins(String mWins) {
        this.mWins = mWins;
    }

    public void setmTotal(String mTotal) {
        this.mTotal = mTotal;
    }

    public void setID(String id) { mID = id; }

    private static int lastFriendId = 0;

}
