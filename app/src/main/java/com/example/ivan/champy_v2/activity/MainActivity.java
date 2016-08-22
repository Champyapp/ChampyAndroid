package com.example.ivan.champy_v2.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.example.ivan.champy_v2.CustomPagerBase;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.adapter.CustomPagerAdapter;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.AppSync;
import com.example.ivan.champy_v2.helper.CHBuildAnim;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.interfaces.Update_user;
import com.example.ivan.champy_v2.model.SelfImprovement_model;
import com.example.ivan.champy_v2.model.User.User;
import com.facebook.FacebookSdk;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final String TAG = "myLogs";
    private long mLastClickTime = 0;
    private int counter = 0;
    private int total = 30;
    private PendingIntent pendingIntent;
    private FloatingActionMenu actionMenu;
    public Context _context;
    public Activity activity;
    private CustomPagerBase pager;
    AlarmManager alarmManager;
    AppSync sync;
    HashMap<String,String> user;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://46.101.213.24:3007");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final SessionManager sessionManager = new SessionManager(getApplicationContext());
            HashMap<String, String> user;
            user = sessionManager.getUserDetails();
            String token = user.get("token");
            mSocket.emit("ready", token);
            Log.i("call", "call: minden fasza");
        }
    };

    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.i("call", "call: connected okay");
        }
    };

    private Emitter.Listener onNewRelationship = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {

            Log.i("call", "new friend request");
        }
    };

    private Emitter.Listener onNewChallenge = new Emitter.Listener()  {

        @Override
        public void call(final Object... args) {
            /*String userId = user.get("id");
            Log.i("call", "call: new challenge request for duel 1");
            try {


                //Log.i("call", "call: " + facebookId);

                sync.getInProgressChallenges(userId);
                Log.i("call", "call: new challenge request for duel2");
            } catch (Exception e) {
                Log.i("call", "call: ERROR: " + e);
            }*/


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*try {
            SessionManager session = new SessionManager(getApplicationContext());
            this.user = session.getUserDetails();
            String userId = user.get("id");
            String facebookId = session.getFacebookId();
            String gcm = session.getGCM();
            this.sync = new AppSync(facebookId, gcm);
        } catch (Exception e) {
            Log.i("onCreate", "Error in on create: " + e);
        }*/
        mSocket.on("connect", onConnect);
        mSocket.on("connected", onConnected);
        mSocket.on("Relationship:new", onNewRelationship);
//        mSocket.on("Relationship:created", onNewRelationship);
//        mSocket.on("Relationship:removed", onNewRelationship);
//        mSocket.on("Relationship:accepted", onNewRelationship);
//        mSocket.on("Relationship:new:removed", onNewRelationship);
//        mSocket.on("Relationship:new:accepted", onNewRelationship);

        mSocket.on("InProgressChallenge:new", onNewChallenge);
//        mSocket.on("InProgressChallenge:accepted", onNewRelationship);
//        mSocket.on("InProgressChallenge:failed", onNewRelationship);
//        mSocket.on("InProgressChallenge:won", onNewRelationship);
//        mSocket.on("InProgressChallenge:checked", onNewRelationship);
//        mSocket.on("InProgressChallenge:updated", onNewRelationship);


        mSocket.connect();

        SessionManager sessionManager = new SessionManager(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        // get_right_token();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_gradient));
        setSupportActionBar(toolbar);

        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmSchedule.class);
        intent.putExtra("alarm", "reset");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));  // було 18
        calendar.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));        //  було 6
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
        final SubActionButton buttonDuelChallenge   = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.duel_yellow)).build();
        final SubActionButton buttonSelfImprovement = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.self_yellow)).build();

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

                    }
                }
            }
        };

        // клик по меню фаба
        actionButton.setOnClickListener(onClickFab);

//        ImageView blurScreenClick = (ImageView)findViewById(R.id.blurScreen);
//        blurScreenClick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.cards);
//                ImageView screen = (ImageView) findViewById(R.id.blurScreen);
//                if (actionMenu.isOpen()) {
//                    actionMenu.getSubActionItems();
//                    OfflineMode offlineMode = new OfflineMode();
//                    if (offlineMode.isConnectedToRemoteAPI(MainActivity.this)) {
//                        buttonSelfImprovement.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(MainActivity.this, SelfImprovementActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//                        buttonDuelChallenge.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//                        buttonWakeUpChallenge.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(MainActivity.this, WakeUpActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//                    }
//                    actionMenu.close(true);
//                    if (!actionMenu.isOpen()) {
//                        screen.setVisibility(View.INVISIBLE);
//                        relativeLayout.setVisibility(View.VISIBLE);
//                        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.plus));
//                    }
//                } else {
//                    actionMenu.close(false);
//                    if (actionMenu.isOpen()) {
//                        screen.setVisibility(View.VISIBLE);
//                        relativeLayout.setVisibility(View.INVISIBLE);
//                        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.plus));
//                    }
//                }
//            }
//        });

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

        CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
        int count = checker.checkPending();
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
        view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        if (count == 0) checker.hideItem();

        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String path_to_pic = user.get("path_to_pic");
        String name = user.get("name");

        if (path_to_pic == null) {
            Log.i("GetUserPhoto", "User Photo == NULL");
            Intent fromLogin = getIntent();
            path_to_pic = fromLogin.getExtras().getString("path_to_pic");
            name = fromLogin.getExtras().getString("name");
        }

        ImageView background    = (ImageView)headerLayout.findViewById(R.id.slide_background);
        ImageView profile_image = (ImageView)headerLayout.findViewById(R.id.profile_image);
        TextView tvUserName     = (TextView)headerLayout.findViewById(R.id.tvUserName);
        tvUserName.setText(name);

        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "blured2.jpg");
        if (file.exists())
            try {
                background.setScaleType(ImageView.ScaleType.CENTER_CROP);
                background.setImageDrawable(initBackground(path));
            }   catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        else {
            new DownloadImageTask().execute(path_to_pic);
        }

        file = new File(path, "profile.jpg");
        Uri uri = Uri.fromFile(file);
        Glide.with(this).load(uri).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(profile_image);
        ViewServer.get(this).addWindow(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        CHBuildAnim chBuildAnim = new CHBuildAnim();
        chBuildAnim.buildAnim(this);
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


    public void uploadPhoto(String path) {
        final String API_URL = "http://46.101.213.24:3007";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");

        String id = user.get("id");
        File f = new File(path);
        Log.d("UploadPhoto", "Token: " + token + "\n Id = " + id);
        Log.d("UploadPhoto", "File = " + f);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), f);

        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_photo(id, token, requestBody);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.d("UploadPhoto", "Status: VSE OK");
                } else Log.d("UploadPhoto", "Status :" + response.code());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Status: " + t);
            }
        });
    }


//    public void buildAnim(Activity activity) {
//        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
//        makeResponsiveScore(width);
//        /*ImageView mImageViewFilling0 = (ImageView) findViewById(R.id.imageView_challenges_animation);
//        ImageView mImageViewFilling1 = (ImageView) findViewById(R.id.imageView_wins_animation);
//        ImageView mImageViewFilling2 = (ImageView) findViewById(R.id.imageView_total_animation);
//        ((AnimationDrawable) mImageViewFilling0.getBackground()).start();
//        ((AnimationDrawable) mImageViewFilling1.getBackground()).start();
//        ((AnimationDrawable) mImageViewFilling2.getBackground()).start();*/
//
//        final TextView tvChallengesCounter = (TextView) findViewById(R.id.textViewChallengesCounter);
//        final TextView tvWinsCounter       = (TextView) findViewById(R.id.textViewWinsCounter);
//        final TextView tvTotalCounter      = (TextView) findViewById(R.id.textViewTotalCounter);
//
//        SessionManager sessionManager = new SessionManager(activity);
//        String challenges = sessionManager.getChampyOptions().get("challenges");
//        String wins       = sessionManager.getChampyOptions().get("wins");
//        String total      = sessionManager.getChampyOptions().get("total");
//
//        int challengesInteger = Integer.parseInt(challenges);
//        int totalInteger      = Integer.parseInt(total);
//        int winsInteger       = Integer.parseInt(wins);
//
////         animator for In progress
//        ValueAnimator animatorInProgress = new ValueAnimator();
//        animatorInProgress.setObjectValues(0, challengesInteger);
//        animatorInProgress.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                tvTotalCounter.setText(String.valueOf(animation.getAnimatedValue()));
//            }
//        });
//        animatorInProgress.setEvaluator(new TypeEvaluator<Integer>() {
//            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
//                return Math.round(startValue + (endValue - startValue) * fraction);
//            }
//        });
//        animatorInProgress.setDuration(1000);
//
////         animator for Total
//        ValueAnimator animatorWins = new ValueAnimator();
//        animatorWins.setObjectValues(0, winsInteger);
//        animatorWins.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                tvWinsCounter.setText(String.valueOf(animation.getAnimatedValue()));
//            }
//        });
//        animatorWins.setEvaluator(new TypeEvaluator<Integer>() {
//            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
//                return Math.round(startValue + (endValue - startValue) * fraction);
//            }
//        });
//        animatorWins.setDuration(1000);
//
////         animator for total
//        ValueAnimator animatorTotal = new ValueAnimator();
//        animatorTotal.setObjectValues(0, totalInteger);
//        animatorTotal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                tvChallengesCounter.setText(String.valueOf(animation.getAnimatedValue()));
//            }
//        });
//        animatorTotal.setEvaluator(new TypeEvaluator<Integer>() {
//            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
//                return Math.round(startValue + (endValue - startValue) * fraction);
//            }
//        });
//        animatorTotal.setDuration(1000);
//
//        animatorTotal.start();
//        animatorWins.start();
//        animatorInProgress.start();
//
//        final TextView textViewChallenges       = (TextView)  findViewById(R.id.textViewChallenges);
//        final TextView textViewWins             = (TextView)  findViewById(R.id.textViewWins);
//        final TextView textViewTotal            = (TextView)  findViewById(R.id.textViewTotal);
//        final ImageView imageViewChallengesLogo = (ImageView) findViewById(R.id.imageView_challenges_logo);
//        final ImageView imageViewWinsLogo       = (ImageView) findViewById(R.id.imageView_wins_logo);
//        final ImageView imageViewTotalLogo      = (ImageView) findViewById(R.id.imageView_total_logo);
//
//        final Animation alphaAnimation          = new AlphaAnimation(0.0f, 1.0f);
//        alphaAnimation.setDuration(3000);
//
//        textViewChallenges.setText("In Progress"); // TODO: 22.06.2016 Change to Challenges
//        textViewChallenges.startAnimation(alphaAnimation);
//        textViewWins.setText("Wins");
//        textViewWins.startAnimation(alphaAnimation);
//        textViewTotal.setText("Total");
//        textViewTotal.startAnimation(alphaAnimation);
//
//        Uri uri = Uri.parse("android.resource://com.example.ivan.champy_v2/drawable/challenges");
//        Glide.with(activity).load(uri).into(imageViewChallengesLogo);
//        imageViewChallengesLogo.startAnimation(alphaAnimation);
//
//        uri = Uri.parse("android.resource://com.example.ivan.champy_v2/drawable/wins");
//        Glide.with(activity).load(uri).into(imageViewWinsLogo);
//        imageViewWinsLogo.startAnimation(alphaAnimation);
//
//        uri = Uri.parse("android.resource://com.example.ivan.champy_v2/drawable/total");
//        Glide.with(activity).load(uri).into(imageViewTotalLogo);
//        imageViewTotalLogo.startAnimation(alphaAnimation);
//
//    }


//    public void makeResponsiveScore(int width) {
//        int x = round(width/100);
//
//        //-------------------------- Animation ---------------------------//
//        ImageView imageView = (ImageView)findViewById(R.id.imageView_challenges_animation);
//        imageView.getLayoutParams().width = x*25;
//        imageView.getLayoutParams().height = x*25;
//
//        imageView = (ImageView)findViewById(R.id.imageView_wins_animation);
//        imageView.getLayoutParams().width = x*25;
//        imageView.getLayoutParams().height = x*25;
//
//        imageView = (ImageView)findViewById(R.id.imageView_total_animation);
//        imageView.getLayoutParams().width = x*25;
//        imageView.getLayoutParams().height = x*25;
//
//        //---------------------------- Logo -----------------------------//
//        imageView = (ImageView)findViewById(R.id.imageView_wins_logo);
//        imageView.getLayoutParams().width = x*5;
//        imageView.getLayoutParams().height = x*5;
//
//        imageView = (ImageView)findViewById(R.id.imageView_total_logo);
//        imageView.getLayoutParams().width = x*5;
//        imageView.getLayoutParams().height = x*5;
//
//        imageView = (ImageView)findViewById(R.id.imageView_challenges_logo);
//        imageView.getLayoutParams().width = x*5;
//        imageView.getLayoutParams().height = x*5;
//
//        //---------------------------- Fab -----------------------------//
//        ImageButton fab = (ImageButton)findViewById(R.id.fabPlus);
//        fab.getLayoutParams().width = x*20;
//        fab.getLayoutParams().height = x*20;
//
//        imageView = (ImageView)findViewById(R.id.profile_image);
//        imageView.getLayoutParams().width = x*25;
//        imageView.getLayoutParams().height = x*25;
//
//        //--------------------------- Score ----------------------------//
//        Float y = x*(float)3.5;
//
//        TextView textViewScoreChallenges = (TextView)findViewById(R.id.textViewChallengesCounter);
//        textViewScoreChallenges.setTextSize(y);
//
//        TextView textViewScoreWins = (TextView)findViewById(R.id.textViewWinsCounter);
//        textViewScoreWins.setTextSize(y);
//
//        TextView textViewScoreTotal = (TextView)findViewById(R.id.textViewTotalCounter);
//        textViewScoreTotal.setTextSize(y);
//
//        //------------------------- TextViews -------------------------//
//        y = x*(float)1.5;
//        TextView textViewChallenges = (TextView)findViewById(R.id.textViewChallenges);
//        TextView textViewWins = (TextView)findViewById(R.id.textViewWins);
//        TextView textViewTotal = (TextView)findViewById(R.id.textViewTotal);
//        textViewChallenges.setTextSize(y);
//        textViewWins.setTextSize(y);
//        textViewTotal.setTextSize(y);
//
//
//
//    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Log.d("DownLoadImageTask", "doInBackground: " + urldisplay);

            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("DownLoadImageTask", "Error: " + e.getMessage());
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

            Log.i("DownloadImageTask", "saveToInternalStorage: MyPath = " + myPath.toString());

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
            catch (FileNotFoundException e) {
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


    public Drawable initBackground(String path) throws FileNotFoundException {
        File file = new File(path, "blured2.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

        Drawable dr = new BitmapDrawable(getResources(), bitmap);
        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);

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
            if(tempView == null) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                tempView = inflater.inflate(R.layout.single_card_fragment, null, false);
            }
            final SelfImprovement_model item = arrayList.get(position);
            ImageView cardImage = (ImageView)tempView.findViewById(R.id.cardImage);
            int x = round(getWindowManager().getDefaultDisplay().getWidth() / 100);
            int y = round(getWindowManager().getDefaultDisplay().getHeight() / 100);
            cardImage.getLayoutParams().width  = x*65;
            cardImage.getLayoutParams().height = y*50;
            if (y > 10) y = 10;

            TextView tvSelfImprovement  = (TextView) tempView.findViewById(R.id.textViewSIC);
            tvSelfImprovement.setText(item.getType());
            String itemGoal = item.getGoal();
            ImageView imageView = (ImageView)tempView.findViewById(R.id.imageViewChallengeLogo);
            //Log.i("getView asddasdas", "getView: " + item.getGoal());

            switch (item.getType()) {
                case "Wake Up":
                    imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.wakeup_white));
                    itemGoal = item.getChallengeName(); // "Wake up at " + "__:__" + " during this period";
                    break;
                case "Duel":
                    imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.duel_white));
                    break;
                case "Self-Improvement":
                    imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.self_white));
                    break;
            }
            tvSelfImprovement.setTextSize((float)(y*1.3));
            Typeface typeface = android.graphics.Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
            tvSelfImprovement.setTypeface(typeface);

            TextView tvChallengeName = (TextView) tempView.findViewById(R.id.textViewChallengeName);
            tvChallengeName.setText(itemGoal);
            tvChallengeName.setTextSize(y);

            TextView tvDuration = (TextView) tempView.findViewById(R.id.textViewDuration);
            tvDuration.setText(item.getDays() + " DAYS TO GO");
            tvDuration.setTextSize(y*2);

            TextView tvLevelAndPoints = (TextView) tempView.findViewById(R.id.textViewLevelAndPoints);
            tvLevelAndPoints.setTextSize(y);

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
                                        ChallengeController challengeController = new ChallengeController(MainActivity.this, MainActivity.this, 0 , 0, 0);
                                        try {
                                            challengeController.give_up(item.getId());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Are you sure")
                            .setMessage("You want to give up?")
                            .setIcon(R.drawable.ic_action_warn)
                            .setCancelable(false)
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                }
            });


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