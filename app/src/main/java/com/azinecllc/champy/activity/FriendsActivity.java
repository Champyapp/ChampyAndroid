package com.azinecllc.champy.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.azinecllc.champy.fragment.MainFragment;
import com.azinecllc.champy.fragment.PendingDuelFragment;
import com.azinecllc.champy.fragment.PrivacyPoliceFragment;
import com.azinecllc.champy.fragment.SettingsFragment;
import com.azinecllc.champy.fragment.TermsFragment;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

import static com.azinecllc.champy.activity.MainActivity.CURRENT_TAG;
import static com.azinecllc.champy.activity.MainActivity.navItemIndex;
import static com.azinecllc.champy.utils.Constants.TAG_CHALLENGES;
import static com.azinecllc.champy.utils.Constants.TAG_PENDING_DUELS;
import static com.azinecllc.champy.utils.Constants.TAG_PRIVACY_POLICE;
import static com.azinecllc.champy.utils.Constants.TAG_SETTINGS;
import static com.azinecllc.champy.utils.Constants.TAG_TERMS;
import static com.azinecllc.champy.utils.Constants.path;

public class FriendsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SessionManager sessionManager;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView background = (ImageView) findViewById(R.id.friends_background);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);

        File fileBlur = new File(path, "blurred.png");
        Uri uriBlur = Uri.fromFile(fileBlur);
        Glide.with(this)
                .load(uriBlur)
                .bitmapTransform(new CropSquareTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(background);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.imageUserPicture);
        ImageView drawerBackground   = (ImageView) headerLayout.findViewById(R.id.slide_background);
        TextView drawerUserName      = (TextView)  headerLayout.findViewById(R.id.tvUserName);
        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        File fileProfile = new File(path, "profile.jpg");
        Uri uriProfile = Uri.fromFile(fileProfile);

        Glide.with(this)
                .load(uriBlur)
                .bitmapTransform(new CropSquareTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerBackground);

        Glide.with(this)
                .load(uriProfile)
                .bitmapTransform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerImageProfile);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        FriendsActivityPagerAdapter adapterViewPager = new FriendsActivityPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(adapterViewPager);

        sessionManager = SessionManager.getInstance(getApplicationContext());
        sessionManager.setRefreshFriends("true");
        sessionManager.setRefreshPending("true");


        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            String friendRequest = bundle.getString("friend_request");
            if (friendRequest != null) {
                sessionManager.setRefreshOthers("true");
                switch (friendRequest) {
                    case "friend_request_confirmed": viewPager.setCurrentItem(0); break;
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

        String name = sessionManager.getUserName();
        drawerUserName.setText(name);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        drawerUserName.setTypeface(typeface);

        CHCheckPendingDuels checker = CHCheckPendingDuels.getInstance();
        int count = checker.getPendingCount(getApplicationContext());
        if (count != 0) {
//            checker.hideItem(navigationView);
//        } else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_duels).getActionView();
            view.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        switch (item.getItemId()) {
            case R.id.nav_challenges:
                navItemIndex = 0;
                CURRENT_TAG = TAG_CHALLENGES;
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.nav_friends:
                new Handler().postDelayed(() -> startActivity(new Intent(this, FriendsActivity.class)), 250);
                break;
            case R.id.nav_history:
                new Handler().postDelayed(() -> startActivity(new Intent(this, HistoryActivity.class)), 250);
                break;
            case R.id.nav_pending_duels:
                navItemIndex = 1;
                CURRENT_TAG = TAG_PENDING_DUELS;
                intent = new Intent(this, PendingDuelActivity.class);
                break;
            case R.id.nav_settings:
                navItemIndex = 2;
                CURRENT_TAG = TAG_SETTINGS;
                break;
            case R.id.nav_terms:
                navItemIndex = 3;
                CURRENT_TAG = TAG_TERMS;
                break;
            case R.id.nav_privacy_policy:
                navItemIndex = 4;
                CURRENT_TAG = TAG_PRIVACY_POLICE;
                break;
        }
        startActivity(intent);
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
            finish();
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.isFinishing();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }




}