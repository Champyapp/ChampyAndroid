package com.azinecllc.champy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.MainActivityCardsAdapter;
import com.azinecllc.champy.fragment.MainFragment;
import com.azinecllc.champy.fragment.PendingDuelFragment;
import com.azinecllc.champy.fragment.PrivacyPoliceFragment;
import com.azinecllc.champy.fragment.SettingsFragment;
import com.azinecllc.champy.fragment.TermsFragment;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.helper.CHDownloadImageTask;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.facebook.FacebookSdk;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

import static android.support.v7.appcompat.R.attr.background;
import static com.azinecllc.champy.utils.Constants.TAG_CHALLENGES;
// friends
// history
import static com.azinecllc.champy.utils.Constants.TAG_PENDING_DUELS;
import static com.azinecllc.champy.utils.Constants.TAG_SETTINGS;
import static com.azinecllc.champy.utils.Constants.TAG_TERMS;
import static com.azinecllc.champy.utils.Constants.TAG_PRIVACY_POLICE;
import static com.azinecllc.champy.utils.Constants.path;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SessionManager sessionManager;
    private DrawerLayout drawer;
    private MainActivityCardsAdapter adapter;
    private NavigationView navigationView;

    public static String CURRENT_TAG = TAG_CHALLENGES;
    public static int navItemIndex = 0;
    private String[] activityTitles;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DRAWER
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
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
        File blurred = new File(path, "blurred.png");
        if (blurred.exists()) {
            ImageView background = (ImageView) findViewById(R.id.main_background);
            Glide.with(this)
                    .load(blurred)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(background);
            File profile = new File(path, "profile.jpg");
            Glide.with(this)
                    .load(profile)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(drawerUserPhoto);
            Glide.with(this)
                    .load(blurred)
                    .bitmapTransform(new CropSquareTransformation(this))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(drawerBackground);
        } else {
            String pathToPic = sessionManager.getPathToPic();
            CHDownloadImageTask chDownloadImageTask = new CHDownloadImageTask(getApplicationContext(), MainActivity.this);
            chDownloadImageTask.execute(pathToPic);
        }

        // USER NAME
        String name = sessionManager.getUserName();
        String email = sessionManager.getUserEmail();
        drawerUserName.setText(name);
        drawerUserEmail.setText(email);

        // PENDING DUEL MENU IN DRAWER
        CHCheckPendingDuels checker = CHCheckPendingDuels.getInstance();
        int count = checker.getPendingCount(getApplicationContext());
        if (count != 0) {
            //checker.hideItem(navigationView);
            //} else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.nav_pending_duels).getActionView();
            view.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
        }


        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_CHALLENGES;
            loadHomeFragment();
        }

    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        navigationView.getMenu().getItem(navItemIndex);   // selecting appropriate nav menu item
        getSupportActionBar().setTitle(activityTitles[navItemIndex]); // set toolbar title

        // if user select the current navigation menu again, just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        mHandler = new Handler();
        mHandler.post(runnable); // If 'runnable' is not null, then add to the message queue
        drawer.closeDrawers();   // Closing drawer on item click
        invalidateOptionsMenu(); // refresh toolbar menu
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (!CURRENT_TAG.equals(TAG_CHALLENGES)) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_CHALLENGES;
            loadHomeFragment();
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
                //navItemIndex = 1;
                //CURRENT_TAG = TAG_FRIENDS;
                startActivity(new Intent(this, FriendsActivity.class));
                break;
            case R.id.nav_history:
                //navItemIndex = 2;
                //CURRENT_TAG = TAG_HISTORY;
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            case R.id.nav_pending_duels:
                navItemIndex = 1;
                CURRENT_TAG = TAG_PENDING_DUELS;
                break;
            case R.id.nav_settings:
                navItemIndex = 2;
                CURRENT_TAG = TAG_SETTINGS;
                break;
            case R.id.nav_terms:
                navItemIndex = 3;
                CURRENT_TAG = TAG_TERMS;
                //startActivity(new Intent(this, TermsActivity.class));
                break;
            case R.id.nav_privacy_policy:
                navItemIndex = 4;
                CURRENT_TAG = TAG_PRIVACY_POLICE;
                //startActivity(new Intent(this, PrivacyActivity.class));
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        loadHomeFragment();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, RoleControllerActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync_app, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return new MainFragment();
//            case 1: return new FragmentFriends();
//            case 2: return new HistoryFragment();
            case 1:
                return new PendingDuelFragment();

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


}