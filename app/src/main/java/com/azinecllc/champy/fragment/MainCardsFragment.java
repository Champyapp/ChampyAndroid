package com.azinecllc.champy.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.CardDetailActivity;
import com.azinecllc.champy.adapter.MainCardAdapter;
import com.azinecllc.champy.interfaces.RecyclerCardClickListener;
import com.azinecllc.champy.model.CardChallenges;
import com.azinecllc.champy.model.Challenge;
import com.azinecllc.champy.utils.OfflineMode;

import java.util.ArrayList;
import java.util.Random;

import static com.azinecllc.champy.utils.Constants.typeDuel;
import static com.azinecllc.champy.utils.Constants.typeSelf;
import static com.azinecllc.champy.utils.Constants.typeWake;

public class MainCardsFragment extends Fragment {

    private SwipeRefreshLayout gSwipeRefreshLayout;
    private OfflineMode offlineMode;
    private ArrayList<CardChallenges> cardChallengesList;
    private MainCardAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offlineMode = OfflineMode.getInstance();
        cardChallengesList = new ArrayList<CardChallenges>();
        adapter = new MainCardAdapter(cardChallengesList, getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_recycler, container, false);
        gSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);

        loadInProgressCards(view);

        adapter.setOnCardClickListener(new RecyclerCardClickListener() {
            @Override
            public void onClick(View v, CardChallenges selectedItem) {
                Intent intent = new Intent(getContext(), CardDetailActivity.class);
                intent.putExtra("mockName", selectedItem.getChallengeName());
                intent.putExtra("mockStreak", selectedItem.getChallengeStreak());
                intent.putExtra("mockPercent", selectedItem.getChallengePercent());
                intent.putExtra("mockDay", selectedItem.getChallengeDay());
                startActivity(intent);
            }
        });

        gSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
                cardChallengesList.clear();
                loadInProgressCards(view);
            }
        });


        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


    private void loadInProgressCards(View view) {
        gSwipeRefreshLayout.setRefreshing(true);
        ArrayList<Challenge> challengesArrayList = Challenge.generate(getContext());
        for (int i = 0; i < /*challengesArrayList.size()*/ 10; i++) {

            /** True Data */
            //Challenge challenge = challengesArrayList.get(i);
            //String challengeName = challenge.getChallengeName();
            //String versus = challenge.getVersus();
            //String constDuration = challenge.getConstDuration();
            //String recipient = challenge.getRecipient();
            //String description = challenge.getGoal();
            //String duration = challenge.getDays();
            //String status = challenge.getStatus();
            //String isRecipient = challenge.getRecipient();
            //String type = challenge.getType();
            //String color = challenge.getColor();

            /** Mock Data */
            Random random = new Random();
            int r = random.nextInt(256), g = random.nextInt(256), b = random.nextInt(256);
            int low = 0;
            int high = 100;

            /** Mock params */

            String randomDays = String.valueOf(random.nextInt(high - low) + low);
            String randomStreak = String.valueOf(random.nextInt(high - low) + low);
            String randomPercent = String.valueOf(random.nextInt(high - low) + low);
            String randomColor = String.valueOf(Color.argb(255, r, g, b));

            String[] mockNames = {"Smoke Weed", "Don't Sleep", "Drink a beer", "Love Translit", "Igratu cs", "Don't die"};
            String[] mockVersus = {"", "Dr.Dre", "Chuck Norris"};
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
                    randomDays,
                    randomStreak,
                    randomPercent,
                    randomVersus,
                    randomColor,
                    randomStatus,
                    randIsRecipient,
                    randomType
            ));

        }
        gSwipeRefreshLayout.setRefreshing(false);
        RecyclerView rvCards = (RecyclerView) view.findViewById(R.id.recycler_view);
        rvCards.setAdapter(adapter);
        rvCards.setLayoutManager(new LinearLayoutManager(getContext()));
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
