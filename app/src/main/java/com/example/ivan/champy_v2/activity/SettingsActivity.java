package com.example.ivan.champy_v2.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
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
import com.example.ivan.champy_v2.helper.CHLoadBlurredPhoto;
import com.example.ivan.champy_v2.helper.CHSetupUI;
import com.example.ivan.champy_v2.interfaces.Update_user;
import com.example.ivan.champy_v2.model.user.Delete;
import com.example.ivan.champy_v2.model.user.Profile_data;
import com.example.ivan.champy_v2.model.user.User;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.io.File;
import java.io.FileNotFoundException;
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
        toolbar.bringToFront();
        final CHSetupUI chSetupUI = new CHSetupUI();
        chSetupUI.setupUI(findViewById(R.id.settings_layout), this);

        dbHelper = new DBHelper(getApplicationContext());
        offlineMode = new OfflineMode();
        mDailyRemind = new DailyRemindController(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());
        userID = sessionManager.getUserId();
        token = sessionManager.getToken();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
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
        final ImageView background = (ImageView) findViewById(R.id.back_settings);
        final ImageView userImageProfile = (ImageView) findViewById(R.id.img_profile);
        final TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        final ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        final ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);
        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);


        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);
        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerImageProfile);

        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(130, 130)
                .into(userImageProfile);



        file = new File(path, "blured2.jpg");
        url = Uri.fromFile(file);
        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropSquareTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(background);
        Glide.with(this)
                .load(url)
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

//        try {
//            background.setImageDrawable(CHLoadBlurredPhoto.Init(path));
//            drawerBackground.setImageDrawable(CHLoadBlurredPhoto.Init(path));
//        } catch (FileNotFoundException e) { e.printStackTrace(); }


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
            TextView pendingCount = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            pendingCount.setText("+" + (count > 0 ? String.valueOf(count) : null));
        }

        ViewServer.get(this).addWindow(this);
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
            Log.d(TAG, "Status: Back");
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
                final EditText etNewName = (EditText) findViewById(R.id.new_name);
                tvEnterYourName.setVisibility(View.VISIBLE);
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
                        etNewName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_warn, 0);
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
        String newChallReq = user.get("newChallReq");
        String acceptedYour = user.get("acceptedYour");
        String challengeEnd = user.get("challengeEnd");
        String dailyRemind = user.get("dailyRemind");

        map.put("challengeEnd", challengeEnd);
        map.put("acceptedYourChallenge", acceptedYour);
        map.put("newChallengeRequests", newChallReq);
        map.put("pushNotifications", pushN);
        map.put("dailyRemind", dailyRemind);
        map.put("joinedChampy", "true");
        map.put("friendRequests", "true");
        map.put("challengeConfirmation", "true");
        map.put("reminderTime", "12"); // was 17
        map.put("challengesForToday", "true");

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
        if (newChallReq.equals("true")) switchorNewChallRequests.setChecked(true);
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

        Switch switchDailyRemind = (Switch) findViewById(R.id.switchDailyRemind);
        if (dailyRemind.equals("true")) switchDailyRemind.setChecked(true);
        else switchDailyRemind.setChecked(false);

        switchDailyRemind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Daily Remind: " + isChecked);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                if (isChecked) {
                    map.put("dailyRemind", "true");
                    cv.put("dailyRemind", "true");
                    mDailyRemind.enableDailyNotificationReminder();
                } else {
                    map.put("dailyRemind", "false");
                    cv.put("dailyRemind", "false");
                    mDailyRemind.disableDailyNotificationReminder();
                }
                db.insert("updated", null, cv);
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
        sessionManager.togglePushNotification(map.get("pushNotifications"));
        sessionManager.toggleNewChallengeRequest(map.get("newChallengeRequests"));
        sessionManager.toggleAcceptYourChallenge(map.get("acceptedYourChallenge"));
        sessionManager.toggleChallengeEnd(map.get("challengeEnd"));
        sessionManager.toggleDailyRemind(map.get("dailyRemind"));

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
