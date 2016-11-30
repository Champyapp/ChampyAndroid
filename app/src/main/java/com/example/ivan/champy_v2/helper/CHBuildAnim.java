package com.example.ivan.champy_v2.helper;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.Display;
import android.view.WindowManager;
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

    private Activity activity;
    private SessionManager sessionManager;
    private Typeface typeface;

    public CHBuildAnim(Activity activity, SessionManager sessionManager, Typeface typeface) {
        this.activity = activity;
        this.sessionManager = sessionManager;
        this.typeface = typeface;
    }

    public void buildAnim() {

//        WindowManager wm = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int x = size.x / 100;
//        int y = size.y / 100;
//
//        ImageView circleInProgress = (ImageView)activity.findViewById(R.id.imageView_challenges_animation);
//        ImageView circleViewWins = (ImageView)activity.findViewById(R.id.imageView_wins_animation);
//        ImageView circleViewTotal = (ImageView)activity.findViewById(R.id.imageView_total_animation);
//
//        circleInProgress.getLayoutParams().width = x*30;
//        circleInProgress.getLayoutParams().height = y*15;
//
//        circleViewWins.getLayoutParams().width = x*30;
//        circleViewWins.getLayoutParams().height = y*15;
//
//        circleViewTotal.getLayoutParams().width = x*30;
//        circleViewTotal.getLayoutParams().height = y*15;

//          //uncomment if need animation in main activity
//        ((AnimationDrawable) mImageViewFilling0.getBackground()).start();
//        ((AnimationDrawable) mImageViewFilling1.getBackground()).start();
//        ((AnimationDrawable) mImageViewFilling2.getBackground()).start();

        final TextView tvChallengesCounter = (TextView)activity.findViewById(R.id.textViewChallengesCounter);
        final TextView tvWinsCounter       = (TextView)activity.findViewById(R.id.textViewWinsCounter);
        final TextView tvTotalCounter      = (TextView)activity.findViewById(R.id.textViewTotalCounter);

        tvChallengesCounter.setTypeface(typeface);
        tvWinsCounter.setTypeface(typeface);
        tvTotalCounter.setTypeface(typeface);

        String challenges = sessionManager.getChampyOptions().get("challenges");
        String wins       = sessionManager.getChampyOptions().get("wins");
        String total      = sessionManager.getChampyOptions().get("total");
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

        textViewChallenges.setText(activity.getString(R.string.challenges));
        textViewChallenges.startAnimation(alphaAnimation);
        textViewChallenges.setTypeface(typeface);

        textViewWins.setText(activity.getString(R.string.wins));
        textViewWins.startAnimation(alphaAnimation);
        textViewWins.setTypeface(typeface);

        textViewTotal.setText(activity.getString(R.string.total));
        textViewTotal.startAnimation(alphaAnimation);
        textViewTotal.setTypeface(typeface);

        welcomeUserName.setText(activity.getString(R.string.welcome) + userName);
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
