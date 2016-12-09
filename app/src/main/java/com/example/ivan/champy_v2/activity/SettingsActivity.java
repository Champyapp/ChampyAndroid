package com.example.ivan.champy_v2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.controller.DailyRemindController;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHSetupUI;
import com.example.ivan.champy_v2.interfaces.Update_user;
import com.example.ivan.champy_v2.model.user.Delete;
import com.example.ivan.champy_v2.model.user.Profile_data;
import com.example.ivan.champy_v2.model.user.User;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

import java.io.File;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.example.ivan.champy_v2.utils.Constants.API_URL;
import static com.example.ivan.champy_v2.utils.Constants.path;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "SettingsActivity";
    private TextView tvChangeName, tvName;
    private String name, userID, token;
    private OfflineMode offlineMode;
    private DBHelper dbHelper;
    private DailyRemindController mDailyRemind;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private SessionManager sessionManager;
    HashMap<String, String> map = new HashMap<>();
    HashMap<String, String> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_settings);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final CHSetupUI chSetupUI = new CHSetupUI();
        chSetupUI.setupUI(findViewById(R.id.settings_layout), this);

        final ImageView background = (ImageView) findViewById(R.id.back_settings);
        final ImageView userImageProfile = (ImageView) findViewById(R.id.img_profile);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);

        File fileBlur = new File(path, "blured2.jpg");
        Uri uriBlur = Uri.fromFile(fileBlur);
        Glide.with(this)
                .load(uriBlur)
                .bitmapTransform(new CropSquareTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(background);

        File fileProfile = new File(path, "profile.jpg");
        Uri uriProfile = Uri.fromFile(fileProfile);

        Glide.with(this)
                .load(uriProfile)
                .bitmapTransform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(130, 130)
                .into(userImageProfile);

        dbHelper = DBHelper.getInstance(getApplicationContext());
        offlineMode = OfflineMode.getInstance();
        mDailyRemind = new DailyRemindController(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());
        userID = sessionManager.getUserId();
        token = sessionManager.getToken();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);
        user = sessionManager.getUserDetails();
        name = user.get("name");

        initSwitches();


        final Typeface typeface = Typeface.createFromAsset(SettingsActivity.this.getAssets(), "fonts/bebasneue.ttf");
        final TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        final ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        final ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(this)
                .load(uriProfile)
                .bitmapTransform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerImageProfile);

        Glide.with(this)
                .load(uriBlur)
                .bitmapTransform(new CropSquareTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerBackground);

        final TextView terms = (TextView)findViewById(R.id.terms);
        final TextView about = (TextView)findViewById(R.id.about);
        final TextView avatar = (TextView)findViewById(R.id.avatar);
        final TextView privacy = (TextView)findViewById(R.id.privacy);
        final TextView tvLegal = (TextView)findViewById(R.id.tvLegal);
        final TextView delete = (TextView)findViewById(R.id.delete_acc);
        final TextView tvGeneral = (TextView)findViewById(R.id.tvGeneral);
        final TextView contactUs = (TextView)findViewById(R.id.contact_us);
        final TextView tvNotifications = (TextView)findViewById(R.id.tvNotifications);

        tvName = (TextView)findViewById(R.id.tvUserName);
        tvChangeName = (TextView)findViewById(R.id.tvName);
        tvName.setText(name);
        drawerUserName.setText(name);

        tvName.setTypeface(typeface);
        tvLegal.setTypeface(typeface);
        tvGeneral.setTypeface(typeface);
        drawerUserName.setTypeface(typeface);
        tvNotifications.setTypeface(typeface);

        about.setOnClickListener(this);
        terms.setOnClickListener(this);
        delete.setOnClickListener(this);
        avatar.setOnClickListener(this);
        privacy.setOnClickListener(this);
        contactUs.setOnClickListener(this);
        tvChangeName.setOnClickListener(this);

        final CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
        int count = checker.getPendingCount();
        if (count == 0) {
            checker.hideItem();
        } else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            view.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
        }

        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (findViewById(R.id.view11).getVisibility() == View.VISIBLE) {
            findViewById(R.id.tvName).setVisibility(View.VISIBLE);
            TextView tvEnterYourName = (TextView)findViewById(R.id.tvEntedYourName);
            tvEnterYourName.setVisibility(View.GONE);
            final EditText etNewName = (EditText)findViewById(R.id.new_name);
            etNewName.setVisibility(View.GONE);
            ImageButton imageButtonAccept = (ImageButton)findViewById(R.id.imageButtonAcceptMaybe);
            imageButtonAccept.setVisibility(View.GONE);
            findViewById(R.id.view11).setVisibility(View.GONE);
        }
        else {
            updateProfile(map);
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.challenges:
                updateProfile(map);
                Intent goToChallenges = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(goToChallenges);
                break;
            case R.id.friends:
                updateProfile(map);
                Intent goToFriends = new Intent(SettingsActivity.this, FriendsActivity.class);
                startActivity(goToFriends);
                break;
            case R.id.history:
                updateProfile(map);
                Intent goToHistory = new Intent(SettingsActivity.this, HistoryActivity.class);
                startActivity(goToHistory);
                break;
            case R.id.pending_duels:
                updateProfile(map);
                Intent goToPendingDuel = new Intent(SettingsActivity.this, PendingDuelActivity.class);
                startActivity(goToPendingDuel);
                break;
            case R.id.share:
                updateProfile(map);
                String message = getString(R.string.share_text2);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, getString(R.string.how_would_you_like_to_share)));
                break;
            case R.id.nav_logout:
                if (offlineMode.isConnectedToRemoteAPI(this)) {
                    updateProfile(map);
                    LoginManager.getInstance().logOut();
                    sessionManager.logout(SettingsActivity.this);
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tvName:
                tvChangeName.setVisibility(View.INVISIBLE);
                final TextView tvEnterYourName = (TextView) findViewById(R.id.tvEntedYourName);
                tvEnterYourName.setVisibility(View.VISIBLE);
                final EditText etNewName = (EditText) findViewById(R.id.new_name);
                etNewName.setVisibility(View.VISIBLE);
                etNewName.setText(name);
                final ImageButton imageButtonAcceptName = (ImageButton) findViewById(R.id.imageButtonAcceptMaybe);
                imageButtonAcceptName.setVisibility(View.VISIBLE);
                final View lineOfTheNed = findViewById(R.id.view11);
                lineOfTheNed.setVisibility(View.VISIBLE);

                imageButtonAcceptName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String checkName = etNewName.getText().toString();
                        if (offlineMode.isConnectedToRemoteAPI(SettingsActivity.this) && !checkName.isEmpty() && !checkName.startsWith(" ")) {
                            String newName = etNewName.getText().toString().trim();
                            sessionManager.change_name(newName);
                            setNewName(newName);

                            tvName.setText(etNewName.getText().toString());
                            imageButtonAcceptName.setVisibility(View.GONE);
                            tvChangeName.setVisibility(View.VISIBLE);
                            tvEnterYourName.setVisibility(View.GONE);
                            lineOfTheNed.setVisibility(View.GONE);
                            etNewName.setVisibility(View.GONE);
                        }
                    }
                });
                break;
            case R.id.delete_acc:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (offlineMode.isConnectedToRemoteAPI(SettingsActivity.this)) {
                                    Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
                                    final Update_user update_user = retrofit.create(Update_user.class);

                                    Call<User> callSurrenderAllChallenges = update_user.surrenderAllChallenge(token);
                                    callSurrenderAllChallenges.enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(Response<User> response, Retrofit retrofit) {
                                            String myLog = (response.isSuccess()) ? " vse ok" : response.message();
                                            Log.d(TAG, "onResponse: surrenderAll: " + myLog);
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            Log.d(TAG, "onFailureSurrenderAllChallenges: vse hyinja");
                                        }
                                    });

                                    Call<Delete> callForDeleteUser = update_user.delete_user(userID, token);
                                    callForDeleteUser.enqueue(new Callback<Delete>() {
                                        @Override
                                        public void onResponse(Response<Delete> response, Retrofit retrofit) {
                                            if (response.isSuccess()) {
                                                File file = new File(path, "blured2.jpg");
                                                file.delete();

                                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                                int clearCount = db.delete("pending", null, null);
                                                clearCount = db.delete("pending_duel", null, null);
                                                clearCount = db.delete("duel", null, null);
                                                clearCount = db.delete("friends", null, null);
                                                clearCount = db.delete("updated", null, null);
                                                clearCount = db.delete("myChallenges", null, null);
                                                Log.d(TAG, "onResponseDeleteUser: Vse ok");
                                            } else Log.d(TAG, "onResponseDeleteUser: failed " + response.message());
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            Log.d(TAG, "onFailureDeleteUser: vse hyinja");
                                        }
                                    });

                                    sessionManager.logout(SettingsActivity.this);
                                    LoginManager.getInstance().logOut();
                                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(R.string.areYouSure)
                        .setMessage(R.string.youWantToDeleteYourAcc)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener)
                        .show();
                break;
            case R.id.about:
                updateProfile(map);
                intent = new Intent(SettingsActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.privacy:
                updateProfile(map);
                intent = new Intent(SettingsActivity.this, PrivacyActivity.class);
                startActivity(intent);
                break;
            case R.id.terms:
                updateProfile(map);
                intent = new Intent(SettingsActivity.this, TermsActivity.class);
                startActivity(intent);
                break;
            case R.id.contact_us:
                updateProfile(map);
                intent = new Intent(SettingsActivity.this, ContactUsActivity.class);
                startActivity(intent);
                break;
            case R.id.avatar:
                if (offlineMode.isConnectedToRemoteAPI(SettingsActivity.this)) {
                    updateProfile(map);
                    intent = new Intent(SettingsActivity.this, PhotoActivity.class);
                    startActivity(intent);
                }
                break;

        }
    }


    private void initSwitches() {
        String pushN = user.get("pushN");
        String acceptedYour = user.get("acceptedYour");
        String challengeEnd = user.get("challengeEnd");
        String newChallengeReq = user.get("newChallReq");
        String challengesForToday = user.get("challengesForToday");
        Log.d(TAG, "initSwitches: challengesForToday: " + challengesForToday);

        map.put("joinedChampy", "true");
        map.put("friendRequests", "true");
        map.put("challengeConfirmation", "true");
        map.put("challengeEnd", challengeEnd);
        map.put("reminderTime", "12"); // was 17
        map.put("challengesForToday", challengesForToday);
        map.put("acceptedYourChallenge", acceptedYour);
        map.put("newChallengeRequests", newChallengeReq);
        map.put("pushNotifications", pushN);

        Switch switchForPushNotif = (Switch) findViewById(R.id.switchPushNotifications);
        if (pushN.equals("true")) switchForPushNotif.setChecked(true);
        else switchForPushNotif.setChecked(false);

        switchForPushNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Push Notifications: " + isChecked);
                if (isChecked) map.put("pushNotifications", "true");
                else map.put("pushNotifications", "false");
            }
        });

        Switch switchorNewChallRequests = (Switch) findViewById(R.id.switchNewChallengeRequest);
        if (newChallengeReq.equals("true")) switchorNewChallRequests.setChecked(true);
        else switchorNewChallRequests.setChecked(false);

        switchorNewChallRequests.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "New Challenge Request: " + isChecked);
                if (isChecked) map.put("newChallengeRequests", "true");
                else map.put("newChallengeRequests", "false");
            }
        });

        Switch switchForAcceptedYourChall = (Switch) findViewById(R.id.switchAcceptedYourChallenge);
        if (acceptedYour.equals("true")) switchForAcceptedYourChall.setChecked(true);
        else switchForAcceptedYourChall.setChecked(false);

        switchForAcceptedYourChall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Accepted Your Challenge: " + isChecked);
                if (isChecked) map.put("acceptedYourChallenge", "true");
                else map.put("acceptedYourChallenge", "false");
            }
        });

        Switch switchForChallengesEnd = (Switch) findViewById(R.id.switchChallengeEnd);
        if (challengeEnd.equals("true")) switchForChallengesEnd.setChecked(true);
        else switchForChallengesEnd.setChecked(false);

        switchForChallengesEnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Challenge End: " + isChecked);
                if (isChecked) map.put("challengeEnd", "true");
                else map.put("challengeEnd", "false");
            }
        });

        Switch switchChallengesForToday = (Switch) findViewById(R.id.switchChallengesForToday);
        if (challengesForToday.equals("true")) switchChallengesForToday.setChecked(true);
        else switchChallengesForToday.setChecked(false);

        switchChallengesForToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "ChallengesForToday: " + isChecked);
                if (isChecked) {
                    map.put("challengesForToday", "true");
                    mDailyRemind.enableDailyNotificationReminder();
                } else {
                    map.put("challengesForToday", "false");
                    mDailyRemind.disableDailyNotificationReminder();
                }
            }
        });

    }


    private void setNewName(String newName) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_user_name(userID, token, newName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) recreate();
                else Log.d(TAG , "SetNewName: vse hyinya");
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG , "SetNewName: " + t);
            }
        });
    }


    public void updateProfile(HashMap<String, String> map) {
        sessionManager.toggleChallengeEnd(map.get("challengeEnd"));
        sessionManager.togglePushNotification(map.get("pushNotifications"));
        sessionManager.toggleChallengesForToday(map.get("challengesForToday"));
        sessionManager.toggleNewChallengeRequest(map.get("newChallengeRequests"));
        sessionManager.toggleAcceptYourChallenge(map.get("acceptedYourChallenge"));

        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        Update_user update_user = retrofit.create(Update_user.class);
        Profile_data profile_data = new Profile_data(map);
        Call<User> call = update_user.update_profile_options(userID, token, profile_data);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                String myLog = (response.isSuccess()) ? " Success" : " Denied";
                Log.d(TAG, "Status: updatedProfile " + myLog);
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "VSE huynya");
            }
        });
    }


}
