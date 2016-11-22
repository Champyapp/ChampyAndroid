package com.example.ivan.champy_v2.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
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
import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHLoadBlurredPhoto;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;

import java.io.File;
import java.io.FileNotFoundException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class WakeUpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public final static String API_URL = "http://46.101.213.24:3007";
    public final static String type_id = "567d51c48322f85870fd931c";
    private NavigationView navigationView;
    private OfflineMode offlineMode;
    private String userId, token;
    private TimePicker alarmTimePicker;
    private CurrentUserHelper user;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        final Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        final ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        final ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        final TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        final TextView tvIChallMySelf = (TextView) findViewById(R.id.tvChallengeToMySelf);
        final TextView tvPoints = (TextView) findViewById(R.id.tvRewardPlus10Points);
        final TextView tvEveryDay = (TextView) findViewById(R.id.tvEveryDayWakeUp);
        final TextView tvLevel = (TextView) findViewById(R.id.tvLevel1Chall);
        final TextView tvDuration = (TextView) findViewById(R.id.tvDays);
        final TextView tvGoal = (TextView) findViewById(R.id.goal_text);

        tvIChallMySelf.setTypeface(typeface);
        tvEveryDay.setTypeface(typeface);
        tvDuration.setTypeface(typeface);
        tvPoints.setTypeface(typeface);
        tvLevel.setTypeface(typeface);
        tvGoal.setTypeface(typeface);

        Glide.with(this).load(R.drawable.wakeupwhite).override(110, 110).into((ImageView) findViewById(R.id.imageViewLogo));
        Glide.with(this).load(R.drawable.wakeuptext).override(180, 150).into((ImageView) findViewById(R.id.imageWakeUpChall));

        @SuppressLint("SdCardPath")
        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);

        user = new CurrentUserHelper(getApplicationContext());
        String userName = user.getName();
        drawerUserName.setText(userName);
        drawerUserName.setTypeface(typeface);

        Glide.with(this).load(R.drawable.points).override(100, 100).into((ImageView) findViewById(R.id.imageViewPoints));
        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerImageProfile);

        try {
            drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
            drawerBackground.setImageDrawable(CHLoadBlurredPhoto.Init(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButtonAccept);

        offlineMode = new OfflineMode();
        if (offlineMode.isConnectedToRemoteAPI(WakeUpActivity.this)) {
            imageButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(final View v) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
//        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());

        final int pickedHour = alarmTimePicker.getCurrentHour();
        final int picketMin = alarmTimePicker.getCurrentMinute();

        // this piece of code need only for check exist challenge
        String sHour = "" + pickedHour;
        String sMinute = "" + picketMin;

        if (pickedHour < 10) sHour  = "0" + sHour;
        if (picketMin < 10) sMinute = "0" + sMinute;

        final String finalSHour = sHour;
        final String finalSMinute = sMinute;

        userId = user.getUserObjectId();
        token  = user.getToken();

        final ChallengeController cc = new ChallengeController(this, this, token, userId);
        final boolean ok = cc.isActiveWakeUp(sHour + sMinute);

        snackbar = Snackbar.make(v, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes!", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ok) {
                    cc.createNewWakeUpChallenge(21, type_id, finalSHour, finalSMinute);
                    snackbar = Snackbar.make(view, "Challenge Created!", Snackbar.LENGTH_SHORT);
                } else {
                    snackbar = Snackbar.make(view, "Already Exist!", Snackbar.LENGTH_SHORT);
                }
                snackbar.show();
            }
        });
        snackbar.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        offlineMode.isConnectedToRemoteAPI(this);
        final CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
        int count = checker.getPendingCount();
        if (count == 0) {
            checker.hideItem();
        } else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(WakeUpActivity.this, MainActivity.class);
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
            case R.id.pending_duels:
                Intent goToPendingDuel = new Intent(this, PendingDuelActivity.class);
                startActivity(goToPendingDuel);
                break;
            case R.id.share:
                String message = "Check out Champy - it helps you improve and compete with your friends!";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "How would you like to share?"));
                break;
            case R.id.nav_logout:
                OfflineMode offlineMode = new OfflineMode();
                SessionManager sessionManager = new SessionManager(this);
                offlineMode.isConnectedToRemoteAPI(this);
                sessionManager.logout(this);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
