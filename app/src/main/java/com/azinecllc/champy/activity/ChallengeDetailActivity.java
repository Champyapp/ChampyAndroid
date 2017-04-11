package com.azinecllc.champy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.StreakAdapter;
import com.azinecllc.champy.model.StreakSection;
import com.azinecllc.champy.model.StreakModel;
import com.azinecllc.champy.utils.CustomScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * @autor SashaKhyzhun
 * Created on 3/21/17.
 */

public class ChallengeDetailActivity extends AppCompatActivity {

    private RelativeLayout layoutCardDetails;
    private RelativeLayout layoutCheckedIn;
    private RelativeLayout layoutContext;
    private RecyclerView recyclerView;
    private CustomScrollView scrollView;
    private ImageView ivChallengeIcon;
    private TextView tvChallengeDayN;
    private TextView tvChallengeStreakN;
    private TextView tvChallengeCompletionN;
    private TextView tvChallengeRules;
    private TextView tvYouCompletedDayN;
    private TextView tvOK;
    private TextView tvShare;
    private Switch switchReminder;
    private Button buttonCheckIn;


    // Slider Layout
    private StreakAdapter mainAdapter;
    private List<StreakModel> streaksList;
    private List<StreakSection> streakSections;
    private String challengeDay;
    // true if we can scroll (not locked)
    // false if we cannot scroll (locked)
    private boolean mScrollable = true;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);
        initViewElements();

        Bundle extras = getIntent().getExtras();
        String challengeName = extras.getString("mockName");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_button_back);
        toolbar.setTitle(challengeName);
        setSupportActionBar(toolbar);

        challengeDay = extras.getString("mockDay"); //String.valueOf(21);
        String challengeStreak = extras.getString("mockStreak"); // "4";
        String challengePercent = extras.getString("mockPercent");



        // Layout Slider
        streaksList = new ArrayList<>();
        initStreak();
        // TODO: 4/10/17 change 'finished' to List<Data>.getStreakStatus().getDayCurrent();
//        initStreak(1, "Finished", 1, "Finished");
//        initStreak(2, "Finished", 3, "Finished");
//        initStreak(3, "In Progress", 7, "In Progress");
//        initStreak(4, "Pending", 7, "Pending");
        //initFirstStreak();
        //initSecondStreak();
        //initThirdStreak();
        //initFourthStreak();


//        initStreak(1, "Finished", 1);
//        initStreak(2, "In Progress", 4);
//        initStreak(3, "Pending", 7);
//        initStreak(4, "Pending", 11);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mainAdapter = new StreakAdapter(this, streaksList);
        recyclerView.setAdapter(mainAdapter);


        tvChallengeDayN.setText(challengeDay);
        tvChallengeStreakN.setText(challengeStreak);
        tvChallengeCompletionN.setText(String.format("%s%%", challengePercent));

    }

    private void initStreak(/*int currStreakN, String currStreakStatus, int daysInStreak, int startOf*/) {
        int countOfStreaks = 4;
        String[] statuses = {"Finished", "In Progress", "Pending", "Pending"};
        int[] daysInStreak = {1, 3, 7, 10};
        int[] startOf = {1, 2, 5, 12};
        //initStreak(i, statuses[i], daysInStreak[i], startOf[i]);


        for (int i = 0; i < countOfStreaks; i++) {


            StreakModel streak = new StreakModel();
            streak.setStreakLabel("Streak " + (i + 1)); // 1 | 2 | 3 | 4
            streak.setStreakStatus(statuses[i]); // Finished | In Progress | Pending

            ArrayList<StreakSection> streakSections = new ArrayList<StreakSection>();


            for (int j = startOf[i]; j < daysInStreak[i] + startOf[i]; j++) { // 1 | 2 3 4 | 5 6 7...
                StreakSection section = new StreakSection();
                section.setDayNumber(j);
                ///////////
                if (streak.getStreakStatus().equals("In Progress")) {
                    section.setCurrentDay(3);
                }
                //////////
                streakSections.add(section);
            }

            streak.setStreakSections(streakSections);
            streaksList.add(streak);

        }

    }


    private void onClickCheckIn() {
        Toast.makeText(this, "Check In", Toast.LENGTH_SHORT).show();
        tvYouCompletedDayN.setText("You Completed Day " + challengeDay + " Of Your\nChallenge");
        layoutCheckedIn.setVisibility(View.VISIBLE);
        disableClicks();
    }

    private void onClickShare() {
        Toast.makeText(this, "Share...", Toast.LENGTH_SHORT).show();
        enableClicks();

        Intent sendIntent2 = new Intent();
        sendIntent2.setAction(Intent.ACTION_SEND);
        sendIntent2.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent2.setType("text/plain");
        startActivity(sendIntent2);
    }

    private void onClickOK() {
        Toast.makeText(this, "Ok...", Toast.LENGTH_SHORT).show();
        enableClicks();
    }

    private void onClickRules() {
        Toast.makeText(this, "Challenge Rules...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ChallengeRulesActivity.class));
    }


    @Override
    public void onBackPressed() {
        if (layoutCheckedIn.getVisibility() == View.VISIBLE) {
            enableClicks();
            return;
        }
        super.onBackPressed();
    }

    /**
     * Method to disable all childes onClickListeners into parent layout
     */
    private void disableClicks() {
        layoutCheckedIn.setVisibility(View.VISIBLE);

//        recyclerView.setClickable(false);
//        recyclerView.setEnabled(false);
//        recyclerView.setScrollContainer(false);
//        recyclerView.setHorizontalFadingEdgeEnabled(false);
//        recyclerView.setVerticalFadingEdgeEnabled(false);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setEnabled(false);
        scrollView.setEnableScrolling(false);
        for (int i = 0; i < layoutContext.getChildCount(); i++) {
            View child = layoutContext.getChildAt(i);
            child.setEnabled(false);
        }
    }

    /**
     * Method to enable all childes onClickListeners into parent layout
     */
    private void enableClicks() {
        for (int i = 0; i < layoutContext.getChildCount(); i++) {
            View child = layoutContext.getChildAt(i);
            child.setEnabled(true);
        }
        scrollView.setEnableScrolling(true);
        layoutCheckedIn.setVisibility(View.GONE);
    }


//    private void initFirstStreak() {
//        StreakModel streak1 = new StreakModel();
//        streak1.setStreakLabel("Streak 1");
//        streak1.setStreakStatus("Finished");
//        streakSections = new ArrayList<StreakSection>();
//        //streakSections.(1);
//        streakSections.get(0).setDayNumber("1");
//        streak1.setStreakSections(streakSections);
//        streaksList.add(streak1);
//    }

//    private void initSecondStreak() {
//        StreakModel streak2 = new StreakModel();
//        streak2.setStreakLabel("Streak 2");
//        streak2.setStreakStatus("In Progress");
//        streakSections = new ArrayList<>();
//        streakSections.add(2);
//        streakSections.add(3);
//        streakSections.add(4);
//        streak2.setStreakSections(streakSections);
//        streaksList.add(streak2);
//    }
//
//    private void initThirdStreak() {
//        StreakModel streak3 = new StreakModel();
//        streak3.setStreakLabel("Streak 3");
//        streak3.setStreakStatus("Pending");
//        streakSections = new ArrayList<>();
//        streakSections.add(5);
//        streakSections.add(6);
//        streakSections.add(7);
//        streakSections.add(8);
//        streakSections.add(9);
//        streakSections.add(10);
//        streakSections.add(11);
//        streak3.setStreakSections(streakSections);
//        streaksList.add(streak3);
//    }
//
//    private void initFourthStreak() {
//        StreakModel streak4 = new StreakModel();
//        streak4.setStreakLabel("Streak 4");
//        streak4.setStreakStatus("Pending");
//        streakSections = new ArrayList<>();
//        streakSections.add(12);
//        streakSections.add(13);
//        streakSections.add(14);
//        streakSections.add(15);
//        streakSections.add(16);
//        streakSections.add(17);
//        streakSections.add(18);
//        streakSections.add(19);
//        streakSections.add(20);
//        streakSections.add(21);
//        streak4.setStreakSections(streakSections);
//        streaksList.add(streak4);
//    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (layoutCheckedIn.getVisibility() == View.VISIBLE) {
            return super.onOptionsItemSelected(item);
        }

        if (id == R.id.action_share) {
            Toast.makeText(this, "Sharing...", Toast.LENGTH_SHORT).show();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initViewElements() {
        layoutCardDetails = (RelativeLayout) findViewById(R.id.layout_card_detail);
        layoutCheckedIn = (RelativeLayout) findViewById(R.id.layout_check_in);
        layoutContext = (RelativeLayout) findViewById(R.id.layout_content);
        recyclerView = (RecyclerView) findViewById(R.id.rv_streaks);

        scrollView = (CustomScrollView) findViewById(R.id.scroll_view);
        switchReminder = (Switch) findViewById(R.id.switch_reminder);

        buttonCheckIn = (Button) findViewById(R.id.button_check_in);
        buttonCheckIn.setOnClickListener(v -> onClickCheckIn());

        ivChallengeIcon = (ImageView) findViewById(R.id.image_view_challenge_icon);

        tvOK = (TextView) findViewById(R.id.text_view_ok);
        tvOK.setOnClickListener(v -> onClickOK());

        tvShare = (TextView) findViewById(R.id.text_view_share);
        tvShare.setOnClickListener(v -> onClickShare());
        tvChallengeDayN = (TextView) findViewById(R.id.text_view_day_n);
        tvChallengeStreakN = (TextView) findViewById(R.id.text_view_streak_n);
        tvChallengeRules = (TextView) findViewById(R.id.text_view_challenge_rules);
        tvChallengeRules.setOnClickListener(v -> onClickRules());
        tvChallengeCompletionN = (TextView) findViewById(R.id.text_view_completion_n);
        tvYouCompletedDayN = (TextView) findViewById(R.id.text_view_you_completed_day_n);
    }




}
