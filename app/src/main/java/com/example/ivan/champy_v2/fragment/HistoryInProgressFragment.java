package com.example.ivan.champy_v2.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.adapter.HistoryChallengeAdapter;
import com.example.ivan.champy_v2.model.HistoryChallenge;
import com.example.ivan.champy_v2.model.SelfImprovement_model;
import com.example.ivan.champy_v2.utils.OfflineMode;

import java.util.ArrayList;

public class HistoryInProgressFragment extends Fragment {

    private SwipeRefreshLayout gSwipeRefreshLayout;
    private OfflineMode offlineMode;
    private ArrayList<SelfImprovement_model> self_improvement;
    private RecyclerView rvContacts;
    private HistoryChallengeAdapter adapter;
    private View gView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offlineMode = new OfflineMode();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history, container, false);

        self_improvement = SelfImprovement_model.generate(getContext());
        ArrayList<HistoryChallenge> all = new ArrayList<>();

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

            all.add(new HistoryChallenge(
                    type, true, description, duration, status, goal, challengeName, versus, recipient, constDuration
            ));
        }

        gSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
        gSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshOtherView(gSwipeRefreshLayout, gView);
                gSwipeRefreshLayout.setRefreshing(false);
            }
        });
        this.gView = view;

        rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        adapter = new HistoryChallengeAdapter(all, getContext());
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private void refreshOtherView(final SwipeRefreshLayout swipeRefreshLayout, final View view) {
        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    //ArrayList<SelfImprovement_model> self_improvement = SelfImprovement_model.generate(getContext());
                    ArrayList<HistoryChallenge> all = new ArrayList<>();

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

                        all.add(new HistoryChallenge(
                                type, true, description, duration, status, goal, challengeName, versus, recipient, constDuration
                        ));
                    }

                    //adapter = new HistoryChallengeAdapter(all, getContext());
                    rvContacts.setAdapter(adapter);
                    rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}
