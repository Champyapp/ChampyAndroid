package com.azinecllc.champy.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.fragment.FriendsFragment;
import com.azinecllc.champy.fragment.MainFragmentWithTabs;
import com.azinecllc.champy.fragment.PrivacyPoliceFragment;
import com.azinecllc.champy.fragment.SettingsHelpFragment;
import com.azinecllc.champy.fragment.SettingsNotificationsFragment;
import com.azinecllc.champy.fragment.SettingsProfileFragment;
import com.azinecllc.champy.fragment.TermsFragment;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.azinecllc.champy.utils.Constants.TAG_CHALLENGES;
import static com.azinecllc.champy.utils.Constants.TAG_PRIVACY_POLICE;
import static com.azinecllc.champy.utils.Constants.TAG_SETTINGS_HELP_AND_FEEDBACK;
import static com.azinecllc.champy.utils.Constants.TAG_SETTINGS_NOTIFICATIONS;
import static com.azinecllc.champy.utils.Constants.TAG_SETTINGS_PROFILE;
import static com.azinecllc.champy.utils.Constants.TAG_TERMS;

/**
 * MAIN ACTIVITY
 */
public class ChallengeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String CURRENT_TAG = TAG_CHALLENGES;
    public static int navItemIndex = 0;

    private static final String TAG = "ChallengeActivity";

    private SessionManager sessionManager;
    private DrawerLayout drawer;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_challenges);
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


        // NAVIGATION VIEW
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(ChallengeActivity.this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.drawer_background);
        ImageView drawerUserPhoto = (ImageView) headerLayout.findViewById(R.id.drawer_user_photo);
        TextView drawerUserEmail = (TextView) headerLayout.findViewById(R.id.drawer_tv_user_email);
        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.drawer_tv_user_name);



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

        // PENDING DUEL MENU IN DRAWER
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

        if (navItemIndex == 6 && getCallingActivity() != null) {
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
            case R.id.nav_settings_profile:
                navItemIndex = 1;
                CURRENT_TAG = TAG_SETTINGS_PROFILE;
                break;
            case R.id.nav_settings_notifications:
                navItemIndex = 2;
                CURRENT_TAG = TAG_SETTINGS_NOTIFICATIONS;
                break;
            case R.id.nav_settings_help_and_feedback:
                navItemIndex = 3;
                CURRENT_TAG = TAG_SETTINGS_HELP_AND_FEEDBACK;
                break;
            case R.id.nav_terms:
                navItemIndex = 4;
                CURRENT_TAG = TAG_TERMS;
                break;
            case R.id.nav_privacy_policy:
                navItemIndex = 5;
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
        //toggleFab();             // show or hide the fab button
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
                return new SettingsProfileFragment();
            case 2:
                return new SettingsNotificationsFragment();
            case 3:
                return new SettingsHelpFragment();
            case 4:
                return new TermsFragment();
            case 5:
                return new PrivacyPoliceFragment();
            case 6:
                return new FriendsFragment();
            default:
                return new MainFragmentWithTabs();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
        if (navItemIndex == 6 && getCallingActivity() != null) {

            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.menu_search, menu);

            MenuItem searchItem = menu.findItem(R.id.action_search);

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            SearchView searchView = null;
            if (searchItem != null) {
                searchView = (SearchView) searchItem.getActionView();
            }
            if (searchView != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == R.id.action_search) {
            Toast.makeText(this, "Search...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}