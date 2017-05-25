package com.azinecllc.champy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    @BindView(R.id.text_view_challenge_name)
    TextView tvChallengeName;
    @BindView(R.id.text_view_challenge_rules)
    TextView tvChallengeRules;
    @BindView(R.id.button_create_challenge)
    Button buttonCreateChallenge;
    @BindView(R.id.text_view_challenge_a_friend)
    TextView tvChallengeAFriend;
    @BindView(R.id.text_view_challenge_description)
    TextView tvChallengeDescription;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_button_back);
        toolbar.setTitle("New Challenge");
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        String challengeName = (extras != null) ? extras.getString("name", "") : "";
        tvChallengeName.setText(challengeName);


    }


    @OnClick(R.id.text_view_challenge_rules)
    public void onClickChallengeRules() {
        startActivity(new Intent(this, ChallengeRulesActivity.class));
    }

    @OnClick(R.id.text_view_challenge_a_friend)
    public void onClickChallengeAFriend() {
        Intent intent = new Intent(this, ChallengesActivity.class);
        intent.putExtra("tag", "TAG_FRIENDS");
        intent.putExtra("index", 6);
        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.button_create_challenge)
    public void onClickCreateChallenge() {
        Toast.makeText(this, "Creating Challenge...", Toast.LENGTH_SHORT).show();
        disableChildClicks();
    }

    @OnClick(R.id.text_view_got_it)
    public void onClickGotIt() {
        Toast.makeText(this, "Got it...", Toast.LENGTH_SHORT).show();
        enableChildClicks();
        Intent intent = new Intent(this, ChallengesActivity.class);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
