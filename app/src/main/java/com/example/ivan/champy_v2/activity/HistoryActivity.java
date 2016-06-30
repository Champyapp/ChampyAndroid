package com.example.ivan.champy_v2.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.adapter.HistoryPagerAdapter;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
// history wins?
public class HistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionMenu actionMenu;
    private final String TAG = "myLogs";
    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        int count = 0;

        String s = sessionManager.get_duel_pending();
        if (s != null){
            count = Integer.parseInt(s);
        }

        TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
        if (count == 0) {
            hideItem();
        }
        else {
            view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        }


        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_history);
        adapterViewPager = new HistoryPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(adapterViewPager);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs_history);
        tabLayout.setupWithViewPager(viewPager);


        String name = user.get("name");

        ImageView profile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        TextView textView = (TextView) headerLayout.findViewById(R.id.tvUserName);
        textView.setText(name);
        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);
        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profile);

        try {
            Drawable dr = Init("/data/data/com.example.ivan.champy_v2/app_imageDir/");
            ImageView imageView = (ImageView) headerLayout.findViewById(R.id.slide_background);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageDrawable(dr);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ViewServer.get(this).addWindow(this);
    }

    private void hideItem() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.pending_duels).setVisible(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        OfflineMode offlineMode = new OfflineMode();
        if (offlineMode.isConnectedToRemoteAPI(this)) {
            switch (item.getItemId()) {
                case R.id.challenges:
                    Intent goToChallenges = new Intent(this, MainActivity.class);
                    startActivity(goToChallenges);
                    break;
                case R.id.friends:
                    Intent goToFriends = new Intent(this, FriendsActivity.class);
                    startActivity(goToFriends);
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
                    offlineMode = new OfflineMode();
                    SessionManager sessionManager = new SessionManager(this);
                    if (offlineMode.isConnectedToRemoteAPI(this)) {
                        sessionManager.logout(this);
                    }
                    break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public Drawable Init(String path) throws FileNotFoundException {
        File file = new File(path, "blured2.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

        Log.d("TAG", "x_y" + bitmap.getWidth() + " " + bitmap.getHeight());
        Drawable dr = new BitmapDrawable(this.getResources(), bitmap);
        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);

        ImageView imageView = (ImageView) findViewById(R.id.history_background);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageDrawable(dr);

        return dr;

    }

}
