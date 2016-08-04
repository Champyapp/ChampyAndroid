package com.example.ivan.champy_v2.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

/**
 * Created by ivan on 23.03.16.
 */
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

        /*SessionManager sessionManager = new SessionManager(_context);
        HashMap<String, String> champy = sessionManager.getChampyOptions();*/

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.history_simple, parent, false);

        TextView tvUserName = (TextView)contactView.findViewById(R.id.name);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/bebasneue.ttf");
        tvUserName.setTypeface(typeFace);

        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(contactView);
        //viewHolder.item_friends_open.setVisibility(View.VISIBLE);
       /* viewHolder.item_friends_close.setVisibility(View.GONE);

        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected.isEmpty()) {
                    selected.add(viewHolder.getAdapterPosition());
                    notifyItemChanged(viewHolder.getAdapterPosition());

                } else {
                    int oldSelected = selected.get(0);
                    selected.clear();
                    if (viewHolder.getAdapterPosition() == oldSelected) selected.add(-1);
                    notifyItemChanged(oldSelected);
                    // notifyItemChanged(viewHolder.getAdapterPosition());

                }
            }
        });*/

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final HistoryChallengeAdapter.ViewHolder viewHolder, int position) {
        HistoryChallenge contact = mContacts.get(position);
        TextView nameTextView = viewHolder.nameTextView;
        TextView level = viewHolder.level;

        SessionManager sessionManager = new SessionManager(_context);
        HashMap<String, String> champy = sessionManager.getChampyOptions();
        String userLevel = champy.get("level");
        level.setText("Level " + userLevel + " Champy");
        //Log.i(TAG, "FUCKING LEVEL = " + level);
        /*TextView level = viewholder.level;
        level.setText("Level " + contact.getLevel() + " Champy");*/

        String type = contact.getType();
        Log.i(TAG, "onBindViewHolder: " + type);

        switch (type) {
            case "Duels":
                nameTextView.setText("Duel Challenge");
                Glide.with(_context).load(R.drawable.duel_yellow).override(80, 80).into(viewHolder.image);
                break;
            case "Self Improvement":
                nameTextView.setText("Self-Improvement Challenge");
                Glide.with(_context).load(R.drawable.self_yellow).override(80, 80).into(viewHolder.image);
                break;
            case "Wake Up":
                nameTextView.setText("Wake Up");
                Glide.with(_context).load(R.drawable.wakeup_yellow).override(80, 80).into(viewHolder.image);
                break;
        }

        switch (contact.getStatus()) {
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

            nameTextView = (TextView) itemView.findViewById(R.id.name);
            level = (TextView) itemView.findViewById(R.id.level);
            image = (ImageView) itemView.findViewById(R.id.picture);
            wins = (ImageView) itemView.findViewById(R.id.imageView_wins_logo);
            total = (ImageView) itemView.findViewById(R.id.imageView_total_logo);

        }
    }

}
