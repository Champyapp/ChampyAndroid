package com.example.ivan.champy_v2.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.adapter.PendingDuelsAdapter;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

import static com.example.ivan.champy_v2.utils.Constants.path;

public class PendingDuelActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private SessionManager sessionManager;
    private DrawerLayout drawer;
    private int size;
    public View spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending__duel);

        sessionManager = new SessionManager(this);
        new ProgressTask().execute();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        setSupportActionBar(toolbar);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        final Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        final TextView tvPendingDuels = (TextView) findViewById(R.id.tvChallengeToMySelf);
        tvPendingDuels.setTypeface(typeface);

        Glide.with(this).load(R.drawable.duel_blue).override(130, 130).into((ImageView) findViewById(R.id.imageViewLogo));


        final ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        final ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        final TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);

        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerImageProfile);

        file = new File (path, "blured2.jpg");
        url = Uri.fromFile(file);
        Glide.with(this).load(url).bitmapTransform(new CropSquareTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerBackground);

        String name = sessionManager.getUserName();
        drawerUserName.setText(name);
        drawerUserName.setTypeface(typeface);


        final CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
        int count = checker.getPendingCount();
        if (count == 0) {
            checker.hideItem();
        } else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            view.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
        }

        spinner.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(PendingDuelActivity.this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
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
            case R.id.history:
                Intent goToHistory = new Intent(this, HistoryActivity.class);
                startActivity(goToHistory);
                break;
            case R.id.settings:
                Intent goToSettings = new Intent(this, SettingsActivity.class);
                startActivity(goToSettings);
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
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private class ProgressTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            spinner = findViewById(R.id.loadingPanel);
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    size = Integer.parseInt(sessionManager.get_duel_pending());
                    PendingDuelsAdapter pagerAdapter = new PendingDuelsAdapter(getSupportFragmentManager());
                    ViewPager viewPager = (ViewPager) findViewById(R.id.pager_pending_duel);
                    pagerAdapter.setCount(size);
                    viewPager.setAdapter(pagerAdapter);
                    viewPager.setOffscreenPageLimit(1);
                    viewPager.setPageMargin(20);
                    viewPager.setClipToPadding(false);
                    viewPager.setPadding(90, 0, 90, 0);
                }
            });
            return null;
        }

    }

}
