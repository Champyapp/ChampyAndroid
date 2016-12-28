package com.azinecllc.champy.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.MainActivityCardsAdapter;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.helper.CHBuildAnim;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.helper.CHDownloadImageTask;
import com.azinecllc.champy.model.SelfImprovement_model;
import com.azinecllc.champy.utils.CustomPagerBase;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

import static com.azinecllc.champy.utils.Constants.path;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private SessionManager sessionManager;
    private Boolean isFabOpen = false;
    private DrawerLayout drawer;
    private FloatingActionButton fabPlus, fabWake, fabSelf, fabDuel;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private MainActivityCardsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = SessionManager.getInstance(getApplicationContext());
        adapter = new MainActivityCardsAdapter(this, SelfImprovement_model.generate(this));
        if (adapter.dataCount() > 0) {
            RelativeLayout cards = (RelativeLayout) findViewById(R.id.cards);
            CustomPagerBase pager = new CustomPagerBase(MainActivity.this, cards, adapter);
            pager.preparePager(0);
        }

        // DRAWER
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (isFabOpen) closeFab();
            }
        };
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // NAVIGATION VIEW
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        ImageView drawerUserPhoto = (ImageView) headerLayout.findViewById(R.id.profile_image);
        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);


        // GET PHOTO AND MAKE BLUR
        ImageView background = (ImageView) findViewById(R.id.main_background);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);
        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        File blurred = new File(path, "blurred.png");
        if (blurred.exists()) {
            File profile = new File(path, "profile.jpg");
            Glide.with(this).load(profile).bitmapTransform(new CropCircleTransformation(this)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerUserPhoto);
            Glide.with(this).load(blurred).bitmapTransform(new CropSquareTransformation(this)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(background);
            Glide.with(this).load(blurred).bitmapTransform(new CropSquareTransformation(this)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerBackground);
        } else {
            final String pathToPic = sessionManager.getPathToPic();
            CHDownloadImageTask chDownloadImageTask = new CHDownloadImageTask(getApplicationContext(), MainActivity.this);
            chDownloadImageTask.execute(pathToPic);
        }

        // USER NAME
        final String name = sessionManager.getUserName();
        drawerUserName.setText(name);
        Typeface typeface = android.graphics.Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
        drawerUserName.setTypeface(typeface);

        // PENDING DUEL MENU IN DRAWER
        CHCheckPendingDuels checker = CHCheckPendingDuels.getInstance();
        int count = checker.getPendingCount(getApplicationContext());
        if (count == 0) {
            checker.hideItem(navigationView);
        } else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            view.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
        }

        // ANIM
        CHBuildAnim chBuildAnim = CHBuildAnim.getInstance();
        chBuildAnim.buildAnim(this, sessionManager, typeface);

        // BUTTONS
        fabPlus   = (FloatingActionButton)findViewById(R.id.fabPlus);
        fabSelf   = (FloatingActionButton)findViewById(R.id.fabSelf);
        fabDuel   = (FloatingActionButton)findViewById(R.id.fabDuel);
        fabWake   = (FloatingActionButton)findViewById(R.id.fabWake);
        fab_open  = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotate_forward  = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        fabPlus.setOnClickListener(v -> animateFAB());
        fabSelf.setOnClickListener(this);
        fabDuel.setOnClickListener(this);
        fabWake.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (adapter.dataCount() < 10) {
            switch (v.getId()) {
                case R.id.fabSelf: startActivity(new Intent(this, SelfImpActivity.class)); finish(); break;
                case R.id.fabDuel: startActivity(new Intent(this, FriendsActivity.class)); finish(); break;
                case R.id.fabWake: startActivity(new Intent(this, WakeUpActivity .class)); finish(); break;
            }
        } else {
            Toast.makeText(this, R.string.you_have_too_much_challenges, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isFabOpen) {
            closeFab();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
//        new Handler().postDelayed(() -> {
//        navDrawerHandler.postDelayed(mRunnable, drawerCloseTime);
            switch (item.getItemId()) {
                case R.id.friends:
                    Intent goToFriends = new Intent(MainActivity.this, FriendsActivity.class);
                    startActivity(goToFriends);
                    startActivity(new Intent(this, FriendsActivity.class));
                    finish();
                    break;
                case R.id.history:
                    Intent goToHistory = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(goToHistory);
                    finish();
                    break;
                case R.id.settings:
                    Intent goToSettings = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(goToSettings);
                    finish();
                    break;
                case R.id.pending_duels:
                    Intent goToPendingDuel = new Intent(MainActivity.this, PendingDuelActivity.class);
                    startActivity(goToPendingDuel);
                    finish();
                    break;
                case R.id.share:
                    String message = getString(R.string.share_text2);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(share, getString(R.string.how_would_you_like_to_share)));
                    break;
                case R.id.nav_logout:
                    OfflineMode offlineMode = OfflineMode.getInstance();
                    if (offlineMode.isConnectedToRemoteAPI(MainActivity.this)) {
                        sessionManager.logout(MainActivity.this);
                        finish();
                    }
                    break;
            }
//        }, drawerCloseTime);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            final String token = sessionManager.getToken();
            final String userId = sessionManager.getUserId();
            ChallengeController cc = new ChallengeController(getApplicationContext(), this, token, userId);
            cc.refreshCardsForPendingDuel(new Intent(this, MainActivity.class));
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


    private void animateFAB() {
        if(isFabOpen) {
            closeFab();
        } else {
            openFab();
        }
    }


    private void closeFab() {
        fabPlus.startAnimation(rotate_backward);
        fabWake.startAnimation(fab_close);
        fabSelf.startAnimation(fab_close);
        fabDuel.startAnimation(fab_close);
        isFabOpen = false;
    }


    private void openFab() {
        fabPlus.startAnimation(rotate_forward);
        fabWake.startAnimation(fab_open);
        fabSelf.startAnimation(fab_open);
        fabDuel.startAnimation(fab_open);
        isFabOpen = true;
    }


}