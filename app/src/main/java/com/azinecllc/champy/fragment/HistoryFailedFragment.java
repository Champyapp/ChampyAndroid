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

public class HistoryFailedFragment extends Fragment {

    private SwipeRefreshLayout gSwipeRefreshLayout;
    private OfflineMode offlineMode;
    private ArrayList<SelfImprovement_model> self_improvement;
    private ArrayList<HistoryChallenge> arr;
    private HistoryChallengeAdapter adapter;
    private RecyclerView rvContacts;
    private View gView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offlineMode = OfflineMode.getInstance();
        arr = new ArrayList<>();
        adapter = new HistoryChallengeAdapter(arr, getContext());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history, container, false);

        self_improvement = SelfImprovement_model.generateFailed(getContext());

        for (int i = 0; i < self_improvement.size(); i++) {
            SelfImprovement_model item = self_improvement.get(i);
            String description = item.getGoal();
            String duration = item.getDays();
            String status = item.getStatus();
            String type = item.getType();
            String goal = item.getGoal();
            String challengeName = item.getChallengeName();
            String versus = item.getVersus();
            String recipient = item.getRecipient();
            String constDuration = item.getConstDuration();

            arr.add(new HistoryChallenge(
                    type, false, description, duration, status, goal, challengeName, versus, recipient, constDuration
            ));
        }

        gSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
        gSwipeRefreshLayout.setOnRefreshListener(() -> refreshOtherView(gSwipeRefreshLayout, gView));
        this.gView = view;

        rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private void refreshOtherView(final SwipeRefreshLayout swipeRefreshLayout, final View view) {
        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.post(() -> {

                arr = new ArrayList<>();
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

                    arr.add(new HistoryChallenge(
                            type, false, description, duration, status, goal, challengeName, versus, recipient, constDuration
                    ));
                }

                rvContacts.setAdapter(adapter);
                rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
                swipeRefreshLayout.setRefreshing(false);
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


}