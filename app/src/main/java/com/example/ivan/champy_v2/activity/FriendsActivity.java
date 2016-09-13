package com.example.ivan.champy_v2.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHImageModule;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.model.Pending_friend;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.adapter.FriendsAdapter;
import com.example.ivan.champy_v2.adapter.PendingAdapter;
import com.example.ivan.champy_v2.adapter.FriendsActivityPagerAdapter;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.interfaces.CustomItemClickListener;
import com.example.ivan.champy_v2.model.Friend.Datum;
import com.example.ivan.champy_v2.model.Friend.Friend_;
import com.example.ivan.champy_v2.model.Friend.Owner;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static java.lang.Math.round;

public class FriendsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FriendsActivityPagerAdapter adapterViewPager;
    private final String TAG = "FriendsActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();

        ImageView imageView;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //actionMenu.close(true);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
        int count = checker.checkPending();
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
        view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        if (count == 0) checker.hideItem();

        String s;

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new FriendsActivityPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(adapterViewPager);

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            s = bundle.getString("friend_request");
//            loadUserPending();
//            if (s != null) {
//                viewPager.setCurrentItem(1);
//            }
//        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                SessionManager sessionManager = new SessionManager(getApplicationContext());
//                String refreshFriends = sessionManager.getRefreshFriends();
//                Log.d(TAG, "RefreshFriends: " + refreshFriends);
//                if (refreshFriends.equals("true")) {
//                    loadUserFriends();
//                    sessionManager.setRefreshFriends("false");
//                }
//
//                String refreshPending = sessionManager.getRefreshPending();
//                Log.d(TAG, "RefreshPending: " + refreshPending);
//                if (refreshPending.equals("true")) {
//                    loadUserPending();
//                    sessionManager.setRefreshPending("false");
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        String name = user.get("name");

        ImageView profile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        TextView tvUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        tvUserName.setText(name);
        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);

        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(profile);

        try {
            CHImageModule CHImageModule = new CHImageModule(this);
            Drawable dr = CHImageModule.Init("/data/data/com.example.ivan.champy_v2/app_imageDir/", FriendsActivity.this);
            imageView = (ImageView) headerLayout.findViewById(R.id.slide_background);
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
        } else {
            Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.challenges:
                Intent goToMainActivity = new Intent(this, MainActivity.class);
                startActivity(goToMainActivity);
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
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        OfflineMode offlineMode = new OfflineMode();
        offlineMode.isConnectedToRemoteAPI(this);
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        ViewServer.get(this).setFocusedWindow(this);
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        AppEventsLogger.deactivateApp(this);
    }

    // load friends from bd
    public void loadUserFriends() {
        // TODO: 31.08.2016 use AppSync.loadUserFriends method;
        final String API_URL = "http://46.101.213.24:3007";
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        final String id = user.get("id");
        String token = user.get("token");
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("friends", null, null);
        final ContentValues cv = new ContentValues();

        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);
        Call<com.example.ivan.champy_v2.model.Friend.Friend> call = friends.getUserFriends(id, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.Friend.Friend>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.Friend.Friend> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        Datum datum = data.get(i);
                        if (datum.getFriend() != null) {

                            if (datum.getStatus().toString().equals("true")) {

                                if (datum.getOwner().get_id().equals(id)) {
                                    Friend_ friend = datum.getFriend();
                                    cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null)
                                         cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.getId());
                                    db.insert("friends", null, cv);
                                } else {
                                    Owner friend = datum.getOwner();
                                    cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null)
                                         cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.get_id());
                                    db.insert("friends", null, cv);
                                }
                            }
                        }
                    }
                    final List<com.example.ivan.champy_v2.Friend> newfriends = new ArrayList<>();
                    final RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
                    Cursor c = db.query("friends", null, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        //int idColIndex = c.getColumnIndex("id");
                        int nameColIndex = c.getColumnIndex("name");
                        int photoColIndex = c.getColumnIndex("photo");
                        int inProgressChallengesCountIndex = c.getColumnIndex("inProgressChallengesCount");
                        int successChallenges = c.getColumnIndex("successChallenges");
                        int allChallengesCount = c.getColumnIndex("allChallengesCount");
                        int level = c.getColumnIndex("level");
                        int index = c.getColumnIndex("user_id");
                        do {
                            Log.i("newusers", "NewUser: " + c.getString(nameColIndex)
                                    + " i: " + c.getString(inProgressChallengesCountIndex)
                                    + " w: " + c.getString(successChallenges)
                                    + " t: " + c.getString(allChallengesCount)
                                    + " Photo: " + c.getString(photoColIndex));
                            newfriends.add(new com.example.ivan.champy_v2.Friend(
                                    c.getString(nameColIndex),
                                    API_URL + c.getString(photoColIndex),
                                    c.getString(index),
                                    c.getString(inProgressChallengesCountIndex),
                                    c.getString(successChallenges),
                                    c.getString(allChallengesCount),
                                    "0"));
                        } while (c.moveToNext());
                    }
                    c.close();

                    Log.i(TAG, "Displayed friends: " + newfriends.toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final FriendsAdapter adapter = new FriendsAdapter(newfriends, getApplicationContext(), FriendsActivity.this, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    com.example.ivan.champy_v2.Friend friend = newfriends.get(position);
                                    friend.getmChallenges();
                                    friend.getmTotal();
                                    friend.getmWins();
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvContacts.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                    Log.i(TAG, "loadUserFriends: finished");
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });
    }

    // load pending from bd
    public void loadUserPending() {
        final String API_URL = "http://46.101.213.24:3007";
        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        final String id = user.get("id");
        String token = user.get("token");
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        //int clearCount = db.delete("pending", null, null);
        final ContentValues cv = new ContentValues();

        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);
        Call<com.example.ivan.champy_v2.model.Friend.Friend> call = friends.getUserFriends(id, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.Friend.Friend>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.Friend.Friend> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        Datum datum = data.get(i);
                        if ((datum.getFriend() != null) && (datum.getOwner() != null )) {
                            if (datum.getStatus().toString().equals("false")) {
                                if (datum.getOwner().get_id().equals(id)) {
                                    Friend_ friend = datum.getFriend();
                                    cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null)
                                         cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.getId());
                                    cv.put("owner", "false");
                                    db.insert("pending", null, cv);
                                } else {
                                    Owner friend = datum.getOwner();
                                    cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null)
                                         cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.get_id());
                                    cv.put("owner", "true");
                                    db.insert("pending", null, cv);
                                }
                            }
                        }
                    }
                    final List<Pending_friend> pendingFriends = new ArrayList<>();
                    Cursor c = db.query("pending", null, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        int nameColIndex = c.getColumnIndex("name");
                        int photoColIndex = c.getColumnIndex("photo");
                        int index = c.getColumnIndex("user_id");
                        int owner = c.getColumnIndex("owner");
                        int inProgressChallengesCountIndex = c.getColumnIndex("inProgressChallengesCount");
                        int successChallenges = c.getColumnIndex("successChallenges");
                        int allChallengesCount = c.getColumnIndex("allChallengesCount");
                        do {
                            pendingFriends.add(new Pending_friend(
                                    c.getString(nameColIndex),
                                    API_URL + c.getString(photoColIndex),
                                    c.getString(index),
                                    c.getString(owner),
                                    c.getString(successChallenges),
                                    c.getString(allChallengesCount),
                                    c.getString(inProgressChallengesCountIndex)
                            ));
                        } while (c.moveToNext());
                    }
                    c.close();

                    RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
                    final PendingAdapter adapter = new PendingAdapter(pendingFriends, FriendsActivity.this, FriendsActivity.this, new CustomItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Pending_friend friend = pendingFriends.get(position);
                        }
                    });
                    rvContacts.setAdapter(adapter);
                }
                Log.i(TAG, "loadUserPending: finished");
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });

    }


}