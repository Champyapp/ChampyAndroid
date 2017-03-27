package com.azinecllc.champy.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.CreateChallengeAdapter;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.model.CardChallenges;
import com.azinecllc.champy.model.CreateChallengeModel;
import com.azinecllc.champy.model.self.Datum;
import com.azinecllc.champy.model.self.SelfImprovement;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;

/**
 * @autor SashaKhyzhun
 * Created on 3/21/17.
 */

public class CreateChallengeActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OfflineMode offlineMode;
    private ArrayList<CardChallenges> cardChallengeList;
    private CreateChallengeAdapter adapter;
    private SessionManager sessionManager;
    private List<CreateChallengeModel> challengeModelList;
    private RecyclerView rvChallenges;
    private SQLiteDatabase db;
    private Retrofit retrofit;
    private DBHelper dbHelper;
    private ContentValues cv;
    private View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge);

        offlineMode = OfflineMode.getInstance();
        sessionManager = SessionManager.getInstance(this);
        dbHelper = DBHelper.getInstance(this);
        db = dbHelper.getReadableDatabase();
        cv = new ContentValues();
        cardChallengeList = new ArrayList<CardChallenges>();


        challengeModelList = new ArrayList<>();
        Cursor c = db.query("selfimprovement", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex("name");
            int coldescription = c.getColumnIndex("description");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            int colduration = c.getColumnIndex("duration");
            int colstatus = c.getColumnIndex("status");
            try {
                do {
                    challengeModelList.add(new CreateChallengeModel(
                            c.getString(nameColIndex),
                            c.getString(coldescription),
                            c.getString(colchallenge_id),
                            c.getInt(colduration) / 24 / 60 / 60,
                            /*c.getInt(colStreak)*/ 4
                    ));
                } while (c.moveToNext());
            } finally {
                c.close();
            }
        }

        rvChallenges = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new CreateChallengeAdapter(challengeModelList, this);

        rvChallenges.setLayoutManager(new LinearLayoutManager(this));
        rvChallenges.setAdapter(adapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        mSwipeRefreshLayout.setEnabled(false);
        //mSwipeRefreshLayout.setOnRefreshListener(() -> refreshChallengesView(mSwipeRefreshLayout, mView));

//        RecyclerView rvCards = (RecyclerView) view.findViewById(R.id.recycler_view);
//        rvCards.setAdapter(adapter);
//        rvCards.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        mSwipeRefreshLayout.setOnRefreshListener(() -> {
//                Toast.makeText(getContext(), "Refresh", Toast.LENGTH_SHORT).show();
//        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    // get standard self-improvement challenges
    private void refreshChallengesView(SwipeRefreshLayout swipeRefreshLayout, View view) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        com.azinecllc.champy.interfaces.SelfImprovement selfImprovement = retrofit.create(com.azinecllc.champy.interfaces.SelfImprovement.class);
        Call<SelfImprovement> call = selfImprovement.getChallenges(sessionManager.getToken());
        call.enqueue(new retrofit.Callback<SelfImprovement>() {
            @Override
            public void onResponse(retrofit.Response<SelfImprovement> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    db.delete("selfimprovement", null, null);

                    List<Datum> data = response.body().getData();
                    //int data_size = 0;
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

                            System.out.println("datum.getDuration(): " + datum.getDuration());
                            int duration = datum.getDuration() / 24 / 60 / 60;
                            System.out.println("my duration: " + duration);
                            challengeModelList.add(new CreateChallengeModel(
                                    datum.getName(),
                                    datum.getDescription(),
                                    datum.get_id(),
                                    duration,
                                    /*datum.getStreak()*/ 4
                            ));

                            rvChallenges.setAdapter(adapter);
                            swipeRefreshLayout.setRefreshing(false);
                            //data_size++;
                        }
                    }
//                    sessionManager.setSelfSize(data_size);
//                    int size = sessionManager.getSelfSize();
//                    SelfImprovementPagerAdapter pagerAdapter = new SelfImprovementPagerAdapter(getSupportFragmentManager());
//                    ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
//                    pagerAdapter.setCount(size);
//                    viewPager.setAdapter(pagerAdapter);
//                    viewPager.setOffscreenPageLimit(1);
//                    viewPager.setPageMargin(20);
//                    viewPager.setClipToPadding(false);
//                    viewPager.setPadding(90, 0, 90, 0);
//                    spinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }
        });


    }

}
