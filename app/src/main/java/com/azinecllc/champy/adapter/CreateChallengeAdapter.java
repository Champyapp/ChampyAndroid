package com.azinecllc.champy.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.CreateChallengeDetailsActivity;
import com.azinecllc.champy.interfaces.RecyclerCardClickListener;
import com.azinecllc.champy.interfaces.RecyclerChallengesClickListener;
import com.azinecllc.champy.model.CreateChallengeModel;

import java.util.List;

/**
 * @autor SashaKhyzhun
 * Created on 3/27/17.
 */

public class CreateChallengeAdapter extends RecyclerView.Adapter<CreateChallengeAdapter.ViewHolder> {

    private RecyclerChallengesClickListener onCardClickListener;
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

        holder.challengeName.setText(challenge.getChallengeName());
        holder.challengeDays.setText(String.valueOf(challenge.getChallengeDuration()));
        holder.challengeStreak.setText(String.valueOf(challenge.getChallengeStreak()));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#cdced2")); // origin: #cdced2
        gd.setCornerRadius(25);
        holder.cardLayout.setBackgroundDrawable(gd);

        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateChallengeDetailsActivity.class);
                intent.putExtra("name", challenge.getChallengeName());
                context.startActivity(intent);
            }
        });

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
     * My custom RecyclerCardClickListener interface
     *
     * @param onCardClickListener -
     */
    public void setOnCardClickListener(RecyclerChallengesClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

}
