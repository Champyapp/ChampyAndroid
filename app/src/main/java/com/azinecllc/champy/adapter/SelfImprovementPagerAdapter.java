package com.azinecllc.champy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.azinecllc.champy.fragment.ChallengeSelfFragment;

public class SelfImprovementPagerAdapter extends FragmentStatePagerAdapter {

    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    private int size = 0;

    public SelfImprovementPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return size+1;
    }


    public void setCount(int count){
        size = count;
    }

    @Override
    public Fragment getItem(int position) {
        return ChallengeSelfFragment.newInstance(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

}
