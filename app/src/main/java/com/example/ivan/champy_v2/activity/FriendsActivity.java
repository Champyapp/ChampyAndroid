package com.example.ivan.champy_v2.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.adapter.FriendsActivityPagerAdapter;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHCheckTableForExist;
import com.example.ivan.champy_v2.helper.CHLoadBlurredPhoto;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.io.File;
import java.io.FileNotFoundException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class FriendsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "FriendsActivity";

    private CHCheckTableForExist checkTableForExist;
    private NavigationView navigationView;
    private SessionManager sessionManager;
    private OfflineMode offlineMode;
    private TabLayout tabLayout;
    private Typeface typeface;
    private int[] tabIcons = {
            R.drawable.ic_tab_friends,
            R.drawable.ic_tab_pending,
            R.drawable.ic_tab_others
    };


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        FriendsActivityPagerAdapter adapterViewPager = new FriendsActivityPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(adapterViewPager);

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.setRefreshFriends("true");
        sessionManager.setRefreshPending("true");


        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null) {
            String friendRequest = bundle.getString("friend_request");
            if (friendRequest != null) {
                switch (friendRequest) {
                    case "friend_request_confirmed": viewPager.setCurrentItem(0); break;
                    case "incoming_friend_request": viewPager.setCurrentItem(1); break;
                    case "friend_request_removed": viewPager.setCurrentItem(2); break;
                    default: viewPager.setCurrentItem(0);
                }
            }
        }

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        //setupCustomTabIcons();

        CurrentUserHelper user = new CurrentUserHelper(getApplicationContext());
        String name = user.getName();

        typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        ImageView background = (ImageView) findViewById(R.id.friends_background);
        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        drawerUserName.setText(name);
        drawerUserName.setTypeface(typeface);

        @SuppressLint("SdCardPath")
        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);

        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerImageProfile);

        try {
            background.setScaleType(ImageView.ScaleType.CENTER_CROP);
            background.setImageDrawable(CHLoadBlurredPhoto.Init(path));
            drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
            drawerBackground.setImageDrawable(CHLoadBlurredPhoto.Init(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
        int count = checker.getPendingCount();

        if (count == 0) {
            checker.hideItem();
        } else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        }

//        CHGetFacebookFriends getFacebookFriends = new CHGetFacebookFriends(getApplicationContext());
//        getFacebookFriends.getUserFacebookFriends(gcm);


//        CHGetFacebookFriends getFbFriends = new CHGetFacebookFriends(getApplicationContext());
//        getFbFriends.getUserFacebookFriends(user.getToken());

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                loadUserFriends();
//                loadUserPending();
//        });
//            }
        ViewServer.get(this).addWindow(this);
    }


    @Override
    public void onBackPressed()  {
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
                String message = getString(R.string.share_text2);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, getString(R.string.how_would_you_like_to_share)));
                break;
            case R.id.nav_logout:
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


//    private void setupCustomTabIcons() {
//        TextView tabFriends = (TextView) LayoutInflater.from(this).inflate(R.layout.item_custom_tab, null);
//        tabFriends.setText("Friends"); //tab label txt
//        tabFriends.setTextColor(Color.WHITE);
//        tabFriends.setTypeface(typeface);
//        tabFriends.setCompoundDrawablesWithIntrinsicBounds(tabIcons[0], 0, 0, 0);
//        tabFriends.setCompoundDrawablePadding(5);
//        tabLayout.getTabAt(0).setCustomView(tabFriends);
//
//        TextView tabPending = (TextView) LayoutInflater.from(this).inflate(R.layout.item_custom_tab, null);
//        tabPending.setText("Pending");
//        tabPending.setTextColor(Color.WHITE);
//        tabPending.setTypeface(typeface);
//        tabPending.setCompoundDrawablesWithIntrinsicBounds(tabIcons[1], 0, 0, 0);
//        tabLayout.getTabAt(1).setCustomView(tabPending);
//
//        TextView tabOthers = (TextView) LayoutInflater.from(this).inflate(R.layout.item_custom_tab, null);
//        tabOthers.setText("Other");
//        tabOthers.setTextColor(Color.WHITE);
//        tabOthers.setTypeface(typeface);
//        tabOthers.setCompoundDrawablesWithIntrinsicBounds(tabIcons[2], 0, 0, 0);
//        tabLayout.getTabAt(2).setCustomView(tabOthers);
//
//    }
//
//
//    // load friends from bd
//    public void loadUserFriends() {
//        //final String API_URL = "http://46.101.213.24:3007";
//        SessionManager sessionManager = new SessionManager(getApplicationContext());
//        HashMap<String, String> user;
//        user = sessionManager.getUserDetails();
//        final String id = user.get("id");
//        String token = user.get("token");
//        final Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
//        DBHelper dbHelper = new DBHelper(getApplicationContext());
//        final SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.delete("friends", null, null);
//        final ContentValues cv = new ContentValues();
//
//        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);
//        Call<Friend> call = friends.getUserFriends(id, token);
//        call.enqueue(new Callback<Friend>() {
//            @Override
//            public void onResponse(Response<Friend> response, Retrofit retrofit) {
//                if (response.isSuccess()) {
//                    List<Datum> data = response.body().getData();
//                    for (int i = 0; i < data.size(); i++) {
//                        Datum datum = data.get(i);
//                        if (datum.getFriend() != null) {
//
//                            if (datum.getStatus().toString().equals("true")) {
//
//                                if (datum.getOwner().get_id().equals(id)) {
//                                    Friend_ friend = datum.getFriend();
//                                    cv.put("name", friend.getName());
//                                    if (friend.getPhoto() != null)
//                                         cv.put("photo", friend.getPhoto().getMedium());
//                                    else cv.put("photo", "");
//                                    cv.put("user_id", friend.getId());
//                                    db.insert("friends", null, cv);
//                                } else {
//                                    Owner friend = datum.getOwner();
//                                    cv.put("name", friend.getName());
//                                    if (friend.getPhoto() != null)
//                                         cv.put("photo", friend.getPhoto().getMedium());
//                                    else cv.put("photo", "");
//                                    cv.put("user_id", friend.get_id());
//                                    db.insert("friends", null, cv);
//                                }
//                            }
//                        }
//                    }
//                    final List<com.example.ivan.champy_v2.model.FriendModel> newfriends = new ArrayList<>();
//                    final RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
//                    Cursor c = db.query("friends", null, null, null, null, null, null);
//                    if (c.moveToFirst()) {
//                        //int idColIndex = c.getColumnIndex("id");
//                        int nameColIndex = c.getColumnIndex("name");
//                        int photoColIndex = c.getColumnIndex("photo");
//                        int inProgressChallengesCountIndex = c.getColumnIndex("inProgressChallengesCount");
//                        int successChallenges = c.getColumnIndex("successChallenges");
//                        int allChallengesCount = c.getColumnIndex("allChallengesCount");
//                        int level = c.getColumnIndex("level");
//                        int index = c.getColumnIndex("user_id");
//                        do {
//                            Log.d("newusers", "NewUser: " + c.getString(nameColIndex)
//                                    + " i: " + c.getString(inProgressChallengesCountIndex)
//                                    + " w: " + c.getString(successChallenges)
//                                    + " t: " + c.getString(allChallengesCount)
//                                    + " Photo: " + c.getString(photoColIndex));
//                            newfriends.add(new com.example.ivan.champy_v2.model.FriendModel(
//                                    c.getString(nameColIndex),
//                                    Constants.API_URL + c.getString(photoColIndex),
//                                    c.getString(index),
//                                    c.getString(inProgressChallengesCountIndex),
//                                    c.getString(successChallenges),
//                                    c.getString(allChallengesCount),
//                                    "0"));
//                        } while (c.moveToNext());
//                    }
//                    c.close();
//
//                    Log.d(TAG, "Displayed friends: " + newfriends.toString());
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            final FriendsAdapter adapter = new FriendsAdapter(newfriends, getApplicationContext(), FriendsActivity.this, new CustomItemClickListener() {
//                                @Override
//                                public void onItemClick(View view, int position) {
//                                    com.example.ivan.champy_v2.model.FriendModel friend = newfriends.get(position);
//                                    friend.getmChallenges();
//                                    friend.getmTotal();
//                                    friend.getmWins();
//                                }
//                            });
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    rvContacts.setAdapter(adapter);
//                                    adapter.notifyDataSetChanged();
//                                }
//                            });
//                        }
//                    }).start();
//                    Log.d(TAG, "loadUserFriends: finished");
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) { }
//        });
//    }
//
//    // load pending from bd
//    public void loadUserPending() {
//        HashMap<String, String> user;
//        user = sessionManager.getUserDetails();
//        final String id = user.get("id");
//        String token = user.get("token");
//        final Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
//        DBHelper dbHelper = new DBHelper(this);
//        final SQLiteDatabase db = dbHelper.getWritableDatabase();
//        //int clearCount = db.delete("pending", null, null);
//        final ContentValues cv = new ContentValues();
//
//        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);
//        Call<com.example.ivan.champy_v2.model.friend.Friend> call = friends.getUserFriends(id, token);
//        call.enqueue(new Callback<com.example.ivan.champy_v2.model.friend.Friend>() {
//            @Override
//            public void onResponse(Response<com.example.ivan.champy_v2.model.friend.Friend> response, Retrofit retrofit) {
//                if (response.isSuccess()) {
//                    List<Datum> data = response.body().getData();
//                    for (int i = 0; i < data.size(); i++) {
//                        Datum datum = data.get(i);
//                        if ((datum.getFriend() != null) && (datum.getOwner() != null )) {
//                            if (datum.getStatus().toString().equals("false")) {
//                                if (datum.getOwner().get_id().equals(id)) {
//                                    Friend_ friend = datum.getFriend();
//                                    cv.put("name", friend.getName());
//                                    if (friend.getPhoto() != null)
//                                         cv.put("photo", friend.getPhoto().getMedium());
//                                    else cv.put("photo", "");
//                                    cv.put("user_id", friend.getId());
//                                    cv.put("owner", "false");
//                                    db.insert("pending", null, cv);
//                                } else {
//                                    Owner friend = datum.getOwner();
//                                    cv.put("name", friend.getName());
//                                    if (friend.getPhoto() != null)
//                                         cv.put("photo", friend.getPhoto().getMedium());
//                                    else cv.put("photo", "");
//                                    cv.put("user_id", friend.get_id());
//                                    cv.put("owner", "true");
//                                    db.insert("pending", null, cv);
//                                }
//                            }
//                        }
//                    }
//                    final List<Pending_friend> pendingFriends = new ArrayList<>();
//                    Cursor c = db.query("pending", null, null, null, null, null, null);
//                    if (c.moveToFirst()) {
//                        int nameColIndex = c.getColumnIndex("name");
//                        int photoColIndex = c.getColumnIndex("photo");
//                        int index = c.getColumnIndex("user_id");
//                        int owner = c.getColumnIndex("owner");
//                        int inProgressChallengesCountIndex = c.getColumnIndex("inProgressChallengesCount");
//                        int successChallenges = c.getColumnIndex("successChallenges");
//                        int allChallengesCount = c.getColumnIndex("allChallengesCount");
//                        do {
//                            pendingFriends.add(new Pending_friend(
//                                    c.getString(nameColIndex),
//                                    Constants.API_URL + c.getString(photoColIndex),
//                                    c.getString(index),
//                                    c.getString(owner),
//                                    c.getString(successChallenges),
//                                    c.getString(allChallengesCount),
//                                    c.getString(inProgressChallengesCountIndex)
//                            ));
//                        } while (c.moveToNext());
//                    }
//                    c.close();
//
//                    RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
//                    final PendingAdapter adapter = new PendingAdapter(pendingFriends, FriendsActivity.this, FriendsActivity.this, new CustomItemClickListener() {
//                        @Override
//                        public void onItemClick(View view, int position) {
//                            Pending_friend friend = pendingFriends.get(position);
//                        }
//                    });
//                    rvContacts.setAdapter(adapter);
//                }
//                Log.i(TAG, "loadUserPending: finished");
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//            }
//        });
//
//    }

    // load others from bd
//    public void loadUserOthers() {
//        // Проверка на оффлайн вкладке OTHERS
//        if (offlineMode.isConnectedToRemoteAPI(this)) {
//            swipeRefreshLayout.setRefreshing(true);
//            swipeRefreshLayout.post(new Runnable() {
//                @Override
//                public void run() {
//                    final Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
//                    final NewUser newUser = retrofit.create(NewUser.class);
//                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                    StrictMode.setThreadPolicy(policy);
//                    DBHelper dbHelper = new DBHelper(getApplicationContext());
//                    final SQLiteDatabase db = dbHelper.getWritableDatabase();
//                    int clearCount = db.delete("mytable", null, null);
//                    final ContentValues cv = new ContentValues();
//                    final List<FriendModel> newFriends = new ArrayList<>();
//
//                    if (offlineMode.isConnectedToRemoteAPI(FriendsActivity.this)) {
//                        final GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
//                            @Override
//                            public void onCompleted(JSONArray array, GraphResponse response) {
//                                if (array.length() == 0) {
//                                    Toast.makeText(getApplicationContext(), R.string.noOneHasInstalledChampy, Toast.LENGTH_SHORT).show();
//                                    swipeRefreshLayout.setRefreshing(false);
//                                    return;
//                                }
//                                for (int i = 0; i < array.length(); i++) {
//                                    try {
//                                        // jwt - Json Web Token...
//                                        final String fb_id = array.getJSONObject(i).getString("id");
//                                        final String user_name = array.getJSONObject(i).getString("name");
//                                        final String jwtString = Jwts.builder()
//                                                .setHeaderParam("alg", "HS256")
//                                                .setHeaderParam("typ", "JWT")
//                                                .setPayload("{\n"+"  \"facebookId\": \"" + fb_id + "\"\n" + "}")
//                                                .signWith(SignatureAlgorithm.HS256, "secret")
//                                                .compact();
//
//                                        Call<User> call = newUser.getUserInfo(jwtString);
//                                        call.enqueue(new Callback<User>() {
//                                            @Override
//                                            public void onResponse(Response<User> response, Retrofit retrofit) {
//                                                if (response.isSuccess()) {
//                                                    Data data = response.body().getData();
//                                                    String photo = null;
//
//                                                    if (data.getPhoto() != null) {
//                                                        photo = Constants.API_URL + data.getPhoto().getMedium();
//                                                    }
//                                                    else {
//                                                        try {
//                                                            URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
//                                                            photo = profile_pic.toString();
//                                                        } catch (Exception e) {
//                                                            e.printStackTrace();
//                                                        }
//                                                    }
//
//                                                    String name = data.getName();
//                                                    cv.put("user_id", data.get_id());
//                                                    cv.put("name", name);
//                                                    cv.put("photo", photo);
//                                                    cv.put("challenges", "" + data.getAllChallengesCount());
//                                                    cv.put("wins", "" + data.getSuccessChallenges());
//                                                    cv.put("total", "" + data.getInProgressChallenges());
//                                                    cv.put("level", "" + data.getLevel().getNumber());
//
//                                                    // отображаем друзей в списке
//                                                    if (!checkTableForExist.isInOtherTable(data.get_id())) {
//                                                        db.insert("mytable", null, cv);
//                                                        newFriends.add(new FriendModel(
//                                                                name,
//                                                                photo,
//                                                                data.get_id(),
//                                                                "" + data.getAllChallengesCount(),
//                                                                "" + data.getSuccessChallenges(),
//                                                                "" + data.getInProgressChallenges(),
//                                                                "" + data.getLevel().getNumber()
//                                                        ));
//                                                    } else {
//                                                        Log.d(TAG, "DBase: not added | " + user_name + " in another table");
//                                                    }
//                                                    swipeRefreshLayout.setRefreshing(false);
//                                                }
////                                            else {
////                                                // отображение всего у человека, который не установил champy
////                                                URL profile_pic = null;
////                                                String photo = null;
////                                                try {
////                                                    profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
////                                                    photo = profile_pic.toString();
////                                                } catch (MalformedURLException e) {
////                                                    e.printStackTrace();
////                                                }
////                                                cv.put("name", user_name);
////                                                cv.put("photo", photo);
////                                                cv.put("challenges", "0");
////                                                cv.put("wins", "0");
////                                                cv.put("total", "0");
////                                                cv.put("level", "0");
////                                                newFriends.add(new FriendModel(user_name, photo, null, "0", "0", "0", "0"));
////                                                db.insert("mytable", null, cv);
////
////                                                RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
////                                                OtherAdapter adapter1 = new OtherAdapter(newFriends, getContext(), getActivity());
////                                                rvContacts.setAdapter(adapter1);
////                                                gSwipeRefreshLayout.setRefreshing(false);
////                                            }
//
//                                                RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
//                                                OtherAdapter otherAdapter = new OtherAdapter(newFriends, getApplicationContext(), FriendsActivity.this);
//                                                rvContacts.setAdapter(otherAdapter);
//                                                gSwipeRefreshLayout.setRefreshing(false);
//                                            }
//
//                                            @Override
//                                            public void onFailure(Throwable t) {
//
//                                            }
//                                        });
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                }
//                            }
//                        });
//                        request.executeAsync();
//                    } else {
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//
//                }
//            });
//            Log.d(TAG, "refreshOtherView: finished");
//        }
//
////    public static OtherFragment newInstance(int page) {
////        Bundle args = new Bundle();
////        args.putInt(ARG_PAGE, page);
////        OtherFragment fragment = new OtherFragment();
////        fragment.setArguments(args);
////        return fragment;
////    }
//
//    }


}