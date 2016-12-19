package com.azinecllc.champy.adapter;

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

import com.azinecllc.champy.R;
import com.azinecllc.champy.model.HistoryChallenge;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class HistoryChallengeAdapter extends RecyclerView.Adapter<HistoryChallengeAdapter.ViewHolder> {

    private List<HistoryChallenge> mContacts;
    private Context mContext;
    private Typeface typeFace;
    private TextView tvPoint;

    public HistoryChallengeAdapter (List<HistoryChallenge> contacts, Context context){
        mContacts = contacts;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_history, parent, false);

        typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/bebasneue.ttf");
        TextView tvUserName = (TextView)contactView.findViewById(R.id.challengeNameInHistory);
        tvPoint = (TextView)contactView.findViewById(R.id.counterInProgress);
        tvUserName.setTypeface(typeFace);
        tvPoint.setTypeface(typeFace);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final HistoryChallengeAdapter.ViewHolder viewHolder, int position) {
        HistoryChallenge itemRow = mContacts.get(position);
        TextView nameTextView = viewHolder.nameTextView;
        String constDuration = itemRow.getConstDuration();
        String wakeUpTime = itemRow.getChallengeName();
        String itemRowType = itemRow.getType();
        String versus = itemRow.getVersus();
        String goal = itemRow.getGoal();

        tvPoint.setText(String.format("%s", constDuration + " days")); //constDuration + " days");
        tvPoint.setTypeface(typeFace);

        switch (itemRowType) {
            case "Duel":
                nameTextView.setText(goal + " with " + versus);
                Glide.with(mContext).load(R.drawable.ic_duel_yellow).diskCacheStrategy(DiskCacheStrategy.ALL).override(80, 80).into(viewHolder.image);
                break;
            case "Wake Up":
                nameTextView.setText(wakeUpTime);
                Glide.with(mContext).load(R.drawable.ic_wakeup_yellow).diskCacheStrategy(DiskCacheStrategy.ALL).override(80, 80).into(viewHolder.image);
                break;
            case "Self-Improvement":
                nameTextView.setText(goal);
                Glide.with(mContext).load(R.drawable.ic_self_yellow).diskCacheStrategy(DiskCacheStrategy.ALL).override(80, 80).into(viewHolder.image);
                break;
            default:
                nameTextView.setText(R.string.lostInternetConnection);
                Glide.with(mContext).load(R.drawable.icon_champy).override(80, 80).into(viewHolder.image);
                break;
        }

        TextView challengeType = (TextView) viewHolder.itemView.findViewById(R.id.counterWins);
//
        switch (itemRowType) {
            case "Self-Improvement":
                challengeType.setText(mContext.getString(R.string.self_improvement)); //"Self-Improvement";
                break;
            case "Duel":
                challengeType.setText(mContext.getString(R.string.duel_challenge)); //"Duel challenge");
                break;
            case "Wake Up":
                challengeType.setText(mContext.getString(R.string.wake_up_challenge)); //"Wake Up challenge");

        }
        challengeType.setTypeface(typeFace);

        Glide.with(mContext).load(R.drawable.ic_score_progress).override(40, 40).into(viewHolder.wins);
        Glide.with(mContext).load(R.drawable.ic_score_total) .override(40, 40).into(viewHolder.total);

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
            image = (ImageView) itemView.findViewById(R.id.picture);
            wins = (ImageView) itemView.findViewById(R.id.imageView_wins_logo);
            total = (ImageView) itemView.findViewById(R.id.imageView_total_logo);

        }
    }

}
