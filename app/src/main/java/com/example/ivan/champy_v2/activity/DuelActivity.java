package com.example.ivan.champy_v2.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.adapter.PagerAdapterDuel;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.model.Self.Datum;
import com.example.ivan.champy_v2.model.Self.SelfImprovement;

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

import static java.lang.Math.round;

public class DuelActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public View spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel);
        new ProgressTask().execute();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String newString = "", name = "", friend_id = "";
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            newString = null;
        } else {
            newString = extras.getString("friend");
            name      = extras.getString("name");
            friend_id = extras.getString("id");
        }

        int x = round(getWindowManager().getDefaultDisplay().getWidth() / 2);

        TextView tvIChallengeMyFriendTo = (TextView)findViewById(R.id.tvIChallengeMyFriendTo);
        TextView textViewYouVsFriend = (TextView)findViewById(R.id.tvYouVsFriend);
        ImageView ivUser2 = (ImageView)findViewById(R.id.user2);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");

        textViewYouVsFriend.setText(getString(R.string.duel_with) + name);
        textViewYouVsFriend.setTypeface(typeface);
        tvIChallengeMyFriendTo.setTypeface(typeface);
        ivUser2.getLayoutParams().width = x;
        ivUser2.getLayoutParams().height = x; // because we need a square
        SessionManager sessionManager = new SessionManager(getApplicationContext());

        ////////////////////////////////////////////////////////////////////////////////////////////

        Glide.with(this).load(newString).centerCrop().into((ImageView)findViewById(R.id.user1));

        @SuppressLint("SdCardPath") String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";

        File file = new File(path, "profile.jpg");

        Uri url = Uri.fromFile(file);

        // i changed this for 'uri' for 'file';
        Glide.with(this).load(file).centerCrop().into((ImageView)findViewById(R.id.user2));

        ////////////////////////////////////////////////////////////////////////////////////////////


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        name = user.get("name");

        ImageView profile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        textViewYouVsFriend = (TextView) headerLayout.findViewById(R.id.tvUserName);
        textViewYouVsFriend.setText(name);

        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(profile);

        try {
            Drawable dr = Init(path);
            ImageView slideBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
            slideBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
            slideBackground.setImageDrawable(dr);
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getChallenges();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.challenges:
                Intent goToChallenges = new Intent(this, MainActivity.class);
                startActivity(goToChallenges);
                break;
            case R.id.friends:
                Intent goToFriends = new Intent(this, FriendsActivity.class);
                startActivity(goToFriends);
                break;
            case R.id.pending_duels:
                Intent goToPendingDuel = new Intent(this, PendingDuelActivity.class);
                startActivity(goToPendingDuel);
                break;
            case R.id.history:
                Intent goToHistory = new Intent(this, HistoryActivity.class);
                startActivity(goToHistory);
                break;
            case R.id.settings:
                Intent goToSettings = new Intent(this, SettingsActivity.class);
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

    // отображаем стандартные карточки в активити
    private void getChallenges() {
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("duel", null, null);
        final ContentValues cv = new ContentValues();
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        final SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");

        com.example.ivan.champy_v2.interfaces.SelfImprovement selfImprovement = retrofit.create(com.example.ivan.champy_v2.interfaces.SelfImprovement.class);

        Call<com.example.ivan.champy_v2.model.Self.SelfImprovement> call = selfImprovement.getChallenges(token);
        call.enqueue(new Callback<SelfImprovement>() {
            @Override
            public void onResponse(Response<SelfImprovement> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    int data_size = 0;
                    for (int i = 0; i < data.size(); i++) {
                        com.example.ivan.champy_v2.model.Self.Datum datum = data.get(i);
                        if (datum.getType().getName().equals("duel")) {
                            if (!datum.getName().equals("User_Challenge")) {
                                //if (check(datum.get_id())) {
                                cv.put("name", datum.getName());
                                cv.put("description", datum.getDescription());
                                cv.put("duration", datum.getDuration());
                                cv.put("challenge_id", datum.get_id());
                                db.insert("duel", null, cv);
                                data_size++;
//                                } else {
//                                    cv.put("name", "active");
//                                    cv.put("description", datum.getDescription());
//                                    cv.put("duration", datum.getDuration());
//                                    cv.put("challenge_id", datum.get_id());
//                                    db.insert("duel", null, cv);
//                                    data_size++;
//                                }
                            }
                        }
                    }
                    sessionManager.setSelfSize(data_size);
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    int size = sessionManager.getSelfSize();
                    PagerAdapterDuel pagerAdapter = new PagerAdapterDuel(getSupportFragmentManager());
                    final ViewPager viewPager = (ViewPager) findViewById(R.id.pager_duel);
                    pagerAdapter.setCount(size);
                    viewPager.setAdapter(pagerAdapter);
                    viewPager.setOffscreenPageLimit(1);
                    viewPager.setPageMargin(20);
                    viewPager.setClipToPadding(false);
                    viewPager.setPadding(90, 0, 90, 0);
                    spinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


    private Drawable Init(String path) throws FileNotFoundException {
        File file = new File(path, "blured2.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        Drawable dr = new BitmapDrawable(getResources(), bitmap);
        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);
        return dr;
    }


    private class ProgressTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            spinner = findViewById(R.id.loadingPanel);
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getChallenges();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

    }

}
