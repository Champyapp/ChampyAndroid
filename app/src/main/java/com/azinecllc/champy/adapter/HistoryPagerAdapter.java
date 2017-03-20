//package com.azinecllc.champy.adapter;
//
//import android.content.Context;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//
//import com.azinecllc.champy.fragment.HistoryFailedFragment;
//import com.azinecllc.champy.fragment.HistoryInProgressFragment;
//import com.azinecllc.champy.fragment.HistoryWinsFragment;
//
//public class HistoryPagerAdapter extends FragmentPagerAdapter {
//
//    private String tabTitles[] = new String[] { "In Progress", "Wins", "Failed" };
//    public Context context;
//
//    public HistoryPagerAdapter(FragmentManager fm, Context context) {
//        super(fm);
//        this.context = context;
//    }
//
//    @Override
//    public int getCount() {
//        return 3;
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        switch(position) {
//            case 0: return new HistoryInProgressFragment();
//            case 1: return new HistoryWinsFragment();
//            case 2: return new HistoryFailedFragment();
//           default: return new Fragment();
//        }
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return tabTitles[position];
//    }
//
//}
