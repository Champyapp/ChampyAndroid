package com.azinecllc.champy.fragment;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.FriendsActivity;
import com.azinecllc.champy.activity.SelfImprovementActivity;
import com.azinecllc.champy.activity.WakeUpActivity;
import com.azinecllc.champy.adapter.MainActivityCardsAdapter;
import com.azinecllc.champy.model.SelfImprovement_model;
import com.azinecllc.champy.utils.CustomPagerBase;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;

import static java.lang.Math.round;

/**
 * Created by SashaKhyzhun on 2/7/17.
 */

public class MainFragment extends Fragment {

    private Typeface typeface;
    private MainActivityCardsAdapter adapter;
    private SessionManager sessionManager;
    private int challengesInteger, winsInteger, totalInteger;
    private String userName;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typeface = android.graphics.Typeface.createFromAsset(getContext().getAssets(), "fonts/bebasneue.ttf");
        sessionManager = SessionManager.getInstance(getContext());
        String challenges = sessionManager.getChampyOptions().get("challenges");
        String wins = sessionManager.getChampyOptions().get("wins");
        String total = sessionManager.getChampyOptions().get("total");
        userName = sessionManager.getUserName();
        challengesInteger = Integer.parseInt(challenges);
        winsInteger = Integer.parseInt(wins);
        totalInteger = Integer.parseInt(total);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new MainActivityCardsAdapter(getContext(), SelfImprovement_model.generate(getContext()));
        if (adapter.dataCount() > 0) {
            RelativeLayout cards = (RelativeLayout) view.findViewById(R.id.cards);
            CustomPagerBase pager = new CustomPagerBase(getActivity(), cards, adapter);
            pager.preparePager(0);
        }

        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int x = round(width / 100);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView_challenges_animation);
        imageView.getLayoutParams().width = x * 25;
        imageView.getLayoutParams().height = x * 25;

        imageView = (ImageView) view.findViewById(R.id.imageView_wins_animation);
        imageView.getLayoutParams().width = x * 25;
        imageView.getLayoutParams().height = x * 25;

        imageView = (ImageView) view.findViewById(R.id.imageView_total_animation);
        imageView.getLayoutParams().width = x * 25;
        imageView.getLayoutParams().height = x * 25;


        TextView tvChallengesCounter = (TextView) view.findViewById(R.id.textViewChallengesCounter);
        TextView tvWinsCounter = (TextView) view.findViewById(R.id.textViewWinsCounter);
        TextView tvTotalCounter = (TextView) view.findViewById(R.id.textViewTotalCounter);

        tvChallengesCounter.setTypeface(typeface);
        tvWinsCounter.setTypeface(typeface);
        tvTotalCounter.setTypeface(typeface);


        ValueAnimator animatorInProgress = new ValueAnimator();
        animatorInProgress.setObjectValues(0, challengesInteger);
        animatorInProgress.addUpdateListener(animation -> tvChallengesCounter.setText(String.valueOf(animation.getAnimatedValue())));
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
        animatorWins.addUpdateListener(animation -> tvWinsCounter.setText(String.valueOf(animation.getAnimatedValue())));
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
        animatorTotal.addUpdateListener(animation -> tvTotalCounter.setText(String.valueOf(animation.getAnimatedValue())));
        animatorTotal.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animatorTotal.setDuration(1000);
        animatorTotal.start();


        TextView textViewWins = (TextView) view.findViewById(R.id.textViewWins);
        TextView textViewTotal = (TextView) view.findViewById(R.id.textViewTotal);
        TextView welcomeUserName = (TextView) view.findViewById(R.id.welcomeUserName);
        TextView textViewChallenges = (TextView) view.findViewById(R.id.textViewChallenges);
        ImageView imageViewWinsLogo = (ImageView) view.findViewById(R.id.imageView_wins_logo);
        ImageView imageViewTotalLogo = (ImageView) view.findViewById(R.id.imageView_total_logo);
        ImageView imageViewChallengesLogo = (ImageView) view.findViewById(R.id.imageView_challenges_logo);
        Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(2000);

        textViewChallenges.setText(R.string.challenges);
        textViewChallenges.startAnimation(alphaAnimation);
        textViewChallenges.setTypeface(typeface);

        textViewWins.setText(R.string.wins);
        textViewWins.startAnimation(alphaAnimation);
        textViewWins.setTypeface(typeface);

        textViewTotal.setText(R.string.total);
        textViewTotal.startAnimation(alphaAnimation);
        textViewTotal.setTypeface(typeface);

        welcomeUserName.setText(String.format("%s", getString(R.string.welcome) + userName));
        welcomeUserName.startAnimation(alphaAnimation);
        welcomeUserName.setTypeface(typeface);

        Uri uri = Uri.parse("android.resource://com.azinecllc.champy/drawable/ic_score_prog");
        Glide.with(getContext()).load(uri).into(imageViewChallengesLogo);
        imageViewChallengesLogo.startAnimation(alphaAnimation);

        uri = Uri.parse("android.resource://com.azinecllc.champy/drawable/ic_score_wins");
        Glide.with(getContext()).load(uri).into(imageViewWinsLogo);
        imageViewWinsLogo.startAnimation(alphaAnimation);

        uri = Uri.parse("android.resource://com.azinecllc.champy/drawable/ic_score_total");
        Glide.with(getContext()).load(uri).into(imageViewTotalLogo);
        imageViewTotalLogo.startAnimation(alphaAnimation);


        return view;
    }


}
