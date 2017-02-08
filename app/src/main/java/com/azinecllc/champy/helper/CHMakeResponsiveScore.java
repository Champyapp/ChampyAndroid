package com.azinecllc.champy.helper;

import android.app.Activity;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.azinecllc.champy.R;

import static java.lang.Math.round;

public class CHMakeResponsiveScore {

    private static CHMakeResponsiveScore instance = null;

    private CHMakeResponsiveScore() {}

    public static CHMakeResponsiveScore getInstance() {
        if (instance == null) {
            instance = new CHMakeResponsiveScore();
        }
        return instance;
    }


    public void makeResponsiveScore(Activity activity, int width) {
        int x = round(width/100);

        //-------------------------- Circles ---------------------------//
//        ImageView imageView = (ImageView)activity.findViewById(R.id.imageView_challenges_animation);
//        imageView.getLayoutParams().width  = x*25;
//        imageView.getLayoutParams().height = x*25;
//
//        imageView = (ImageView)activity.findViewById(R.id.imageView_wins_animation);
//        imageView.getLayoutParams().width  = x*25;
//        imageView.getLayoutParams().height = x*25;
//
//        imageView = (ImageView)activity.findViewById(R.id.imageView_total_animation);
//        imageView.getLayoutParams().width  = x*25;
//        imageView.getLayoutParams().height = x*25;
//
//        //---------------------------- Logo -----------------------------//
//        imageView = (ImageView)activity.findViewById(R.id.imageView_wins_logo);
//        imageView.getLayoutParams().width  = x*5;
//        imageView.getLayoutParams().height = x*5;
//
//        imageView = (ImageView)activity.findViewById(R.id.imageView_total_logo);
//        imageView.getLayoutParams().width  = x*5;
//        imageView.getLayoutParams().height = x*5;
//
//        imageView = (ImageView)activity.findViewById(R.id.imageView_challenges_logo);
//        imageView.getLayoutParams().width  = x*5;
//        imageView.getLayoutParams().height = x*5;
//
//        //---------------------------- Fab -----------------------------//
//        ImageButton fab = (ImageButton)activity.findViewById(R.id.fabPlus);
//        fab.getLayoutParams().width  = x*20;
//        fab.getLayoutParams().height = x*20;


    }



}
