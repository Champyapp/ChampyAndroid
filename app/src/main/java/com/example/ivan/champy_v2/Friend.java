package com.example.ivan.champy_v2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 05.02.16.
 */
public class Friend {
    private String mName;
    private String mPicture;
    private int mID;
   // private List<Friend> friends;

    public Friend(String name, String picture, int ID) {
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

    public int getID(){ return  mID; }

    public void setID(int id) { mID = id; }

    private static int lastFriendId = 0;

    /*public List<Friend> getFriends()
    {
        return friends;
    }

    public void add_user(String name, String picture)
    {
        friends.add(new Friend(name, picture));
    }*/

    public static List<Friend> createFriendsList() {
        List<Friend> friends = new ArrayList<Friend>();

        for (int i=0; i<20; i++)
        {
            friends.add(new Friend("My friend number "+i, "http://loremflickr.com/320/240?random="+(i+1), 0));
        }
        return friends;

    }
}
