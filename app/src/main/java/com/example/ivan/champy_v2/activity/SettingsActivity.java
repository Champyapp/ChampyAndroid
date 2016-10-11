package com.example.ivan.champy_v2.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHLoadBlurredPhoto;
import com.example.ivan.champy_v2.helper.CHSetupUI;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.interfaces.Update_user;
import com.example.ivan.champy_v2.model.User.Delete;
import com.example.ivan.champy_v2.model.User.Profile_data;
import com.example.ivan.champy_v2.model.User.User;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @SuppressLint("SdCardPath")
    final private String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
    final private String API_URL = "http://46.101.213.24:3007";
    final private String TAG = "SettingsActivity";
    SessionManager sessionManager;
    HashMap<String, String> map = new HashMap<>();
    HashMap<String, String> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_settings);
        final OfflineMode offlineMode = new OfflineMode();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        CHSetupUI chSetupUI = new CHSetupUI();
        chSetupUI.setupUI(findViewById(R.id.settings_layout), this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);
        CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
        int count = checker.getPendingCount();
        TextView pendingCount = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
        pendingCount.setText("+" + (count > 0 ? String.valueOf(count) : null));
        if (count == 0) checker.hideItem();

        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();
        final String name = user.get("name");
        initSwitches();

        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);

        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        ImageView background = (ImageView) findViewById(R.id.back_settings);
        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        drawerUserName.setText(name);
        Typeface typeface = Typeface.createFromAsset(SettingsActivity.this.getAssets(), "fonts/bebasneue.ttf");

        final TextView tvName = (TextView)findViewById(R.id.userNameInSettings);
        tvName.setText(name);
        tvName.setTypeface(typeface);

        TextView tvNotifications = (TextView)findViewById(R.id.tvNotifications);
        tvNotifications.setTypeface(typeface);
        TextView tvGeneral = (TextView)findViewById(R.id.tvGeneral);
        tvGeneral.setTypeface(typeface);
        TextView tvLegal = (TextView)findViewById(R.id.tvLegal);
        tvLegal.setTypeface(typeface);

        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerImageProfile);
        drawerImageProfile = (ImageView) findViewById(R.id.img_profile);
//        drawerImageProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (offlineMode.isConnectedToRemoteAPI(SettingsActivity.this)) {
//                    updateProfile(map);
//                    Intent intent = new Intent(SettingsActivity.this, PhotoActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).override(130, 130).into(drawerImageProfile);

        try {
            background.setScaleType(ImageView.ScaleType.CENTER_CROP);
            background.setImageDrawable(CHLoadBlurredPhoto.Init(path));
            drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
            drawerBackground.setImageDrawable(CHLoadBlurredPhoto.Init(path));
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        final TextView tvChangeName = (TextView)findViewById(R.id.tvName);
        tvChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            SessionManager sessionManager = new SessionManager(getApplicationContext());
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
            }
        });
        TextView delete = (TextView)findViewById(R.id.delete_acc);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (offlineMode.isConnectedToRemoteAPI(SettingsActivity.this)) {
                                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                                    CurrentUserHelper user = new CurrentUserHelper(getApplicationContext());
                                    final String token = user.getToken();
                                    String id = user.getUserObjectId();
                                    Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
                                    final Update_user update_user = retrofit.create(Update_user.class);

                                    Call<User> callSurrenderAllChallenges = update_user.surrenderAllChallenge(token);
                                    callSurrenderAllChallenges.enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(Response<User> response, Retrofit retrofit) {
                                            String myLog = (response.isSuccess()) ? "SurrenderAllChallenge: vse ok" : "SurrenderAllChallenge: " + response.code();
                                            Log.i(TAG, "onResponse: " + myLog);
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            Log.i(TAG, "onFailureSurrenderAllChallenges: vse hyinja");
                                        }
                                    });

                                    Call<Delete> callForDeleteUser = update_user.delete_user(id, token);
                                    callForDeleteUser.enqueue(new Callback<Delete>() {
                                        @Override
                                        public void onResponse(Response<Delete> response, Retrofit retrofit) {
                                            if (response.isSuccess()) {
                                                File file = new File(path, "blured2.jpg");
                                                DBHelper dbHelper = new DBHelper(getApplicationContext());
                                                final SQLiteDatabase db = dbHelper.getWritableDatabase();
                                                int clearCount = db.delete("pending", null, null);
                                                clearCount = db.delete("pending_duel", null, null);
                                                clearCount = db.delete("duel", null, null);
                                                clearCount = db.delete("friends", null, null);
                                                clearCount = db.delete("myChallenges", null, null);
                                                file.delete();
                                                Log.i(TAG, "onResponseDeleteUser: Vse ok");
                                            } else Log.i(TAG, "onResponseDeleteUser: failed " + response.message());
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            Log.i(TAG, "onFailureDeleteUser: vse hyinja");
                                        }
                                    });

                                    sessionManager.logoutUser();
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
                        .setIcon(R.drawable.warn)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
                }
        });

        TextView about = (TextView)findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        TextView privacy = (TextView)findViewById(R.id.privacy);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(map);
                Intent intent = new Intent(SettingsActivity.this, PrivacyActivity.class);
                startActivity(intent);
            }
        });

        TextView terms = (TextView)findViewById(R.id.terms);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, TermsActivity.class);
                startActivity(intent);
            }
        });

        TextView contactUs = (TextView)findViewById(R.id.contact_us);
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(map);
                Intent intent = new Intent(SettingsActivity.this, ContactUsActivity.class);
                startActivity(intent);
            }
        });

        TextView avatar = (TextView)findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (offlineMode.isConnectedToRemoteAPI(SettingsActivity.this)) {
                    updateProfile(map);
                    Intent intent = new Intent(SettingsActivity.this, PhotoActivity.class);
                    startActivity(intent);
                }
            }
        });

        ViewServer.get(this).addWindow(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        OfflineMode offlineMode = new OfflineMode();
        offlineMode.isConnectedToRemoteAPI(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    @SuppressWarnings("StatementWithEmptyBody")
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
                String message = "Check out Champy - it helps you improve and compete with your friends!";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "How would you like to share?"));
                break;
            case R.id.nav_logout:
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isConnectedToRemoteAPI(this)) {
                    updateProfile(map);
                    LoginManager.getInstance().logOut();
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    sessionManager.logoutUser();
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void initSwitches() {
        String pushN = user.get("pushN");
        String newChallReq = user.get("newChallReq");
        String acceptedYour = user.get("acceptedYour");
        String challengeEnd = user.get("challengeEnd");
        map.put("joinedChampy", "true");
        map.put("friendRequests", "true");
        map.put("challengeConfirmation", "true");
        map.put("challengeEnd", challengeEnd);
        map.put("reminderTime", "12"); // was 17
        map.put("challengesForToday", "true");
        map.put("acceptedYourChallenge", acceptedYour);
        map.put("newChallengeRequests", newChallReq);
        map.put("pushNotifications", pushN);

        Switch switchForPushNotif = (Switch) findViewById(R.id.switch1);
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

        Switch switchorNewChallRequests = (Switch) findViewById(R.id.switch2);
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

        Switch switchForAcceptedYourChall = (Switch) findViewById(R.id.switch3);
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

        Switch switchForChallengesEnd = (Switch) findViewById(R.id.switch4);
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
    }


    private void setNewName(String newName) {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String id = user.get("id");
        String token = user.get("token");

        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_user_name(id, token, newName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) recreate();
                else Log.i(TAG , "SetNewName: Vse OK");
            }
            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG , "SetNewName: " + t);
            }
        });
    }


    public void updateProfile(HashMap<String, String> map){
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String id = user.get("id");
        String token = user.get("token");

        sessionManager.toogle1(map.get("pushNotifications"));
        sessionManager.toogle2(map.get("newChallengeRequests"));
        sessionManager.toogle3(map.get("acceptedYourChallenge"));
        sessionManager.toogle4(map.get("challengeEnd"));

        final String API_URL = "http://46.101.213.24:3007";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        Update_user update_user = retrofit.create(Update_user.class);
        Profile_data profile_data = new Profile_data(map);
        Call<User> call = update_user.update_profile_options(id, token, profile_data);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.d(TAG, "Status: Profile updated");
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "VSE huynya");
            }
        });
    }


}
