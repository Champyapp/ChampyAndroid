package com.azinecllc.champy.interfaces;

import android.view.View;

import com.azinecllc.champy.model.FriendModel;

public interface CustomRecyclerClickListener {
    void onItemClick(View v, FriendModel currentFriend);
}
