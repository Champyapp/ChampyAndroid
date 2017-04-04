package com.azinecllc.champy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.MainActivityPagerAdapter;
import com.azinecllc.champy.fragment.MainFragmentWithTabs;
import com.azinecllc.champy.fragment.SettingsHelpFragment;
import com.azinecllc.champy.fragment.SettingsNotificationsFragment;
import com.azinecllc.champy.fragment.SettingsProfileFragment;
import com.azinecllc.champy.fragment.FriendsFragment;
import com.azinecllc.champy.fragment.MainCardsFragment;
import com.azinecllc.champy.fragment.PrivacyPoliceFragment;
import com.azinecllc.champy.fragment.TermsFragment;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.azinecllc.champy.Champy.getContext;
import static com.azinecllc.champy.utils.Constants.TAG_CHALLENGES;
import static com.azinecllc.champy.utils.Constants.TAG_FRIENDS;
import static com.azinecllc.champy.utils.Constants.TAG_PRIVACY_POLICE;
import static com.azinecllc.champy.utils.Constants.TAG_SETTINGS_HELP_AND_FEEDBACK;
import static com.azinecllc.champy.utils.Constants.TAG_SETTINGS_NOTIFICATIONS;
import static com.azinecllc.champy.utils.Constants.TAG_SETTINGS_PROFILE;
import static com.azinecllc.champy.utils.Constants.TAG_TERMS;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String CURRENT_TAG = TAG_CHALLENGES;
    public static int navItemIndex = 0;

    private static final String TAG = "MainActivity";

    private FloatingActionButton fabPlus, fabWake, fabSelf, fabDuel;
    //private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private boolean isFabOpen = false;
    //private TextView nothingHere;
    private ImageView circleLogo;
    private SessionManager sessionManager;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    //private Socket mSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i(TAG, "onCreate: ");
        if (getIntent().getExtras() != null) {
            String extrasTag = getIntent().getExtras().getString("tag");
            int extrasIndex = getIntent().getExtras().getInt("index");
            if (extrasTag != null) {
                System.out.println("main activity extras tag: " + extrasTag);
                System.out.println("main activity extras ind: " + extrasIndex);
                navItemIndex = extrasIndex;
                CURRENT_TAG = extrasTag;
            }
        }

        // DRAWER
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                InputMethodManager input = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (getWindow().getCurrentFocus() != null)
                    input.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

//        MainActivityPagerAdapter adapterViewPager = new MainActivityPagerAdapter(getSupportFragmentManager(), this);
//        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager_main);
//        viewPager.setAdapter(adapterViewPager);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_main);
//        tabLayout.setupWithViewPager(viewPager);

        // NAVIGATION VIEW
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.drawer_background);
        ImageView drawerUserPhoto = (ImageView) headerLayout.findViewById(R.id.drawer_user_photo);
        TextView drawerUserEmail = (TextView) headerLayout.findViewById(R.id.drawer_tv_user_email);
        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.drawer_tv_user_name);

        fabPlus = (FloatingActionButton) findViewById(R.id.fabPlus);
        //nothingHere = (TextView) findViewById(R.id.tv_noting_here_yet);
//        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
//        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
//        rotate_forward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_forward);
//        rotate_backward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_backward);

        // GET PHOTO AND MAKE BLUR
        sessionManager = SessionManager.getInstance(getApplicationContext());
        String userPicture = sessionManager.getUserPicture();
        String userEmail = sessionManager.getUserEmail();
        String userName = sessionManager.getUserName();
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


//        int inProgress = Integer.parseInt(sessionManager.getChampyOptions().get("challenges"));
//        if (!sessionManager.getChampyOptions().get("challenges").isEmpty()) {
//            nothingHere.setVisibility(View.INVISIBLE);
//        }


        fabPlus.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CreateChallengeActivity.class));
        });


        // PENDING DUEL MENU IN DRAWER
        //setCounterForPendingDuels();

        loadCurrentFragment();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
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
        Log.i(TAG, "onStop: ");
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
        Log.i(TAG, "onBackPressed: ");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (navItemIndex == 1 && getCallingActivity() != null) {
            System.out.println("activity for result");
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
        navItemIndex = 0;
        CURRENT_TAG = TAG_CHALLENGES;
        loadCurrentFragment();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_challenges:
                navItemIndex = 0;
                CURRENT_TAG = TAG_CHALLENGES;
                break;
            case R.id.nav_friends:
                navItemIndex = 1;
                CURRENT_TAG = TAG_FRIENDS;
                break;
            case R.id.nav_settings_profile:
                navItemIndex = 2;
                CURRENT_TAG = TAG_SETTINGS_PROFILE;
                break;
            case R.id.nav_settings_notifications:
                navItemIndex = 3;
                CURRENT_TAG = TAG_SETTINGS_NOTIFICATIONS;
                break;
            case R.id.nav_settings_help_and_feedback:
                navItemIndex = 4;
                CURRENT_TAG = TAG_SETTINGS_HELP_AND_FEEDBACK;
                break;
            case R.id.nav_terms:
                navItemIndex = 5;
                CURRENT_TAG = TAG_TERMS;
                break;
            case R.id.nav_privacy_policy:
                navItemIndex = 6;
                CURRENT_TAG = TAG_PRIVACY_POLICE;
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        loadCurrentFragment();
        return true;
    }


    /**
     * Method which shows selected fragment from drawer and loads it in UI Thread, because we need to
     * avoid large object and freezes. In this method I also was set current title for toolbar.
     */
    private void loadCurrentFragment() {
        // if user select the current navigation menu again, just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        Runnable runnable = () -> {
            // update the main content by replacing fragments
            Fragment fragment = getHomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        };

        String[] activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        //navigationView.getMenu().getItem(navItemIndex);   // selecting appropriate nav menu item
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(activityTitles[navItemIndex]); // set toolbar title
        }
        Handler mHandler = new Handler();
        mHandler.post(runnable); // If 'runnable' is not null, then add to the message queue
        drawer.closeDrawers();   // Closing drawer on item click
        invalidateOptionsMenu(); // refresh toolbar menu
        toggleFab();             // show or hide the fab button
    }

    /**
     * Method provider which returns respected fragment that user selected from navigation menu.
     * I transmit needed fragment to method 'loadCurrentFragment' which will show it.
     */
    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return new MainFragmentWithTabs();
            case 1:
                return new FriendsFragment();
            case 2:
                return new SettingsProfileFragment();
            case 3:
                return new SettingsNotificationsFragment();
            case 4:
                return new SettingsHelpFragment();
            case 5:
                return new TermsFragment();
            case 6:
                return new PrivacyPoliceFragment();
            default:
                return new MainFragmentWithTabs();
        }
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

    //    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.i(TAG, "onStart: ");
//
//        try {
//            mSocket = IO.socket(API_URL);
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//
//        mSocket.on("connect", onConnect);
//        mSocket.on("connected", onConnected);
//        mSocket.on("InProgressChallenge:accepted", modifiedChallenges);
//        mSocket.on("InProgressChallenge:new", modifiedChallenges);
//        mSocket.on("InProgressChallenge:won", modifiedChallenges);
//        //mSocket.on("InProgressChallenge:updated", modifiedChallenges);
//
//        mSocket.connect();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        //mSocket.disconnect();
//        mSocket.off("InProgressChallenge:accepted", modifiedChallenges);
//        mSocket.off("InProgressChallenge:new", modifiedChallenges);
//        mSocket.off("InProgressChallenge:won", modifiedChallenges);
//
//    }

    //    private Emitter.Listener onConnect = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            Log.i(TAG, "Sockets call: onConnect");
//            mSocket.emit("ready", sessionManager.getToken());
//        }
//    };
//
//    private Emitter.Listener onConnected = args -> Log.i(TAG, "Sockets call: onConnected!");
//
//    private Emitter.Listener modifiedChallenges = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            Log.i(TAG, "Sockets call: modifiedChallenges");
//            ChallengeController cc = new ChallengeController(getApplicationContext(), MainActivity.this);
//            cc.refreshCardsForPendingDuel(null);
//            setCounterForPendingDuels(); // not good solution
//        }
//    };

//    /**
//     * Method-toggle to control visibility of the sub buttons. This method works like a on-off system.
//     */
//    private void animateFAB() {
//        if (isFabOpen) {
//            //closeFab();
//            fabPlus.startAnimation(rotate_backward);
//            fabWake.startAnimation(fab_close);
//            fabSelf.startAnimation(fab_close);
//            fabDuel.startAnimation(fab_close);
//            isFabOpen = false;
//        } else {
//            //openFab();
//            fabPlus.startAnimation(rotate_forward);
//            fabWake.startAnimation(fab_open);
//            fabSelf.startAnimation(fab_open);
//            fabDuel.startAnimation(fab_open);
//            isFabOpen = true;
//        }
//    }

//    /**
//     * Method to check pending counter and after that set value for navigation drawer menu
//     */
//    private void setCounterForPendingDuels() {
//        // SETTING CURRENT PENDING COUNT
//        if (!sessionManager.getDuelPending().equals("0")) {
//            TextView view = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_duels).getActionView();
//            runOnUiThread(() -> view.setText(String.format("%s%s", getString(R.string.plus), sessionManager.getDuelPending())));
//        }
//
//    }
//
//


}