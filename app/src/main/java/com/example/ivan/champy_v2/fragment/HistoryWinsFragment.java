package com.example.ivan.champy_v2.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ivan.champy_v2.model.HistoryChallenge;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.adapter.HistoryChallengeAdapter;
import com.example.ivan.champy_v2.model.SelfImprovement_model;

import java.util.ArrayList;

public class HistoryWinsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_second, container, false);

        ArrayList<SelfImprovement_model> self_improvement = SelfImprovement_model.generateWins(getContext());
        ArrayList<HistoryChallenge> winsArray = new ArrayList<>();

        for (int i = 0; i < self_improvement.size(); i++) {
            SelfImprovement_model item = self_improvement.get(i);
            String description = item.getGoal();
            String duration = item.getDays();
            String status = item.getStatus();
            String goal = item.getGoal();
            String type = item.getType();
            String challengeName = item.getChallengeName();
            String versus = item.getVersus();

            winsArray.add(new HistoryChallenge(type, false /*was true*/, description, duration, status, goal, challengeName, versus));
        }

        final RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        HistoryChallengeAdapter adapter = new HistoryChallengeAdapter(winsArray, getContext());
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

}
