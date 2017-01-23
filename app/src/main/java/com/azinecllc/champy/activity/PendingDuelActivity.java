package com.azinecllc.champy.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.PendingDuelsAdapter;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.utils.SessionManager;

public class PendingDuelActivity extends AppCompatActivity /*implements NavigationView.OnNavigationItemSelectedListener*/ {

    //    private DrawerLayout drawer;
    public View spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_duel);
        //SessionManager sessionManager = SessionManager.getInstance(getApplicationContext());
        new ProgressTask().execute();

        //spinner = findViewById(R.id.loadingPanel);
        //spinner.setVisibility(View.VISIBLE);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
//        navigationView.setNavigationItemSelectedListener(this);

        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        TextView tvPendingDuels = (TextView) findViewById(R.id.tvChallengeToMySelf);
        tvPendingDuels.setTypeface(typeface);

//        ImageView drawerImageProfile = (ImageView) headerLayout.findViewById(R.id.profile_image);
//        ImageView drawerBackground = (ImageView) headerLayout.findViewById(R.id.slide_background);
//        TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
//        drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//        File file = new File(path, "profile.jpg");
//        Uri url = Uri.fromFile(file);
//
//        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(this))
//                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerImageProfile);
//
//        file = new File(path, "blurred.png");
//        url = Uri.fromFile(file);
//        Glide.with(this).load(url).bitmapTransform(new CropSquareTransformation(this))
//                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerBackground);
//        String name = sessionManager.getUserName();
//        drawerUserName.setText(name);
//        drawerUserName.setTypeface(typeface);

        CHCheckPendingDuels checker = CHCheckPendingDuels.getInstance();
        int count = checker.getPendingCount(getApplicationContext());
//        if (count == 0) {
//            checker.hideItem(navigationView);
//        } else {
//            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
//            view.setText(String.format("%s%s", getString(R.string.plus), (count > 0 ? String.valueOf(count) : null)));
//        }

        ImageView imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);
        imageViewLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 1/20/17 Test code for search bug
                SessionManager s = SessionManager.getInstance(getApplicationContext());
                String uID = s.getUserId();
                String token = s.getToken();
                ChallengeController cc = new ChallengeController(getApplicationContext(), PendingDuelActivity.this, token, uID);
                cc.refreshCardsForPendingDuel(new Intent(getApplicationContext(), PendingDuelActivity.class));
            }
        });

        spinner.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
            Intent intent = new Intent(PendingDuelActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
//        }
    }


    private class ProgressTask extends AsyncTask<Void, Void, Void> {
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
                    SessionManager sessionManager = SessionManager.getInstance(getApplicationContext());
                    int size = Integer.parseInt(sessionManager.get_duel_pending());
                    PendingDuelsAdapter pagerAdapter = new PendingDuelsAdapter(getSupportFragmentManager());
                    ViewPager viewPager = (ViewPager) findViewById(R.id.pager_pending_duel);
                    pagerAdapter.setCount(size);
                    viewPager.setAdapter(pagerAdapter);
                    viewPager.setOffscreenPageLimit(1);
                    viewPager.setPageMargin(20);
                    viewPager.setClipToPadding(false);
                    viewPager.setPadding(90, 0, 90, 0);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.challenges:
//                Intent goToChallenges = new Intent(this, MainActivity.class);
//                startActivity(goToChallenges);
//                finish();
//                break;
//            case R.id.friends:
//                Intent goToFriends = new Intent(this, FriendsActivity.class);
//                startActivity(goToFriends);
//                finish();
//                break;
//            case R.id.history:
//                Intent goToHistory = new Intent(this, HistoryActivity.class);
//                startActivity(goToHistory);
//                finish();
//                break;
//            case R.id.settings:
//                Intent goToSettings = new Intent(this, SettingsActivity.class);
//                startActivity(goToSettings);
//                finish();
//                break;
//            case R.id.share:
//                String message = getString(R.string.share_text2);
//                Intent share = new Intent(Intent.ACTION_SEND);
//                share.setType("text/plain");
//                share.putExtra(Intent.EXTRA_TEXT, message);
//                startActivity(Intent.createChooser(share, getString(R.string.how_would_you_like_to_share)));
//                break;
//            case R.id.nav_logout:
//                OfflineMode offlineMode = OfflineMode.getInstance();
//                if (offlineMode.isConnectedToRemoteAPI(this)) {
//                    sessionManager.logout(this);
//                    finish();
//                }
//                break;
//        }
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

}
