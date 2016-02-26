package com.example.ivan.champy_v2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static java.lang.Math.round;

public class Friends extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentPagerAdapter adapterViewPager;
    private final String TAG = "myLogs";

    private FloatingActionMenu actionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final com.melnykov.fab.FloatingActionButton actionButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.imageButton);
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);


        SubActionButton button1 = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.wakeupcolor)).build();
        SubActionButton button2 = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.duelcolor)).build();
        SubActionButton button3 = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.selfimprovementcolor)).build();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int x = round(width/100);
        button1.getLayoutParams().height = x*20;
        button1.getLayoutParams().width = x*20;
        button2.getLayoutParams().height = x*20;
        button2.getLayoutParams().width = x*20;
        button3.getLayoutParams().height = x*20;
        button3.getLayoutParams().width = x*20;

        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .setRadius(350)
                .attachTo(actionButton)
                .build();

        FloatingActionButton.OnClickListener onClickListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "clicked");
                ImageView screen = (ImageView) findViewById(R.id.blured);
                if (screen.getDrawable() == null) {
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.friends_view);
                    relativeLayout.setDrawingCacheEnabled(true);
                    relativeLayout.buildDrawingCache();
                    Bitmap bm = relativeLayout.getDrawingCache();


                    Blur blur = new Blur();
                    if (bm == null) Log.d(TAG, "SUKAAAAA");
                    else {

                        Bitmap blured = Blur.blurRenderScript(getApplicationContext(), bm, 25);

                        screen = (ImageView) findViewById(R.id.blured);

                        Drawable ob = new BitmapDrawable(getResources(), blured);
                        screen.setImageDrawable(ob);
                    }
                }
                else Log.d(TAG, "Vse zaebok");

                //      screen.bringToFront();
                Log.d("TAG", "menu " + actionMenu.isOpen());
                actionMenu.toggle(true);
                ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
                if (!actionMenu.isOpen()) {
                    screen.setVisibility(View.INVISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                }
                else {
                    screen.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.INVISIBLE);
                }
            }
        };
        actionButton.setOnClickListener(onClickListener2);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                actionMenu.close(true);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new SampleFragmentPagerAdapter(getSupportFragmentManager(), Friends.this);
        viewPager.setAdapter(adapterViewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position>0) {
                    com.melnykov.fab.FloatingActionButton floatingActionButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.imageButton);
                    floatingActionButton.setVisibility(View.INVISIBLE);
                }
                else {
                    com.melnykov.fab.FloatingActionButton floatingActionButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.imageButton);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });





        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String url = user.get("path_to_pic");
        String name = user.get("name");

        ImageView profile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        TextView textView = (TextView) headerLayout.findViewById(R.id.textView);
        textView.setText(name);

        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (actionMenu.isOpen()) {
            actionMenu.close(true);
            ImageView screen = (ImageView) findViewById(R.id.blured);
            screen.setVisibility(View.INVISIBLE);
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setVisibility(View.VISIBLE);
        }
        else {

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_logout) {
            OfflineMode offlineMode = new OfflineMode();
            if (offlineMode.isInternetAvailable(this)) {
                LoginManager.getInstance().logOut();
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.logoutUser();
                Intent intent = new Intent(Friends.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Bye Bye!!!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "Lost internet connection!", Toast.LENGTH_LONG).show();
        }
        if (id == R.id.challenges) {
            Intent intent = new Intent(Friends.this, MainActivity.class);
            startActivity(intent);
        }
        if (id == R.id.settings){
            Intent intent = new Intent(Friends.this, Settings.class);
            startActivity(intent);
        }
        else if (id == R.id.share) {
            String message = "Check out Champy - it helps you improve and compete with your friends!";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, message);

            startActivity(Intent.createChooser(share, "How would you like to share?"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private Drawable Init(String path) throws FileNotFoundException {
        File file = new File(path, "blured2.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

        Log.d("TAG", "x_y" + bitmap.getWidth() + " " + bitmap.getHeight());
        Drawable dr = new BitmapDrawable(getResources(), bitmap);
        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);


        ImageView imageView = (ImageView) findViewById(R.id.friends_background);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageDrawable(dr);

        return dr;

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }


}
