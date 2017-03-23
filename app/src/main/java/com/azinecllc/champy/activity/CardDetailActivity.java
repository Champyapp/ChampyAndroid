package com.azinecllc.champy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;

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

    // Slider Layout
    //private View viewSliderStick;
    //private ImageView ivCircleDay;


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


    private String challengeDay;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        // Layout Top Buttons
        buttonBack = (ImageView) findViewById(R.id.image_view_back);
        buttonShare = (ImageView) findViewById(R.id.image_view_share);
        tvChallengeName = (TextView) findViewById(R.id.text_view_challenge_name);

        // Layout Statistics
        tvChallengeDayN = (TextView) findViewById(R.id.text_view_day_n);
        tvChallengeStreakN = (TextView) findViewById(R.id.text_view_streak_n);
        tvChallengeCompletionN = (TextView) findViewById(R.id.text_view_completion_n);

        // Layout Slider
        //viewSliderStick = findViewById(R.id.view_slider_stick);
        //ivCircleDay = (ImageView) findViewById(R.id.image_view_circle_day);


        // Views Below slider
        tvChallengeRules = (TextView) findViewById(R.id.text_view_challenge_rules);
        switchReminder = (Switch) findViewById(R.id.switch_reminder);

        // Layout bottom buttons
        tvCheckIn = (TextView) findViewById(R.id.text_view_check_in);
        tvSkipDay = (TextView) findViewById(R.id.text_view_skip_day);

        // Layout after click 'Check in'
        layoutGreatJob = (RelativeLayout) findViewById(R.id.layout_item_check_in);
        tvYouCompletedDayN = (TextView) findViewById(R.id.text_view_you_completed_day_n);
        tvOK = (TextView) findViewById(R.id.text_view_ok);
        tvShare = (TextView) findViewById(R.id.text_view_share);



        Bundle extras = getIntent().getExtras();
        challengeDay = String.valueOf(21); //extras.getString("mockDay");
        String challengeName = extras.getString("mockName");
        String challengeStreak = extras.getString("mockStreak");
        String challengePercent = extras.getString("mockPercent");

        tvChallengeDayN.setText(challengeDay);
        tvChallengeName.setText(challengeName);
        tvChallengeStreakN.setText(challengeStreak);
        tvChallengeCompletionN.setText(String.format("%s%%", challengePercent));

        tvOK.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        tvCheckIn.setOnClickListener(this);
        tvSkipDay.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        buttonShare.setOnClickListener(this);
        tvChallengeRules.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_view_challenge_rules:
                startActivity(new Intent(this, ChallengeRulesActivity.class));
                break;
            case R.id.text_view_skip_day:
                Toast.makeText(this, "Skip a Day", Toast.LENGTH_SHORT).show();
                break;
            case R.id.text_view_check_in:
                Toast.makeText(this, "Check In", Toast.LENGTH_SHORT).show();
                layoutGreatJob.setVisibility(View.VISIBLE);
                tvYouCompletedDayN.setText("You Completed Day " + challengeDay + " Of Your\nChallenge");
                disableClicks();
                break;
            case R.id.image_view_share:
                Toast.makeText(this, "Sharing...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.image_view_back:
                finish();
                break;
            case R.id.text_view_ok:
                Toast.makeText(this, "Ok...", Toast.LENGTH_SHORT).show();
                layoutGreatJob.setVisibility(View.INVISIBLE);
                enableClicks();
                break;
            case R.id.text_view_share:
                Toast.makeText(this, "Share...", Toast.LENGTH_SHORT).show();
                layoutGreatJob.setVisibility(View.INVISIBLE);
                enableClicks();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
        }

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


}
