package com.example.ivan.champy_v2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.model.Self.*;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SelfImprovement extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_self_improvement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);
   /* TextView textView = (TextView)findViewById(R.id.textView19);*/
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        //textView.setTypeface(typeface);
        TextView textView = (TextView)findViewById(R.id.textView20);
        textView.setTypeface(typeface);

        Glide.with(this)
                .load(R.drawable.selfimprovementicon)
                .override(130, 130)
                .into((ImageView) findViewById(R.id.imageView13));

        Glide.with(this)
                .load(R.drawable.selfimprtext)
                .override(200, 170)
                .into((ImageView) findViewById(R.id.imageView12));


        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.selfimprovement);
        relativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.selfimprovementback));

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);
        String name = user.get("name");

        ImageView profile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        textView = (TextView) headerLayout.findViewById(R.id.textView);
        textView.setText(name);

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
            imageView.setImageDrawable(dr); final String API_URL = "http://46.101.213.24:3007";


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        getChallenges();

    }

    private Drawable Init(String path) throws FileNotFoundException {
        File file = new File(path, "blured2.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

        Log.d("TAG", "x_y" + bitmap.getWidth() + " " + bitmap.getHeight());
        Drawable dr = new BitmapDrawable(getResources(), bitmap);
        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);

        return dr;

    }

    public void getChallenges()
    {
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("selfimprovement", null, null);
        final ContentValues cv = new ContentValues();

        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        final SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        com.example.ivan.champy_v2.interfaces.SelfImprovement selfImprovement = retrofit.create(com.example.ivan.champy_v2.interfaces.SelfImprovement.class);
        Call<com.example.ivan.champy_v2.model.Self.SelfImprovement> call = selfImprovement.getChallenges(token);
        Log.i("stat", "Status: RUN");
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.Self.SelfImprovement>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.Self.SelfImprovement> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("stat", "Status: OK");
                    List<Datum> data = response.body().getData();
                    int data_size = 0;
                    for (int i = 0; i < data.size(); i++) {
                        com.example.ivan.champy_v2.model.Self.Datum datum = data.get(i);
                        Log.i("stat", "Status: " + datum.getType().getName());
                        if (datum.getType().getName().equals("self improvement")) {
                            if (check(datum.get_id())) {
                                cv.put("name", datum.getName());
                                Log.i("stat", "Status: " + datum.getName());
                                cv.put("description", datum.getDescription());
                                cv.put("duration", datum.getDuration());
                                cv.put("challenge_id", datum.get_id());
                                db.insert("selfimprovement", null, cv);
                                data_size++;
                            }
                            else {
                                cv.put("name", "active");
                                Log.i("stat", "Status: " + "active");
                                cv.put("description", datum.getDescription());
                                cv.put("duration", datum.getDuration());
                                cv.put("challenge_id", datum.get_id());
                                db.insert("selfimprovement", null, cv);
                                data_size++;
                            }
                        }
                    }
                    sessionManager.setSelfSize(data_size);
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    int size = sessionManager.getSelfSize();
                    PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
                    final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
                    pagerAdapter.setCount(size);
                    viewPager.setAdapter(pagerAdapter);
                    viewPager.setOffscreenPageLimit(1);
                    viewPager.setPageMargin(20);
                    viewPager.setClipToPadding(false);
                    viewPager.setPadding(90, 0, 90, 0);

                } else Log.i("stat", "Status: WRONG");
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
            if (offlineMode.isInternetAvailable(this)) Logout();
            else Toast.makeText(this, "Lost internet connection!", Toast.LENGTH_LONG).show();
        }
        if (id == R.id.friends) {
            Intent intent = new Intent(SelfImprovement.this, Friends.class);
            startActivity(intent);
        }
        if (id == R.id.history){
            Intent intent = new Intent(SelfImprovement.this, SelfImprovement.class);
            startActivity(intent);
        }
        if (id == R.id.settings){
            Intent intent = new Intent(SelfImprovement.this, Settings.class);
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

    public void Logout(){
        LoginManager.getInstance().logOut();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.logoutUser();
        Intent intent = new Intent(SelfImprovement.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Bye Bye!!!", Toast.LENGTH_SHORT).show();
    }
    public boolean check(String id)
    {
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        final ContentValues cv = new ContentValues();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        int o = 0;
        boolean ok = true;
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int coldescription = c.getColumnIndex("description");
            int colduration = c.getColumnIndex("duration");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            do {
              String checked = c.getString(colchallenge_id);
                if (checked.equals(id)) {
                    Log.i("stat", "Cheked");
                    ok = false;
                    break;
                }
            } while (c.moveToNext());
        } else
            Log.i("stat", "kwo0 rows");
        c.close();
        return ok;
    }

}
