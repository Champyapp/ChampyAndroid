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
import com.azinecllc.champy.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @autor SashaKhyzhun
 * Created on 3/28/17.
 */

public class CreateChallengeDetailsActivity extends AppCompatActivity {

    @BindView(R.id.text_view_cancel)
    TextView tvCancel;
    @BindView(R.id.text_view_challenge_name)
    TextView tvChallengeName;
    @BindView(R.id.text_view_challenge_rules)
    TextView tvChallengeRules;
    @BindView(R.id.text_view_create_challenge)
    TextView tvCreateChallenge;
    @BindView(R.id.text_view_challenge_a_friend)
    TextView tvChallengeAFriend;
    @BindView(R.id.text_view_challenge_description)
    TextView tvChallengeDescription;
    @BindView(R.id.image_view_back)
    ImageView ivBack;
    @BindView(R.id.image_view_challenge_icon)
    ImageView ivChallengeIcon;
    @BindView(R.id.switch_reminder)
    Switch switchDailyReminder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge_detail);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        String challengeName = extras.getString("name", "null");
        tvChallengeName.setText(challengeName);


    }

    @OnClick(R.id.text_view_cancel)
    public void onClickCancel(View view) {
        Toast.makeText(this, "Cancel Button", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.image_view_back)
    public void onClickBack() {
        finish();
    }

    @OnClick(R.id.text_view_challenge_rules)
    public void onClickChallengeRules() {
        startActivity(new Intent(this, ChallengeRulesActivity.class));
    }

    @OnClick(R.id.text_view_challenge_a_friend)
    public void onClickChallengeAFriend() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("tag", "friends");
        startActivityForResult(intent, 1);
//        Toast.makeText(this, "Challenge A Friends", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.text_view_create_challenge)
    public void onClickCreateChallenge() {
        Toast.makeText(this, "Create Challenge", Toast.LENGTH_SHORT).show();
    }


}
