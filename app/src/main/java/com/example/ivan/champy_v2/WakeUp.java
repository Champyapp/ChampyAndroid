package com.example.ivan.champy_v2;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class WakeUp extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private TextView alarmTextView;
    public static WakeUp inst;

    public static WakeUp instance() {
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);
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
        textView  = (TextView)findViewById(R.id.goal_text);
        textView.setTypeface(typeface);
        textView = (TextView)findViewById(R.id.textView23);
        textView.setTypeface(typeface);

        Glide.with(this)
                .load(R.drawable.wakeupwhite)
                .override(130, 130)
                .into((ImageView) findViewById(R.id.imageView13));

        Glide.with(this)
                .load(R.drawable.wakeuptext)
                .override(200, 170)
                .into((ImageView) findViewById(R.id.imageView12));


        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.wake_up);
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
                .load(R.drawable.points)
                .override(100, 100)
                .into((ImageView)findViewById(R.id.imageView14));

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
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        ImageButton imageButton = (ImageButton)findViewById(R.id.imageButton5);
        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                alarmTimePicker = (TimePicker)findViewById(R.id.timePicker);
                int hour = alarmTimePicker.getCurrentHour();
                int minute = alarmTimePicker.getCurrentMinute();
                String shour = ""+hour;
                String sminute = ""+minute;
                if (hour<10) shour = "0"+shour;
                if (minute<10) sminute = "0"+sminute;
                Log.i("stat", "Give up: "+shour+" "+sminute);

                boolean ok = check(shour+sminute);
                if (ok) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
                    Intent myIntent = new Intent(WakeUp.this, AlarmReceiver.class);
                    int id = Integer.parseInt(shour+sminute);
                    Log.i("stat", "Give up: "+id);
                    pendingIntent = PendingIntent.getBroadcast(WakeUp.this, id, myIntent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    Toast.makeText(WakeUp.this, "Created", Toast.LENGTH_SHORT).show();
                    ChallengeController challengeController = new ChallengeController(WakeUp.this, WakeUp.this, hour, minute);
                    challengeController.Create_new_challenge("Wake Up", 21, "567d51c48322f85870fd931c");
                } else Toast.makeText(WakeUp.this, "Already exist!", Toast.LENGTH_SHORT).show();

                return true;
            }
        });


    }


    public boolean check(String time)
    {
        boolean ok = true;

        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        final ContentValues cv = new ContentValues();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        int o = 0;
        if (c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex("name");
            Log.i("stat", "Statuskwo: o=" + o);
            do {
                o++;

                if (c.getString(nameColIndex).equals("Wake Up")){

                    if (c.getString(c.getColumnIndex("description")).equals(time)){
                        ok = false;
                        break;
                    }
                }
            } while (c.moveToNext());
        } else
            Log.i("status", "kwo0 rows");
        c.close();
        return ok;
    }

    private Drawable Init(String path) throws FileNotFoundException {
        File file = new File(path, "blured2.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

        Log.d("TAG", "x_y" + bitmap.getWidth() + " " + bitmap.getHeight());
        Drawable dr = new BitmapDrawable(getResources(), bitmap);
        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);

        return dr;

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
        getMenuInflater().inflate(R.menu.wake_up, menu);
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
        OfflineMode offlineMode = new OfflineMode();
        if (offlineMode.isInternetAvailable(this)) {
            if (id == R.id.challenges) {
                Intent intent = new Intent(WakeUp.this, MainActivity.class);
                startActivity(intent);
            }
            if (id == R.id.history){
                Intent intent = new Intent(WakeUp.this, History.class);
                startActivity(intent);
            }
            if (id == R.id.nav_logout) {

                if (offlineMode.isInternetAvailable(this)) Logout();
                else Toast.makeText(this, "Lost internet connection!", Toast.LENGTH_LONG).show();
            }
            if (id == R.id.friends) {
                Intent intent = new Intent(WakeUp.this, Friends.class);
                startActivity(intent);
            }
            if (id == R.id.settings) {
                Intent intent = new Intent(WakeUp.this, Settings.class);
                startActivity(intent);
            } else if (id == R.id.share) {
                String message = "Check out Champy - it helps you improve and compete with your friends!";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);

                startActivity(Intent.createChooser(share, "How would you like to share?"));
            }
        }
        else Toast.makeText(this, "Lost internet connection!", Toast.LENGTH_LONG).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void Logout(){
        LoginManager.getInstance().logOut();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.logoutUser();
        Intent intent = new Intent(WakeUp.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Bye Bye!!!", Toast.LENGTH_SHORT).show();
    }

}
