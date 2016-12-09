package com.example.ivan.champy_v2.activity;

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

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.adapter.MainActivityCardsAdapter;
import com.example.ivan.champy_v2.controller.ChallengeController;
import com.example.ivan.champy_v2.helper.CHBuildAnim;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHDownloadImageTask;
import com.example.ivan.champy_v2.model.SelfImprovement_model;
import com.example.ivan.champy_v2.utils.CustomPagerBase;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

import static com.example.ivan.champy_v2.utils.Constants.path;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MainActivityCardsAdapter adapter;
    private SessionManager sessionManager;
    private RelativeLayout contentMain;
    private ImageView blurScreen;
    private DrawerLayout drawer;
    private Boolean isFabOpen = false;
    private FloatingActionButton fabPlus, fabWakeUp, fabSelf, fabDuel;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(getApplicationContext());
        adapter = new MainActivityCardsAdapter(this, SelfImprovement_model.generate(this), sessionManager);
        if (adapter.dataCount() > 0) {
            RelativeLayout cards = (RelativeLayout) findViewById(R.id.cards);
            CustomPagerBase pager = new CustomPagerBase(MainActivity.this, cards, adapter);
            pager.preparePager(0);
        }

        blurScreen = (ImageView) findViewById(R.id.blurScreen);
        contentMain = (RelativeLayout) findViewById(R.id.content_main);

        // DRAWER
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                blurScreen.setVisibility(View.INVISIBLE);
                if (isFabOpen) closeFab();
//                actionMenu.close(true);
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


        /********************************* Get photo and make bg **********************************/
        ImageView background = (ImageView) findViewById(R.id.main_background);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // call here System.gc(); ?
        // call here Runtime.getRuntime().gc(); ?

        File profile = new File(path, "profile.jpg");
        File blurred = new File(path, "blured2.jpg");
        if (blurred.exists()) {
            Glide.with(this).load(blurred).bitmapTransform(new CropSquareTransformation(this))
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(background);
            drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(this).load(blurred).bitmapTransform(new CropSquareTransformation(this))
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerBackground);
            Glide.with(this).load(profile).bitmapTransform(new CropCircleTransformation(this))
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerUserPhoto);
        }
        else {
            String pathToPic = sessionManager.getPathToPic();
            if (pathToPic == null) pathToPic = getIntent().getExtras().getString("path_to_pic");
            CHDownloadImageTask chDownloadImageTask = new CHDownloadImageTask(getApplicationContext(), MainActivity.this);
            chDownloadImageTask.execute(pathToPic);
        }

        String name = sessionManager.getUserName();
        drawerUserName.setText(name);
        Typeface typeface = android.graphics.Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
        drawerUserName.setTypeface(typeface);

        /******************************** Display 'Pending duels' menu ****************************/
        CHCheckPendingDuels checker = new CHCheckPendingDuels(this, navigationView, sessionManager);
        int count = checker.getPendingCount();
        if (count == 0) {
            checker.hideItem();
        } else {
            TextView duelCount = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            duelCount.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
        }

        /*********************************** Display fab buttons **********************************/
//        final SubActionButton.Builder itemBuilder = new SubActionButton.Builder(MainActivity.this);
//        buttonWakeUpChallenge = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.wakeupcolor)).build();
//        buttonDuelChallenge   = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.duel_yellow)).build();
//        buttonSelfImprovement = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.self_yellow)).build();

//        ImageView fabPlus = (ImageButton) findViewById(R.id.fabPlus);
//        actionMenu = new FloatingActionMenu.Builder(this) //.setStartAngle(-89).setEndAngle(-90).setRadius(220)
//                .addSubActionView(buttonWakeUpChallenge)
//                .addSubActionView(buttonDuelChallenge)
//                .addSubActionView(buttonSelfImprovement)
//                .attachTo(fabPlus).build();

//        int x = round(width / 100);
//        buttonWakeUpChallenge.getLayoutParams().height = x * 20;
//        buttonWakeUpChallenge.getLayoutParams().width  = x * 20;
//        buttonDuelChallenge  .getLayoutParams().height = x * 20;
//        buttonDuelChallenge  .getLayoutParams().width  = x * 20;
//        buttonSelfImprovement.getLayoutParams().height = x * 20;
//        buttonSelfImprovement.getLayoutParams().width  = x * 20;


        CHBuildAnim chBuildAnim = CHBuildAnim.getInstance();
        chBuildAnim.buildAnim(this, sessionManager, typeface);

        fabPlus   = (FloatingActionButton)findViewById(R.id.fabPlus);
        fabWakeUp = (FloatingActionButton)findViewById(R.id.fabWake);
        fabSelf = (FloatingActionButton)findViewById(R.id.fabSelf);
        fabDuel = (FloatingActionButton)findViewById(R.id.fabDuel);
        fab_open  = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fabPlus.setOnClickListener(v -> animateFAB());
        fabSelf.setOnClickListener(v -> startActivity(new Intent(this, SelfImprovementActivity.class)));
        fabDuel.setOnClickListener(v -> startActivity(new Intent(this, FriendsActivity.class)));
        fabWakeUp.setOnClickListener(v -> startActivity(new Intent(this, WakeUpActivity.class)));

        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
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
            switch (item.getItemId()) {
                case R.id.friends:
                    Intent goToFriends = new Intent(MainActivity.this, FriendsActivity.class);
                    startActivity(goToFriends);
                    break;
                case R.id.history:
                    Intent goToHistory = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(goToHistory);
                    break;
                case R.id.settings:
                    Intent goToSettings = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(goToSettings);
                    break;
                case R.id.pending_duels:
                    Intent goToPendingDuel = new Intent(MainActivity.this, PendingDuelActivity.class);
                    startActivity(goToPendingDuel);
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
                    if (offlineMode.isConnectedToRemoteAPI(MainActivity.this)) { sessionManager.logout(MainActivity.this); }
                    break;

            }
        finish();
//        }, 200);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // TODO: create method generate without intents and call this method and after this call 'pager.prepare(0)'
            // TODO: remove from this initialization with word "new" !
            String token = sessionManager.getToken();
            String userId = sessionManager.getUserId();
            ChallengeController cc = new ChallengeController(getApplicationContext(), this, token, userId);
            cc.refreshCardsForPendingDuel();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync_app, menu);
        return true;
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
        fabWakeUp.startAnimation(fab_close);
        fabSelf.startAnimation(fab_close);
        fabDuel.startAnimation(fab_close);
        isFabOpen = false;
    }

    private void openFab() {
        fabPlus.startAnimation(rotate_forward);
        fabWakeUp.startAnimation(fab_open);
        fabSelf.startAnimation(fab_open);
        fabDuel.startAnimation(fab_open);
        isFabOpen = true;
    }


}