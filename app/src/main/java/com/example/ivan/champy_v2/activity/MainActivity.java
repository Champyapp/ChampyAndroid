package com.example.ivan.champy_v2.activity;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.AlarmSchedule;
import com.example.ivan.champy_v2.Blur;
import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.adapter.CustomPagerAdapter;
import com.example.ivan.champy_v2.CustomPagerBase;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.duel.Duel;
import com.example.ivan.champy_v2.helper.CHMakeScoreWithAnim;
import com.example.ivan.champy_v2.helper.CHUploadPhoto;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.SelfImprovement_model;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.Update_user;
import com.example.ivan.champy_v2.model.User.Data;
import com.example.ivan.champy_v2.model.User.User;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;
import com.example.ivan.champy_v2.model.active_in_progress.Datum;
import com.example.ivan.champy_v2.model.active_in_progress.Recipient;
import com.example.ivan.champy_v2.model.active_in_progress.Sender;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static java.lang.Math.max;
import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final String TAG = "myLogs";
    private long mLastClickTime = 0;
    private int counter = 0;
    private int total = 30;
    private PendingIntent pendingIntent;
    private FloatingActionMenu actionMenu;
    private Context _context;
    private Activity activity;
    private CustomPagerBase pager;
    AlarmManager alarmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(this);
        FacebookSdk.sdkInitialize(getApplicationContext());

        // get_right_token();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_gradient));
        setSupportActionBar(toolbar);

        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmSchedule.class);
        intent.putExtra("alarm", "reset");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);  // було 18 ?
        calendar.set(Calendar.MINUTE, 0);        //  було 6 ?
        calendar.set(Calendar.SECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        RelativeLayout cards = (RelativeLayout)findViewById(R.id.cards);
        CustomAdapter adapter = new CustomAdapter(this, SelfImprovement_model.generate(this));
        if (adapter.dataCount() > 0){
            pager = new CustomPagerBase(this,  cards, adapter);
            pager.preparePager(0);
        }

        final ImageButton actionButton = (ImageButton)findViewById(R.id.fabPlus);
        final SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        final SubActionButton buttonWakeUpChallenge = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.wakeupcolor)).build();
        final SubActionButton buttonDuelChallenge   = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.duelcolor)).build();
        final SubActionButton buttonSelfImprovement = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.selfimprovementcolor)).build();

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

        // клик фаба
        FloatingActionButton.OnClickListener onClickFab = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
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

                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isConnectedToRemoteAPI(MainActivity.this)) {
                    actionMenu.toggle(true);
                    if (!actionMenu.isOpen()) {
                        blurScreen.setVisibility(View.INVISIBLE);
                        cardsLayout.setVisibility(View.VISIBLE);
                    } else {
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
                        blurScreen.setVisibility(View.VISIBLE);
                        cardsLayout.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };

        // клик по меню фаба
        actionButton.setOnClickListener(onClickFab);

        ImageView blurScreenClick = (ImageView)findViewById(R.id.blurScreen);
        blurScreenClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.cards);
                ImageView screen = (ImageView) findViewById(R.id.blurScreen);
                if (actionMenu.isOpen()) {
                    actionMenu.getSubActionItems();
                    OfflineMode offlineMode = new OfflineMode();
                    if (offlineMode.isConnectedToRemoteAPI(MainActivity.this)) {
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
                    }
                    actionMenu.close(true);
                    if (!actionMenu.isOpen()) {
                        screen.setVisibility(View.INVISIBLE);
                        relativeLayout.setVisibility(View.VISIBLE);
                        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.plus));
                    }
                } else {
                    actionMenu.close(false);
                    if (actionMenu.isOpen()) {
                        screen.setVisibility(View.VISIBLE);
                        relativeLayout.setVisibility(View.INVISIBLE);
                        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.plus));
                    }
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
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
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        int count = checkPending();
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
        view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        if (count == 0) hideItem();

        //SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String url = user.get("path_to_pic");
        Log.d(TAG, "Url :" + url);
        String name = user.get("name");

        if (url == null) {
            Log.d(TAG, "intent");
            intent = getIntent();
            url = intent.getExtras().getString("path_to_pic");
            name = intent.getExtras().getString("name");
        }
        Log.d(TAG, "Image: "+url);

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.slider);
        ImageView profile_image       = (ImageView)headerLayout.findViewById(R.id.profile_image);
        TextView tvUserName           = (TextView)headerLayout.findViewById(R.id.tvUserName);
        blurScreenClick               = (ImageView)headerLayout.findViewById(R.id.slide_background);
        tvUserName.setText(name);

        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "blured2.jpg");
        if (file.exists())
            try {
                //Log.d(TAG, "Image: Exist");
                blurScreenClick.setScaleType(ImageView.ScaleType.CENTER_CROP);
                blurScreenClick.setImageDrawable(Init(path));
            }   catch (FileNotFoundException e) {
                e.printStackTrace();
            } else {
            new DownloadImageTask().execute(url);
        }

        file = new File(path, "profile.jpg");
        Uri uri = Uri.fromFile(file);
        Glide.with(this).load(uri).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(profile_image);
        /*CHMakeScoreWithAnim chMakeScoreWithAnim = new CHMakeScoreWithAnim(getApplicationContext());
        chMakeScoreWithAnim.*/
        buildAnim(this);
        ViewServer.get(this).addWindow(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        OfflineMode offlineMode = new OfflineMode();
        if (offlineMode.isConnectedToRemoteAPI(this)) {
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
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
        /*CHMakeScoreWithAnim chMakeScoreWithAnim = new CHMakeScoreWithAnim(getApplicationContext());
        chMakeScoreWithAnim.*/

        buildAnim(this);
    }


    public void uploadPhoto(String path) {
        final String API_URL = "http://46.101.213.24:3007";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");

        String id = user.get("id");
        File f = new File(path);
        Log.d(TAG, "USER: " + token + " " + id);
        Log.d(TAG, "Status: " + f);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), f);

        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_photo(id, token, requestBody);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.d(TAG, "Status: photo_uploaded");
                } else Log.d(TAG, "Status :" + response.code());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Status: " + t);
            }
        });
    }


    public int checkPending() {
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        Cursor c = db.query("pending_duel", null, null, null, null, null, null);
        int o = 0;
        if (c.moveToFirst()) {
            do {
                o++;
            } while (c.moveToNext());
        }
        c.close();
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.set_duel_pending("" + o);
        Log.d(TAG, "O: " + o);
        return o;
    }


    private void hideItem() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.pending_duels).setVisible(false);
    }


    public void buildAnim(Activity activity) {
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        makeResponsiveScore(width);
        ImageView mImageViewFilling0 = (ImageView) findViewById(R.id.imageView_challenges_animation);
        ImageView mImageViewFilling1 = (ImageView) findViewById(R.id.imageView_wins_animation);
        ImageView mImageViewFilling2 = (ImageView) findViewById(R.id.imageView_total_animation);
        ((AnimationDrawable) mImageViewFilling0.getBackground()).start();
        ((AnimationDrawable) mImageViewFilling1.getBackground()).start();
        ((AnimationDrawable) mImageViewFilling2.getBackground()).start();

        final TextView tvChallengesCounter = (TextView) findViewById(R.id.textViewChallengesCounter);
        final TextView tvWinsCounter       = (TextView) findViewById(R.id.textViewWinsCounter);
        final TextView tvTotalCounter      = (TextView) findViewById(R.id.textViewTotalCounter);

        SessionManager sessionManager = new SessionManager(activity);

        String challenges = sessionManager.getChampyOptions().get("challenges");
        String wins       = sessionManager.getChampyOptions().get("wins");
        String total      = sessionManager.getChampyOptions().get("total");

        final int challengesInteger = Integer.parseInt(challenges);
        final int totalInteger      = Integer.parseInt(total);
        final int winsInteger       = Integer.parseInt(wins);

        // animator for In progress
        ValueAnimator animatorInProgress = new ValueAnimator();
        animatorInProgress.setObjectValues(0, challengesInteger);
        animatorInProgress.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tvTotalCounter.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animatorInProgress.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animatorInProgress.setDuration(1000);

        // animator for Total
        ValueAnimator animatorWins = new ValueAnimator();
        animatorWins.setObjectValues(0, winsInteger);
        animatorWins.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tvWinsCounter.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animatorWins.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animatorWins.setDuration(1000);

        // animator for total
        ValueAnimator animatorTotal = new ValueAnimator();
        animatorTotal.setObjectValues(0, totalInteger);
        animatorTotal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tvChallengesCounter.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animatorTotal.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animatorTotal.setDuration(1000);

        animatorTotal.start();
        animatorWins.start();
        animatorInProgress.start();

        final TextView textViewChallenges       = (TextView)  findViewById(R.id.textViewChallenges);
        final TextView textViewWins             = (TextView)  findViewById(R.id.textViewWins);
        final TextView textViewTotal            = (TextView)  findViewById(R.id.textViewTotal);
        final ImageView imageViewChallengesLogo = (ImageView) findViewById(R.id.imageView_challenges_logo);
        final ImageView imageViewWinsLogo       = (ImageView) findViewById(R.id.imageView_wins_logo);
        final ImageView imageViewTotalLogo      = (ImageView) findViewById(R.id.imageView_total_logo);

        final Animation alphaAnimation          = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(3000);

        textViewChallenges.setText("In Progress"); // TODO: 22.06.2016 Change to Challenges
        textViewChallenges.startAnimation(alphaAnimation);
        textViewWins.setText("Wins");
        textViewWins.startAnimation(alphaAnimation);
        textViewTotal.setText("Total");
        textViewTotal.startAnimation(alphaAnimation);

        Uri uri = Uri.parse("android.resource://com.example.ivan.champy_v2/drawable/challenges");
        Glide.with(activity).load(uri).into(imageViewChallengesLogo);
        imageViewChallengesLogo.startAnimation(alphaAnimation);

        uri = Uri.parse("android.resource://com.example.ivan.champy_v2/drawable/wins");
        Glide.with(activity).load(uri).into(imageViewWinsLogo);
        imageViewWinsLogo.startAnimation(alphaAnimation);

        uri = Uri.parse("android.resource://com.example.ivan.champy_v2/drawable/total");
        Glide.with(activity).load(uri).into(imageViewTotalLogo);
        imageViewTotalLogo.startAnimation(alphaAnimation);

    }


    public void makeResponsiveScore(int width) {
        int x = round(width/100);

        //-------------------------- Animation ---------------------------//
        ImageView imageView = (ImageView)findViewById(R.id.imageView_challenges_animation);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;

        imageView = (ImageView)findViewById(R.id.imageView_wins_animation);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;

        imageView = (ImageView)findViewById(R.id.imageView_total_animation);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;

        //---------------------------- Logo -----------------------------//
        imageView = (ImageView)findViewById(R.id.imageView_wins_logo);
        imageView.getLayoutParams().width = x*5;
        imageView.getLayoutParams().height = x*5;

        imageView = (ImageView)findViewById(R.id.imageView_total_logo);
        imageView.getLayoutParams().width = x*5;
        imageView.getLayoutParams().height = x*5;

        imageView = (ImageView)findViewById(R.id.imageView_challenges_logo);
        imageView.getLayoutParams().width = x*5;
        imageView.getLayoutParams().height = x*5;

        //---------------------------- Fab -----------------------------//
        ImageButton fab = (ImageButton)findViewById(R.id.fabPlus);
        fab.getLayoutParams().width = x*20;
        fab.getLayoutParams().height = x*20;

        /*imageView = (ImageView)findViewById(R.id.profile_image);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;*/

        //--------------------------- Score ----------------------------//
        Float y = x*(float)3.5;

        TextView textViewScoreChallenges = (TextView)findViewById(R.id.textViewChallengesCounter);
        textViewScoreChallenges.setTextSize(y);

        TextView textViewScoreWins = (TextView)findViewById(R.id.textViewWinsCounter);
        textViewScoreWins.setTextSize(y);

        TextView textViewScoreTotal = (TextView)findViewById(R.id.textViewTotalCounter);
        textViewScoreTotal.setTextSize(y);

        //------------------------- TextViews -------------------------//
        y = x*(float)1.5;
        TextView textViewChallenges = (TextView)findViewById(R.id.textViewChallenges);
        TextView textViewWins = (TextView)findViewById(R.id.textViewWins);
        TextView textViewTotal = (TextView)findViewById(R.id.textViewTotal);
        textViewChallenges.setTextSize(y);
        textViewWins.setTextSize(y);
        textViewTotal.setTextSize(y);



    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Log.d(TAG, "lalala: " + urldisplay);

            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        private String saveToInternalStorage(Bitmap bitmapImage){
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/saved_images");
            myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-"+ n +".jpg";
            File file = new File (myDir, fname);
            if (file.exists()) {
                file.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            uploadPhoto(Uri.fromFile(file).getPath());

            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            File myPath = new File(directory,"profile.jpg");

            Log.d(TAG, "MY_PATH: "+ myPath.toString());

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(myPath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return directory.getAbsolutePath();
        }

        public void loadImageFromStorage(String path) {

            try {
                File f = new File(path, "profile.jpg");
                Uri uri = Uri.fromFile(f);
                Glide.with(getApplicationContext())
                        .load(uri)
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into((ImageView)findViewById(R.id.profile_image));
                File file = new File(path, "blured2.jpg");
                if (file.exists()) {
                    return;
                } else {
                    file.createNewFile();
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

                    Blur blur = new Blur();

                    Bitmap blured = blur.blurRenderScript(getApplicationContext(), b, 10);

                    Bitmap bitmap = blured;

                    Drawable dr = new BitmapDrawable(getResources(), bitmap);
                    dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);
                    ImageView background = (ImageView) findViewById(R.id.slide_background);
                    background.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    background.setImageDrawable(dr);
                    background = (ImageView) findViewById(R.id.main_background);
                    background.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    background.setImageDrawable(dr);
                    bitmap = ((BitmapDrawable)dr).getBitmap();

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content_main);
                    relativeLayout.setDrawingCacheEnabled(true);
                    relativeLayout.buildDrawingCache();
                    Bitmap bm = relativeLayout.getDrawingCache();


                    blur = new Blur();

                    blured = blur.blurRenderScript(getApplicationContext(), bm, 25);

                    ImageView screen = (ImageView) findViewById(R.id.blurScreen);

                    Drawable ob = new BitmapDrawable(getResources(), blured);
                    screen.setImageDrawable(ob);
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        protected void onPostExecute(Bitmap result) {
            // Do your staff here to save image
            saveToInternalStorage(result);
            loadImageFromStorage("/data/data/com.example.ivan.champy_v2/app_imageDir/");
        }

    }


    public Drawable Init(String path) throws FileNotFoundException {
        File file = new File(path, "blured2.jpg");

        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        Drawable dr = new BitmapDrawable(getResources(), bitmap);
        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);
        bitmap = ((BitmapDrawable)dr).getBitmap();

        ImageView background = (ImageView)findViewById(R.id.main_background);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);
        background.setImageDrawable(dr);

        return dr;

    }


    public class CustomAdapter extends CustomPagerAdapter {

        private ArrayList<SelfImprovement_model> arrayList;

        public CustomAdapter(Context context, ArrayList<SelfImprovement_model> marrayList) {
            super(context);
            this.arrayList = marrayList;
        }

        @Override
        public View getView(int position, View convertView) {
            View tempView = convertView;
            if(tempView == null){
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                tempView = inflater.inflate(R.layout.single_card_fragment, null, false);
            }
            final SelfImprovement_model item = arrayList.get(position);
            ImageView cardImage = (ImageView)tempView.findViewById(R.id.cardImage);
            int x = round(getWindowManager().getDefaultDisplay().getWidth() / 100);
            int y = round(getWindowManager().getDefaultDisplay().getHeight() / 100);
            cardImage.getLayoutParams().width = x*65;
            cardImage.getLayoutParams().height = y*50;

            // cardImage.setImageDrawable(RecyclerView_Activity.this.getResources().getDrawable(R.drawable.card_image));

            Button buttonGiveUp = (Button) tempView.findViewById(R.id.buttonGiveUp);
            buttonGiveUp.getLayoutParams().width = x*10;
            buttonGiveUp.getLayoutParams().height = x*10;
            buttonGiveUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    OfflineMode offlineMode = new OfflineMode();
                                    if (offlineMode.isConnectedToRemoteAPI(MainActivity.this)){
                                        ChallengeController challengeController = new ChallengeController(MainActivity.this, MainActivity.this, 0 , 0);
                                        try {
                                            challengeController.give_up(item.getId());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Are you sure you want to give up?")
                            .setPositiveButton("Yes!", dialogClickListener)
                            .setNegativeButton("No!", dialogClickListener).show();

                }
            });
            Log.d("TAG", "X: "+x*y);

            if (y > 10) y = 10;

            TextView textView  = (TextView) tempView.findViewById(R.id.textViewSIC);
            textView.setText(item.getType());
            String s = item.getGoal();
            if (item.getType().equals("Wake Up")) {
                ImageView imageView = (ImageView)tempView.findViewById(R.id.imageViewChallengeLogo);
                imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.wakeupwhite));
                s = "Wake up at "+s.substring(0, 2)+":"+s.substring(2,4);
            }
            textView.setTextSize((float)(y*1.3));
            Typeface typeface = android.graphics.Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
            textView.setTypeface(typeface);

            TextView tvChallengeName = (TextView) tempView.findViewById(R.id.textViewChallengeName);
            tvChallengeName.setText(s);
            tvChallengeName.setTextSize(y);

            TextView tvDuration = (TextView) tempView.findViewById(R.id.textViewDuration);
            tvDuration.setText(item.getDays() + " DAYS TO GO");
            tvDuration.setTextSize(y*2);

            TextView tvLevelAndPoints = (TextView) tempView.findViewById(R.id.textViewLevelAndPoints);
            tvLevelAndPoints.setTextSize(y);

            Button buttonDoneForToday = (Button) tempView.findViewById(R.id.buttonDoneForToday);
            buttonDoneForToday.getLayoutParams().width = x*10;
            buttonDoneForToday.getLayoutParams().height = x*10;
            final Button finalButton = buttonDoneForToday;
            if (item.getUpdated() != null){
                if (!item.getType().equals("Wake Up")) { //?
                    if (item.getUpdated().equals("false")) {
                        buttonDoneForToday.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.accept1));
                    }
                }
            }
            buttonDoneForToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = item.getId();
                    //Log.d("myLogs", "Click " + id);
                    SQLiteDatabase localSQLiteDatabase = new DBHelper(MainActivity.this).getWritableDatabase();
                    ContentValues localContentValues = new ContentValues();
                    localContentValues.put("updated", "true");
                    localSQLiteDatabase.update("myChallenges", localContentValues, "challenge_id = ?", new String[]{id});
                    int i = localSQLiteDatabase.update("updated", localContentValues, "challenge_id = ?", new String[]{id});
                    //Log.d("myLogs", "Updated: " + i);
                    finalButton.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.icon_share));
                }
            });

            return tempView;
        }

        @Override
        public int dataCount() {
            return arrayList.size();
        }

    }


}