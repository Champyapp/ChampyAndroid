package com.azinecllc.champy.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.azinecllc.champy.fragment.FriendsFragment;
import com.azinecllc.champy.fragment.MainCardsFragment;

/**
 * @autor SashaKhyzhun
 * Created on 4/4/17.
 */

public class MainActivityPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[]{"Challenges", "Friends"};
    public Context context;

    public MainActivityPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MainCardsFragment();
            case 1:
                return new FriendsFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }


}