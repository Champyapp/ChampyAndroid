package com.azinecllc.champy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private TextView tvOK;
    private TextView tvShare;
    private TextView tvCheckIn;
    private TextView tvSkipDay;
    private TextView tvChallengeDay;
    private TextView tvChallengeRules;
    private TextView tvChallengeName;
    private TextView tvChallengeStreak;
    private TextView tvYouCompletedDayN;
    private TextView tvChallengeCompletion;

    private Switch switchReminder;

    private ImageView buttonBack;
    private ImageView buttonShare;

    private RelativeLayout layoutGreatJob;

    private String challengeDay;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        tvOK = (TextView) findViewById(R.id.text_view_ok);
        tvShare = (TextView) findViewById(R.id.text_view_share);
        tvCheckIn = (TextView) findViewById(R.id.text_view_check_in);
        tvSkipDay = (TextView) findViewById(R.id.text_view_skip_day);
        tvChallengeDay = (TextView) findViewById(R.id.text_view_day_n);
        tvChallengeName = (TextView) findViewById(R.id.text_view_challenge_name);
        tvChallengeRules = (TextView) findViewById(R.id.text_view_challenge_rules);
        tvChallengeStreak = (TextView) findViewById(R.id.text_view_streak_n);
        tvYouCompletedDayN = (TextView) findViewById(R.id.text_view_you_completed_day_n);
        tvChallengeCompletion = (TextView) findViewById(R.id.text_view_completion_n);

        switchReminder = (Switch) findViewById(R.id.switch_reminder);

        buttonBack = (ImageView) findViewById(R.id.image_view_back);
        buttonShare = (ImageView) findViewById(R.id.image_view_share);

        layoutGreatJob = (RelativeLayout) findViewById(R.id.layout_item_check_in);


        Bundle extras = getIntent().getExtras();
        challengeDay = extras.getString("mockDay");
        String challengeName = extras.getString("mockName");
        String challengeStreak = extras.getString("mockStreak");
        String challengePercent = extras.getString("mockPercent");

        tvChallengeDay.setText(challengeDay);
        tvChallengeName.setText(challengeName);
        tvChallengeStreak.setText(challengeStreak);
        tvChallengeCompletion.setText(String.format("%s%%", challengePercent));

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
