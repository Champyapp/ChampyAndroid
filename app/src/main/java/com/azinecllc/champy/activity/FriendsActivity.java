package com.azinecllc.champy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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
import com.azinecllc.champy.adapter.FriendsActivityPagerAdapter;
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

public class FriendsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SessionManager sessionManager = SessionManager.getInstance(getApplicationContext());

        // MAKE BACKGROUND (great again)
        String userPicture = sessionManager.getUserPicture();
        ImageView background = (ImageView) findViewById(R.id.friends_background);
        Glide.with(this).load(userPicture)
                .bitmapTransform(new BlurTransformation(getApplicationContext(), 25))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(background);

        // DRAWER
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
        navigationView.setNavigationItemSelectedListener(this);

        // VIEW PAGER
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        FriendsActivityPagerAdapter adapterViewPager = new FriendsActivityPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(adapterViewPager);


        sessionManager.setRefreshFriends("true");
        sessionManager.setRefreshPending("true");
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            String friendRequest = bundle.getString("friend_request");
            if (friendRequest != null) {
                sessionManager.setRefreshOthers("true");
                switch (friendRequest) {
                    case "friend_request_confirmed":
                        viewPager.setCurrentItem(0);
                        break;
                    case "friend_request_incoming":
                        viewPager.setCurrentItem(1);
                        break;
                    case "friend_request_removed":
                        viewPager.setCurrentItem(2);
                        break;
                    default: viewPager.setCurrentItem(0);
                }
            }
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs_friends);
        tabLayout.setupWithViewPager(viewPager);

        // OTHER INFO FOR DRAWER
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.drawer_user_photo);
        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.drawer_background);
        TextView drawerUserEmail = (TextView) headerLayout.findViewById(R.id.drawer_tv_user_email);
        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.drawer_tv_user_name);

        Glide.with(this)
                .load(userPicture)
                .bitmapTransform(new BlurTransformation(getApplicationContext(), 25))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerBackground);

        Glide.with(this)
                .load(userPicture)
                .bitmapTransform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerImageProfile);

        String userEmail = sessionManager.getUserEmail();
        String userName = sessionManager.getUserName();
        drawerUserEmail.setText(userEmail);
        drawerUserName.setText(userName);

        if (!sessionManager.getDuelPending().isEmpty()) {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_duels).getActionView();
            view.setText(String.format("%s%s", getString(R.string.plus), sessionManager.getDuelPending()));
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
                break;
            case R.id.nav_history:
                new Handler().postDelayed(() -> startActivity(new Intent(this, HistoryActivity.class)), 250);
                break;
            case R.id.nav_pending_duels:
                startActivity(new Intent(this, PendingDuelActivity.class));
                break;
            case R.id.nav_settings:
                navItemIndex = 1;
                CURRENT_TAG = TAG_SETTINGS;
                startActivity(intent);
                break;
            case R.id.nav_terms:
                navItemIndex = 2;
                CURRENT_TAG = TAG_TERMS;
                startActivity(intent);
                break;
            case R.id.nav_privacy_policy:
                navItemIndex = 3;
                CURRENT_TAG = TAG_PRIVACY_POLICE;
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        View spinner = findViewById(R.id.loadingPanel);
        if (spinner.getVisibility() == View.VISIBLE) {
            spinner.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed()  {
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
    protected void onDestroy() {
        super.onDestroy();
        this.isFinishing();
        Glide.get(getApplicationContext()).clearMemory();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }




}