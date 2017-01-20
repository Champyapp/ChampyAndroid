package com.azinecllc.champy.activity;

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

import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

import static com.azinecllc.champy.utils.Constants.path;

public class WakeUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TimePicker alarmTimePicker;
    private OfflineMode offlineMode;
    private ChallengeController cc;
    //private DrawerLayout drawer;
    private Snackbar snackbar;
    private TextView etDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);
//        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                super.onDrawerSlide(drawerView, slideOffset);
//            }
//        };
//        drawer.setDrawerListener(toggle);
//        toggle.setDrawerIndicatorEnabled(true);
//        toggle.syncState();

//        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
//        navigationView.setNavigationItemSelectedListener(this);

        final Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
//        final ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
//        final ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
//        final TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
//        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

//        File file = new File(path, "profile.jpg");
//        Uri url = Uri.fromFile(file);
//
//        Glide.with(this)
//                .load(url)
//                .bitmapTransform(new CropCircleTransformation(this))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(drawerImageProfile);
//
//        file = new File(path, "blurred.png");
//        url = Uri.fromFile(file);
//
//        Glide.with(this)
//                .load(url)
//                .bitmapTransform(new CropSquareTransformation(this))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(drawerBackground);

        final TextView tvIChallengeMySelf = (TextView) findViewById(R.id.tvChallengeToMySelf);
        final ImageButton buttonAccept = (ImageButton) findViewById(R.id.imageButtonAccept);
        final ImageButton buttonMinus = (ImageButton) findViewById(R.id.imageButtonMinus);
        final ImageButton buttonPlus = (ImageButton) findViewById(R.id.imageButtonPlus);
        final TextView tvEveryDay = (TextView) findViewById(R.id.tvEveryDayWakeUp);
        final TextView tvWakeUp = (TextView) findViewById(R.id.tvWakeUpChallenge);
        final TextView tvGoal = (TextView) findViewById(R.id.goal_text);
        final TextView tvDays = (TextView) findViewById(R.id.tvDays);
        etDays = (TextView) findViewById(R.id.etDays);

        tvIChallengeMySelf.setTypeface(typeface);
        tvEveryDay.setTypeface(typeface);
        tvWakeUp.setTypeface(typeface);
        etDays.setTypeface(typeface);
        tvDays.setTypeface(typeface);
        tvGoal.setTypeface(typeface);

        SessionManager sessionManager = SessionManager.getInstance(getApplicationContext());
        offlineMode = OfflineMode.getInstance();

//        CHCheckPendingDuels checker = CHCheckPendingDuels.getInstance();
//        int count = checker.getPendingCount(getApplicationContext());
//        if (count == 0) {
//            checker.hideItem(navigationView);
//        } else {
//            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
//            view.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
//        }

//        final String userName = sessionManager.getUserName();
//        drawerUserName.setText(userName);
//        drawerUserName.setTypeface(typeface);

        final String userID = sessionManager.getUserId();
        final String token = sessionManager.getToken();
        cc = new ChallengeController(this, this, token, userID);

        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);

        buttonAccept.setOnClickListener(this);
        buttonMinus.setOnClickListener(this);
        buttonPlus.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.imageButtonAccept:
                final int pickedHour = alarmTimePicker.getCurrentHour();
                final int picketMin = alarmTimePicker.getCurrentMinute();

                // this piece of code need only for check exist challenge
                String sHour   = String.format("%s", pickedHour); //"" + pickedHour;
                String sMinute = String.format("%s", picketMin);  // "" + picketMin;

                if (pickedHour < 10) sHour  = "0" + sHour;
                if (picketMin < 10) sMinute = "0" + sMinute;

                String fHour = sHour;
                String fMin = sMinute;

                final boolean isActive = cc.isActive(sHour + sMinute);
                if (offlineMode.isConnectedToRemoteAPI(WakeUpActivity.this)) {
                    snackbar = Snackbar.make(v, R.string.are_you_sure, Snackbar.LENGTH_LONG).setAction(R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isActive) {
                                cc.createNewWakeUpChallenge(Integer.parseInt(etDays.getText().toString()), fHour, fMin);
                                snackbar = Snackbar.make(view, R.string.challenge_created, Snackbar.LENGTH_SHORT);
                            } else {
                                snackbar = Snackbar.make(view, R.string.cant_create_this_challenge, Snackbar.LENGTH_SHORT);
                            }
                            snackbar.show();
                        }
                    });
                    snackbar.show();
                }
                break;
            case R.id.imageButtonPlus:
                int daysCount = Integer.parseInt(etDays.getText().toString());
                int newDaysCount;
                if (daysCount < 1000) {
                    newDaysCount = daysCount + 1;
                    etDays.setText(String.valueOf(newDaysCount));
                }
                break;

            case R.id.imageButtonMinus:
                daysCount = Integer.parseInt(etDays.getText().toString());
                if (daysCount > 1) {
                    newDaysCount = daysCount - 1;
                    etDays.setText(String.valueOf(newDaysCount));
                }
                break;
        }

    }

    @Override
    public void onBackPressed() {
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
            Intent intent = new Intent(WakeUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
//        }
    }

//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.challenges:
//                Intent goToChallenges = new Intent(this, MainActivity.class);
//                startActivity(goToChallenges);
//                finish();
//                break;
//            case R.id.friends:
//                Intent goToFriends = new Intent(this, FriendsActivity.class);
//                startActivity(goToFriends);
//                finish();
//                break;
//            case R.id.history:
//                Intent goToHistory = new Intent(this, HistoryActivity.class);
//                startActivity(goToHistory);
//                finish();
//                break;
//            case R.id.settings:
//                Intent goToSettings = new Intent(this, SettingsActivity.class);
//                startActivity(goToSettings);
//                finish();
//                break;
//            case R.id.pending_duels:
//                Intent goToPendingDuel = new Intent(this, PendingDuelActivity.class);
//                startActivity(goToPendingDuel);
//                finish();
//                break;
//            case R.id.share:
//                String message = getString(R.string.share_text2);
//                Intent share = new Intent(Intent.ACTION_SEND);
//                share.setType("text/plain");
//                share.putExtra(Intent.EXTRA_TEXT, message);
//                startActivity(Intent.createChooser(share, getString(R.string.how_would_you_like_to_share)));
//                break;
//            case R.id.nav_logout:
//                if (offlineMode.isConnectedToRemoteAPI(this)) {
//                    sessionManager.logout(this);
//                    finish();
//                }
//                break;
//        }
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }


}
