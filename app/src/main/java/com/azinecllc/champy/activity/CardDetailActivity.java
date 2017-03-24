package com.azinecllc.champy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.CardDetailAdapter;
import com.azinecllc.champy.model.SectionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @autor SashaKhyzhun
 * Created on 3/21/17.
 */

public class CardDetailActivity extends AppCompatActivity implements View.OnClickListener {

    // Layout Top Buttons
    private ImageView buttonBack;
    private ImageView buttonShare;
    private TextView tvChallengeName;
    // Challenge icon
    private ImageView ivChallenge;
    // Layout Statistics
    private TextView tvChallengeDayN;
    private TextView tvChallengeStreakN;
    private TextView tvChallengeCompletionN;
    // Views below slider
    private TextView tvChallengeRules;
    private Switch switchReminder;
    // Bottom Layout of buttons
    private TextView tvCheckIn;
    private TextView tvSkipDay;
    // Layout after click 'check in'
    private RelativeLayout layoutGreatJob;
    private TextView tvYouCompletedDayN;
    private TextView tvOK;
    private TextView tvShare;

    // Slider Layout
    private RecyclerView recyclerView;
    private CardDetailAdapter mainAdapter;
    private List<SectionModel> sections;
    private List<Integer> items;


    private String challengeDay;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

//        // Layout Top Buttons
//        buttonBack = (ImageView) findViewById(R.id.image_view_back);
//        buttonShare = (ImageView) findViewById(R.id.image_view_share);
//        tvChallengeName = (TextView) findViewById(R.id.text_view_challenge_name);
//
//        // Layout Statistics
//        tvChallengeDayN = (TextView) findViewById(R.id.text_view_day_n);
//        tvChallengeStreakN = (TextView) findViewById(R.id.text_view_streak_n);
//        tvChallengeCompletionN = (TextView) findViewById(R.id.text_view_completion_n);
//
//        // Layout Slider
        recyclerView = (RecyclerView) findViewById(R.id.main_rv);
        initItems();
        init();


//        // Views Below slider
//        tvChallengeRules = (TextView) findViewById(R.id.text_view_challenge_rules);
//        switchReminder = (Switch) findViewById(R.id.switch_reminder);
//
//        // Layout bottom buttons
//        tvCheckIn = (TextView) findViewById(R.id.text_view_check_in);
//        tvSkipDay = (TextView) findViewById(R.id.text_view_skip_day);
//
//        // Layout after click 'Check in'
//        layoutGreatJob = (RelativeLayout) findViewById(R.id.layout_item_check_in);
//        tvYouCompletedDayN = (TextView) findViewById(R.id.text_view_you_completed_day_n);
//        tvOK = (TextView) findViewById(R.id.text_view_ok);
//        tvShare = (TextView) findViewById(R.id.text_view_share);
//
//
//
//        Bundle extras = getIntent().getExtras();
//        challengeDay = String.valueOf(21); //extras.getString("mockDay");
//        String challengeName = extras.getString("mockName");
//        String challengeStreak = extras.getString("mockStreak");
//        String challengePercent = extras.getString("mockPercent");
//
//
//        tvChallengeDayN.setText(challengeDay);
//        tvChallengeName.setText(challengeName);
//        tvChallengeStreakN.setText(challengeStreak);
//        tvChallengeCompletionN.setText(String.format("%s%%", challengePercent));
//
//        tvOK.setOnClickListener(this);
//        tvShare.setOnClickListener(this);
//        tvCheckIn.setOnClickListener(this);
//        tvSkipDay.setOnClickListener(this);
//        buttonBack.setOnClickListener(this);
//        buttonShare.setOnClickListener(this);
//        tvChallengeRules.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.text_view_challenge_rules:
//                startActivity(new Intent(this, ChallengeRulesActivity.class));
//                break;
//            case R.id.text_view_skip_day:
//                Toast.makeText(this, "Skip a Day", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.text_view_check_in:
//                Toast.makeText(this, "Check In", Toast.LENGTH_SHORT).show();
//                layoutGreatJob.setVisibility(View.VISIBLE);
//                tvYouCompletedDayN.setText("You Completed Day " + challengeDay + " Of Your\nChallenge");
//                disableClicks();
//                break;
//            case R.id.image_view_share:
//                Toast.makeText(this, "Sharing...", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.image_view_back:
//                finish();
//                break;
//            case R.id.text_view_ok:
//                Toast.makeText(this, "Ok...", Toast.LENGTH_SHORT).show();
//                layoutGreatJob.setVisibility(View.INVISIBLE);
//                enableClicks();
//                break;
//            case R.id.text_view_share:
//                Toast.makeText(this, "Share...", Toast.LENGTH_SHORT).show();
//                layoutGreatJob.setVisibility(View.INVISIBLE);
//                enableClicks();
//
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
//                sendIntent.setType("text/plain");
//                startActivity(sendIntent);
//                break;
//        }

    }


    /**
     * Work around
     */
    private void disableClicks() {
        tvCheckIn.setClickable(false);
        tvSkipDay.setClickable(false);
        buttonBack.setClickable(false);
        buttonShare.setClickable(false);
        switchReminder.setClickable(false);
        tvChallengeRules.setClickable(false);
    }

    /**
     * Work around
     */
    private void enableClicks() {
        tvCheckIn.setClickable(true);
        tvSkipDay.setClickable(true);
        buttonBack.setClickable(true);
        buttonShare.setClickable(true);
        switchReminder.setClickable(true);
        tvChallengeRules.setClickable(true);
    }

    private void initItems() {
        SectionModel sectionModel1 = new SectionModel();
        sectionModel1.setLabel("Streak 1");
        items = new ArrayList<>();
        items.add(1);
        sectionModel1.setItems(items);
//        items.clear();

        SectionModel sectionModel2 = new SectionModel();
        sectionModel2.setLabel("Streak 2");
        items = new ArrayList<>();
        items.add(2);
        items.add(3);
        items.add(4);
        sectionModel2.setItems(items);
//        items.clear();

        SectionModel sectionModel3 = new SectionModel();
        sectionModel3.setLabel("Streak 3");
        items = new ArrayList<>();
        items.add(5);
        items.add(6);
        items.add(7);
        items.add(8);
        items.add(9);
        items.add(10);
        items.add(11);
        sectionModel3.setItems(items);
//        items.clear();

        SectionModel sectionModel4 = new SectionModel();
        sectionModel4.setLabel("Streak 4");
        items = new ArrayList<>();
        items.add(12);
        items.add(13);
        items.add(14);
        items.add(15);
        items.add(16);
        items.add(17);
        items.add(18);
        items.add(19);
        items.add(20);
        items.add(21);
        sectionModel4.setItems(items);
//        items.clear();

        sections = new ArrayList<>();
        sections.add(sectionModel1);
        sections.add(sectionModel2);
        sections.add(sectionModel3);
        sections.add(sectionModel4);
    }

    private void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        mainAdapter = new CardDetailAdapter(this, sections);
        recyclerView.setAdapter(mainAdapter);
    }



}
