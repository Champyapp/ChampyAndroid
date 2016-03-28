package com.example.ivan.champy_v2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by ivan on 23.03.16.
 */
public class HistoryAll extends Fragment{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_first, container, false);
        ArrayList<SelfImprovement_model> self_improvement = SelfImprovement_model.generate(getContext());
        ArrayList<HistoryChallenge> all = new ArrayList<>();
        for (int i=0; i<self_improvement.size(); i++)
        {
           SelfImprovement_model item = self_improvement.get(i);
           String descritption = item.getGoal();
           String duration = item.getDays();

           all.add(new HistoryChallenge("self",true,descritption, duration));
        }

        final RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        HistoryChallengeAdapter adapter = new HistoryChallengeAdapter(all, getContext());
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }
}
