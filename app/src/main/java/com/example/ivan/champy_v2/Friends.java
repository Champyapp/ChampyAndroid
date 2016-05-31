package com.example.ivan.champy_v2;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
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

public class Friends extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FragmentPagerAdapter adapterViewPager;
    private final String TAG = "myLogs";
    private com.facebook.CallbackManager CallbackManager;
    private FloatingActionMenu actionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        OfflineMode offlineMode = new OfflineMode();
        if (!offlineMode.isInternetAvailable(this)){
            Intent intent = new Intent(Friends.this, MainActivity.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String update = user.get("updateDB");

        /* if (update.equals("true")) {
            getFriends();
            sessionManager.setUpdateFalse();
        }*/

        final com.melnykov.fab.FloatingActionButton actionButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.imageButton);
        actionButton.setVisibility(View.INVISIBLE);
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

        /*final FloatingActionButton.OnClickListener onClickListener2 = new View.OnClickListener() {
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
                    if (bm == null) {
                        Log.d(TAG, "SUKAAAAA");
                    } else {
                        Bitmap blured = Blur.blurRenderScript(getApplicationContext(), bm, 25);
                        screen = (ImageView) findViewById(R.id.blured);
                        Drawable ob = new BitmapDrawable(getResources(), blured);
                        screen.setImageDrawable(ob);
                    }
                }
                else {
                    Log.d(TAG, "Vse zaebok");
                }
                //      screen.bringToFront();
                Log.d("TAG", "menu " + actionMenu.isOpen());
                actionMenu.toggle(true);
                ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
                if (!actionMenu.isOpen()) {
                    screen.setVisibility(View.INVISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    actionButton.setImageDrawable(getResources().getDrawable(R.drawable.plus));
                }
                else {
                    screen.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.INVISIBLE);
                    actionButton.setImageDrawable(getResources().getDrawable(R.drawable.close));
                }
            }
        };*/


        final FloatingActionButton.OnClickListener onClickInviteFriends = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appLinkUrl, previewImageUrl;

                appLinkUrl = "https://fb.me/583663125129793";
                previewImageUrl = "http://champyapp.com/images/Icon.png";

                Activity activity = Friends.this;
                if (AccessToken.getCurrentAccessToken() == null) {
                    // start login...
                } else {
                    FacebookSdk.sdkInitialize(Friends.this.getApplicationContext());
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

                    // sharing method ???
                    AppInviteDialog appInviteDialog = new AppInviteDialog(activity);
                    if (appInviteDialog.canShow()) {
                        AppInviteContent.Builder content = new AppInviteContent.Builder();
                        content.setApplinkUrl(appLinkUrl);
                        content.setPreviewImageUrl(previewImageUrl);
                        AppInviteContent appInviteContent = content.build();
                        appInviteDialog.registerCallback(CallbackManager, facebookCallback);
                        appInviteDialog.show(activity, appInviteContent);
                        //Toast.makeText(getApplicationContext(), "TIPA OTPRAVLENNO", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };


        ImageView imageView = (ImageView)findViewById(R.id.blured);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
                ImageView screen = (ImageView) findViewById(R.id.blured);
                if (actionMenu.isOpen()) {
                    actionMenu.close(true);
                    if (!actionMenu.isOpen()) {
                        screen.setVisibility(View.INVISIBLE);
                        viewPager.setVisibility(View.VISIBLE);
                        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.plus));
                    }
                } else {
                    actionMenu.close(false);
                    if (!actionMenu.isOpen()) {
                        screen.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.INVISIBLE);
                        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.plus));
                    }
                }
            }


        });

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

            @Override // отвечает за кнопку fab в разделе Friends
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
                /*if (position == 1) {
                    com.melnykov.fab.FloatingActionButton floatingActionButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.imageButton);
                    floatingActionButton.setVisibility(View.INVISIBLE);
                } else *//*(position == 0)*//*{
                    com.melnykov.fab.FloatingActionButton floatingActionButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.imageButton);
                    if (position == 0) floatingActionButton.setOnClickListener(onClickListener2);
                    if (position == 2) floatingActionButton.setOnClickListener(onClickInviteFriends);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }*/
                if (position == 2) {
                    com.melnykov.fab.FloatingActionButton floatingActionButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.imageButton);
                    floatingActionButton.setVisibility(View.VISIBLE);
                    floatingActionButton.setOnClickListener(onClickInviteFriends);
                } else {
                    com.melnykov.fab.FloatingActionButton floatingActionButton = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.imageButton);
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
        TextView textView = (TextView) headerLayout.findViewById(R.id.textView);
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
            ImageModule imageModule = new ImageModule(this);
            Drawable dr = imageModule.Init("/data/data/com.example.ivan.champy_v2/app_imageDir/", Friends.this);
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

        OfflineMode offlineMode = new OfflineMode();
        if (offlineMode.isInternetAvailable(this)) {
            if (id == R.id.nav_logout) {

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
            if (id == R.id.history) {
                Intent intent = new Intent(Friends.this, History.class);
                startActivity(intent);
            }
            if (id == R.id.settings) {
                Intent intent = new Intent(Friends.this, Settings.class);
                startActivity(intent);
            } else if (id == R.id.share) {
                String message = "Check out Champy - it helps you improve and compete with your friends!";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "How would you like to share?"));
            } else if (id == R.id.pending_duels) {
                Intent intent = new Intent(Friends.this, Pending_Duel.class);
                startActivity(intent);
            }
        }
        else Toast.makeText(this, "Lost internet connection!", Toast.LENGTH_LONG).show();
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
        OfflineMode offlineMode = new OfflineMode();
        if (!offlineMode.isInternetAvailable(this)){
            Intent intent = new Intent(Friends.this, MainActivity.class);
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
        OfflineMode offlineMode = new OfflineMode();
        if (!offlineMode.isInternetAvailable(this)){
            Toast.makeText(this, "Lost Internet Connection!!!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Friends.this, MainActivity.class);
            startActivity(intent);
        }
        ViewServer.get(this).setFocusedWindow(this);
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }


    public void getOther() {
        final String API_URL = "http://46.101.213.24:3007";
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String id = user.get("id");
        String token = user.get("token");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("pending", null, null);
        final ContentValues cv = new ContentValues();

        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);
        Call<com.example.ivan.champy_v2.model.Friend.Friend> call = friends.getUserFriends(id, token);
        call.enqueue(new Callback<Friend>() {
            @Override
            public void onResponse(Response<Friend> response, Retrofit retrofit) {
                if (response.isSuccess()){
                    List<Datum> data = response.body().getData();
                    for (int i=0; i<data.size(); i++){
                        Datum datum = data.get(i);
                        Log.d(TAG, "Status: "+response.body().toString());
                        if (datum.getStatus().toString().equals("false")) {
                            Owner owner = datum.getOwner();
                            cv.put("name", owner.getName());
                            Log.d(TAG, "Status: "+owner.getName());
                            cv.put("photo", owner.getPhoto().getMedium());
                            cv.put("user_id", owner.get_id());
                            db.insert("pending", null, cv);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


    public void UpdateFriendsList() {
        final String API_URL = "http://46.101.213.24:3007";
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = new HashMap<>();
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
                    final List<com.example.ivan.champy_v2.Friend> newfriends = new ArrayList<com.example.ivan.champy_v2.Friend>();
                    final RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
                    Cursor c = db.query("friends", null, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        int idColIndex = c.getColumnIndex("id");
                        int nameColIndex = c.getColumnIndex("name");
                        int photoColIndex = c.getColumnIndex("photo");
                        int index = c.getColumnIndex("user_id");
                        do {
                            Log.i("newusers", "NewUser: " + c.getString(nameColIndex) + " Photo: " + c.getString(photoColIndex));
                            newfriends.add(new com.example.ivan.champy_v2.Friend(c.getString(nameColIndex), API_URL+c.getString(photoColIndex), c.getString(index), "0", "0", "0" ,"0"));
                        } while (c.moveToNext());
                    } else
                        Log.i("stat", "0 0 0 0");
                    c.close();

                    Log.i("stat", "Friends :" + newfriends.toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final FriendsAdapter adapter = new FriendsAdapter(newfriends, getApplicationContext(), new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    com.example.ivan.champy_v2.Friend friend = newfriends.get(position);
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
                    Log.i(TAG, "Refresh Friends List");
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
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        final String id = user.get("id");
        String token = user.get("token");
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("pending", null, null);
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
                    final List<Pending_friend> newfriends = new ArrayList<Pending_friend>();
                    Cursor c = db.query("pending", null, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        int idColIndex = c.getColumnIndex("id");
                        int nameColIndex = c.getColumnIndex("name");
                        int photoColIndex = c.getColumnIndex("photo");
                        int index = c.getColumnIndex("user_id");
                        int owner = c.getColumnIndex("owner");
                        do {
                            Log.i("newusers", "NewUser: " + c.getString(nameColIndex) + " Photo: " + c.getString(photoColIndex));
                            newfriends.add(new Pending_friend(c.getString(nameColIndex), API_URL+c.getString(photoColIndex), c.getString(index), c.getString(owner)));
                        } while (c.moveToNext());
                    } else
                        Log.i("stat", "null rows");
                    c.close();

                    Log.i("stat", "Friends :" + newfriends.toString());


                    RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
                    final PendingAdapter adapter = new PendingAdapter(newfriends, Friends.this, new CustomItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Pending_friend friend = newfriends.get(position);
                        }
                    });
                    rvContacts.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }


}