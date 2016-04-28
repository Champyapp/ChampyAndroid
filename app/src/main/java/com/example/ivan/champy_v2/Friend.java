package com.example.ivan.champy_v2;

/**
 * Created by ivan on 05.02.16.
 */
public class Friend {
    private String mName;
    private String mPicture;
    private String mID;
   // private List<Friend> friends;

    public Friend(String name, String picture, String ID) {
        mName = name;
        mPicture = picture;
        mID = ID;
    }

    public String getName() {
        return mName;
    }

    public String getPicture() {
        return mPicture;
    }

    public String  getID(){ return  mID; }

    public void setID(String id) { mID = id; }

    private static int lastFriendId = 0;

}
