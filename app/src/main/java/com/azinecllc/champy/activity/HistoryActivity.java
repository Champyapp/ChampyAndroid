package com.azinecllc.champy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.HistoryPagerAdapter;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.azinecllc.champy.activity.MainActivity.CURRENT_TAG;
import static com.azinecllc.champy.activity.MainActivity.navItemIndex;
import static com.azinecllc.champy.utils.Constants.TAG_CHALLENGES;
import static com.azinecllc.champy.utils.Constants.TAG_PRIVACY_POLICE;
import static com.azinecllc.champy.utils.Constants.TAG_SETTINGS;
import static com.azinecllc.champy.utils.Constants.TAG_TERMS;

public class HistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SessionManager sessionManager;
    private DrawerLayout drawer;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = SessionManager.getInstance(getApplicationContext());
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);


        ImageView layoutBackground = (ImageView) findViewById(R.id.history_background);
        final String userPicture = sessionManager.getUserPicture();
        Glide.with(this)
                .load(userPicture)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .bitmapTransform(new BlurTransformation(getApplicationContext(), 25))
                .into(layoutBackground);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_history);
        FragmentPagerAdapter adapterViewPager = new HistoryPagerAdapter(getSupportFragmentManager(), context);
        viewPager.setAdapter(adapterViewPager);
        viewPager.setOffscreenPageLimit(3);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs_history);
        tabLayout.setupWithViewPager(viewPager);

        // this out method for open "in progress" when you click on notification about win request
        String extras = this.getIntent().getStringExtra("challenge_request_win");
        if (extras != null && "true".equals(extras)) { viewPager.setCurrentItem(1); }

        // OTHER DRAWER INFO
        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.drawer_user_photo);
        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.drawer_background);
        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.drawer_tv_user_name);
        TextView drawerUserEmail = (TextView) headerLayout.findViewById(R.id.drawer_tv_user_email);
        Glide.with(this)
                .load(userPicture)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(drawerImageProfile);
        Glide.with(this)
                .load(userPicture)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .bitmapTransform(new BlurTransformation(this, 25))
                .into(drawerBackground);
        final String userEmail = sessionManager.getUserEmail();
        final String userName = sessionManager.getUserName();
        drawerUserName.setText(userName);
        drawerUserEmail.setText(userEmail);

        // SETTING CURRENT PENDING COUNT
        CHCheckPendingDuels checker = CHCheckPendingDuels.getInstance();
        int count = checker.getPendingCount(getApplicationContext());
        if (count != 0) {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_duels).getActionView();
            view.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            navItemIndex = 0;
            CURRENT_TAG = TAG_CHALLENGES;
            startActivity(new Intent(this, MainActivity.class));
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        switch (item.getItemId()) {
            case R.id.nav_challenges:
                navItemIndex = 0;
                CURRENT_TAG = TAG_CHALLENGES;
                startActivity(intent);
                break;
            case R.id.nav_friends:
                new Handler().postDelayed(() -> startActivity(new Intent(this, FriendsActivity.class)), 250);
                break;
            case R.id.nav_history:
                break;
            case R.id.nav_pending_duels:
                new Handler().postDelayed(() -> startActivity(new Intent(this, PendingDuelActivity.class)), 250);
                break;
            case R.id.nav_settings:
                navItemIndex = 2;
                CURRENT_TAG = TAG_SETTINGS;
                startActivity(intent);
                break;
            case R.id.nav_terms:
                navItemIndex = 3;
                CURRENT_TAG = TAG_TERMS;
                startActivity(intent);
                break;
            case R.id.nav_privacy_policy:
                navItemIndex = 4;
                CURRENT_TAG = TAG_PRIVACY_POLICE;
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
