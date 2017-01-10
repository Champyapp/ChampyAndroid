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
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.model.HistoryChallenge;
import com.azinecllc.champy.model.SelfImprovement_model;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;

import java.util.ArrayList;

public class HistoryInProgressFragment extends Fragment {

    private SwipeRefreshLayout gSwipeRefreshLayout;
    private OfflineMode offlineMode;
    private ArrayList<SelfImprovement_model> self_improvement;
    private ArrayList<HistoryChallenge> all;
    private HistoryChallengeAdapter adapter;
    private RecyclerView rvContacts;
    private View gView;
    private String token, uID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offlineMode = OfflineMode.getInstance();
        all = new ArrayList<>();
        adapter = new HistoryChallengeAdapter(all, getContext());
        SessionManager sessionManager = SessionManager.getInstance(getContext());
        token = sessionManager.getToken();
        uID   = sessionManager.getUserId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history, container, false);

        loadInProgressHistory(view);

        gSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
        gSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
                all.clear();
                //rvContacts.getLayoutManager().removeAllViews();
                gSwipeRefreshLayout.setRefreshing(true);
                loadInProgressHistory(view);
                gSwipeRefreshLayout.setRefreshing(false);
            }
        });
        this.gView = view;


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


    private void loadInProgressHistory(View view) {
        self_improvement = SelfImprovement_model.generate(getContext());
        for (int i = 0; i < self_improvement.size(); i++) {
            SelfImprovement_model item = self_improvement.get(i);
            String challengeName = item.getChallengeName();
            String constDuration = item.getConstDuration();
            String recipient = item.getRecipient();
            String description = item.getGoal();
            String duration = item.getDays();
            String status = item.getStatus();
            String versus = item.getVersus();
            String type = item.getType();
            String goal = item.getGoal();

            all.add(new HistoryChallenge(
                    type, true, description, duration, status, goal, challengeName, versus, recipient, constDuration
            ));
        }

        rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));

    }

}
