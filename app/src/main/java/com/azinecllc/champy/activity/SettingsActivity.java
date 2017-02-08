package com.azinecllc.champy.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.controller.DailyRemindController;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.helper.CHSetupUI;
import com.azinecllc.champy.interfaces.Update_user;
import com.azinecllc.champy.model.user.Delete;
import com.azinecllc.champy.model.user.Profile_data;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;
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

import static com.azinecllc.champy.utils.Constants.API_URL;
import static com.azinecllc.champy.utils.Constants.path;

public class SettingsActivity extends AppCompatActivity {

//    private TextView tvChangeName, tvName;
//    private String name, userID, token;
//    private OfflineMode offline;
//    private DBHelper dbHelper;
//    private DailyRemindController reminder;
//    private DrawerLayout drawer;
//    private SessionManager session;
//    HashMap<String, String> map = new HashMap<>();
//    HashMap<String, String> user = new HashMap<>();
//    Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        setContentView(R.layout.activity_settings);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        ImageView background = (ImageView) findViewById(R.id.back_settings);
//        ImageView userImageProfile = (ImageView) findViewById(R.id.img_profile);
//        background.setScaleType(ImageView.ScaleType.CENTER_CROP);

        CHSetupUI setupUI = new CHSetupUI();
        setupUI.setupUI(findViewById(R.id.settings_layout), this);

//        File fileBlur = new File(path, "blurred.png");
//        Uri uriBlur = Uri.fromFile(fileBlur);
//        Glide.with(this)
//                .load(uriBlur)
//                .bitmapTransform(new CropSquareTransformation(this))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(background);

//        File fileProfile = new File(path, "profile.jpg");
//        Uri uriProfile = Uri.fromFile(fileProfile);
//
//        Glide.with(this)
//                .load(uriProfile)
//                .bitmapTransform(new CropCircleTransformation(this))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .override(130, 130)
//                .into(userImageProfile);
//
//        dbHelper = DBHelper.getInstance(getApplicationContext());
//        offline  = OfflineMode.getInstance();
//        reminder = new DailyRemindController(getApplicationContext());
//        session  = SessionManager.getInstance(getApplicationContext());
//        userID   = session.getUserId();
//        token    = session.getToken();

//        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                super.onDrawerSlide(drawerView, slideOffset);
//            }
//        };
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        Typeface typeface = Typeface.createFromAsset(SettingsActivity.this.getAssets(), "fonts/bebasneue.ttf");
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
//        //navigationView.setNavigationItemSelectedListener(this);
//        user = session.getUserDetails();
//        name = user.get("name");

//        initSwitches();

//        TextView  drawerUserName     = (TextView)  headerLayout.findViewById(R.id.tvUserName);
//        ImageView drawerBackground   = (ImageView) headerLayout.findViewById(R.id.slide_background);
//        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.imageUserPicture);
//        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//        Glide.with(this)
//                .load(uriProfile)
//                .bitmapTransform(new CropCircleTransformation(this))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(drawerImageProfile);
//
//        Glide.with(this)
//                .load(uriBlur)
//                .bitmapTransform(new CropSquareTransformation(this))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(drawerBackground);

//        TextView terms     = (TextView)findViewById(R.id.terms);
//        TextView about     = (TextView)findViewById(R.id.about);
//        TextView avatar    = (TextView)findViewById(R.id.avatar);
//        TextView privacy   = (TextView)findViewById(R.id.privacy);
//        TextView tvLegal   = (TextView)findViewById(R.id.tvLegal);
//        TextView delete    = (TextView)findViewById(R.id.delete_acc);
//        TextView tvGeneral = (TextView)findViewById(R.id.tvGeneral);
//        TextView contactUs = (TextView)findViewById(R.id.contact_us);
//        TextView tvNotif   = (TextView)findViewById(R.id.tvNotifications);
//
//        tvName = (TextView)findViewById(R.id.tvUserName);
//        tvChangeName = (TextView)findViewById(R.id.tvName);
//        tvName.setText(name);
////        drawerUserName.setText(name);
//
//        tvName.setTypeface(typeface);
//        tvLegal.setTypeface(typeface);
//        tvNotif.setTypeface(typeface);
//        tvGeneral.setTypeface(typeface);
////        drawerUserName.setTypeface(typeface);
//
//        about.setOnClickListener(this);
//        terms.setOnClickListener(this);
//        delete.setOnClickListener(this);
//        avatar.setOnClickListener(this);
//        privacy.setOnClickListener(this);
//        contactUs.setOnClickListener(this);
//        tvChangeName.setOnClickListener(this);

//        CHCheckPendingDuels checker = CHCheckPendingDuels.getInstance();
//        int count = checker.getPendingCount(getApplicationContext());
//        if (count == 0) {
//            checker.hideItem(navigationView);
//        } else {
//            TextView view = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_duels).getActionView();
//            view.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
//        }

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Runtime.getRuntime().runFinalization();
//        Runtime.getRuntime().gc();
//    }

    /**
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
            Button imageButtonAccept = (Button)findViewById(R.id.imageButtonAcceptMaybe);
            imageButtonAccept.setVisibility(View.GONE);
            findViewById(R.id.view11).setVisibility(View.GONE);
        }
        else {
            updateProfile(map);
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }

     */

//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.challenges:
//                updateProfile(map);
//                Intent goToChallenges = new Intent(SettingsActivity.this, MainActivity.class);
//                startActivity(goToChallenges);
//                finish();
//                break;
//            case R.id.friends:
//                updateProfile(map);
//                Intent goToFriends = new Intent(SettingsActivity.this, FriendsActivity.class);
//                startActivity(goToFriends);
//                finish();
//                break;
//            case R.id.history:
//                updateProfile(map);
//                Intent goToHistory = new Intent(SettingsActivity.this, HistoryActivity.class);
//                startActivity(goToHistory);
//                finish();
//                break;
//            case R.id.pending_duels:
//                updateProfile(map);
//                Intent goToPendingDuel = new Intent(SettingsActivity.this, PendingDuelActivity.class);
//                startActivity(goToPendingDuel);
//                finish();
//                break;
//            case R.id.share:
//                updateProfile(map);
//                String message = getString(R.string.share_text2);
//                Intent share = new Intent(Intent.ACTION_SEND);
//                share.setType("text/plain");
//                share.putExtra(Intent.EXTRA_TEXT, message);
//                startActivity(Intent.createChooser(share, getString(R.string.how_would_you_like_to_share)));
//                break;
//            case R.id.nav_logout:
//                if (offline.isConnectedToRemoteAPI(this)) {
//                    updateProfile(map);
//                    LoginManager.getInstance().logOut();
//                    session.logout(SettingsActivity.this);
//                    finish();
//                }
//                break;
//        }
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

//    @Override
//    public void onClick(View v) {
//        Intent intent;
//        switch (v.getId()) {
//            case R.id.tvName:
//                tvChangeName.setVisibility(View.INVISIBLE);
//                TextView tvEnterYourName = (TextView) findViewById(R.id.tvEntedYourName);
//                tvEnterYourName.setVisibility(View.VISIBLE);
//
//                EditText etNewName = (EditText) findViewById(R.id.new_name);
//                etNewName.setVisibility(View.VISIBLE);
//                etNewName.setText(name);
//
//                Button imageButtonAcceptName = (Button) findViewById(R.id.imageButtonAcceptMaybe);
//                imageButtonAcceptName.setVisibility(View.VISIBLE);
//
//                View lineOfTheNed = findViewById(R.id.view11);
//                lineOfTheNed.setVisibility(View.VISIBLE);
//
//                imageButtonAcceptName.setOnClickListener(v1 -> {
//                    String checkName = etNewName.getText().toString();
//                    if (offline.isConnectedToRemoteAPI(this) && !checkName.isEmpty()) {
//                        String newName = etNewName.getText().toString().trim();
//                        session.change_name(newName);
//                        setNewName(newName);
//
//                        tvName.setText(etNewName.getText().toString());
//                        imageButtonAcceptName.setVisibility(View.GONE);
//                        tvChangeName.setVisibility(View.VISIBLE);
//                        tvEnterYourName.setVisibility(View.GONE);
//                        lineOfTheNed.setVisibility(View.GONE);
//                        etNewName.setVisibility(View.GONE);
//                    }
//                });
//                break;
//            case R.id.avatar:
//                updateProfile(map);
//                intent = new Intent(SettingsActivity.this, PhotoActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.delete_acc:
//                if (!session.getChampyOptions().get("challenges").equals("0")) {
//                    surrenderAllChallengesDialog();
//                } else {
//                    deleteAccountDialog();
//                }
//                break;
//            case R.id.about:
//                updateProfile(map);
//                intent = new Intent(SettingsActivity.this, AboutActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.privacy:
//                updateProfile(map);
//                intent = new Intent(SettingsActivity.this, PrivacyActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.terms:
//                updateProfile(map);
//                intent = new Intent(SettingsActivity.this, TermsActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.contact_us:
//                updateProfile(map);
//                intent = new Intent(SettingsActivity.this, ContactUsActivity.class);
//                startActivity(intent);
//                break;
//
//        }
//    }


//    private void updateProfile(HashMap<String, String> map) {
//        session.toggleChallengeEnd(map.get("challengeEnd"));
//        session.togglePushNotification(map.get("pushNotifications"));
//        session.toggleChallengesForToday(map.get("challengesForToday"));
//        session.toggleNewChallengeRequest(map.get("newChallengeRequests"));
//        session.toggleAcceptYourChallenge(map.get("acceptedYourChallenge"));
//
//        Update_user update_user = retrofit.create(Update_user.class);
//        Profile_data profile_data = new Profile_data(map);
//        Call<User> call = update_user.update_profile_options(userID, token, profile_data);
//
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Response<User> response, Retrofit retrofit) {
//            }
//            @Override
//            public void onFailure(Throwable t) {
//            }
//        });
//    }


//    private void initSwitches() {
//        String pushNotify = user.get("pushN");
//        String acceptedYour = user.get("acceptedYour");
//        String challengeEnd = user.get("challengeEnd");
//        String newChallengeReq = user.get("newChallReq");
//        String challForToday = user.get("challengesForToday");
//
//        map.put("joinedChampy",          "true");
//        map.put("friendRequests",        "true");
//        map.put("challengeConfirmation", "true");
//        map.put("reminderTime",          "12"); // was 17
//        map.put("challengeEnd", challengeEnd);
//        map.put("challengesForToday", challForToday);
//        map.put("acceptedYourChallenge", acceptedYour);
//        map.put("newChallengeRequests", newChallengeReq);
//        map.put("pushNotifications", pushNotify);
//
//        Switch switchForPushNotif = (Switch) findViewById(R.id.switchPushNotifications);
//        switchForPushNotif.setChecked(Boolean.parseBoolean(pushNotify));
//        switchForPushNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                map.put("pushNotifications", "true");
//                updateProfile(map);
//            } else {
//                map.put("pushNotifications", "false");
//                updateProfile(map);
//            }
//        });
//
//        Switch switchForNewChallRequests = (Switch) findViewById(R.id.switchNewChallengeRequest);
//        switchForNewChallRequests.setChecked(Boolean.parseBoolean(newChallengeReq));
//        switchForNewChallRequests.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                map.put("newChallengeRequests", "true");
//                updateProfile(map);
//            } else {
//                map.put("newChallengeRequests", "false");
//                updateProfile(map);
//            }
//        });
//
//        Switch switchForAcceptedYourChall = (Switch) findViewById(R.id.switchAcceptedYourChallenge);
//        switchForAcceptedYourChall.setChecked(Boolean.parseBoolean(acceptedYour));
//        switchForAcceptedYourChall.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                map.put("acceptedYourChallenge", "true");
//                updateProfile(map);
//            } else {
//                map.put("acceptedYourChallenge", "false");
//                updateProfile(map);
//            }
//        });
//
//        Switch switchForChallengesEnd = (Switch) findViewById(R.id.switchChallengeEnd);
//        switchForChallengesEnd.setChecked(Boolean.parseBoolean(challengeEnd));
//        switchForChallengesEnd.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                map.put("challengeEnd", "true");
//                updateProfile(map);
//            } else {
//                map.put("challengeEnd", "false");
//                updateProfile(map);
//            }
//        });
//
//        Switch switchChallengesForToday = (Switch) findViewById(R.id.switchChallengesForToday);
//        switchChallengesForToday.setChecked(Boolean.parseBoolean(challForToday));
//        switchChallengesForToday.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                map.put("challengesForToday", "true");
//                reminder.enableDailyNotificationReminder();
//                updateProfile(map);
//            } else {
//                map.put("challengesForToday", "false");
//                reminder.disableDailyNotificationReminder();
//                updateProfile(map);
//            }
//        });
//
//    }


//    private void setNewName(String newName) {
//        Update_user update_user = retrofit.create(Update_user.class);
//        Call<User> call = update_user.update_user_name(userID, token, newName);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Response<User> response, Retrofit retrofit) {
//                if (response.isSuccess()) {
//                    recreate();
//                } else {
//                    Toast.makeText(SettingsActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
//                }
//
//            }
//            @Override
//            public void onFailure(Throwable t) {
//                Toast.makeText(SettingsActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
//            }
//        });
//    }


//    private void surrenderAllChallengesDialog() {
//        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
//            boolean canDeleteAcc = false;
//            switch (which) {
//                case DialogInterface.BUTTON_POSITIVE:
//                    if (offline.isConnectedToRemoteAPI(SettingsActivity.this)) {
//                        SQLiteDatabase db = dbHelper.getWritableDatabase();
//                        ChallengeController cc = new ChallengeController(
//                                getApplicationContext(),
//                                SettingsActivity.this,
//                                token,
//                                userID
//                        );
//
//                        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
//                        if (c.moveToFirst()) {
//                            int colchallenge_id = c.getColumnIndex("challenge_id");
//                            do {
//                                String challenge_id = c.getString(colchallenge_id);
//                                try {
//                                    cc.give_up(challenge_id, 0, null);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            } while (c.moveToNext());
//                        }
//                        canDeleteAcc = true;
//                        c.close();
//                    }
//                    break;
//                case DialogInterface.BUTTON_NEGATIVE:
//                    break;
//            }
//
//            if (canDeleteAcc) {
//                dialog.cancel();
//                deleteAccountDialog();
//            }
//
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
//        builder.setTitle(R.string.areYouSure)
//                .setMessage("If you continue you will lose all your challenges")
//                .setCancelable(false)
//                .setPositiveButton("Continue", dialogClickListener)
//                .setNegativeButton("Cancel", dialogClickListener)
//                .show();
//
//    }
//
//
//    private void deleteAccountDialog() {
//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent role = new Intent(SettingsActivity.this, RoleControllerActivity.class);
//                switch (which) {
//                    case DialogInterface.BUTTON_POSITIVE:
//                        final Update_user update_user = retrofit.create(Update_user.class);
//
//                        Call<Delete> callForDeleteUser = update_user.delete_user(userID, token);
//                        callForDeleteUser.enqueue(new Callback<Delete>() {
//                            @Override
//                            public void onResponse(Response<Delete> response, Retrofit retrofit) {
//                                if (response.isSuccess()) {
//                                    File profile = new File(path, "profile.jpg");
//                                    profile.delete();
//                                    File blurred = new File(path, "blurred.png");
//                                    blurred.delete();
//
//                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
//                                    db.delete("pending", null, null);
//                                    db.delete("pending_duel", null, null);
//                                    db.delete("duel", null, null);
//                                    db.delete("friends", null, null);
//                                    db.delete("updated", null, null);
//                                    db.delete("myChallenges", null, null);
//
//                                    session.logout(SettingsActivity.this);
//                                    LoginManager.getInstance().logOut();
//                                    startActivity(role);
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Throwable t) {}
//                        });
//                        break;
//
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        startActivity(role);
//                        break;
//                }
//            }
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
//        builder.setTitle(R.string.areYouSure)
//                .setMessage(R.string.youWantToDeleteYourAcc)
//                .setCancelable(false)
//                .setPositiveButton(R.string.yes, dialogClickListener)
//                .setNegativeButton(R.string.no, dialogClickListener)
//                .show();
//    }



}
