package com.azinecllc.champy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.interfaces.OnCardClickListener;
import com.azinecllc.champy.model.Cards;

import java.util.List;

/**
 * @autor SashaKhyzhun
 * Created on 3/20/17.
 */

public class MainCardAdapter extends RecyclerView.Adapter<MainCardAdapter.ViewHolder> implements View.OnClickListener {

    private List<Cards> mCardsList;
    private Context mContext;
    private OnCardClickListener onCardClickListener;

    public MainCardAdapter(List<Cards> cardsList, Context context) {
        mCardsList = cardsList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.fragment_main, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final MainCardAdapter.ViewHolder viewHolder, int position) {
        Cards itemCard = mCardsList.get(position);

        String name = itemCard.getChallengeName();
        String days = itemCard.getChallengeDays();
        String streak = itemCard.getChallengeStreak();
        String percent = itemCard.getChallengePercent();
        System.out.println("MainCardAdapter MockData: | name: " + name
                + " days: " + days
                + " streak: " + streak
                + " percent: " + percent);

        viewHolder.challengeName.setText(name);
        viewHolder.challengeDays.setText(days);
        viewHolder.challengeStreak.setText(streak);
        viewHolder.challengePercent.setText(String.format("%s%% complete", percent));
        viewHolder.cardLayout.setBackgroundColor(Integer.parseInt(itemCard.getChallengeColor()));


        viewHolder.itemParentLayout.setOnClickListener(this);

    }


    @Override
    public int getItemCount() {
        return mCardsList.size();
    }

    @Override
    public void onClick(View v) {
        onCardClickListener.onClick();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView challengeName;
        private TextView challengeDays;
        private TextView challengeStreak;
        private TextView challengePercent;
        private RelativeLayout itemParentLayout;
        private LinearLayout cardLayout;

        ViewHolder(View itemView) {
            super(itemView);
            challengeName = (TextView) itemView.findViewById(R.id.tv_challenge_name);
            challengeDays = (TextView) itemView.findViewById(R.id.text_view_day_n);
            challengeStreak = (TextView) itemView.findViewById(R.id.text_view_streak_n);
            challengePercent = (TextView) itemView.findViewById(R.id.tv_percent_complete);
            cardLayout = (LinearLayout) itemView.findViewById(R.id.card_layout);
            itemParentLayout = (RelativeLayout) itemView.findViewById(R.id.item_parent_layout);
        }


    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }


}

