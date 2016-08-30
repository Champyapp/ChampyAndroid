package com.example.ivan.champy_v2.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.ivan.champy_v2.fragment.HistoryFailedFragment;
import com.example.ivan.champy_v2.fragment.HistoryInProgressFragment;
import com.example.ivan.champy_v2.fragment.HistoryWinsFragment;

public class HistoryPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "In Progress", "Wins", "Failed" };
    private Context context;

    public HistoryPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return new HistoryInProgressFragment();
            case 1: return new HistoryWinsFragment();
            case 2: return new HistoryFailedFragment();
            default: return new Fragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
