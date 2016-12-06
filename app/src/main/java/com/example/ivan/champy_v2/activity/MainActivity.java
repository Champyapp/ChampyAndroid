package com.example.ivan.champy_v2.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.adapter.MainActivityCardsAdapter;
import com.example.ivan.champy_v2.controller.ChallengeController;
import com.example.ivan.champy_v2.helper.CHBuildAnim;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHDownloadImageTask;
import com.example.ivan.champy_v2.helper.CHMakeResponsiveScore;
import com.example.ivan.champy_v2.model.SelfImprovement_model;
import com.example.ivan.champy_v2.utils.CustomPagerBase;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.facebook.FacebookSdk;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

import static com.example.ivan.champy_v2.utils.Constants.path;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private long mLastClickTime = 0;
    private MainActivityCardsAdapter adapter;
    private SessionManager sessionManager;
    private RelativeLayout contentMain;
    private ImageView blurScreen;
    private DrawerLayout drawer;
    private Boolean isFabOpen = false;
    private FloatingActionButton fabPlus, fabWakeUp, fabDuel, fabSelf;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int width = getWindowManager().getDefaultDisplay().getWidth();
        CHMakeResponsiveScore chMakeResponsiveScore = new CHMakeResponsiveScore();
        chMakeResponsiveScore.makeResponsiveScore(this, width);

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
        CHCheckPendingDuels checker = new CHCheckPendingDuels(this, navigationView);
        int count = checker.getPendingCount();
        if (count == 0) {
            checker.hideItem();
        } else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            view.setText("+" + (count > 0 ? String.valueOf(count) : null));
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


        CHBuildAnim chBuildAnim = new CHBuildAnim(this, sessionManager, typeface);
        chBuildAnim.buildAnim();

        fabPlus   = (FloatingActionButton)findViewById(R.id.fabPlus);
        fabWakeUp = (FloatingActionButton)findViewById(R.id.fabWakeUp);
        fabDuel   = (FloatingActionButton)findViewById(R.id.fabDuel);
        fabSelf   = (FloatingActionButton)findViewById(R.id.fabSelf);
        fab_open  = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fabPlus.setOnClickListener(v -> animateFAB());

        fabSelf.setOnClickListener(v -> new Intent(this, SelfImprovementActivity.class));
        fabDuel.setOnClickListener(v -> new Intent(this, FriendsActivity.class));
        fabWakeUp.setOnClickListener(v -> new Intent(this, WakeUpActivity.class));


        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ViewServer.get(this).removeWindow(this);
    }

//    @Override
//    public void onClick(View v) {
//        Log.d(TAG, "onClick: click on fabPlus");
////        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
////        mLastClickTime = SystemClock.elapsedRealtime();
////
////        if (adapter.dataCount() >= 5) {
////            Toast.makeText(this, "You have too much challenges", Toast.LENGTH_SHORT).show();
////            return;
////        }
//        animateFAB();
//
////        actionMenu.toggle(true);
////        if (actionMenu.isOpen()) {
////            /************************************** Blur *****************************************/
////
////            // idea: set "blurred2.jpg" upper all view except button 'fab';
//////            contentMain.buildDrawingCache();
//////            Bitmap bm = contentMain.getDrawingCache();
//////            Bitmap blurred = Blur.blurRenderScript(getApplicationContext(), bm, 25);
//////            Drawable ob = new BitmapDrawable(getResources(), blurred);
//////            blurScreen.setImageDrawable(ob);
//////            blurScreen.setVisibility(View.VISIBLE);
//////            contentMain.destroyDrawingCache();
////
////            /*********************************** Sub buttons **************************************/
////            buttonSelfImprovement.setOnClickListener(v0 -> startActivity(new Intent(this, SelfImprovementActivity.class)));
////            buttonDuelChallenge  .setOnClickListener(v1 -> startActivity(new Intent(this, FriendsActivity.class)));
////            buttonWakeUpChallenge.setOnClickListener(v2 -> startActivity(new Intent(this, WakeUpActivity.class)));
////        } else {
////            blurScreen.setVisibility(View.INVISIBLE);
////        }
//
//    }

    private void animateFAB() {

        if(isFabOpen) {
            fabPlus.startAnimation(rotate_backward);

            fabWakeUp.startAnimation(fab_close);
            fabDuel.startAnimation(fab_close);
            fabSelf.startAnimation(fab_close);

            fabWakeUp.setClickable(false);
            fabDuel.setClickable(false);
            fabSelf.setClickable(false);

            isFabOpen = false;
        } else {
            fabPlus.startAnimation(rotate_forward);

            fabWakeUp.startAnimation(fab_open);
            fabDuel.startAnimation(fab_open);
            fabSelf.startAnimation(fab_open);

            fabWakeUp.setClickable(true);
            fabDuel.setClickable(true);
            fabSelf.setClickable(true);

            isFabOpen = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
//        actionMenu.close(true);
        //cards.setVisibility(View.VISIBLE);
//        blurScreen.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.friends:
                Intent goToFriends = new Intent(this, FriendsActivity.class);
                startActivity(goToFriends);
                break;
            case R.id.history:
                Intent goToHistory = new Intent(this, HistoryActivity.class);
                startActivity(goToHistory);
                break;
            case R.id.settings:
                Intent goToSettings = new Intent(this, SettingsActivity.class);
                startActivity(goToSettings);
                break;
            case R.id.pending_duels:
                Intent goToPendingDuel = new Intent(this, PendingDuelActivity.class);
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
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isConnectedToRemoteAPI(this)) { sessionManager.logout(this); }
                break;
        }
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


}