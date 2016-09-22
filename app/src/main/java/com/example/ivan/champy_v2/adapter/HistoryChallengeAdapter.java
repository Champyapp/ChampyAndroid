package com.example.ivan.champy_v2.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.model.HistoryChallenge;
import com.example.ivan.champy_v2.R;

import java.util.HashMap;
import java.util.List;

public class HistoryChallengeAdapter extends RecyclerView.Adapter<HistoryChallengeAdapter.ViewHolder> {

    private List<HistoryChallenge> mContacts;
    private Context mContext;

    public HistoryChallengeAdapter (List<HistoryChallenge> contacts, Context context){
        mContacts = contacts;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.history_simple, parent, false);

        TextView tvUserName = (TextView)contactView.findViewById(R.id.challengeNameInHistory);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/bebasneue.ttf");
        tvUserName.setTypeface(typeFace);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final HistoryChallengeAdapter.ViewHolder viewHolder, int position) {
        HistoryChallenge itemRow = mContacts.get(position);
        TextView nameTextView = viewHolder.nameTextView;
        String itemRowType = itemRow.getType();
        String challengeName = itemRow.getChallengeName();
        String duration = itemRow.getDuration();
        String versus = itemRow.getVersus();

        // Here "type" in stupid string format. its works.
        switch (itemRowType) {
            // TODO: 29.08.2016 make auto size for text because '\n' is not good solution
            case "Duel":
                nameTextView.setText(challengeName + ": " + duration + " days\nwith " + versus);
                Glide.with(mContext).load(R.drawable.duel_yellow).override(80, 80).into(viewHolder.image);
                break;
            case "Wake Up":
                nameTextView.setText(challengeName + " During this period:" + duration + " days");
                Glide.with(mContext).load(R.drawable.wakeup_yellow).override(80, 80).into(viewHolder.image);
                break;
            case "Self-Improvement":
                nameTextView.setText(challengeName + " during this period: " + duration + " days");
                Glide.with(mContext).load(R.drawable.self_yellow).override(80, 80).into(viewHolder.image);
                break;
            default:
                nameTextView.setText(R.string.lostInternetConnection);
                break;
        }

        switch (itemRow.getStatus()) {
            case "started":
                nameTextView = (TextView) viewHolder.itemView.findViewById(R.id.counterWins);
                nameTextView.setText(R.string.inProgress);
                break;
            case "finished":
                nameTextView = (TextView) viewHolder.itemView.findViewById(R.id.counterWins);
                nameTextView.setText(R.string.wins);
                break;
            case "failed":
                nameTextView = (TextView) viewHolder.itemView.findViewById(R.id.counterWins);
                nameTextView.setText(R.string.failed);
                break;
        }

        Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/bebasneue.ttf");
        nameTextView.setTypeface(typeFace);

        Glide.with(mContext).load(R.drawable.challenges).override(40, 40).into(viewHolder.wins);
        Glide.with(mContext).load(R.drawable.challenge) .override(40, 40).into(viewHolder.total);

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        public TextView level;
        public ImageView image;
        public ImageView challenges;
        public ImageView wins;
        public ImageView total;
        public ImageButton add;
        public RelativeLayout info;

        ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.challengeNameInHistory);
            level = (TextView) itemView.findViewById(R.id.level);
            image = (ImageView) itemView.findViewById(R.id.picture);
            wins = (ImageView) itemView.findViewById(R.id.imageView_wins_logo);
            total = (ImageView) itemView.findViewById(R.id.imageView_total_logo);

        }
    }

}
