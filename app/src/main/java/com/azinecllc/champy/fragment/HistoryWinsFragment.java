package com.azinecllc.champy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.HistoryChallengeAdapter;
import com.azinecllc.champy.model.HistoryChallenge;
import com.azinecllc.champy.model.SelfImprovement_model;
import com.azinecllc.champy.utils.OfflineMode;

import java.util.ArrayList;

public class HistoryWinsFragment extends Fragment {

    private ArrayList<HistoryChallenge> winsArray;
    private SwipeRefreshLayout gSwipeRefreshLayout;
    private HistoryChallengeAdapter adapter;
    private OfflineMode offlineMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offlineMode = OfflineMode.getInstance();
        winsArray = new ArrayList<>();
        adapter = new HistoryChallengeAdapter(winsArray, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.item_recycler, container, false);

        loadHistoryWins(view);

        gSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
        gSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
                gSwipeRefreshLayout.setRefreshing(true);
                winsArray.clear();
                loadHistoryWins(view);
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


    private void loadHistoryWins(View view) {
        ArrayList<SelfImprovement_model> self_improvement = SelfImprovement_model.generateWins(getContext());

        for (int i = 0; i < self_improvement.size(); i++) {
            SelfImprovement_model item = self_improvement.get(i);
            String challengeName = item.getChallengeName();
            String constDuration = item.getConstDuration();
            String recipient = item.getRecipient();
            String description = item.getGoal();
            String duration = item.getDays();
            String versus = item.getVersus();
            String status = item.getStatus();
            String type = item.getType();
            String goal = item.getGoal();

            winsArray.add(new HistoryChallenge(
                    type, false, description, duration, status, goal, challengeName, versus, recipient, constDuration
            ));
        }

        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
    }


}
