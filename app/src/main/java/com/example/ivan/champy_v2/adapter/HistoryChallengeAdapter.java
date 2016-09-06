package com.example.ivan.champy_v2.adapter;

import android.annotation.SuppressLint;
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

    final private String API_URL = "http://46.101.213.24:3007";
    final private String TAG = "myLogs";
    private List<HistoryChallenge> mContacts;
    private Context _context;

    public HistoryChallengeAdapter (List<HistoryChallenge> contacts, Context context){
        mContacts = contacts;
        _context = context;
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
        TextView level = viewHolder.level;

        String type = itemRow.getType();
        String challengeName = itemRow.getChallengeName();
        String goal = itemRow.getGoal();
        String description = itemRow.getDescription();
        String duration = itemRow.getDuration();
        String versus = itemRow.getVersus();

        // Here "type" in stupid string format. its works.
        switch (type) {
            // TODO: 29.08.2016 make auto size for text because '\n' is not good solution
            case "Duel":
                nameTextView.setText(description + ": " + duration + " days\nwith " + versus);
                Glide.with(_context).load(R.drawable.duel_yellow).override(80, 80).into(viewHolder.image);
                break;
            case "Wake Up":
                // challengeName because when we created "wake up challenge" we've set name
                // "Wake up at $hour : $minute during this period (in ChallengeController);
                nameTextView.setText(challengeName + ": " + duration + " days");
                Glide.with(_context).load(R.drawable.wakeup_yellow).override(80, 80).into(viewHolder.image);
                break;
            case "Self-Improvement":
                //nameTextView.setText(challengeName + " during " + duration + " days");
                nameTextView.setText(description + ": " + duration + " days");
                Glide.with(_context).load(R.drawable.self_yellow).override(80, 80).into(viewHolder.image);
                break;
            default:
                nameTextView.setText("Lost internet connection");
                break;
        }

        switch (itemRow.getStatus()) {
            case "started":
                nameTextView = (TextView) viewHolder.itemView.findViewById(R.id.counterWins);
                nameTextView.setText("In Progress");
                break;
            case "finished":
                nameTextView = (TextView) viewHolder.itemView.findViewById(R.id.counterWins);
                nameTextView.setText("Wins");
                break;
            case "failed":
                nameTextView = (TextView) viewHolder.itemView.findViewById(R.id.counterWins);
                nameTextView.setText("Failed");
                break;
        }

        SessionManager sessionManager = new SessionManager(_context);
        HashMap<String, String> champy = sessionManager.getChampyOptions();
        String userLevel = champy.get("level");
        level.setText("Level " + userLevel + " Champy");

        Typeface typeFace = Typeface.createFromAsset(_context.getAssets(), "fonts/bebasneue.ttf");
        nameTextView.setTypeface(typeFace);

        Glide.with(_context).load(R.drawable.challenges).override(40, 40).into(viewHolder.wins);
        Glide.with(_context).load(R.drawable.challenge) .override(40, 40).into(viewHolder.total);

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView level;
        public ImageView image;
        public ImageView challenges;
        public ImageView wins;
        public ImageView total;
        public ImageView mwins;
        public ImageView dop;
        public ImageView mchallenges;
        public ImageView mtotal;
        public ImageButton block;
        public ImageButton add;
        public RelativeLayout simple;
        public RelativeLayout info;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.challengeNameInHistory);
            level = (TextView) itemView.findViewById(R.id.level);
            image = (ImageView) itemView.findViewById(R.id.picture);
            wins = (ImageView) itemView.findViewById(R.id.imageView_wins_logo);
            total = (ImageView) itemView.findViewById(R.id.imageView_total_logo);

        }
    }

}
