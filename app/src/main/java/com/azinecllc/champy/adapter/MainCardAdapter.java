package com.azinecllc.champy.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.CardDetailActivity;
import com.azinecllc.champy.interfaces.RecyclerCardClickListener;
import com.azinecllc.champy.model.CardChallenges;

import java.util.List;

/**
 * @autor SashaKhyzhun
 * Created on 3/20/17.
 */

public class MainCardAdapter extends RecyclerView.Adapter<MainCardAdapter.ViewHolder> {

    public static final String TAG = "MainCardAdapter";
    private List<CardChallenges> mCardChallengesList;
    private Context mContext;
    private RecyclerCardClickListener onCardClickListener;

    public MainCardAdapter(List<CardChallenges> cardChallengesList, Context context) {
        mCardChallengesList = cardChallengesList;
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View cardView = inflater.inflate(R.layout.item_card_challenges, parent, false);
        Log.i(TAG, "onCreateViewHolder: ");
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(final MainCardAdapter.ViewHolder viewHolder, int position) {
        CardChallenges itemCard = mCardChallengesList.get(position);

        String duration = itemCard.getChallengeDuration();          // 21
        String day = itemCard.getCurrentDay();                      // current day
        String streak = itemCard.getChallengeStreak();              // <?>
        String percent = itemCard.getChallengePercent();            // <n%>
        String status = itemCard.getChallengeStatus();              // started-pending
        String recipient = itemCard.getChallengeIsRecipient();      // true / false
        String name = itemCard.getChallengeName();                  // no tv
        String type = itemCard.getChallengeType();                  // self-duel-wake
        String versus = itemCard.getChallengeVersus();              // versus name

//        System.out.println("___________________________________");
//        System.out.println("duration  : " + duration);
//        System.out.println("day       : " + day);
//        System.out.println("streak    : " + streak);
//        System.out.println("percent   : " + percent);
//        System.out.println("status    : " + status);
//        System.out.println("recipient : " + recipient);
//        System.out.println("name      : " + name);
//        System.out.println("type      : " + type);
//        System.out.println("versus    : " + versus);
//        System.out.println("___________________________________");

        if (status.equals("pending")) {
            viewHolder.tvDay.setVisibility(View.INVISIBLE);
            viewHolder.tvStreak.setVisibility(View.INVISIBLE);
            viewHolder.progressBar.setVisibility(View.INVISIBLE);
            viewHolder.challengeDays.setVisibility(View.INVISIBLE);
            viewHolder.challengeStreak.setVisibility(View.INVISIBLE);
            viewHolder.challengePercent.setVisibility(View.INVISIBLE);

            if (recipient.equals("true")) {
                viewHolder.buttonDecline.setVisibility(View.VISIBLE);
                viewHolder.buttonAccept.setVisibility(View.VISIBLE);
            } else {
                viewHolder.buttonCancel.setVisibility(View.VISIBLE);
            }
        }


        viewHolder.buttonDecline.setOnClickListener(v -> {
            Toast.makeText(mContext, "Decline", Toast.LENGTH_SHORT).show();
        });
        viewHolder.buttonAccept.setOnClickListener(v -> {
            Toast.makeText(mContext, "Accept", Toast.LENGTH_SHORT).show();
        });
        viewHolder.buttonCancel.setOnClickListener(v -> {
            Toast.makeText(mContext, "Cancel", Toast.LENGTH_SHORT).show();
        });

        viewHolder.challengeName.setText(name);
        viewHolder.challengeDays.setText(day);
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
                Intent intent = new Intent(mContext, CardDetailActivity.class);
                intent.putExtra("mockName", itemCard.getChallengeName());
                intent.putExtra("mockStreak", itemCard.getChallengeStreak());
                intent.putExtra("mockPercent", itemCard.getChallengePercent());
                intent.putExtra("mockDay", itemCard.getChallengeDuration());
                mContext.startActivity(intent);
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
        private TextView buttonCancel;
        private TextView tvDay;
        private TextView tvStreak;
        private RelativeLayout cardLayout;
        private ProgressBar progressBar;
        //private RelativeLayout itemParentLayout;

        ViewHolder(View cardView) {
            super(cardView);
            challengeName = (TextView) cardView.findViewById(R.id.tv_challenge_name);
            challengeDays = (TextView) cardView.findViewById(R.id.text_view_day_n);
            challengeStreak = (TextView) cardView.findViewById(R.id.text_view_streak_n);
            challengePercent = (TextView) cardView.findViewById(R.id.tv_percent_complete);
            buttonAccept = (TextView) cardView.findViewById(R.id.button_accept);
            buttonDecline = (TextView) cardView.findViewById(R.id.button_decline);
            buttonCancel = (TextView) cardView.findViewById(R.id.button_cancel);
            tvDay = (TextView) cardView.findViewById(R.id.text_view_day);
            tvStreak = (TextView) cardView.findViewById(R.id.text_view_streak);
            progressBar = (ProgressBar) cardView.findViewById(R.id.card_progress_bar);
            cardLayout = (RelativeLayout) cardView.findViewById(R.id.card_layout);
            //itemParentLayout = (RelativeLayout) cardView.findViewById(R.id.item_parent_layout);
        }
    }

    /**
     * My custom RecyclerCardClickListener interface
     * @param onCardClickListener -
     */
    public void setOnCardClickListener(RecyclerCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }



}

