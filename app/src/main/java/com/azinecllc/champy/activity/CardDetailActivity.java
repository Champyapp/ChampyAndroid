package com.azinecllc.champy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;

/**
 * @autor SashaKhyzhun
 * Created on 3/21/17.
 */

public class CardDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvCheckIn;
    private TextView tvSkipDay;
    private TextView tvChallengeDay;
    private TextView tvChallengRules;
    private TextView tvChallengeName;
    private TextView tvChallengeStreak;
    private TextView tvChallengeCompletion;

    private Switch switchReminder;

    private ImageView buttonBack;
    private ImageView buttonShare;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        tvCheckIn = (TextView) findViewById(R.id.text_view_check_in);
        tvSkipDay = (TextView) findViewById(R.id.text_view_skip_day);
        tvChallengeDay = (TextView) findViewById(R.id.text_view_day_n);
        tvChallengeName = (TextView) findViewById(R.id.text_view_challenge_name);
        tvChallengRules = (TextView) findViewById(R.id.text_view_challenge_rules);
        tvChallengeStreak = (TextView) findViewById(R.id.text_view_streak_n);
        tvChallengeCompletion = (TextView) findViewById(R.id.text_view_completion_n);

        switchReminder = (Switch) findViewById(R.id.switch_reminder);

        buttonBack = (ImageView) findViewById(R.id.image_view_back);
        buttonShare = (ImageView) findViewById(R.id.image_view_share);


        Bundle extras = getIntent().getExtras();
        String challengeName = extras.getString("mockName");
        String challengeStreak = extras.getString("mockStreak");
        String challengePercent = extras.getString("mockPercent");
        String challengeDay = extras.getString("mockDay");

        tvChallengeDay.setText(challengeDay);
        tvChallengeName.setText(challengeName);
        tvChallengeStreak.setText(challengeStreak);
        tvChallengeCompletion.setText(String.format("%s%%", challengePercent));

        buttonBack.setOnClickListener(this);
        buttonShare.setOnClickListener(this);
        tvCheckIn.setOnClickListener(this);
        tvSkipDay.setOnClickListener(this);
        tvChallengRules.setOnClickListener(this);

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
                break;
            case R.id.image_view_share:
                Toast.makeText(this, "Sharing...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.image_view_back:
                finish();
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

}
