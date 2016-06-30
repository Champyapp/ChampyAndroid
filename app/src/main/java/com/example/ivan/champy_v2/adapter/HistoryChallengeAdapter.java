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
import com.example.ivan.champy_v2.HistoryChallenge;
import com.example.ivan.champy_v2.R;

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

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.history_simple, parent, false);

        TextView textView = (TextView)contactView.findViewById(R.id.name);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/bebasneue.ttf");
        textView.setTypeface(typeFace);


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
    public void onBindViewHolder(final HistoryChallengeAdapter.ViewHolder viewholder, int position) {
        HistoryChallenge contact = mContacts.get(position);
        TextView textView = viewholder.nameTextView;
        String type = contact.getType();
        if (type.equals("Wake Up")) {
            textView.setText("Wake Up");
            Glide.with(_context).load(R.drawable.wakeupcolor).override(80, 80).into(viewholder.image);
        } else {
            textView.setText("Self-Improvement Challenge");
            Glide.with(_context).load(R.drawable.selfimprovementcolor).override(80, 80).into(viewholder.image);
        }

        if (contact.getStatus().equals("started")) {
            textView = (TextView)viewholder.itemView.findViewById(R.id.counterWins);
            textView.setText("In Progress");
        } else {
            textView = (TextView)viewholder.itemView.findViewById(R.id.counterWins);
            textView.setText("Failed");
        }
        Typeface typeFace = Typeface.createFromAsset(_context.getAssets(), "fonts/bebasneue.ttf");
        textView.setTypeface(typeFace);

        Glide.with(_context).load(R.drawable.challenges).override(40, 40).into(viewholder.wins);
        Glide.with(_context).load(R.drawable.challenge) .override(40, 40).into(viewholder.total);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView image;
        public ImageView challenges;
        public ImageView wins;
        public ImageView total;
        public ImageButton block;
        public ImageButton add;
        public ImageView mchallenges;
        public ImageView mwins;
        public ImageView mtotal;
        public RelativeLayout simple;
        public RelativeLayout info;
        public ImageView dop;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.picture);
            wins = (ImageView) itemView.findViewById(R.id.imageView_wins_logo);
            total = (ImageView) itemView.findViewById(R.id.imageView_total_logo);

        }
    }

}
