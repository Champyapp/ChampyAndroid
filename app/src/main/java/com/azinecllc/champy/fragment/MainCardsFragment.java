package com.azinecllc.champy.fragment;

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
            Challenge challenge = challengesArrayList.get(i);
            String challengeName = challenge.getChallengeName();
            String constDuration = challenge.getConstDuration();
            String recipient = challenge.getRecipient();
            String description = challenge.getGoal();
            String duration = challenge.getDays();
            String versus = challenge.getVersus();
            //String streak = challenge.get
            cardsList.add(new Cards(challengeName, constDuration, "", "21%", "", ""));
        }

        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    /**
     * Method-toggle to control visibility of the sub buttons. This method works like a on-off system.
     */


}
