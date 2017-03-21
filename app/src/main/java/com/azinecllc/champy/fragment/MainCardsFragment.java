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
import com.azinecllc.champy.interfaces.OnCardClickListener;
import com.azinecllc.champy.model.Cards;
import com.azinecllc.champy.model.Challenge;
import com.azinecllc.champy.utils.OfflineMode;

import java.util.ArrayList;
import java.util.Random;

import static com.azinecllc.champy.utils.Constants.typeDuel;
import static com.azinecllc.champy.utils.Constants.typeSelf;
import static com.azinecllc.champy.utils.Constants.typeWake;

public class MainCardsFragment extends Fragment implements OnCardClickListener {

    private SwipeRefreshLayout gSwipeRefreshLayout;
    private OfflineMode offlineMode;
    private ArrayList<Cards> cardsList;
    private MainCardAdapter adapter;

    /**
     * Mock params
     */
    private String mockName;
    private String mockDays;
    private String mockStreak;
    private String mockPercent;
    private String mockVersus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offlineMode = OfflineMode.getInstance();
        cardsList = new ArrayList<>();
        adapter = new MainCardAdapter(cardsList, getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_recycler, container, false);
        gSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);

        loadInProgressCards(view);

        gSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
                cardsList.clear();
                loadInProgressCards(view);
            }
        });


        return view;
    }

    @Override
    public void onClick() {
//        Intent intent = new Intent(getContext(), CardDetailActivity.class);
//        intent.putExtra("mockName", mockName);
//        intent.putExtra("mockStreak", mockStreak);
//        intent.putExtra("mockPercent", mockPercent);
//        intent.putExtra("mockDays", mockDays);
//        startActivity(intent);
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
        for (int i = 0; i < challengesArrayList.size(); i++) {

            /** True Data */
            Challenge challenge = challengesArrayList.get(i);
            String challengeName = challenge.getChallengeName();
            String versus = challenge.getVersus();
            String constDuration = challenge.getConstDuration();
            String recipient = challenge.getRecipient();
            String description = challenge.getGoal();
            String duration = challenge.getDays();
            String status = challenge.getStatus();
            String isRecipient = challenge.getRecipient();
            String type = challenge.getType();
            //String color = challenge.getColor();

            /** Mock Data */
            Random random = new Random();
            int r = random.nextInt(256), g = random.nextInt(256), b = random.nextInt(256);
            int low = 0;
            int high = 100;

            mockName = challengeName.replaceAll("e", "e");
            mockDays = String.valueOf(random.nextInt(high - low) + low);
            mockStreak = String.valueOf(random.nextInt(high - low) + low);
            mockPercent = String.valueOf(random.nextInt(high - low) + low);
            mockVersus = versus.replaceAll("a", "a");
            String mockColor = String.valueOf(Color.argb(255, r, g, b));

            String[] mockStatuses = {"pending", "started", /*"failed"*/};
            String[] mockRecipients = {"true", "false"};
            String[] mockTypes = {typeSelf, typeDuel, typeWake};
            String mockStatus = mockStatuses[(int) (Math.random() * mockStatuses.length)];
            String mockIsRecipient = mockRecipients[(int) (Math.random() * mockRecipients.length)];
            String mockType = mockTypes[(int) (Math.random() * mockTypes.length)];


            System.out.println("MainCardFragment MockData: | name: " + mockName
                    + " days: " + mockDays
                    + " streak: " + mockStreak
                    + " percent: " + mockPercent
                    + " status: " + mockStatus
                    + " isRecipient: " + mockIsRecipient
                    + " type: " + mockType
                    + " versus: " + mockVersus
            );

            /** Fill the model */
            cardsList.add(new Cards(mockName, mockDays, mockStreak, mockPercent, mockVersus, mockColor, mockStatus, mockIsRecipient, mockType));

        }
        gSwipeRefreshLayout.setRefreshing(false);
        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setOnCardClickListener(this);
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
