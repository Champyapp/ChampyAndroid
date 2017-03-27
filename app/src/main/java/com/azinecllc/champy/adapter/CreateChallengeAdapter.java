package com.azinecllc.champy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.interfaces.OnCardClickListener;
import com.azinecllc.champy.model.CreateChallengeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @autor SashaKhyzhun
 * Created on 3/27/17.
 */

public class CreateChallengeAdapter extends RecyclerView.Adapter<CreateChallengeAdapter.ViewHolder> {

    private OnCardClickListener onCardClickListener;
    private List<CreateChallengeModel> mChallenges;
    private Context context;

    public CreateChallengeAdapter(List<CreateChallengeModel> mChallenges, Context context) {
        this.mChallenges = mChallenges;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View challengeView = inflater.inflate(R.layout.item_card_create_challenge, parent, false);
        return new ViewHolder(challengeView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CreateChallengeModel challenge = mChallenges.get(position);
        System.out.println("duration: " + challenge.getChallengeDuration());
        System.out.println();

        holder.challengeName.setText(challenge.getChallengeName());
        holder.challengeDays.setText(String.valueOf(challenge.getChallengeDuration()));
        holder.challengeStreak.setText(String.valueOf(challenge.getChallengeStreak()));

    }

    @Override
    public int getItemCount() {
        return mChallenges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView challengeName;
        private TextView challengeDays;
        private TextView challengeStreak;
        private RelativeLayout cardLayout;

        public ViewHolder(View cardView) {
            super(cardView);
            challengeName = (TextView) cardView.findViewById(R.id.tv_challenge_name);
            challengeDays = (TextView) cardView.findViewById(R.id.text_view_day_n);
            challengeStreak = (TextView) cardView.findViewById(R.id.text_view_streak_n);
            cardLayout = (RelativeLayout) cardView.findViewById(R.id.card_layout);
        }
    }

    /**
     * My custom OnCardClickListener interface
     *
     * @param onCardClickListener -
     */
    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

}
