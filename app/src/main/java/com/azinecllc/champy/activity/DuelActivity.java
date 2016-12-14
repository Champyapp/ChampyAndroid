package com.azinecllc.champy.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.DuelPagerAdapter;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.model.self.Datum;
import com.azinecllc.champy.model.self.SelfImprovement;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;

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
import static java.lang.Math.round;

public class DuelActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SessionManager sessionManager;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel);
        sessionManager = SessionManager.getInstance(getApplicationContext());

        spinner = findViewById(R.id.loadingPanel);
        spinner.setVisibility(View.VISIBLE);

        //new ProgressTask().execute();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final Bundle extras = getIntent().getExtras();
        String friendsPhoto, name;
        if(extras == null) {
            Uri uri = Uri.parse("android.resource://com.azinecllc.champy/drawable/icon_champy");
            friendsPhoto = uri.toString();
            name = "Your friend";
        } else {
            friendsPhoto = extras.getString("photo");
            name = extras.getString("name");
        }

        int x = round(getWindowManager().getDefaultDisplay().getWidth() / 2);
        final ImageView imageMyPhoto = (ImageView)findViewById(R.id.imageMyPhoto);
        imageMyPhoto.getLayoutParams().width = x;
        imageMyPhoto.getLayoutParams().height = x; // because we need a square

        File fileProfile = new File(path, "profile.jpg");
        File fileBlur = new File(path, "blured2.jpg");
        Uri uriProfile = Uri.fromFile(fileProfile);
        Uri uriBlur = Uri.fromFile(fileBlur);

        Glide.with(this)
                .load(friendsPhoto)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .into((ImageView)findViewById(R.id.imageFriendsPhoto));

        Glide.with(this)
                .load(uriProfile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageMyPhoto);

        final TextView tvIChallengeMyFriendTo = (TextView)findViewById(R.id.tvIChallengeMyFriendTo);
        final TextView textViewYouVsFriend = (TextView)findViewById(R.id.tvYouVsFriend);
        final Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");

        textViewYouVsFriend.setText(getString(R.string.duel_with) + name);
        textViewYouVsFriend.setTypeface(typeface);
        tvIChallengeMyFriendTo.setTypeface(typeface);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);


        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(this)
                .load(uriBlur)
                .bitmapTransform(new CropSquareTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerBackground);

        Glide.with(this)
                .load(uriProfile)
                .bitmapTransform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerImageProfile);

        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        name = sessionManager.getUserName();
        drawerUserName.setText(name);
        drawerUserName.setTypeface(typeface);


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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // get standard cards for duel activity
    private void getChallenges() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("duel", null, null);
        final ContentValues cv = new ContentValues();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        final String token = sessionManager.getToken();

        com.azinecllc.champy.interfaces.SelfImprovement selfImprovement = retrofit.create(com.azinecllc.champy.interfaces.SelfImprovement.class);

        Call<com.azinecllc.champy.model.self.SelfImprovement> call = selfImprovement.getChallenges(token);
        call.enqueue(new Callback<SelfImprovement>() {
            @Override
            public void onResponse(Response<SelfImprovement> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    int data_size = 0;
                    for (int i = 0; i < data.size(); i++) {
                        com.azinecllc.champy.model.self.Datum datum = data.get(i);
                        if (datum.getType().getName().equals("duel") && !datum.getName().equals("User_Challenge")) {
                            cv.put("name", datum.getName());
                            cv.put("description", datum.getDescription());
                            cv.put("duration", datum.getDuration());
                            cv.put("challenge_id", datum.get_id());
                            db.insert("duel", null, cv);
                            data_size++;
                        }
                    }
                    sessionManager.setSelfSize(data_size);
                    int size = sessionManager.getSelfSize();
                    DuelPagerAdapter pagerAdapter = new DuelPagerAdapter(getSupportFragmentManager());
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


//    private class ProgressTask extends AsyncTask<Void,Void,Void> {
//        @Override
//        protected Void doInBackground(Void... arg0) {
//            getChallenges();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//        }
//    }

}
