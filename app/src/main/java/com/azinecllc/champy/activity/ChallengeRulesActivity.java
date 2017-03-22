package com.azinecllc.champy.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.azinecllc.champy.R;

/**
 * @autor SashaKhyzhun
 * Created on 3/22/17.
 */

public class ChallengeRulesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_rules);
        ImageView back = (ImageView) findViewById(R.id.image_view_back);
        back.setOnClickListener(v -> finish());
    }
}
