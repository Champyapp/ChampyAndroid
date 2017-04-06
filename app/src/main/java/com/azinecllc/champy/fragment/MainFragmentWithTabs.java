package com.azinecllc.champy.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.MainActivityPagerAdapter;

/**
 * @autor SashaKhyzhun
 * Created on 4/4/17.
 */

public class MainFragmentWithTabs extends Fragment {

    public static final String TAG = "MainFragmentWithTabs";
    private int[] tabIcons = {
            R.mipmap.nav_challenges,
            R.mipmap.nav_friends,
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_with_tabs, container, false);

        Log.i(TAG, "onCreateView: ");
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager_main);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout_main);
        tabLayout.setupWithViewPager(viewPager);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabLayout.setElevation(15);
        } else {
            //android:background="@android:drawable/dialog_holo_light_frame";
            //tabLayout.setBackground(android.R.drawable.dialog_holo_light_frame);
            Drawable drawable = getResources().getDrawable(android.R.drawable.dialog_holo_light_frame);
            tabLayout.setBackground(drawable);

        }
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);



        MainActivityPagerAdapter adapterViewPager = new MainActivityPagerAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(adapterViewPager);


        return view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: ");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
    }

}
