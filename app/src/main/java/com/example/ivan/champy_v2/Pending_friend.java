package com.example.ivan.champy_v2;

/**
 * Created by ivan on 05.02.16.
 */
public class Pending_friend {
    private String mName;
    private String mPicture;
    private String mID;
    private String mOwner;



    public Pending_friend(String name, String picture, String ID, String owner) {
        mName = name;
        mPicture = picture;
        mID = ID;
        mOwner = owner;
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

    /*public List<Friend> getFriends()
    {
        return friends;
    }

    public void add_user(String name, String picture)
    {
        friends.add(new Friend(name, picture));
    }*/

}
