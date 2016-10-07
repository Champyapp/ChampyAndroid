package com.example.ivan.champy_v2.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.example.ivan.champy_v2.fragment.PendingDuelFragment;

public class PendingDuelsAdapter extends FragmentStatePagerAdapter {

    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private int size = 0;

    public PendingDuelsAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return size;
    }


    public void setCount(int count){
        size = count;
    }

    @Override
    public Fragment getItem(int position) {
        return PendingDuelFragment.newInstance(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }


    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        notifyDataSetChanged();
        super.destroyItem(container, position, object);
    }


    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

}
