package com.azinecllc.champy.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private TabLayout tabLayout;
    private int[] tabIcons = { R.mipmap.ic_tab_friends, R.mipmap.ic_tab_friends };

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

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_with_tabs, container, false);
        Log.i(TAG, "onCreateView: ");
        setHasOptionsMenu(true);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager_main);
        viewPager.setOffscreenPageLimit(2);
        MainActivityPagerAdapter pagerAdapter = new MainActivityPagerAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout_main);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(TAG, "onTabSelected: " + tab.getPosition());
                String currentTitle = (tab.getPosition() == 0) ? "Challenge" : "Friends";
                Log.i(TAG, "onTabSelected: " + currentTitle);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i(TAG, "onTabUnselected: ");

                Log.i(TAG, "onTabUnselected: SearchView: + searchView");


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i(TAG, "onTabReselected: ");
            }
        });

//        final TabLayout.Tab tab1 = tabLayout.newTab();
//        final TabLayout.Tab tab2 = tabLayout.newTab();
//        tab1.setIcon(R.mipmap.ic_tab_friends);
//        tab2.setIcon(R.mipmap.ic_tab_friends);
//
//        tabLayout.addTab(tab1);
//        tabLayout.addTab(tab2);


        //CoordinatorLayout layout = (CoordinatorLayout) view.findViewById(R.id.layout_fragment_main);

//        layout = (RelativeLayout) view.findViewById(R.id.layout_fragment_main);
//        layout.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY > oldScrollY) {
//                    fab.hide();
//                } else {
//                    fab.show();
//                }
//            }
//        });


        /** Care With THIS, ALLO */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabLayout.setElevation(15);
        } else {
            //android:background="@android:drawable/dialog_holo_light_frame";
            //tabLayout.setBackground(android.R.drawable.dialog_holo_light_frame);
            Drawable drawable = getResources().getDrawable(android.R.drawable.dialog_holo_light_frame);
            tabLayout.setBackground(drawable);
        }

        return view;

    }


    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_custom_tab, null);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[0], 0, 0);
        tabOne.setSelected(true); // set as default;
        //tabOne.setBackground(getResources().getDrawable(R.drawable.tab_challenges_selector));
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_custom_tab, null);
        tabTwo.setTextColor(Color.WHITE);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[1], 0, 0);
        //tabOne.setBackground(getResources().getDrawable(R.drawable.tab_friends_selector));
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }


    /**
     * Method to set visible for FabPlus if current fragment != main
     */
//    private void toggleFab() {
//        if (navItemIndex == 0) {
//            fab.show();
//        } else {
//            fab.hide();
//        }
//    }


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
