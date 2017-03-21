package com.azinecllc.champy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
        String status = itemCard.getChallengeStatus();
        String recipient = itemCard.getChallengeIsRecipient();
        System.out.println("");
        System.out.println("MainCardAdapter  MockData: | name: " + name
                + " days: " + days
                + " streak: " + streak
                + " percent: " + percent
                + " status: " + status
                + " recipient: " + recipient
        );

        boolean isRecipient = Boolean.parseBoolean(recipient);
        if (status.equals("pending")) {
            viewHolder.buttonDecline.setVisibility(View.VISIBLE);

            viewHolder.tvDay.setVisibility(View.INVISIBLE);
            viewHolder.tvStreak.setVisibility(View.INVISIBLE);
            viewHolder.progressBar.setVisibility(View.INVISIBLE);
            viewHolder.challengeDays.setVisibility(View.INVISIBLE);
            viewHolder.challengeStreak.setVisibility(View.INVISIBLE);
            viewHolder.challengePercent.setVisibility(View.INVISIBLE);
            if (isRecipient) {
                viewHolder.buttonAccept.setVisibility(View.VISIBLE);
            }
        }

        viewHolder.challengeName.setText(name);
        viewHolder.challengeDays.setText(days);
        viewHolder.challengeStreak.setText(streak);
        viewHolder.challengePercent.setText(String.format("%s%% complete", percent));
        viewHolder.progressBar.setProgress(Integer.parseInt(percent));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Integer.parseInt(itemCard.getChallengeColor()));
        gd.setCornerRadius(25);
        viewHolder.cardLayout.setBackgroundDrawable(gd);

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
        private TextView buttonAccept;
        private TextView buttonDecline;
        private TextView tvDay;
        private TextView tvStreak;
        private RelativeLayout itemParentLayout;
        private LinearLayout cardLayout;
        private ProgressBar progressBar;
        ViewHolder(View itemView) {
            super(itemView);
            challengeName = (TextView) itemView.findViewById(R.id.tv_challenge_name);
            challengeDays = (TextView) itemView.findViewById(R.id.text_view_day_n);
            challengeStreak = (TextView) itemView.findViewById(R.id.text_view_streak_n);
            challengePercent = (TextView) itemView.findViewById(R.id.tv_percent_complete);
            cardLayout = (LinearLayout) itemView.findViewById(R.id.card_layout);
            itemParentLayout = (RelativeLayout) itemView.findViewById(R.id.item_parent_layout);
            progressBar = (ProgressBar) itemView.findViewById(R.id.card_progress_bar);
            buttonAccept = (TextView) itemView.findViewById(R.id.button_accept);
            buttonDecline = (TextView) itemView.findViewById(R.id.button_decline);
            tvDay = (TextView) itemView.findViewById(R.id.text_view_day);
            tvStreak = (TextView) itemView.findViewById(R.id.text_view_streak);
        }
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    public String getSykaText() {
        return "Syka Blyat'";
    }


}

