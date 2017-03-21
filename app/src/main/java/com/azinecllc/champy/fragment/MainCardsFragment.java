package com.azinecllc.champy.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.MainCardAdapter;
import com.azinecllc.champy.interfaces.OnCardClickListener;
import com.azinecllc.champy.model.Cards;
import com.azinecllc.champy.model.Challenge;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;

import java.util.ArrayList;
import java.util.Random;

import static com.azinecllc.champy.utils.Constants.typeDuel;
import static com.azinecllc.champy.utils.Constants.typeSelf;
import static com.azinecllc.champy.utils.Constants.typeWake;

public class MainCardsFragment extends Fragment {

    private SwipeRefreshLayout gSwipeRefreshLayout;
    private OfflineMode offlineMode;
    private ArrayList<Cards> cardsList;
    private MainCardAdapter adapter;
    private SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offlineMode = OfflineMode.getInstance();
        sessionManager = SessionManager.getInstance(getContext());
        cardsList = new ArrayList<>();
        adapter = new MainCardAdapter(cardsList, getContext());
        adapter.setOnCardClickListener(new OnCardClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.item_recycler, container, false);


        loadInProgressCards(view);

        gSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        gSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
                cardsList.clear();
                gSwipeRefreshLayout.setRefreshing(true);
                loadInProgressCards(view);
                gSwipeRefreshLayout.setRefreshing(false);
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

            String mockName = challengeName.replaceAll("e", "e");
            String mockDays = String.valueOf(random.nextInt(high - low) + low);
            String mockStreak = String.valueOf(random.nextInt(high - low) + low);
            String mockPercent = String.valueOf(random.nextInt(high - low) + low);
            String mockVersus = versus.replaceAll("a", "a");
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

        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));

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
