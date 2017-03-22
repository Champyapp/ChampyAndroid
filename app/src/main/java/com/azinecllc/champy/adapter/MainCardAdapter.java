package com.azinecllc.champy.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.interfaces.OnCardClickListener;
import com.azinecllc.champy.model.CardChallenges;

import java.util.List;

/**
 * @autor SashaKhyzhun
 * Created on 3/20/17.
 */

public class MainCardAdapter extends RecyclerView.Adapter<MainCardAdapter.ViewHolder> {

    private List<CardChallenges> mCardChallengesList;
    private Context mContext;
    private OnCardClickListener onCardClickListener;

    public MainCardAdapter(List<CardChallenges> cardChallengesList, Context context) {
        mCardChallengesList = cardChallengesList;
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View cardView = inflater.inflate(R.layout.item_card_challenges, parent, false);

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(final MainCardAdapter.ViewHolder viewHolder, int position) {
        CardChallenges itemCard = mCardChallengesList.get(position);

        String days = itemCard.getChallengeDay();                   // 21
        String streak = itemCard.getChallengeStreak();              // <?>
        String percent = itemCard.getChallengePercent();            // <?>
        String status = itemCard.getChallengeStatus();              // started-pending
        String recipient = itemCard.getChallengeIsRecipient();      // true / false
        String type = itemCard.getChallengeType();                  // self-duel-wake
        String versus = itemCard.getChallengeVersus();              // versus name
        String name = itemCard.getChallengeName();                  // no tv

        System.out.println("_________________________________________");
        System.out.println("MainCardAdapter  MockData: | name: " + name + " days: " + days
                + " streak: " + streak + " percent: " + percent + " status: " + status
                + " recipient: " + recipient + " type: " + type + " versus: " + versus
        );

        if (status.equals("pending")) {
            viewHolder.tvDay.setVisibility(View.INVISIBLE);
            viewHolder.tvStreak.setVisibility(View.INVISIBLE);
            viewHolder.progressBar.setVisibility(View.INVISIBLE);
            viewHolder.challengeDays.setVisibility(View.INVISIBLE);
            viewHolder.challengeStreak.setVisibility(View.INVISIBLE);
            viewHolder.challengePercent.setVisibility(View.INVISIBLE);

            viewHolder.buttonDecline.setVisibility(View.VISIBLE);
            viewHolder.buttonDecline.setOnClickListener(v -> {
                Toast.makeText(mContext, "Decline", Toast.LENGTH_SHORT).show();
            });

            viewHolder.buttonAccept.setVisibility(View.VISIBLE);
            viewHolder.buttonAccept.setOnClickListener(v -> {
                Toast.makeText(mContext, "Accept", Toast.LENGTH_SHORT).show();
            });

            if (recipient.equals("false")) viewHolder.buttonAccept.setVisibility(View.INVISIBLE);

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

        viewHolder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCardClickListener.onClick(v, itemCard);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mCardChallengesList.size();
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

        ViewHolder(View cardView) {
            super(cardView);
            challengeName = (TextView) cardView.findViewById(R.id.tv_challenge_name);
            challengeDays = (TextView) cardView.findViewById(R.id.text_view_day_n);
            challengeStreak = (TextView) cardView.findViewById(R.id.text_view_streak_n);
            challengePercent = (TextView) cardView.findViewById(R.id.tv_percent_complete);
            cardLayout = (LinearLayout) cardView.findViewById(R.id.card_layout);
            itemParentLayout = (RelativeLayout) cardView.findViewById(R.id.item_parent_layout);
            progressBar = (ProgressBar) cardView.findViewById(R.id.card_progress_bar);
            buttonAccept = (TextView) cardView.findViewById(R.id.button_accept);
            buttonDecline = (TextView) cardView.findViewById(R.id.button_decline);
            tvDay = (TextView) cardView.findViewById(R.id.text_view_day);
            tvStreak = (TextView) cardView.findViewById(R.id.text_view_streak);
        }
    }

    /**
     * My custom OnCardClickListener interface
     * @param onCardClickListener -
     */
    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }



}

