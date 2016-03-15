package com.example.ivan.champy_v2;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.model.Self.Datum;

import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SelfImprovement extends AppCompatActivity {

    private static RecyclerView recyclerView;

    FragmentPagerAdapter adapterViewPager;


    final private String TAG = "myLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_improvment);

       /* TextView textView = (TextView)findViewById(R.id.textView19);*/
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        //textView.setTypeface(typeface);
        TextView textView = (TextView)findViewById(R.id.textView20);
        textView.setTypeface(typeface);

        Glide.with(this)
                .load(R.drawable.selfimprovementicon)
                .override(130, 130)
                .into((ImageView) findViewById(R.id.imageView13));

        Glide.with(this)
                .load(R.drawable.selfimprtext)
                .override(200, 170)
                .into((ImageView) findViewById(R.id.imageView12));


        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.selfimprovement);
        relativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.selfimprovementback));

        getChallenges();

    }

    public void getChallenges()
    {
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("selfimprovement", null, null);
        final ContentValues cv = new ContentValues();

        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        final SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        com.example.ivan.champy_v2.interfaces.SelfImprovement selfImprovement = retrofit.create(com.example.ivan.champy_v2.interfaces.SelfImprovement.class);
        Call<com.example.ivan.champy_v2.model.Self.SelfImprovement> call = selfImprovement.getChallenges(token);
        Log.i("stat", "Status: RUN");
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.Self.SelfImprovement>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.Self.SelfImprovement> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("stat", "Status: OK");
                    List<Datum> data = response.body().getData();
                    sessionManager.setSelfSize(data.size());
                    for (int i = 0; i < data.size(); i++) {
                        com.example.ivan.champy_v2.model.Self.Datum datum = data.get(i);
                        Log.i("stat", "Status: " + datum.getType().getName());
                        if (datum.getType().getName().equals("self improvement")) {
                            cv.put("name", datum.getName());
                            Log.i("stat", "Status: " + datum.getName());
                            cv.put("description", datum.getDescription());
                            cv.put("duration", datum.getDuration());
                            cv.put("challenge_id", datum.get_id());
                            db.insert("selfimprovement", null, cv);
                        }
                    }
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    int size = sessionManager.getSelfSize();
                    PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getApplicationContext());
                    pagerAdapter.setCount(size);
                    ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
                    viewPager.setAdapter(pagerAdapter);
                    viewPager.setOffscreenPageLimit(3);
                    viewPager.setPageMargin(20);
                    viewPager.setClipToPadding(false);
                    viewPager.setPadding(90, 0, 90, 0);
                } else Log.i("stat", "Status: WRONG");
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }





}
