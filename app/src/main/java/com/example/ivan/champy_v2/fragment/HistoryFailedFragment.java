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

/**
 * Created by ivan on 30.03.16.
 */
public class HistoryFailedFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_second, container, false);

        ArrayList<SelfImprovement_model> self_improvement = SelfImprovement_model.generateFailed(getContext());
        ArrayList<HistoryChallenge> allArray = new ArrayList<>();

        for (int i = 0; i < self_improvement.size(); i++) {
            SelfImprovement_model item = self_improvement.get(i);
            String description = item.getGoal();
            String duration = item.getDays();
            String status = item.getStatus();
            String type = item.getType();

            allArray.add(new HistoryChallenge(type, true, description, duration, status));
        }

        final RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        HistoryChallengeAdapter adapter = new HistoryChallengeAdapter(allArray, getContext());
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }
}
