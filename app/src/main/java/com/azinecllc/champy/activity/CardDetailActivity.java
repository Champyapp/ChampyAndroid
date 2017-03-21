package com.azinecllc.champy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.azinecllc.champy.R;

/**
 * @autor SashaKhyzhun
 * Created on 3/21/17.
 */

public class CardDetailActivity extends AppCompatActivity {

    private TextView tvCheckIn;
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

        tvChallengeName.setText(challengeName);

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
