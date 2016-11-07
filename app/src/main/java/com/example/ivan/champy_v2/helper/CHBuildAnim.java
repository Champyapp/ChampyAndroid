package com.example.ivan.champy_v2.helper;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.utils.SessionManager;

/**
 * this is class helper for create animation, set right size of circles, count, alpha of animation,
 * numbers animation and other operation with score in main activity
 */
public class CHBuildAnim {


    public void buildAnim(Activity activity) {
//        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
//        CHMakeResponsiveScore chMakeResponsiveScore = new CHMakeResponsiveScore(activity);
//        chMakeResponsiveScore.makeResponsiveScore(width);

        Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/bebasneue.ttf");
        
//          //uncomment if need animation in main activity
//        ImageView mImageViewFilling0 = (ImageView)activity.findViewById(R.id.imageView_challenges_animation);
//        ImageView mImageViewFilling1 = (ImageView)activity.findViewById(R.id.imageView_wins_animation);
//        ImageView mImageViewFilling2 = (ImageView)activity.findViewById(R.id.imageView_total_animation);
//        ((AnimationDrawable) mImageViewFilling0.getBackground()).start();
//        ((AnimationDrawable) mImageViewFilling1.getBackground()).start();
//        ((AnimationDrawable) mImageViewFilling2.getBackground()).start();

        final TextView tvChallengesCounter = (TextView)activity.findViewById(R.id.textViewChallengesCounter);
        final TextView tvWinsCounter       = (TextView)activity.findViewById(R.id.textViewWinsCounter);
        final TextView tvTotalCounter      = (TextView)activity.findViewById(R.id.textViewTotalCounter);

        tvChallengesCounter.setTypeface(typeface);
        tvWinsCounter.setTypeface(typeface);
        tvTotalCounter.setTypeface(typeface);

        SessionManager sessionManager = new SessionManager(activity);
        String challenges = sessionManager.getChampyOptions().get("total"); // i know
        String wins       = sessionManager.getChampyOptions().get("wins");
        String total      = sessionManager.getChampyOptions().get("challenges"); // i know
        String userName   = sessionManager.getUserName();

        int challengesInteger = Integer.parseInt(challenges);
        int winsInteger       = Integer.parseInt(wins);
        int totalInteger      = Integer.parseInt(total);

        // animator for In progress
        ValueAnimator animatorInProgress = new ValueAnimator();
        animatorInProgress.setObjectValues(0, challengesInteger);
        animatorInProgress.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tvChallengesCounter.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animatorInProgress.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animatorInProgress.setDuration(1000);
        animatorInProgress.start();

        // animator for Wins
        ValueAnimator animatorWins = new ValueAnimator();
        animatorWins.setObjectValues(0, winsInteger);
        animatorWins.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tvWinsCounter.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animatorWins.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animatorWins.setDuration(1000);
        animatorWins.start();


        // animator for Total
        ValueAnimator animatorTotal = new ValueAnimator();
        animatorTotal.setObjectValues(0, totalInteger);
        animatorTotal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tvTotalCounter.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animatorTotal.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animatorTotal.setDuration(1000);
        animatorTotal.start();


        final TextView  textViewWins            = (TextView)  activity.findViewById(R.id.textViewWins);
        final TextView  textViewTotal           = (TextView)  activity.findViewById(R.id.textViewTotal);
        final TextView  welcomeUserName         = (TextView)  activity.findViewById(R.id.welcomeUserName);
        final TextView  textViewChallenges      = (TextView)  activity.findViewById(R.id.textViewChallenges);
        final ImageView imageViewWinsLogo       = (ImageView) activity.findViewById(R.id.imageView_wins_logo);
        final ImageView imageViewTotalLogo      = (ImageView) activity.findViewById(R.id.imageView_total_logo);
        final ImageView imageViewChallengesLogo = (ImageView) activity.findViewById(R.id.imageView_challenges_logo);
        final Animation alphaAnimation          = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(2000);

        textViewChallenges.setText("In Progress");
        textViewChallenges.startAnimation(alphaAnimation);
        textViewChallenges.setTypeface(typeface);

        textViewWins.setText("Wins");
        textViewWins.startAnimation(alphaAnimation);
        textViewWins.setTypeface(typeface);

        textViewTotal.setText("Total");
        textViewTotal.startAnimation(alphaAnimation);
        textViewTotal.setTypeface(typeface);

        welcomeUserName.setText("Welcome " + userName);
        welcomeUserName.startAnimation(alphaAnimation);
        welcomeUserName.setTypeface(typeface);

        Uri uri = Uri.parse("android.resource://com.example.ivan.champy_v2/drawable/challenges");
        Glide.with(activity).load(uri).into(imageViewChallengesLogo);
        imageViewChallengesLogo.startAnimation(alphaAnimation);

        uri = Uri.parse("android.resource://com.example.ivan.champy_v2/drawable/wins");
        Glide.with(activity).load(uri).into(imageViewWinsLogo);
        imageViewWinsLogo.startAnimation(alphaAnimation);

        uri = Uri.parse("android.resource://com.example.ivan.champy_v2/drawable/total");
        Glide.with(activity).load(uri).into(imageViewTotalLogo);
        imageViewTotalLogo.startAnimation(alphaAnimation);

    }

}
