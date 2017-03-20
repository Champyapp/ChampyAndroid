package com.azinecllc.champy.adapter;

import android.content.Context;
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
import com.azinecllc.champy.utils.SessionManager;

import java.util.List;
import java.util.Random;

import static com.azinecllc.champy.Champy.getContext;


/**
 * @autor SashaKhyzhun
 * Created on 3/20/17.
 */

public class MainCardAdapter extends RecyclerView.Adapter<MainCardAdapter.ViewHolder> implements View.OnClickListener {

    private List<Cards> mCards;
    private Context mContext;
    private OnCardClickListener onCardClickListener;
    private SessionManager sessionManager;

    public MainCardAdapter(List<Cards> cardsList, Context context) {
        mCards = cardsList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.fragment_main, parent, false);
        sessionManager = SessionManager.getInstance(getContext());

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final MainCardAdapter.ViewHolder viewHolder, int position) {
        Cards itemCard = mCards.get(position);

        Random r = new Random();
        int low = 0;
        int high = 100;


        String mockStreak = String.valueOf(r.nextInt(high - low) + low);
        String mockPercent = String.valueOf(r.nextInt(high - low) + low);
        String mockDays = String.valueOf(r.nextInt(high - low) + low);

        String name = itemCard.getChallengeName();
        String days = itemCard.getChallengeDays();
        String streak = itemCard.getChallengeStreak();
        String percent = itemCard.getChallengePercent();


        viewHolder.challengeName.setText(name);
        viewHolder.challengeDays.setText(mockDays);
        viewHolder.challengeStreak.setText(mockStreak);
        viewHolder.challengePercent.setText(mockPercent);
        //viewHolder.cardLayout.setBackgroundColor(Color.RED);


        viewHolder.itemParentLayout.setOnClickListener(this);

    }


    @Override
    public int getItemCount() {
        return mCards.size();
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

