package com.example.ivan.champy_v2;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by ivan on 14.03.16.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    int PAGE_COUNT;
    private Context context;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public void setCount(int count) {
        this.PAGE_COUNT = count;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("stat", "Status: "+position);
        return SelfImprovementFragment.newInstance(position);
    }

}