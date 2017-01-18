package com.azinecllc.champy.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
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
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

import static com.azinecllc.champy.utils.Constants.path;

public class HistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SessionManager sessionManager;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_history);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);

        final ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        final ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        final TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        final ImageView background = (ImageView) findViewById(R.id.history_background);
        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);

        File fileProfile = new File(path, "profile.jpg");
        Uri uriProfile = Uri.fromFile(fileProfile);

        Glide.with(this)
                .load(uriProfile)
                .bitmapTransform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerImageProfile);

        File fileBlur = new File(path, "blurred.png");
        Uri uriBlur = Uri.fromFile(fileBlur);

        Glide.with(this)
                .load(uriBlur)
                .bitmapTransform(new CropSquareTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(background);
        Glide.with(this)
                .load(uriBlur)
                .bitmapTransform(new CropSquareTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerBackground);


        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        navigationView.setNavigationItemSelectedListener(this);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_history);
        final FragmentPagerAdapter adapterViewPager = new HistoryPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(adapterViewPager);
        viewPager.setOffscreenPageLimit(3);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs_history);
        tabLayout.setupWithViewPager(viewPager);

        // this out method for open "in progress" when you click on notification about win request
        String extras = this.getIntent().getStringExtra("challenge_request_win");
        if (extras != null && "true".equals(extras)) { viewPager.setCurrentItem(1); }


        final String name = sessionManager.getUserName();
        drawerUserName.setText(name);
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
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.challenges:
                Intent goToChallenges = new Intent(this, MainActivity.class);
                startActivity(goToChallenges);
                finish();
                break;
            case R.id.friends:
                Intent goToFriends = new Intent(this, FriendsActivity.class);
                startActivity(goToFriends);
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
