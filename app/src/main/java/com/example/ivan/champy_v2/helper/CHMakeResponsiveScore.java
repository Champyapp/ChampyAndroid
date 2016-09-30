package com.example.ivan.champy_v2.helper;

import android.app.Activity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ivan.champy_v2.R;

import static java.lang.Math.round;

public class CHMakeResponsiveScore {


    Activity activity;


    CHMakeResponsiveScore(Activity activity) {
        this.activity = activity;
    }

    void makeResponsiveScore(int width) {
        int x = round(width/100);

        //-------------------------- Animation ---------------------------//
        ImageView imageView = (ImageView)activity.findViewById(R.id.imageView_challenges_animation);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;

        imageView = (ImageView)activity.findViewById(R.id.imageView_wins_animation);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;

        imageView = (ImageView)activity.findViewById(R.id.imageView_total_animation);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;

        //---------------------------- Logo -----------------------------//
        imageView = (ImageView)activity.findViewById(R.id.imageView_wins_logo);
        imageView.getLayoutParams().width = x*5;
        imageView.getLayoutParams().height = x*5;

        imageView = (ImageView)activity.findViewById(R.id.imageView_total_logo);
        imageView.getLayoutParams().width = x*5;
        imageView.getLayoutParams().height = x*5;

        imageView = (ImageView)activity.findViewById(R.id.imageView_challenges_logo);
        imageView.getLayoutParams().width = x*5;
        imageView.getLayoutParams().height = x*5;

        //---------------------------- Fab -----------------------------//
        ImageButton fab = (ImageButton)activity.findViewById(R.id.fabPlus);
        fab.getLayoutParams().width = x*20;
        fab.getLayoutParams().height = x*20;

        /*imageView = (ImageView)findViewById(R.id.profile_image);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;*/

        //--------------------------- Score ----------------------------//
        Float y = x*(float)3.5;

        TextView textViewScoreChallenges = (TextView)activity.findViewById(R.id.textViewChallengesCounter);
        textViewScoreChallenges.setTextSize(y);

        TextView textViewScoreWins = (TextView)activity.findViewById(R.id.textViewWinsCounter);
        textViewScoreWins.setTextSize(y);

        TextView textViewScoreTotal = (TextView)activity.findViewById(R.id.textViewTotalCounter);
        textViewScoreTotal.setTextSize(y);

        //------------------------- TextViews -------------------------//
        y = x*(float)1.5;
        TextView textViewChallenges = (TextView)activity.findViewById(R.id.textViewChallenges);
        TextView textViewWins = (TextView)activity.findViewById(R.id.textViewWins);
        TextView textViewTotal = (TextView)activity.findViewById(R.id.textViewTotal);
        textViewChallenges.setTextSize(y);
        textViewWins.setTextSize(y);
        textViewTotal.setTextSize(y);

    }



}
