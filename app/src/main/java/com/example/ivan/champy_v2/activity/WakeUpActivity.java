package com.example.ivan.champy_v2.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.controller.ChallengeController;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

import static com.example.ivan.champy_v2.utils.Constants.path;

public class WakeUpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private SessionManager sessionManager;
    private TimePicker alarmTimePicker;
    private OfflineMode offlineMode;
    private ChallengeController cc;
    private DrawerLayout drawer;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        final Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        final ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        final ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);

        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerImageProfile);

        file = new File(path, "blured2.jpg");
        url = Uri.fromFile(file);

        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropSquareTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerBackground);

        final TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        final TextView tvIChallMySelf = (TextView) findViewById(R.id.tvChallengeToMySelf);
        final TextView tvEveryDay = (TextView) findViewById(R.id.tvEveryDayWakeUp);
        final TextView tvDuration = (TextView) findViewById(R.id.tvDays);
        final TextView tvGoal = (TextView) findViewById(R.id.goal_text);

        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        final ImageButton imageButton = (ImageButton) findViewById(R.id.imageButtonAccept);
        imageButton.setOnClickListener(this);

        tvIChallMySelf.setTypeface(typeface);
        tvEveryDay.setTypeface(typeface);
        tvDuration.setTypeface(typeface);
        tvGoal.setTypeface(typeface);

        Glide.with(this).load(R.drawable.wakeupwhite).override(110, 110).into((ImageView) findViewById(R.id.imageViewLogo));
        Glide.with(this).load(R.drawable.wakeuptext).override(180, 150).into((ImageView) findViewById(R.id.imageWakeUpChall));

        CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView, sessionManager);
        int count = checker.getPendingCount();
        if (count == 0) {
            checker.hideItem();
        } else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            view.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
        }

        offlineMode = OfflineMode.getInstance();
        sessionManager = new SessionManager(this);
        String userName = sessionManager.getUserName();
        String userID = sessionManager.getUserId();
        String token = sessionManager.getToken();
        cc = new ChallengeController(this, this, token, userID);
        drawerUserName.setText(userName);
        drawerUserName.setTypeface(typeface);

    }

    @Override
    public void onClick(final View v) {
        final int pickedHour = alarmTimePicker.getCurrentHour();
        final int picketMin = alarmTimePicker.getCurrentMinute();

        // this piece of code need only for check exist challenge
        String sHour = "" + pickedHour;
        String sMinute = "" + picketMin;

        if (pickedHour < 10) sHour  = "0" + sHour;
        if (picketMin < 10) sMinute = "0" + sMinute;

        String finalSHour = sHour;
        String finalSMinute = sMinute;

        final boolean ok = cc.isActiveWakeUp(sHour + sMinute);
        if (offlineMode.isConnectedToRemoteAPI(WakeUpActivity.this)) {
            snackbar = Snackbar.make(v, R.string.are_you_sure, Snackbar.LENGTH_LONG).setAction(R.string.yes, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ok) {
                        cc.createNewWakeUpChallenge(21, finalSHour, finalSMinute);
                        snackbar = Snackbar.make(view, R.string.challenge_created, Snackbar.LENGTH_SHORT);
                    } else {
                        snackbar = Snackbar.make(view, R.string.cant_create_this_challenge, Snackbar.LENGTH_SHORT);
                    }
                    snackbar.show();
                }
            });
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(WakeUpActivity.this, MainActivity.class);
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
            case R.id.history:
                Intent goToHistory = new Intent(this, HistoryActivity.class);
                startActivity(goToHistory);
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
                if (offlineMode.isConnectedToRemoteAPI(this)) { sessionManager.logout(this); }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
