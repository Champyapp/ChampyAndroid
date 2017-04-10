package com.azinecllc.champy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.StreakAdapter;
import com.azinecllc.champy.model.StreakModel;
import com.azinecllc.champy.utils.CustomScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private List<Integer> items;
    private String challengeDay;
    // true if we can scroll (not locked)
    // false if we cannot scroll (locked)
    private boolean mScrollable = true;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);
        initViews();

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
        initFirstStreak();
        initSecondStreak();
        initThirdStreak();
        initFourthStreak();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mainAdapter = new StreakAdapter(this, streaksList);
        recyclerView.setAdapter(mainAdapter);


        tvChallengeDayN.setText(challengeDay);
        tvChallengeStreakN.setText(challengeStreak);
        tvChallengeCompletionN.setText(String.format("%s%%", challengePercent));

    }


    @OnClick(R.id.button_check_in)
    public void onClickCheckIn() {
        Toast.makeText(this, "Check In", Toast.LENGTH_SHORT).show();
        tvYouCompletedDayN.setText("You Completed Day " + challengeDay + " Of Your\nChallenge");
        layoutCheckedIn.setVisibility(View.VISIBLE);
        disableClicks();
    }

    @OnClick(R.id.text_view_share)
    public void onClickShareCheckIn() {
        Toast.makeText(this, "Share...", Toast.LENGTH_SHORT).show();
        enableClicks();

        Intent sendIntent2 = new Intent();
        sendIntent2.setAction(Intent.ACTION_SEND);
        sendIntent2.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent2.setType("text/plain");
        startActivity(sendIntent2);
    }

    @OnClick(R.id.text_view_ok)
    public void onClickOK() {
        Toast.makeText(this, "Ok...", Toast.LENGTH_SHORT).show();
        enableClicks();
    }

    @OnClick(R.id.text_view_challenge_rules)
    public void onClickRules() {
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


    private void initFirstStreak() {
        StreakModel streak1 = new StreakModel();
        streak1.setLabel("Streak 1");
        streak1.setStatus("Finished");
        items = new ArrayList<>();
        items.add(1);
        streak1.setItems(items);
        streaksList.add(streak1);
    }

    private void initSecondStreak() {
        StreakModel streak2 = new StreakModel();
        streak2.setLabel("Streak 2");
        streak2.setStatus("In Progress");
        items = new ArrayList<>();
        items.add(2);
        items.add(3);
        items.add(4);
        streak2.setItems(items);
        streaksList.add(streak2);
    }

    private void initThirdStreak() {
        StreakModel streak3 = new StreakModel();
        streak3.setLabel("Streak 3");
        streak3.setStatus("Pending");
        items = new ArrayList<>();
        items.add(5);
        items.add(6);
        items.add(7);
        items.add(8);
        items.add(9);
        items.add(10);
        items.add(11);
        streak3.setItems(items);
        streaksList.add(streak3);
    }

    private void initFourthStreak() {
        StreakModel streak4 = new StreakModel();
        streak4.setLabel("Streak 4");
        streak4.setStatus("Pending");
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
        streak4.setItems(items);
        streaksList.add(streak4);
    }



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


    private void initViews() {
        layoutCardDetails = (RelativeLayout) findViewById(R.id.layout_card_detail);
        layoutCheckedIn = (RelativeLayout) findViewById(R.id.layout_check_in);
        layoutContext = (RelativeLayout) findViewById(R.id.layout_content);
        recyclerView = (RecyclerView) findViewById(R.id.rv_streaks);

        scrollView = (CustomScrollView) findViewById(R.id.scroll_view);
        switchReminder = (Switch) findViewById(R.id.switch_reminder);
        buttonCheckIn = (Button) findViewById(R.id.button_check_in);
        ivChallengeIcon = (ImageView) findViewById(R.id.image_view_challenge_icon);

        tvOK = (TextView) findViewById(R.id.text_view_ok);
        tvShare = (TextView) findViewById(R.id.text_view_share);
        tvChallengeDayN = (TextView) findViewById(R.id.text_view_day_n);
        tvChallengeStreakN = (TextView) findViewById(R.id.text_view_streak_n);
        tvChallengeRules = (TextView) findViewById(R.id.text_view_challenge_rules);
        tvChallengeCompletionN = (TextView) findViewById(R.id.text_view_completion_n);
        tvYouCompletedDayN = (TextView) findViewById(R.id.text_view_you_completed_day_n);
    }




}
