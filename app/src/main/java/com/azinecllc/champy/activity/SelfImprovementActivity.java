package com.azinecllc.champy.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.SelfImprovementPagerAdapter;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.model.self.Datum;
import com.azinecllc.champy.utils.SessionManager;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;

public class SelfImprovementActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    public View spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_improvement);
        sessionManager = SessionManager.getInstance(getApplicationContext());

        spinner = findViewById(R.id.loadingPanel);
        spinner.setVisibility(View.VISIBLE);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
        final TextView tvIChallengeMySelfTo = (TextView) findViewById(R.id.tvChallengeToMySelf);
        final TextView tvSIC = (TextView) findViewById(R.id.tvSIC);
        tvSIC.setTypeface(typeface);
        tvIChallengeMySelfTo.setTypeface(typeface);

        getSelfChallenges();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.isFinishing();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    // get standard self-improvement challenges
    private void getSelfChallenges() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv  = new ContentValues();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        com.azinecllc.champy.interfaces.SelfImprovement selfImprovement = retrofit.create(com.azinecllc.champy.interfaces.SelfImprovement.class);
        Call<com.azinecllc.champy.model.self.SelfImprovement> call = selfImprovement.getChallenges(sessionManager.getToken());
        call.enqueue(new Callback<com.azinecllc.champy.model.self.SelfImprovement>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.self.SelfImprovement> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    db.delete("selfimprovement", null, null);

                    List<Datum> data = response.body().getData();
                    int data_size = 0;
                    for (int i = 0; i < data.size(); i++) {
                        com.azinecllc.champy.model.self.Datum datum = data.get(i);
                        String datumType = datum.getType().getName();


                        if (datumType.equals("self improvement") && datum.getCreatedBy() == null) {

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
                            db.insert("selfimprovement", null, cv);
                            data_size++;
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



}
