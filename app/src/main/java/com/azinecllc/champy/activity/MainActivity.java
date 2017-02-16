package com.azinecllc.champy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.MainActivityCardsAdapter;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.fragment.MainFragment;
import com.azinecllc.champy.fragment.PrivacyPoliceFragment;
import com.azinecllc.champy.fragment.SettingsFragment;
import com.azinecllc.champy.fragment.TermsFragment;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.Date;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.azinecllc.champy.utils.Constants.API_URL;
import static com.azinecllc.champy.utils.Constants.TAG_CHALLENGES;
import static com.azinecllc.champy.utils.Constants.TAG_SETTINGS;
import static com.azinecllc.champy.utils.Constants.TAG_TERMS;
import static com.azinecllc.champy.utils.Constants.TAG_PRIVACY_POLICE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainActivity";
    public static String CURRENT_TAG = TAG_CHALLENGES;
    public static int navItemIndex = 0;

    private String[] activityTitles;
    private boolean isFabOpen = false;

    private SessionManager sessionManager;
    private DrawerLayout drawer;

    private FloatingActionButton fabPlus, fabWake, fabSelf, fabDuel;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    private NavigationView navigationView;
    private Handler mHandler;
    private Context context;
    private Socket mSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: ");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DRAWER
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                //if (isFabOpen) closeFab();
            }
        };
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // NAVIGATION VIEW
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        ImageView drawerUserPhoto = (ImageView) headerLayout.findViewById(R.id.imageUserPicture);
        TextView drawerUserEmail = (TextView) headerLayout.findViewById(R.id.tvUserEmail);
        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);

        // GET PHOTO AND MAKE BLUR
        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
        sessionManager = SessionManager.getInstance(getApplicationContext());
        String userPicture = sessionManager.getUserPicture();
        String userEmail = sessionManager.getUserEmail();
        String userName = sessionManager.getUserName();
        ImageView background = (ImageView) findViewById(R.id.main_background);
        Glide.with(this)
                .load(userPicture)
                .bitmapTransform(new BlurTransformation(getApplicationContext(), 25))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(background);
        Glide.with(this)
                .load(userPicture)
                .bitmapTransform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerUserPhoto);
        Glide.with(this)
                .load(userPicture)
                .bitmapTransform(new BlurTransformation(getApplicationContext(), 25))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerBackground);
        drawerUserName.setText(userName);
        drawerUserEmail.setText(userEmail);

        fabPlus = (FloatingActionButton) findViewById(R.id.fabPlus);
        fabSelf = (FloatingActionButton) findViewById(R.id.fabSelf);
        fabDuel = (FloatingActionButton) findViewById(R.id.fabDuel);
        fabWake = (FloatingActionButton) findViewById(R.id.fabWake);
        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        fabPlus.setOnClickListener(v -> animateFAB());
        fabSelf.setOnClickListener(this);
        fabDuel.setOnClickListener(this);
        fabWake.setOnClickListener(this);

        // PENDING DUEL MENU IN DRAWER
        setCounterForPendingDuels();

        loadHomeFragment();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");

        try {
            mSocket = IO.socket(API_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.on("connect", onConnect);
        mSocket.on("connected", onConnected);

//        InProgressChallenge:new
//        InProgressChallenge:accepted
//        InProgressChallenge:failed
//        InProgressChallenge:checked
//        InProgressChallenge:recipient:checked
//        InProgressChallenge:sender:checked
//        InProgressChallenge:updated
//        InProgressChallenge:won
//        InProgress:finish

        mSocket.on("InProgressChallenge:accepted", modifiedChallenges);
        mSocket.on("InProgressChallenge:new", modifiedChallenges);
        mSocket.on("InProgressChallenge:won", modifiedChallenges);
        mSocket.on("InProgressChallenge:updated", modifiedChallenges);

        mSocket.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Date now = new Date(System.currentTimeMillis());
        Log.i(TAG, "onStop: Sockets disconnected "
                + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds());

        //mSocket.disconnect();
        mSocket.off("InProgressChallenge:accepted", modifiedChallenges);
        mSocket.off("InProgressChallenge:new", modifiedChallenges);
        mSocket.off("InProgressChallenge:won", modifiedChallenges);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        navItemIndex = 0;
        CURRENT_TAG = TAG_CHALLENGES;
        loadHomeFragment();

    }

    @Override
    public void onClick(View v) {
        if (Integer.parseInt(sessionManager.getChampyOptions().get("challenges")) < 10) {
            switch (v.getId()) {
                case R.id.fabSelf:
                    startActivity(new Intent(this, SelfImprovementActivity.class));
                    break;
                case R.id.fabDuel:
                    new Handler().postDelayed(() -> startActivity(new Intent(this, FriendsActivity.class)), 250);
                    break;
                case R.id.fabWake:
                    startActivity(new Intent(this, WakeUpActivity.class));
                    break;
            }
            animateFAB();
        } else {
            Toast.makeText(this, R.string.challenges_to_much, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_challenges:
                navItemIndex = 0;
                CURRENT_TAG = TAG_CHALLENGES;
                break;
            case R.id.nav_friends:
                new Handler().postDelayed(() -> startActivity(new Intent(this, FriendsActivity.class)), 250);
                break;
            case R.id.nav_history:
                new Handler().postDelayed(() -> startActivity(new Intent(this, HistoryActivity.class)), 250);
                break;
            case R.id.nav_pending_duels:
                new Handler().postDelayed(() -> startActivity(new Intent(this, PendingDuelActivity.class)), 250);
                break;
            case R.id.nav_settings:
                navItemIndex = 2;
                CURRENT_TAG = TAG_SETTINGS;
                break;
            case R.id.nav_terms:
                navItemIndex = 3;
                CURRENT_TAG = TAG_TERMS;
                break;
            case R.id.nav_privacy_policy:
                navItemIndex = 4;
                CURRENT_TAG = TAG_PRIVACY_POLICE;
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        loadHomeFragment();
        return true;
    }



    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "Sockets call: onConnect");
            mSocket.emit("ready", sessionManager.getToken());
        }
    };

    private Emitter.Listener onConnected = args -> Log.i(TAG, "Sockets call: onConnected!");

    private Emitter.Listener modifiedChallenges = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "Sockets call: modifiedChallenges");
            ChallengeController cc = new ChallengeController(
                    context,
                    MainActivity.this,
                    sessionManager.getToken(),
                    sessionManager.getUserId());

            cc.refreshCardsForPendingDuel(null);
            setCounterForPendingDuels();
        }
    };



    /**
     * Method provider which returns respected fragment that user selected from navigation menu.
     * I transmit needed fragment to method 'loadHomeFragment' which will show it.
     */
    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return new MainFragment();
            //case 1:
            //    return new PendingDuelFragment();
            case 2:
                return new SettingsFragment();
            case 3:
                return new TermsFragment();
            case 4:
                return new PrivacyPoliceFragment();
            default:
                return new MainFragment();
        }
    }

    /**
     * Method which shows selected fragment from drawer and loads it in UI Thread, because we need to
     * avoid large object and freezes. In this method I also was set current title for toolbar.
     */
    private void loadHomeFragment() {
        // if user select the current navigation menu again, just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Sometimes, when fragment has huge data, screen seems hanging when switching between
                // navigation menus So using runnable, the fragment is loaded with cross fade effect
                // This effect can be seen in GMail app
                // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        navigationView.getMenu().getItem(navItemIndex);   // selecting appropriate nav menu item
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(activityTitles[navItemIndex]); // set toolbar title

        mHandler = new Handler();
        mHandler.post(runnable); // If 'runnable' is not null, then add to the message queue
        drawer.closeDrawers();   // Closing drawer on item click
        invalidateOptionsMenu(); // refresh toolbar menu
        toggleFab();             // show or hide the fab button
    }

    /**
     * Method to check pending counter and after that set value for navigation drawer menu
     */
    private void setCounterForPendingDuels() {
        CHCheckPendingDuels checker = CHCheckPendingDuels.getInstance();
        int count = checker.getPendingCount(getApplicationContext());
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_duels).getActionView();
        runOnUiThread(() -> view.setText(count > 0 ? String.valueOf(getString(R.string.plus) + count) : null));
    }

    /**
     * Method which includes two method below. This method works like a toggle, on-off system.
     */
    private void animateFAB() {
        if (isFabOpen) {
            closeFab();
        } else {
            openFab();
        }
    }

    /**
     * Method to animate fab button, and make visible all sub buttons,
     */
    private void closeFab() {
        fabPlus.startAnimation(rotate_backward);
        fabWake.startAnimation(fab_close);
        fabSelf.startAnimation(fab_close);
        fabDuel.startAnimation(fab_close);
        isFabOpen = false;
    }

    /**
     * Method to animate fab button, and make invisible all sub buttons,
     */
    private void openFab() {
        fabPlus.startAnimation(rotate_forward);
        fabWake.startAnimation(fab_open);
        fabSelf.startAnimation(fab_open);
        fabDuel.startAnimation(fab_open);
        isFabOpen = true;
    }

    /**
     * Method to set visible for FabPlus if current fragment != main
     */
    private void toggleFab() {
        if (navItemIndex == 0) {
            fabPlus.show();
        } else {
            fabPlus.hide();
        }
    }



}