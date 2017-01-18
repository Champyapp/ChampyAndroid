package com.azinecllc.champy.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.model.HistoryChallenge;
import com.bumptech.glide.Glide;

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
        TextView nameTextView    = viewHolder.nameTextView;

        String constDuration  = itemRow.getConstDuration();
        String wakeUpTime     = itemRow.getChallengeName();
        String itemRowType    = itemRow.getType();
        String versus         = itemRow.getVersus();
        String goal           = itemRow.getGoal();

        tvPoint.setText(String.format("%s", constDuration + " days")); //constDuration + " days");
        tvPoint.setTypeface(typeFace);

        switch (itemRowType) {
            // removed .diskCacheStrategy(DiskCacheStrategy.ALL)
            case "Duel":
                nameTextView.setText(goal + " with " + versus);
                Glide.with(mContext).load(R.drawable.ic_duel_yellow).override(80, 80).into(viewHolder.pic);
                break;
            case "Wake Up":
                nameTextView.setText(wakeUpTime);
                Glide.with(mContext).load(R.drawable.ic_wakeup_yellow).override(80, 80).into(viewHolder.pic);
                break;
            case "Self-Improvement":
                nameTextView.setText(goal);
                Glide.with(mContext).load(R.drawable.ic_self_yellow).override(80, 80).into(viewHolder.pic);
                break;
            default:
                nameTextView.setText("Unknown");
                Glide.with(mContext).load(R.drawable.ic_champy_circle).override(80, 80).into(viewHolder.pic);
                break;
        }

        TextView challengeType = (TextView) viewHolder.itemView.findViewById(R.id.counterWins);
        switch (itemRowType) {
            case "Self-Improvement": challengeType.setText(mContext.getString(R.string.self_improvement));  break;
            case "Duel":             challengeType.setText(mContext.getString(R.string.duel_challenge));    break;
            case "Wake Up":          challengeType.setText(mContext.getString(R.string.wake_up_challenge)); break;
        }
        challengeType.setTypeface(typeFace);

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private ImageView pic;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.challengeNameInHistory);
            pic = (ImageView) itemView.findViewById(R.id.picture);

        }
    }

}
