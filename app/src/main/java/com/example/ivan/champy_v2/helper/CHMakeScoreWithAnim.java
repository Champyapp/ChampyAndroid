package com.example.ivan.champy_v2.helper;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.Friend.Datum;
import com.example.ivan.champy_v2.model.Friend.Friend;
import com.example.ivan.champy_v2.model.Friend.Friend_;
import com.example.ivan.champy_v2.model.Friend.Owner;
import com.example.ivan.champy_v2.model.User.Data;
import com.example.ivan.champy_v2.model.User.User;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static java.lang.Math.max;
import static java.lang.Math.round;

public class CHMakeScoreWithAnim extends View {


    public CHMakeScoreWithAnim(Context context) {
        super(context);
    }


    public void buildAnim(Activity activity) {
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        makeResponsiveScore(width);
        ImageView mImageViewFilling = (ImageView) findViewById(R.id.imageView_challenges_animation);
        ((AnimationDrawable) mImageViewFilling.getBackground()).start();
        ImageView mImageViewFilling1 = (ImageView) findViewById(R.id.imageView_wins_animation);
        ((AnimationDrawable) mImageViewFilling1.getBackground()).start();
        ImageView mImageViewFilling2 = (ImageView) findViewById(R.id.imageView_total_animation);
        ((AnimationDrawable) mImageViewFilling2.getBackground()).start();

        final TextView tvChallengesCounter = (TextView) findViewById(R.id.textViewChallengesCounter);
        final TextView tvWinsCounter       = (TextView) findViewById(R.id.textViewWinsCounter);
        final TextView tvTotalCounter      = (TextView) findViewById(R.id.textViewTotalCounter);

        SessionManager sessionManager = new SessionManager(activity);

        String challenges = sessionManager.getChampyOptions().get("challenges");
        String wins       = sessionManager.getChampyOptions().get("wins");
        String total      = sessionManager.getChampyOptions().get("total");

        final int challengesInteger = Integer.parseInt(challenges);
        final int totalInteger      = Integer.parseInt(total);
        final int winsInteger       = Integer.parseInt(wins);

        //----------------------- animator for Challenges -----------------------//
        ValueAnimator animatorChallenges = new ValueAnimator();
        animatorChallenges.setObjectValues(0, totalInteger);
        animatorChallenges.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tvChallengesCounter.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animatorChallenges.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animatorChallenges.setDuration(1000);

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

        // animator for Total
        ValueAnimator animatorTotal = new ValueAnimator();
        animatorTotal.setObjectValues(0, challengesInteger);
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

        animatorChallenges.start();
        animatorWins.start();
        animatorTotal.start();

        final TextView textViewChallenges       = (TextView)  findViewById(R.id.textViewChallenges);
        final TextView textViewWins             = (TextView)  findViewById(R.id.textViewWins);
        final TextView textViewTotal            = (TextView)  findViewById(R.id.textViewTotal);
        final ImageView imageViewChallengesLogo = (ImageView) findViewById(R.id.imageView_challenges_logo);
        final ImageView imageViewWinsLogo       = (ImageView) findViewById(R.id.imageView_wins_logo);
        final ImageView imageViewTotalLogo      = (ImageView) findViewById(R.id.imageView_total_logo);

        final Animation alphaAnimation          = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(3000);

        textViewChallenges.setText("In Progress"); // TODO: 22.06.2016 Change to Challenges
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


    public void makeResponsiveScore(int width) {
        int x = round(width/100);

        //-------------------------- Animation ---------------------------//
        ImageView imageView = (ImageView)findViewById(R.id.imageView_challenges_animation);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;

        imageView = (ImageView)findViewById(R.id.imageView_wins_animation);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;

        imageView = (ImageView)findViewById(R.id.imageView_total_animation);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;

        //---------------------------- Logo -----------------------------//
        imageView = (ImageView)findViewById(R.id.imageView_wins_logo);
        imageView.getLayoutParams().width = x*5;
        imageView.getLayoutParams().height = x*5;

        imageView = (ImageView)findViewById(R.id.imageView_total_logo);
        imageView.getLayoutParams().width = x*5;
        imageView.getLayoutParams().height = x*5;

        imageView = (ImageView)findViewById(R.id.imageView_challenges_logo);
        imageView.getLayoutParams().width = x*5;
        imageView.getLayoutParams().height = x*5;

        //---------------------------- Fab -----------------------------//
        ImageButton fab = (ImageButton)findViewById(R.id.fabPlus);
        fab.getLayoutParams().width = x*20;
        fab.getLayoutParams().height = x*20;

        /*imageView = (ImageView)findViewById(R.id.profile_image);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;*/

        //--------------------------- Score ----------------------------//
        Float y = x*(float)3.5;

        TextView textViewScoreChallenges = (TextView)findViewById(R.id.textViewChallengesCounter);
        textViewScoreChallenges.setTextSize(y);

        TextView textViewScoreWins = (TextView)findViewById(R.id.textViewWinsCounter);
        textViewScoreWins.setTextSize(y);

        TextView textViewScoreTotal = (TextView)findViewById(R.id.textViewTotalCounter);
        textViewScoreTotal.setTextSize(y);

        //------------------------- TextViews -------------------------//
        y = x*(float)1.5;
        TextView textViewChallenges = (TextView)findViewById(R.id.textViewChallenges);
        textViewChallenges.setTextSize(y);

        TextView textViewWins = (TextView)findViewById(R.id.textViewWins);
        textViewWins.setTextSize(y);

        TextView textViewTotal = (TextView)findViewById(R.id.textViewTotal);
        textViewTotal.setTextSize(y);

    }

}
