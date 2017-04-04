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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @autor SashaKhyzhun
 * Created on 3/28/17.
 */

public class ChallengeCreateDetailsActivity extends AppCompatActivity {

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

    @BindView(R.id.layout_challenge_created)
    View layoutCreated;
    @BindView(R.id.layout_create_challenge)
    RelativeLayout layoutCreateChallenge;
    @BindView(R.id.text_view_got_it)
    TextView tvGotIt;
    @BindView(R.id.text_view_share)
    TextView tvShare;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_create_detail);
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
        Intent intent = new Intent(this, ChallengeActivity.class);
        intent.putExtra("tag", "TAG_FRIENDS");
        intent.putExtra("index", 6);
        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.text_view_create_challenge)
    public void onClickCreateChallenge() {
        Toast.makeText(this, "Creating Challenge...", Toast.LENGTH_SHORT).show();
        disableChildClicks();
    }

    @OnClick(R.id.text_view_got_it)
    public void onClickGotIt() {
        Toast.makeText(this, "Got it...", Toast.LENGTH_SHORT).show();
        enableChildClicks();
        Intent intent = new Intent(this, ChallengeActivity.class);
        intent.putExtra("tag", "TAG_CHALLENGES");
        intent.putExtra("index", 0);
        startActivity(intent);
    }

    @OnClick(R.id.text_view_share)
    public void onClickShare() {
        Toast.makeText(this, "Share...", Toast.LENGTH_SHORT).show();
        enableChildClicks();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent == null) {
            System.out.println("data == null");
            return;
        }

        switch (resultCode) {
            case RESULT_OK:
                String name = intent.getStringExtra("name");
                tvChallengeAFriend.setText(name);
                break;
            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (layoutCreated.getVisibility() == View.VISIBLE) {
            enableChildClicks();
            return;
        }
        super.onBackPressed();
    }

    /**
     * Method to enable all childes onClickListeners into parent layout
     */
    private void enableChildClicks() {
        layoutCreateChallenge = (RelativeLayout) findViewById(R.id.layout_create_challenge);
        for (int i = 0; i < layoutCreateChallenge.getChildCount(); i++) {
            View child = layoutCreateChallenge.getChildAt(i);
            child.setEnabled(true);
        }

        layoutCreated.setVisibility(View.GONE);
    }


    /**
     * Method to disable all childes onClickListeners into parent layout
     */
    private void disableChildClicks() {
        layoutCreateChallenge = (RelativeLayout) findViewById(R.id.layout_create_challenge);
        for (int i = 0; i < layoutCreateChallenge.getChildCount(); i++) {
            View child = layoutCreateChallenge.getChildAt(i);
            child.setEnabled(false);
        }
        layoutCreated.setVisibility(View.VISIBLE);
    }


}
