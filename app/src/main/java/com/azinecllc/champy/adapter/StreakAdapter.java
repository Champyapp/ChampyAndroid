package com.azinecllc.champy.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.model.StreakModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SashaKhyzhun on 3/23/17.
 */

public class StreakAdapter extends RecyclerView.Adapter<StreakAdapter.ViewHolder> {

    private Context mContext;
    private List<StreakModel> mStreaks = new ArrayList<>();

    public StreakAdapter(Context context, List<StreakModel> items) {
        mContext = context;
        mStreaks = items;
    }

    @Override
    public StreakAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_streak, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StreakModel streakModel = mStreaks.get(position);

        if (position == mStreaks.size() - 1) {
            holder.borderView.setVisibility(View.INVISIBLE);
        } else {
            holder.borderView.setVisibility(View.VISIBLE);
        }

        holder.sectionLabelTextView.setText(streakModel.getStreakLabel());
        holder.tvStreakStatus.setText(streakModel.getStreakStatus());

//        switch (streakModel.getStreakStatus()) {
//            case "finished":
//                System.out.println("finished");
//                break;
//            case "in progress":
//                System.out.println("in progress");
//                break;
//            case "pending":
//                System.out.println("pending");
//                break;
//        }

        if (streakModel.getStreakStatus().equals("Finished")) {
            holder.ivStreakStatus.setVisibility(View.VISIBLE);
            holder.tvStreakStatus.setVisibility(View.INVISIBLE);
        } else {
            holder.ivStreakStatus.setVisibility(View.INVISIBLE);
            holder.tvStreakStatus.setVisibility(View.VISIBLE);
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        holder.sectionRecyclerView.setLayoutManager(layoutManager);

        SectionAdapter sectionAdapter = new SectionAdapter(mContext, streakModel);
        holder.sectionRecyclerView.setAdapter(sectionAdapter);
    }

    @Override
    public int getItemCount() {
        return mStreaks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sectionLabelTextView;
        private RecyclerView sectionRecyclerView;
        private View borderView;
        private TextView tvStreakStatus;
        private ImageView ivStreakStatus;

        ViewHolder(View itemView) {
            super(itemView);
            sectionLabelTextView = (TextView) itemView.findViewById(R.id.text_view_streak_n);
            sectionRecyclerView = (RecyclerView) itemView.findViewById(R.id.rv_streak);
            borderView = itemView.findViewById(R.id.border);
            tvStreakStatus = (TextView) itemView.findViewById(R.id.text_view_streak_status);
            ivStreakStatus = (ImageView) itemView.findViewById(R.id.image_view_streak_status);

        }
    }
}
