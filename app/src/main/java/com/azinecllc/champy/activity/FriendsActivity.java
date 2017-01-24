package com.azinecllc.champy.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
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
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

import static com.azinecllc.champy.utils.Constants.path;

public class FriendsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SessionManager sessionManager;
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
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
        //setupCustomTabIcons();

        String name = sessionManager.getUserName();
        drawerUserName.setText(name);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        drawerUserName.setTypeface(typeface);

        CHCheckPendingDuels checker = CHCheckPendingDuels.getInstance();
        int count = checker.getPendingCount(getApplicationContext());
        if (count == 0) {
            checker.hideItem(navigationView);
        } else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            view.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
        }

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
            Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
            startActivity(intent);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.challenges:
                Intent goToMainActivity = new Intent(this, MainActivity.class);
                startActivity(goToMainActivity);
                finish();
                break;
            case R.id.history:
                Intent goToHistory = new Intent(this, HistoryActivity.class);
                startActivity(goToHistory);
                finish();
                break;
            case R.id.settings:
                Intent goToSettings = new Intent(this, SettingsActivity.class);
                startActivity(goToSettings);
                finish();
                break;
            case R.id.pending_duels:
                Intent goToPendingDuel = new Intent(this, PendingDuelActivity.class);
                startActivity(goToPendingDuel);
                finish();
                break;
            case R.id.share:
                String message = getString(R.string.share_text2);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, getString(R.string.how_would_you_like_to_share)));
                break;
            case R.id.nav_logout:
                OfflineMode offlineMode = OfflineMode.getInstance();
                if (offlineMode.isConnectedToRemoteAPI(this)) {
                    sessionManager.logout(this);
                    finish();
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}