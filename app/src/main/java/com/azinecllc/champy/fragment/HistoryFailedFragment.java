//package com.azinecllc.champy.fragment;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.azinecllc.champy.R;
//import com.azinecllc.champy.adapter.HistoryChallengeAdapter;
//import com.azinecllc.champy.model.HistoryChallenge;
//import com.azinecllc.champy.model.Challenge;
//import com.azinecllc.champy.utils.OfflineMode;
//
//import java.util.ArrayList;
//
//public class HistoryFailedFragment extends Fragment {
//
//    private SwipeRefreshLayout gSwipeRefreshLayout;
//    private OfflineMode offlineMode;
//    private ArrayList<HistoryChallenge> arr;
//    private HistoryChallengeAdapter adapter;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        offlineMode = OfflineMode.getInstance();
//        arr = new ArrayList<>();
//        adapter = new HistoryChallengeAdapter(arr, getContext());
//
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        final View view = inflater.inflate(R.layout.item_recycler, container, false);
//
//        loadHistoryFailed(view);
//
//        gSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
//        gSwipeRefreshLayout.setOnRefreshListener(() -> {
//            if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
//                gSwipeRefreshLayout.setRefreshing(true);
//                arr.clear();
//                loadHistoryFailed(view);
//                gSwipeRefreshLayout.setRefreshing(false);
//            }
//        });
//
//        return view;
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Runtime.getRuntime().runFinalization();
//        Runtime.getRuntime().gc();
//    }
//
//
//    private void loadHistoryFailed(View view) {
//
//        ArrayList<Challenge> self_improvement = Challenge.generateFailed(getContext());
//
//        for (int i = 0; i < self_improvement.size(); i++) {
//            Challenge item = self_improvement.get(i);
//            String constDuration = item.getConstDuration();
//            String challengeName = item.getChallengeName();
//            String recipient = item.getRecipient();
//            String description = item.getGoal();
//            String duration = item.getDays();
//            String status = item.getStatus();
//            String versus = item.getVersus();
//            String type = item.getType();
//            String goal = item.getGoal();
//
//            arr.add(new HistoryChallenge(
//                    type, false, description, duration, status, goal, challengeName, versus, recipient, constDuration
//            ));
//        }
//
//        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
//        rvContacts.setAdapter(adapter);
//        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
//    }
//
//
//
//}
