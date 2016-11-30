package com.example.ivan.champy_v2.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.adapter.HistoryPagerAdapter;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHLoadBlurredPhoto;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.facebook.FacebookSdk;

import java.io.File;
import java.io.FileNotFoundException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.ivan.champy_v2.utils.Constants.path;

public class HistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SessionManager sessionManager;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_history);
        final FragmentPagerAdapter adapterViewPager = new HistoryPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(adapterViewPager);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs_history);
        tabLayout.setupWithViewPager(viewPager);

        // this out method for open "wins" when you click on notification about win request
        String extras = this.getIntent().getStringExtra("win_request");
        if (extras != null && "true".equals(extras)) { viewPager.setCurrentItem(1); }


        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        ImageView background = (ImageView) findViewById(R.id.history_background);
        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        String name = sessionManager.getUserName();
        drawerUserName.setText(name);
        drawerUserName.setTypeface(typeface);

        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);
        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerImageProfile);

        try {
            Drawable dr = CHLoadBlurredPhoto.Init(path);
            background.setScaleType(ImageView.ScaleType.CENTER_CROP);
            background.setImageDrawable(dr);
            drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
            drawerBackground.setImageDrawable(dr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
        int count = checker.getPendingCount();
        if (count == 0) {
            checker.hideItem();
        } else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        }

        ViewServer.get(this).addWindow(this);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.challenges:
                Intent goToChallenges = new Intent(this, MainActivity.class);
                startActivity(goToChallenges);
                break;
            case R.id.friends:
                Intent goToFriends = new Intent(this, FriendsActivity.class);
                startActivity(goToFriends);
                break;
            case R.id.settings:
                Intent goToSettings = new Intent(this, SettingsActivity.class);
                startActivity(goToSettings);
                break;
            case R.id.pending_duels:
                Intent goToPendingDuel = new Intent(this, PendingDuelActivity.class);
                startActivity(goToPendingDuel);
                break;
            case R.id.share:
                String message = getString(R.string.share_text2);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, getString(R.string.how_would_you_like_to_share)));
                break;
            case R.id.nav_logout:
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isConnectedToRemoteAPI(this)) {
                    sessionManager.logout(this);
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
