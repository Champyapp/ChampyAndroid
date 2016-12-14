package com.azinecllc.champy.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.azinecllc.champy.fragment.FriendsFragment;
import com.azinecllc.champy.fragment.OtherFragment;
import com.azinecllc.champy.fragment.PendingFragment;

public class FriendsActivityPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] { "Friends", "Pending", "Other" };
    public Context context;

    public FriendsActivityPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return new FriendsFragment();
            case 1: return new PendingFragment();
            case 2: return new OtherFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }


}