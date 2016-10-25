package com.example.ivan.champy_v2.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.Blur;
import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.CustomPagerBase;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.adapter.MainActivityCardsAdapter;
import com.example.ivan.champy_v2.helper.CHBuildAnim;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHDownloadImageTask;
import com.example.ivan.champy_v2.helper.CHLoadBlurredPhoto;
import com.example.ivan.champy_v2.helper.CHSocket;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.model.SelfImprovement_model;
import com.facebook.FacebookSdk;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;
import java.io.FileNotFoundException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static final String TAG = "MainActivity";
    private long mLastClickTime = 0;
    public Activity activity;
    private SubActionButton buttonWakeUpChallenge, buttonDuelChallenge, buttonSelfImprovement;
    private MainActivityCardsAdapter adapter;
    private FloatingActionMenu actionMenu;
    private View headerLayout;
    private CHSocket sockets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_gradient));
        setSupportActionBar(toolbar);

        sockets = new CHSocket(MainActivity.this, getApplicationContext());
        sockets.tryToConnect();
        sockets.connectAndEmmit();

        RelativeLayout cards = (RelativeLayout)findViewById(R.id.cards);
        adapter = new MainActivityCardsAdapter(this, SelfImprovement_model.generate(this));
        if (adapter.dataCount() > 0) {
            CustomPagerBase pager = new CustomPagerBase(this, cards, adapter);
            pager.preparePager(0);
        }

        final ImageButton actionButton = (ImageButton)findViewById(R.id.fabPlus);
        final SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        buttonWakeUpChallenge = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.wakeupcolor)).build();
        buttonDuelChallenge   = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.duel_yellow)).build();
        buttonSelfImprovement = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.self_yellow)).build();

        int width = getWindowManager().getDefaultDisplay().getWidth();
        int x = round(width/100);

        buttonWakeUpChallenge.getLayoutParams().height = x*20;
        buttonWakeUpChallenge.getLayoutParams().width  = x*20;
        buttonDuelChallenge  .getLayoutParams().height = x*20;
        buttonDuelChallenge  .getLayoutParams().width  = x*20;
        buttonSelfImprovement.getLayoutParams().height = x*20;
        buttonSelfImprovement.getLayoutParams().width  = x*20;

        actionMenu = new FloatingActionMenu.Builder(this).addSubActionView(buttonWakeUpChallenge)
                .addSubActionView(buttonDuelChallenge).addSubActionView(buttonSelfImprovement)
                .setRadius(350).attachTo(actionButton).build();

        actionButton.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                ImageView screen = (ImageView) findViewById(R.id.blurScreen);
                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.cards);
                relativeLayout.setVisibility(View.VISIBLE);
                screen.setVisibility(View.INVISIBLE);
                actionMenu.close(true);
            }
        };
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
        int count = checker.getPendingCount();
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
        view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        if (count == 0) checker.hideItem();


        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        CurrentUserHelper user = new CurrentUserHelper(getApplicationContext());
        String pathToPic = user.getPathToPic();
        String name = user.getName();

        if (pathToPic == null) pathToPic = getIntent().getExtras().getString("path_to_pic");

        ImageView drawerBackground = (ImageView)headerLayout.findViewById(R.id.slide_background);
        ImageView drawerUserPhoto = (ImageView)headerLayout.findViewById(R.id.profile_image);
        ImageView background = (ImageView)findViewById(R.id.main_background);
        TextView  drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        drawerUserName.setText(name);

        @SuppressLint("SdCardPath")
        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "blured2.jpg");
        if (file.exists())
            try {
                background.setScaleType(ImageView.ScaleType.CENTER_CROP);
                background.setImageDrawable(CHLoadBlurredPhoto.Init(path));
                drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                drawerBackground.setImageDrawable(CHLoadBlurredPhoto.Init(path));
            } catch (FileNotFoundException e) { e.printStackTrace(); }
        else {
            CHDownloadImageTask chDownloadImageTask = new CHDownloadImageTask(getApplicationContext(), this);
            chDownloadImageTask.execute(pathToPic);
        }

        file = new File(path, "profile.jpg");
        Uri uri = Uri.fromFile(file);
        Glide.with(this).load(uri).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerUserPhoto);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CHBuildAnim chBuildAnim = new CHBuildAnim();
                chBuildAnim.buildAnim(MainActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
        mLastClickTime = SystemClock.elapsedRealtime();

        //Here we make our background is blurred
        ImageView blurScreen;
        RelativeLayout contentMain = (RelativeLayout) findViewById(R.id.content_main);
        contentMain.destroyDrawingCache();
        contentMain.buildDrawingCache();
        Bitmap bm = contentMain.getDrawingCache();
        Bitmap blured = Blur.blurRenderScript(getApplicationContext(), bm, 25);
        blurScreen = (ImageView) findViewById(R.id.blurScreen);
        Drawable ob = new BitmapDrawable(getResources(), blured);
        blurScreen.setImageDrawable(ob);
        RelativeLayout cardsLayout = (RelativeLayout) findViewById(R.id.cards);

        // first we check action menu and if "is open" then we setup our inside click for FAB
        actionMenu.toggle(true);
        if (!actionMenu.isOpen()) {
            blurScreen.setVisibility(View.INVISIBLE);
            cardsLayout.setVisibility(View.VISIBLE);
        } else {
            if (adapter.dataCount() <= 10) {
                blurScreen.setVisibility(View.VISIBLE);
                cardsLayout.setVisibility(View.INVISIBLE);
                buttonSelfImprovement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, SelfImprovementActivity.class);
                        startActivity(intent);
                    }
                });
                buttonDuelChallenge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                        startActivity(intent);
                    }
                });
                buttonWakeUpChallenge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, WakeUpActivity.class);
                        startActivity(intent);

                    }
                });
            } else {
                actionMenu.toggle(false);
                Toast.makeText(MainActivity.this, "You have so much challenges", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        actionMenu.close(true);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.cards);
        relativeLayout.setVisibility(View.VISIBLE);
        ImageView screen = (ImageView) findViewById(R.id.blurScreen);
        screen.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        sockets.disconnectSockets();
    }

    @SuppressWarnings("StatementWithEmptyBody")
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
                String message = "Check out Champy - it helps you improve and compete with your friends!";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "How would you like to share?"));
                break;
            case R.id.nav_logout:
                OfflineMode offlineMode = new OfflineMode();
                SessionManager sessionManager = new SessionManager(this);
                if (offlineMode.isConnectedToRemoteAPI(this)) {
                    sessionManager.logout(this);
                }
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            ChallengeController cc = new ChallengeController(getApplicationContext(), MainActivity.this, 0, 0, 0);
            CurrentUserHelper user = new CurrentUserHelper(getApplicationContext());
            String token = user.getToken();
            String userId = user.getUserObjectId();
            cc.refreshCardsForPendingDuel(token, userId);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.duel, menu);
        return true;
    }


}