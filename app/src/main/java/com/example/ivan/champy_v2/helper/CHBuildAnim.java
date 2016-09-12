package com.example.ivan.champy_v2.helper;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;

/**
 * this is class helper for create animation, set right size of circles, count, alpha of animation,
 * numbers animation and other operation with score in main activity
 */
public class CHBuildAnim {


    public void buildAnim(Activity activity) {
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        CHMakeResponsiveScore chMakeResponsiveScore = new CHMakeResponsiveScore(activity);
        chMakeResponsiveScore.makeResponsiveScore(width);

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

        SessionManager sessionManager = new SessionManager(activity);
        String challenges = sessionManager.getChampyOptions().get("challenges");
        String wins       = sessionManager.getChampyOptions().get("wins");
        String total      = sessionManager.getChampyOptions().get("total");

        int challengesInteger = Integer.parseInt(challenges);
        int totalInteger      = Integer.parseInt(total);
        int winsInteger       = Integer.parseInt(wins);

        // animator for In progress
        ValueAnimator animatorInProgress = new ValueAnimator();
        animatorInProgress.setObjectValues(0, challengesInteger);
        animatorInProgress.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tvTotalCounter.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animatorInProgress.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animatorInProgress.setDuration(1000);

        // animator for Total
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

        // animator for total
        ValueAnimator animatorTotal = new ValueAnimator();
        animatorTotal.setObjectValues(0, totalInteger);
        animatorTotal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tvChallengesCounter.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animatorTotal.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animatorTotal.setDuration(1000);

        animatorTotal.start();
        animatorWins.start();
        animatorInProgress.start();

        final TextView  textViewChallenges      = (TextView)  activity.findViewById(R.id.textViewChallenges);
        final TextView  textViewWins            = (TextView)  activity.findViewById(R.id.textViewWins);
        final TextView  textViewTotal           = (TextView)  activity.findViewById(R.id.textViewTotal);
        final ImageView imageViewChallengesLogo = (ImageView) activity.findViewById(R.id.imageView_challenges_logo);
        final ImageView imageViewWinsLogo       = (ImageView) activity.findViewById(R.id.imageView_wins_logo);
        final ImageView imageViewTotalLogo      = (ImageView) activity.findViewById(R.id.imageView_total_logo);

        final Animation alphaAnimation          = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(3000);

        textViewChallenges.setText("In Progress");
        textViewChallenges.startAnimation(alphaAnimation);
        textViewWins.setText("Wins");
        textViewWins.startAnimation(alphaAnimation);
        textViewTotal.setText("Total");
        textViewTotal.startAnimation(alphaAnimation);

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
