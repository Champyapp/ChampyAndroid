package com.azinecllc.champy.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.ChallengeCreateActivity;
import com.azinecllc.champy.adapter.MainCardAdapter;
import com.azinecllc.champy.model.CardChallenges;
import com.azinecllc.champy.model.Challenge;
import com.azinecllc.champy.utils.OfflineMode;

import java.util.ArrayList;
import java.util.Random;

import static com.azinecllc.champy.utils.Constants.typeDuel;
import static com.azinecllc.champy.utils.Constants.typeSelf;
import static com.azinecllc.champy.utils.Constants.typeWake;

public class MainCardsFragment extends Fragment {

    public static final String TAG = "MainCardFragment";
    private boolean isFabOpen = false;
    private FloatingActionButton fab;
    private SwipeRefreshLayout gSwipeRefreshLayout;
    private OfflineMode offlineMode;
    private ArrayList<CardChallenges> cardChallengesList;
    private MainCardAdapter adapter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        offlineMode = OfflineMode.getInstance();
        cardChallengesList = new ArrayList<CardChallenges>();
        adapter = new MainCardAdapter(cardChallengesList, getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_recycler_cards, container, false);
        Log.i(TAG, "onCreateView: ");
        gSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);

//        SessionManager sessionManager = SessionManager.getInstance(getContext());
//        if (sessionManager.getChampyOptions().get("challenges").isEmpty()) {
//            TextView textView = new TextView(getContext());
//            textView.setText("Time: "+System.currentTimeMillis());
//        }


        loadInProgressCards(view);

        gSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
                cardChallengesList.clear();
                loadInProgressCards(view);
            }
        });

        fab = (FloatingActionButton) view.findViewById(R.id.fabPlus);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ChallengeCreateActivity.class));
        });


//        FabScrollBehavior behavior = new FabScrollBehavior(getContext());
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
//        params.setBehavior(behavior);
//        fab.setLayoutParams(params);



        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: ");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
    }




    private void loadInProgressCards(View view) {
        gSwipeRefreshLayout.setRefreshing(true);
        ArrayList<Challenge> challengesArrayList = Challenge.generate(getContext());

        for (int i = 0; i < /*challengesArrayList.size()*/ 10; i++) {

            ///** True Data */
            //Challenge challenge = challengesArrayList.get(i);
            //String challengeName = challenge.getChallengeName();
            //String versus = challenge.getVersus();
            //String constDuration = challenge.getConstDuration();
            //String recipient = challenge.getRecipient();
            //String description = challenge.getGoal();
            //String duration = challenge.getDays();
            //String status = challenge.getStreakStatus();
            //String isRecipient = challenge.getRecipient();
            //String type = challenge.getType();
            //String color = challenge.getColor();

            /** Mock Data */
            Random random = new Random();
            int r = random.nextInt(256), g = random.nextInt(256), b = random.nextInt(256);
            int low = 0;
            int high = 100;

            /** Mock params
             *
             * 21 days = 100%
             * 5 day = x %
             *
             * x = (5 * 100) / 21;
             *
             * */
            String randomCurrentDays = String.valueOf(random.nextInt(20 - 1) + 1);
            String randomDuration = "21";
            String randomStreak = "4"; //String.valueOf(random.nextInt(high - low) + low);
            String randomPercent = String.valueOf((Integer.parseInt(randomCurrentDays) * 100) / Integer.parseInt(randomDuration));
            String randomColor = String.valueOf(Color.argb(255, r, g, b));

            String[] mockNames = {"Smoke Weed", "Love Cats", "Drink a beer", "Love Translit", "Igratu cs", "Don't die"};
            String[] mockVersus = {"Dr.Dre", "Chuck Norris"};
            String[] mockStatuses = {"pending", "started", /*"failed"*/};
            String[] mockRecipients = {"true", "false"};
            String[] mockTypes = {typeSelf, typeDuel, typeWake};

            String randomName = mockNames[(int) (Math.random() * mockNames.length)];
            String randomVersus = mockVersus[(int) (Math.random() * mockVersus.length)];
            String randomType = mockTypes[(int) (Math.random() * mockTypes.length)];
            String randomStatus = mockStatuses[(int) (Math.random() * mockStatuses.length)];
            String randIsRecipient = mockRecipients[(int) (Math.random() * mockRecipients.length)];

            String mockName = (randomType.equals(typeDuel)) ? randomName + " with " + randomVersus : randomName;

            /** Fill the model */
            cardChallengesList.add(new CardChallenges(
                    mockName,
                    randomDuration,
                    randomStreak,
                    randomPercent,
                    randomVersus,
                    randomColor,
                    randomStatus,
                    randIsRecipient,
                    randomType,
                    randomCurrentDays
            ));

        }
        gSwipeRefreshLayout.setRefreshing(false);
        RecyclerView rvCards = (RecyclerView) view.findViewById(R.id.recycler_view);
        rvCards.setAdapter(adapter);
        rvCards.setLayoutManager(new LinearLayoutManager(getContext()));


        rvCards.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fab.hide();
                } else if (dy < 0) {
                    fab.show();
                }
            }
        });

    }

//    /**
//     * Method to animate fab button, and make visible all sub buttons,
//     */
//    private void closeFab() {
//        fabPlus.startAnimation(rotate_backward);
//        fabWake.startAnimation(fab_close);
//        fabSelf.startAnimation(fab_close);
//        fabDuel.startAnimation(fab_close);
//        isFabOpen = false;
//    }

//    /**
//     * Method to animate fab button, and make invisible all sub buttons,
//     */
//    private void openFab() {
//        fabPlus.startAnimation(rotate_forward);
//        fabWake.startAnimation(fab_open);
//        fabSelf.startAnimation(fab_open);
//        fabDuel.startAnimation(fab_open);
//        isFabOpen = true;
//    }


}
