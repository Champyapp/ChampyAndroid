package com.azinecllc.champy.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.SelfImprovementPagerAdapter;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.model.self.Datum;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;
import static com.azinecllc.champy.utils.Constants.path;

public class SelfImpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SessionManager sessionManager;
    private DrawerLayout drawer;
    public View spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_self_improvement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        sessionManager = SessionManager.getInstance(getApplicationContext());

        spinner = findViewById(R.id.loadingPanel);
        spinner.setVisibility(View.VISIBLE);

        //new getChallenges().execute();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        //Glide.with(this).load(R.drawable.ic_self_white).override(130, 130).into((ImageView) findViewById(R.id.imageViewLogo));
        //Glide.with(this).load(R.drawable.selfimprtext).override(280, 250).into((ImageView) findViewById(R.id.imageWakeUpChall));

        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        final TextView drawerUsername = (TextView) headerLayout.findViewById(R.id.tvUserName);

        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);
        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerImageProfile);

        File fileBlur = new File(path, "blurred.png");
        url = Uri.fromFile(fileBlur);
        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropSquareTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerBackground);

        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        final TextView tvIChallengeMySelfTo = (TextView)findViewById(R.id.tvChallengeToMySelf);
        final TextView tvSIC = (TextView) findViewById(R.id.tvSIC);
        tvIChallengeMySelfTo.setTypeface(typeface);
        tvSIC.setTypeface(typeface);
        String name = sessionManager.getUserName();
        drawerUsername.setText(name);
        drawerUsername.setTypeface(typeface);

        CHCheckPendingDuels checker = CHCheckPendingDuels.getInstance();
        int count = checker.getPendingCount(getApplicationContext());
        if (count == 0) {
            checker.hideItem(navigationView);
        } else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            view.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
        }

        getChallenges();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.isFinishing();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.challenges:
                Intent goToChallenges = new Intent(this, MainActivity.class);
                startActivity(goToChallenges);
                finish();
                break;
            case R.id.friends:
                Intent goToFriends = new Intent(this, FriendsActivity.class);
                startActivity(goToFriends);
                finish();
                break;
            case R.id.history:
                Intent goToHistory = new Intent(this, HistoryActivity.class);
                startActivity(goToHistory);
                finish();
                break;
            case R.id.pending_duels:
                Intent goToPendingDuel = new Intent(this, PendingDuelActivity.class);
                startActivity(goToPendingDuel);
                finish();
                break;
            case R.id.settings:
                Intent goToSettings = new Intent(this, SettingsActivity.class);
                startActivity(goToSettings);
                finish();
                break;
            case R.id.share:
                String message = getString(R.string.share_text2);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, getString(R.string.how_would_you_like_to_share)));
                break;
            case R.id.nav_logout:
                OfflineMode offlineMode = OfflineMode.getInstance();
                if (offlineMode.isConnectedToRemoteAPI(this)) {
                    sessionManager.logout(this);
                    finish();
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(SelfImpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }


    // get standard self-improvement challenges
    private void getChallenges() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv  = new ContentValues();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        int   clearCount        = db.delete("selfimprovement", null, null);

        com.azinecllc.champy.interfaces.SelfImprovement selfImprovement = retrofit.create(com.azinecllc.champy.interfaces.SelfImprovement.class);
        Call<com.azinecllc.champy.model.self.SelfImprovement> call = selfImprovement.getChallenges(sessionManager.getToken());
        call.enqueue(new Callback<com.azinecllc.champy.model.self.SelfImprovement>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.self.SelfImprovement> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    int data_size = 0;
                    for (int i = 0; i < data.size(); i++) {
                        com.azinecllc.champy.model.self.Datum datum = data.get(i);
                        String datumType = datum.getType().getName();
                        if (datumType.equals("self improvement")) {
                            if (!datum.getName().equals("User_Challenge")) {
                                cv.put("name", datum.getName());
                                cv.put("description", datum.getDescription());
                                cv.put("duration", datum.getDuration());
                                cv.put("challenge_id", datum.get_id());
                                db.insert("selfimprovement", null, cv);
                                data_size++;
                            }
                        }
                    }
                    sessionManager.setSelfSize(data_size);
                    int size = sessionManager.getSelfSize();
                    SelfImprovementPagerAdapter pagerAdapter = new SelfImprovementPagerAdapter(getSupportFragmentManager());
                    ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
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
            public void onFailure(Throwable t) { }
        });

    }


//    private class getChallenges extends AsyncTask<Void,Void,Void> {
//        @Override
//        protected Void doInBackground(Void... arg0) {
//            getChallenges();
//            return null;
//        }
//
//    }


}