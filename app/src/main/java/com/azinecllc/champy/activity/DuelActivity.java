package com.azinecllc.champy.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.DuelPagerAdapter;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.model.self.Datum;
import com.azinecllc.champy.model.self.SelfImprovement;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;
import static java.lang.Math.round;

public class DuelActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private View spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel);
        sessionManager = SessionManager.getInstance(getApplicationContext());

        spinner = findViewById(R.id.loadingPanel);
        spinner.setVisibility(View.VISIBLE);

        Bundle extras = getIntent().getExtras();
        String friendsPhoto, name;
        if (extras == null) {
            Uri uri = Uri.parse("android.resource://com.azinecllc.champy/drawable/ic_champy_circle");
            friendsPhoto = uri.toString();
            name = "Your friend";
        } else {
            friendsPhoto = extras.getString("photo");
            name = extras.getString("name");
        }

        int x = round(getWindowManager().getDefaultDisplay().getWidth() / 2);
        ImageView imageMyPhoto = (ImageView) findViewById(R.id.imageMyPhoto);
        ImageView imageFriendPhoto = (ImageView) findViewById(R.id.imageFriendsPhoto);
        imageMyPhoto.getLayoutParams().width = x;
        imageMyPhoto.getLayoutParams().height = x; // because we need a square
        imageFriendPhoto.getLayoutParams().width = x;
        imageFriendPhoto.getLayoutParams().height = x;


        Glide.with(this)
                .load(friendsPhoto)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .into(imageFriendPhoto);

        Glide.with(this)
                .load(sessionManager.getUserPicture())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .into(imageMyPhoto);

        TextView tvIChallengeMyFriendTo = (TextView) findViewById(R.id.tvIChallengeMyFriendTo);
        TextView textViewYouVsFriend = (TextView) findViewById(R.id.tvYouVsFriend);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");

        textViewYouVsFriend.setText(String.format("%s", getString(R.string.duel_with) + name));
        textViewYouVsFriend.setTypeface(typeface);
        tvIChallengeMyFriendTo.setTypeface(typeface);

        getChallenges();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // get standard cards for button_duel activity
    private void getChallenges() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        String token = sessionManager.getToken();

        com.azinecllc.champy.interfaces.SelfImprovement selfImprovement = retrofit.create(com.azinecllc.champy.interfaces.SelfImprovement.class);
        Call<com.azinecllc.champy.model.self.SelfImprovement> call = selfImprovement.getChallenges(token);
        call.enqueue(new Callback<SelfImprovement>() {
            @Override
            public void onResponse(Response<SelfImprovement> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    db.delete("duel", null, null);

                    List<Datum> data = response.body().getData();
                    int data_size = 0;
                    for (int i = 0; i < data.size(); i++) {
                        com.azinecllc.champy.model.self.Datum datum = data.get(i);
                        if (datum.getType().getName().equals("duel") && datum.getCreatedBy() == null) {

                            if (datum.getName().equals("Taking stares")) {
                                datum.setName("Taking Stairs");
                            }
                            if (datum.getName().equals("Reading a books")) {
                                datum.setName("Reading Books");
                            }

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



}
