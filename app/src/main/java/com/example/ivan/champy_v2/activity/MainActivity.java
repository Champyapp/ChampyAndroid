package com.example.ivan.champy_v2.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.adapter.MainActivityCardsAdapter;
import com.example.ivan.champy_v2.controller.ChallengeController;
import com.example.ivan.champy_v2.helper.CHBuildAnim;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHDownloadImageTask;
import com.example.ivan.champy_v2.helper.CHLoadBlurredPhoto;
import com.example.ivan.champy_v2.helper.CHMakeResponsiveScore;
import com.example.ivan.champy_v2.model.SelfImprovement_model;
import com.example.ivan.champy_v2.utils.Blur;
import com.example.ivan.champy_v2.utils.CustomPagerBase;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.facebook.FacebookSdk;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;
import java.io.FileNotFoundException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.ivan.champy_v2.utils.Constants.path;
import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static long mLastClickTime = 0;
    private SubActionButton buttonWakeUpChallenge, buttonDuelChallenge, buttonSelfImprovement;
    private MainActivityCardsAdapter adapter;
    private FloatingActionMenu actionMenu;
    private SessionManager sessionManager;
    private RelativeLayout contentMain;
    private ImageView blurScreen;
    private DrawerLayout drawer;

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


        // DRAWER
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //cards.setVisibility(View.VISIBLE);
                blurScreen.setVisibility(View.INVISIBLE);
                actionMenu.close(true);
            }
        };
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();


        ImageView background = (ImageView) findViewById(R.id.main_background);

        // NAVIGATION VIEW
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        ImageView drawerUserPhoto = (ImageView) headerLayout.findViewById(R.id.profile_image);
        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);

        String name = sessionManager.getUserName();
        drawerUserName.setText(name);
        Typeface typeface = android.graphics.Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
        drawerUserName.setTypeface(typeface);

        /********************************* Get photo and make bg **********************************/

        File blurred = new File(path, "blured2.jpg");
        if (blurred.exists()) {
            try {
                background.setScaleType(ImageView.ScaleType.CENTER_CROP);
                background.setImageDrawable(CHLoadBlurredPhoto.Init(path));
                drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                drawerBackground.setImageDrawable(CHLoadBlurredPhoto.Init(path));
            } catch (FileNotFoundException e) {e.printStackTrace();}
        }
        else {
            String pathToPic = sessionManager.getPathToPic();
            if (pathToPic == null) pathToPic = getIntent().getExtras().getString("path_to_pic");
            CHDownloadImageTask chDownloadImageTask = new CHDownloadImageTask(getApplicationContext(), MainActivity.this);
            chDownloadImageTask.execute(pathToPic);
        }


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
        final SubActionButton.Builder itemBuilder = new SubActionButton.Builder(MainActivity.this);
        buttonWakeUpChallenge = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.wakeupcolor)).build();
        buttonDuelChallenge   = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.duel_yellow)).build();
        buttonSelfImprovement = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.self_yellow)).build();

        ImageView fabPlus = (ImageButton) findViewById(R.id.fabPlus);
        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(buttonWakeUpChallenge)
                .addSubActionView(buttonDuelChallenge)
                .addSubActionView(buttonSelfImprovement)
                .setRadius(350).attachTo(fabPlus).build();

        int x = round(width / 100);
        buttonWakeUpChallenge.getLayoutParams().height = x * 20;
        buttonWakeUpChallenge.getLayoutParams().width  = x * 20;
        buttonDuelChallenge  .getLayoutParams().height = x * 20;
        buttonDuelChallenge  .getLayoutParams().width  = x * 20;
        buttonSelfImprovement.getLayoutParams().height = x * 20;
        buttonSelfImprovement.getLayoutParams().width  = x * 20;


        CHBuildAnim chBuildAnim = new CHBuildAnim(this, sessionManager, typeface);
        chBuildAnim.buildAnim();

        File profile = new File(path, "profile.jpg");
        Uri uri = Uri.fromFile(profile);
        Glide.with(this).load(uri).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerUserPhoto);

        fabPlus.setOnClickListener(MainActivity.this);
        contentMain = (RelativeLayout) findViewById(R.id.content_main);
        blurScreen = (ImageView) findViewById(R.id.blurScreen);

        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
        mLastClickTime = SystemClock.elapsedRealtime();
        if (adapter.dataCount() >= 5) {
            Toast.makeText(this, "You have too much challenges", Toast.LENGTH_SHORT).show();
            return;
        }

        actionMenu.toggle(true);
        if (actionMenu.isOpen()) {
            /************************************** Blur *****************************************/
            contentMain.buildDrawingCache();
            Bitmap bm = contentMain.getDrawingCache();
            Bitmap blurred = Blur.blurRenderScript(getApplicationContext(), bm, 25);
            Drawable ob = new BitmapDrawable(getResources(), blurred);
            blurScreen.setImageDrawable(ob);
            blurScreen.setVisibility(View.VISIBLE);
            contentMain.destroyDrawingCache();
            /*********************************** Sub buttons **************************************/
            buttonSelfImprovement.setOnClickListener(v0 -> {
                Intent intent = new Intent(MainActivity.this, SelfImprovementActivity.class);
                startActivity(intent);
            });
            buttonDuelChallenge.setOnClickListener(v1 -> {
                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                startActivity(intent);
            });
            buttonWakeUpChallenge.setOnClickListener(v2 -> {
                Intent intent = new Intent(MainActivity.this, WakeUpActivity.class);
                startActivity(intent);
            });
        } else {
            blurScreen.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        actionMenu.close(true);
        //cards.setVisibility(View.VISIBLE);
        blurScreen.setVisibility(View.INVISIBLE);

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