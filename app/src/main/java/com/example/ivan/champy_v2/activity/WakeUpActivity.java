package com.example.ivan.champy_v2.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import com.example.ivan.champy_v2.AlarmReceiver;
import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static java.lang.Math.round;

public class WakeUpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private TextView alarmTextView;
    public static WakeUpActivity inst;

    public static WakeUpActivity instance() {
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        OfflineMode offlineMode = new OfflineMode();
        if (!offlineMode.isConnectedToRemoteAPI(this)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
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
        int count = check_pending();
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
        view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        if (count == 0) hideItem();

        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        TextView tvIChallengeMyselfTo = (TextView)findViewById(R.id.textView20);
        tvIChallengeMyselfTo.setTypeface(typeface);
        TextView tvGoal = (TextView)findViewById(R.id.goal_text);
        tvGoal.setTypeface(typeface);
        TextView tvDuration = (TextView)findViewById(R.id.textView23);
        tvDuration.setTypeface(typeface);

        Glide.with(this).load(R.drawable.wakeupwhite).override(110, 110).into((ImageView) findViewById(R.id.imageViewLogo));
        Glide.with(this).load(R.drawable.wakeuptext).override(180, 150).into((ImageView) findViewById(R.id.imageView12));

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.wake_up);
        relativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.selfimprovementback));

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);
        String name = user.get("name");

        ImageView profile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        tvDuration = (TextView) headerLayout.findViewById(R.id.tvUserName);
        tvDuration.setText(name);

        Glide.with(this).load(R.drawable.points).override(100, 100).into((ImageView)findViewById(R.id.imageViewAcceptButton));
        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(profile);

        try {
            Drawable dr = Init("/data/data/com.example.ivan.champy_v2/app_imageDir/");
            ImageView imageView = (ImageView) headerLayout.findViewById(R.id.slide_background);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageDrawable(dr);
            final String API_URL = "http://46.101.213.24:3007";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        ImageButton imageButton = (ImageButton)findViewById(R.id.imageButtonAcceptSelfImprovement);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                alarmTimePicker = (TimePicker)findViewById(R.id.timePicker);

                                int hour = alarmTimePicker.getCurrentHour();
                                int minute = alarmTimePicker.getCurrentMinute();

                                String sHour = "" + hour;
                                String sMinute = "" + minute;

                                if (hour < 10) {
                                    sHour = "0" + sHour;
                                }
                                if (minute < 10) {
                                    sMinute = "0" + sMinute;
                                }
                                //Log.i("stat", "Give up: " + sHour + " " + sMinute);

                                boolean ok = check(sHour + sMinute);
                                OfflineMode offlineMode = new OfflineMode();
                                if (offlineMode.isConnectedToRemoteAPI(WakeUpActivity.this)) {
                                    if (ok) {
                                        Calendar calendar = Calendar.getInstance();
                                        Date date = new Date();
                                        date.setHours(alarmTimePicker.getCurrentHour());
                                        date.setMinutes(alarmTimePicker.getCurrentMinute());
                                        calendar.set(Calendar.SECOND, 0);
                                        calendar.setTime(date);

                                        long current = Calendar.getInstance().getTimeInMillis();
                                        calendar.set(Calendar.SECOND, 0);
                                        long time = calendar.getTimeInMillis();

                                        time = time - (time % 60000);
                                        if (current > time) {
                                            calendar.add(Calendar.DATE, 1);
                                        }
                                        time = calendar.getTimeInMillis();

                                        //Log.i("stat", "Time: " + (Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis()));

                                        Intent myIntent = new Intent(WakeUpActivity.this, AlarmReceiver.class);
                                        int id = Integer.parseInt(sHour + sMinute);
                                        pendingIntent = PendingIntent.getBroadcast(WakeUpActivity.this, id, myIntent, 0);
                                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 24 * 60 * 60 * 1000, pendingIntent); // 24*60*60*1000 = 1 day;

                                        Toast.makeText(WakeUpActivity.this, "Challenge created", Toast.LENGTH_SHORT).show();
                                        ChallengeController challengeController = new ChallengeController(WakeUpActivity.this, WakeUpActivity.this, hour, minute);
                                        challengeController.Create_new_challenge("Wake Up", 21, "567d51c48322f85870fd931c");

                                    } else {
                                        Toast.makeText(WakeUpActivity.this, "Already exist!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WakeUpActivity.this);
                builder.setTitle("Are you sure")
                        .setMessage("You wanna create this challenge?")
                        .setIcon(R.drawable.challengecceptedmeme)
                        .setCancelable(false)
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No",  dialogClickListener).show();
            }
        });



    }


    public boolean check(String time) {
        boolean ok = true;
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        int o = 0;
        if (c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex("name");
            int status = c.getColumnIndex("status");
            Log.i("stat", "Statuskwo: o=" + o);
            do {
                o++;
                if (c.getString(nameColIndex).equals("Wake Up")){
                    if (c.getString(status).equals("started")) {
                        Log.i("stat", "Time : " + c.getString(c.getColumnIndex("description")) + " " + time);
                        if (c.getString(c.getColumnIndex("description")).equals(time)) {
                            ok = false;
                            break;
                        }
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        return ok;
    }


    private Drawable Init(String path) throws FileNotFoundException {
        File file = new File(path, "blured2.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

        //Log.d("TAG", "x_y" + bitmap.getWidth() + " " + bitmap.getHeight());
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
            Intent intent = new Intent(WakeUpActivity.this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
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


    public int check_pending() {
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        Cursor c = db.query("pending_duel", null, null, null, null, null, null);
        int o = 0;
        if (c.moveToFirst()) {
            do {
                o++;
            } while (c.moveToNext());
        } else {
            Log.i("stat", "kwo0 rows");
        }
        c.close();
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.set_duel_pending("" + o);
        Log.d("TAG", "O: " + o);
        return o;
    }


    private void hideItem() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.pending_duels).setVisible(false);
    }

}
