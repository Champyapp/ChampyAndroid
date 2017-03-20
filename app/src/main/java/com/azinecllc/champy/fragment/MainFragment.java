//package com.azinecllc.champy.fragment;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.azinecllc.champy.R;
//import com.azinecllc.champy.activity.FriendsActivity;
//import com.azinecllc.champy.activity.SelfImprovementActivity;
//import com.azinecllc.champy.activity.WakeUpActivity;
//import com.azinecllc.champy.utils.SessionManager;
//
//import java.util.ArrayList;
//
///**
// * Created by SashaKhyzhun on 2/7/17.
// */
//
//public class MainFragment extends Fragment implements View.OnClickListener {
//
//
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    /**
//     MainCardAdapter adapter = new MainCardAdapter(getContext(), SelfImprovement_model.generate(getContext()));
//     if (adapter.dataCount() > 0) {
//         RelativeLayout cards = (RelativeLayout) view.findViewById(R.id.cards);
//         CustomPagerBase pager = new CustomPagerBase(getActivity(), cards, adapter);
//         pager.preparePager(0);
//     }
//     */
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_main, container, false);
//        sessionManager = SessionManager.getInstance(getContext());
//
////        cardArrayAdapter = new CardArrayAdapter(getContext(), R.layout.item_card_list);
////        listView = (ListView) view.findViewById(R.id.card_list_view);
////        listView.addHeaderView(new View(getContext()));
////        listView.addFooterView(new View(getContext()));
////        listView.setAdapter(cardArrayAdapter);
//
////        MainCardAdapter adapter = new MainCardAdapter(getContext(), Challenge.generate(getContext()));
////        if (adapter.dataCount() > 0) {
////            ListView listView = (ListView)view.findViewById(R.id.card_list_view);
////            listView.setAdapter(adapter);
////        }
//
//
////        cardArrayAdapter = new CardArrayAdapter(getContext(), R.layout.item_card_list);
////        Challenge currentCard = arrayList.get(position);
////        for (int i = 0; i < 10; i++) {
////            Card card = new Card(
////                    "Card " + (i + 1),
////                    "3", "21", "27");
////            cardArrayAdapter.add(card);
////        }
////        listView.setAdapter(cardArrayAdapter);
////        //Challenge challenge = Challenge.generate(getContext());
//
////        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
////        int x = round(width / 100);
//
////        ImageView imageView = (ImageView) view.findViewById(R.id.imageView_challenges_animation);
////        imageView.getLayoutParams().width = x * 25;
////        imageView.getLayoutParams().height = x * 25;
////
////        imageView = (ImageView) view.findViewById(R.id.imageView_wins_animation);
////        imageView.getLayoutParams().width = x * 25;
////        imageView.getLayoutParams().height = x * 25;
////
////        imageView = (ImageView) view.findViewById(R.id.imageView_total_animation);
////        imageView.getLayoutParams().width = x * 25;
////        imageView.getLayoutParams().height = x * 25;
////
////
////        TextView tvChallengesCounter = (TextView) view.findViewById(R.id.textViewChallengesCounter);
////        TextView tvWinsCounter = (TextView) view.findViewById(R.id.textViewWinsCounter);
////        TextView tvTotalCounter = (TextView) view.findViewById(R.id.textViewTotalCounter);
////
////        tvChallengesCounter.setTypeface(typeface);
////        tvWinsCounter.setTypeface(typeface);
////        tvTotalCounter.setTypeface(typeface);
////
//
////        ValueAnimator animatorInProgress = new ValueAnimator();
////        animatorInProgress.setObjectValues(0, challengesInteger);
////        animatorInProgress.addUpdateListener(animation -> tvChallengesCounter.setText(String.valueOf(animation.getAnimatedValue())));
////        animatorInProgress.setEvaluator(new TypeEvaluator<Integer>() {
////            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
////                return Math.round(startValue + (endValue - startValue) * fraction);
////            }
////        });
////        animatorInProgress.setChallengeDuration(1000);
////        animatorInProgress.start();
////
////           //animator for Wins
////        ValueAnimator animatorWins = new ValueAnimator();
////        animatorWins.setObjectValues(0, winsInteger);
////        animatorWins.addUpdateListener(animation -> tvWinsCounter.setText(String.valueOf(animation.getAnimatedValue())));
////        animatorWins.setEvaluator(new TypeEvaluator<Integer>() {
////            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
////                return Math.round(startValue + (endValue - startValue) * fraction);
////            }
////        });
////        animatorWins.setChallengeDuration(1000);
////        animatorWins.start();
////
////
////          //animator for Total
////        ValueAnimator animatorTotal = new ValueAnimator();
////        animatorTotal.setObjectValues(0, totalInteger);
////        animatorTotal.addUpdateListener(animation -> tvTotalCounter.setText(String.valueOf(animation.getAnimatedValue())));
////        animatorTotal.setEvaluator(new TypeEvaluator<Integer>() {
////            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
////                return Math.round(startValue + (endValue - startValue) * fraction);
////            }
////        });
////        animatorTotal.setChallengeDuration(1000);
////        animatorTotal.start();
//
//
////        TextView textViewWins = (TextView) view.findViewById(R.id.textViewWins);
////        TextView textViewTotal = (TextView) view.findViewById(R.id.textViewTotal);
////        TextView welcomeUserName = (TextView) view.findViewById(R.id.welcomeUserName);
////        TextView textViewChallenges = (TextView) view.findViewById(R.id.textViewChallenges);
////        ImageView imageViewWinsLogo = (ImageView) view.findViewById(R.id.imageView_wins_logo);
////        ImageView imageViewTotalLogo = (ImageView) view.findViewById(R.id.imageView_total_logo);
////        ImageView imageViewChallengesLogo = (ImageView) view.findViewById(R.id.imageView_challenges_logo);
////        Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
////        alphaAnimation.setChallengeDuration(2000);
////
////        textViewChallenges.setText(R.string.challenges);
////        textViewChallenges.startAnimation(alphaAnimation);
////        textViewChallenges.setTypeface(typeface);
////
////        textViewWins.setText(R.string.wins);
////        textViewWins.startAnimation(alphaAnimation);
////        textViewWins.setTypeface(typeface);
////
////        textViewTotal.setText(R.string.total);
////        textViewTotal.startAnimation(alphaAnimation);
////        textViewTotal.setTypeface(typeface);
//
////        welcomeUserName.setText(String.format("%s", getString(R.string.welcome) + userName));
////        welcomeUserName.startAnimation(alphaAnimation);
////        welcomeUserName.setTypeface(typeface);
//
////        Uri uri = Uri.parse("android.resource://com.azinecllc.champy/drawable/ic_score_prog");
////        Glide.with(getContext()).load(uri).into(imageViewChallengesLogo);
////        imageViewChallengesLogo.startAnimation(alphaAnimation);
////
////        uri = Uri.parse("android.resource://com.azinecllc.champy/drawable/ic_score_wins");
////        Glide.with(getContext()).load(uri).into(imageViewWinsLogo);
////        imageViewWinsLogo.startAnimation(alphaAnimation);
////
////        uri = Uri.parse("android.resource://com.azinecllc.champy/drawable/ic_score_total");
////        Glide.with(getContext()).load(uri).into(imageViewTotalLogo);
////        imageViewTotalLogo.startAnimation(alphaAnimation);
//
//
//
//
//        return view;
//    }
//
////    @Override
////    public void onClick(View v) {
////        if (Integer.parseInt(sessionManager.getChampyOptions().get("challenges")) < 10) {
////            switch (v.getId()) {
////                case R.id.fabSelf:
////                    startActivity(new Intent(getContext(), SelfImprovementActivity.class));
////                    break;
////                case R.id.fabDuel:
////                    new Handler().postDelayed(() -> startActivity(new Intent(getContext(), FriendsActivity.class)), 250);
////                    break;
////                case R.id.fabWake:
////                    startActivity(new Intent(getContext(), WakeUpActivity.class));
////                    break;
////            }
////            animateFAB();
////        } else {
////            Toast.makeText(getContext(), R.string.challenges_to_much, Toast.LENGTH_LONG).show();
////        }
////    }
//
//
//
////    /**
////     * Method to animate fab button, and make visible all sub buttons,
////     */
////    private void closeFab() {
////        fabPlus.startAnimation(rotate_backward);
////        fabWake.startAnimation(fab_close);
////        fabSelf.startAnimation(fab_close);
////        fabDuel.startAnimation(fab_close);
////        isFabOpen = false;
////    }
//
////    /**
////     * Method to animate fab button, and make invisible all sub buttons,
////     */
////    private void openFab() {
////        fabPlus.startAnimation(rotate_forward);
////        fabWake.startAnimation(fab_open);
////        fabSelf.startAnimation(fab_open);
////        fabDuel.startAnimation(fab_open);
////        isFabOpen = true;
////    }
//
//}
