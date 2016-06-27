package com.example.ivan.champy_v2.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.support.v7.widget.RecyclerView;
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
import com.example.ivan.champy_v2.ImageModule;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.Pending_friend;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.adapter.FriendsAdapter;
import com.example.ivan.champy_v2.adapter.PendingAdapter;
import com.example.ivan.champy_v2.adapter.SampleFragmentPagerAdapter;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.interfaces.CustomItemClickListener;
import com.example.ivan.champy_v2.model.Friend.Datum;
import com.example.ivan.champy_v2.model.Friend.Friend;
import com.example.ivan.champy_v2.model.Friend.Friend_;
import com.example.ivan.champy_v2.model.Friend.Owner;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;
import java.io.FileInputStream;
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

    FragmentPagerAdapter adapterViewPager;
    private final String TAG = "myLogs";
    private com.facebook.CallbackManager CallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger logger = AppEventsLogger.newLogger(this);

        OfflineMode offlineMode = new OfflineMode();
        if (!offlineMode.isConnectedToRemoteAPI(this)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        //String update = user.get("updateDB");

        // invite friends button
        final FloatingActionButton.OnClickListener onClickInviteFriends = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appLinkUrl, previewImageUrl;

                appLinkUrl = "https://fb.me/583663125129793";
                previewImageUrl = "http://champyapp.com/images/Icon.png";

                Activity activity = FriendsActivity.this;
                if (AccessToken.getCurrentAccessToken() != null) {
                    FacebookSdk.sdkInitialize(FriendsActivity.this.getApplicationContext());
                    CallbackManager = com.facebook.CallbackManager.Factory.create();
                    FacebookCallback<AppInviteDialog.Result> facebookCallback= new FacebookCallback<AppInviteDialog.Result>() {
                        @Override
                        public void onSuccess(AppInviteDialog.Result result) {
                            Log.i(TAG, "MainACtivity, InviteCallback - SUCCESS!" + result.getData());
                        }

                        @Override
                        public void onCancel() {
                            Log.i(TAG, "MainACtivity, InviteCallback - CANCEL!");
                        }

                        @Override
                        public void onError(FacebookException e) {
                            Log.e(TAG, "MainACtivity, InviteCallback - ERROR! " + e.getMessage());
                        }

                    };
                    AppInviteDialog appInviteDialog = new AppInviteDialog(activity);
                    if (AppInviteDialog.canShow()) {
                        AppInviteContent.Builder content = new AppInviteContent.Builder();
                        content.setApplinkUrl(appLinkUrl);
                        content.setPreviewImageUrl(previewImageUrl);
                        AppInviteContent appInviteContent = content.build();
                        appInviteDialog.registerCallback(CallbackManager, facebookCallback);
                        AppInviteDialog.show(activity, appInviteContent);
                    }
                }
            }
        };


        ImageView imageView;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
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

        int count = 0;

        String s = sessionManager.get_duel_pending();
        if (s != null) {
            count = Integer.parseInt(s);
        }

        TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
        if (count == 0) {
            hideItem();
        }
        else {
            view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new SampleFragmentPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(adapterViewPager);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            s = bundle.getString("friend_request");
            UpdatePending();
            if (s != null) {
                viewPager.setCurrentItem(1);
            }
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                SessionManager sessionManager1 = new SessionManager(getApplicationContext());
                String refresh = sessionManager1.getRefreshFriends();
                Log.d(TAG, "RefreshFriends: " + refresh);
                if (refresh.equals("true")) {
                    UpdateFriendsList();
                    sessionManager1.setRefreshFriends("false");
                }
                refresh = sessionManager1.getRefreshPending();
                Log.d(TAG, "RefreshPending: "+refresh);
                if (refresh.equals("true")) {
                    UpdatePending();
                    sessionManager1.setRefreshPending("false");
                }
                com.melnykov.fab.FloatingActionButton floatingActionButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.fabPlus);
                if (position == 2) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                    floatingActionButton.setOnClickListener(onClickInviteFriends);
                } else {
                    floatingActionButton.setVisibility(View.INVISIBLE);
                }
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
            ImageModule imageModule = new ImageModule(this);
            Drawable dr = imageModule.Init("/data/data/com.example.ivan.champy_v2/app_imageDir/", FriendsActivity.this);
            imageView = (ImageView) headerLayout.findViewById(R.id.slide_background);
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
            Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        OfflineMode offlineMode = new OfflineMode();
        if (offlineMode.isConnectedToRemoteAPI(this)) {
            switch (item.getItemId()) {
                case R.id.challenges:
                    Intent goToChallenges = new Intent(FriendsActivity.this, MainActivity.class);
                    startActivity(goToChallenges);
                    break;
                case R.id.history:
                    Intent goToHistory = new Intent(FriendsActivity.this, HistoryActivity.class);
                    startActivity(goToHistory);
                    break;
                case R.id.pending_duels:
                    Intent goToPendingDuel = new Intent(FriendsActivity.this, PendingDuelActivity.class);
                    startActivity(goToPendingDuel);
                    break;
                case R.id.settings:
                    Intent goToSettings = new Intent(FriendsActivity.this, SettingsActivity.class);
                    startActivity(goToSettings);
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
                    if (offlineMode.isConnectedToRemoteAPI(this)) {
                        Logout();
                    }
                    break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        OfflineMode offlineMode = new OfflineMode();
        if (!offlineMode.isConnectedToRemoteAPI(this)){
            Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
            startActivity(intent);
        }
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
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }


    public void Logout(){
        LoginManager.getInstance().logOut();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.logoutUser();
        Intent intent = new Intent(FriendsActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    public void UpdateFriendsList() {
        final String API_URL = "http://46.101.213.24:3007";
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        final String id = user.get("id");
        String token = user.get("token");
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
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
                                    if (friend.getPhoto() != null) {
                                        cv.put("photo", friend.getPhoto().getMedium());
                                    } else {
                                        cv.put("photo", "");
                                    }
                                    cv.put("user_id", friend.getId());
                                    db.insert("friends", null, cv);
                                } else {
                                    Owner friend = datum.getOwner();
                                    cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null) {
                                        cv.put("photo", friend.getPhoto().getMedium());
                                    } else {
                                        cv.put("photo", "");
                                    }
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
                        int index = c.getColumnIndex("user_id");
                        do {
                            Log.i("newusers", "NewUser: " + c.getString(nameColIndex) + " Photo: " + c.getString(photoColIndex));
                            newfriends.add(new com.example.ivan.champy_v2.Friend(
                                    c.getString(nameColIndex),
                                    API_URL + c.getString(photoColIndex),
                                    c.getString(index),
                                    "0", "0", "0" ,"0"));
                        } while (c.moveToNext());
                    } //else Log.i("stat", "0 0 0 0");
                    c.close();

                    Log.i("stat", "FriendsActivity :" + newfriends.toString());
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
                    Log.i(TAG, "Refresh FriendsActivity List");
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


    public void UpdatePending() {
        final String API_URL = "http://46.101.213.24:3007";
        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        final String id = user.get("id");
        String token = user.get("token");
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
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
                    final List<Pending_friend> newfriends = new ArrayList<>();
                    Cursor c = db.query("pending", null, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        //int idColIndex = c.getColumnIndex("id");
                        int nameColIndex = c.getColumnIndex("name");
                        int photoColIndex = c.getColumnIndex("photo");
                        int index = c.getColumnIndex("user_id");
                        int owner = c.getColumnIndex("owner");
                        //int challenges = c.getColumnIndex("challenges");
                        do {
                            Log.i("newusers", "NewUser: " + c.getString(nameColIndex) + " Photo: " + c.getString(photoColIndex));
                            newfriends.add(new Pending_friend(c.getString(nameColIndex),
                                    API_URL+c.getString(photoColIndex),
                                    c.getString(index),
                                    c.getString(owner)));
                        } while (c.moveToNext());
                    } else
                        Log.i("stat", "null rows");
                    c.close();

                    Log.i("stat", "FriendsActivity :" + newfriends.toString());

                    /*RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
                    final PendingAdapter adapter = new PendingAdapter(newfriends, FriendsActivity.this, FriendsActivity.this, new CustomItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Pending_friend friend = newfriends.get(position);
                        }
                    });
                    rvContacts.setAdapter(adapter);*/
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });

    }


}