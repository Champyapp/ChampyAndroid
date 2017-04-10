package com.azinecllc.champy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.model.StreakModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SashaKhyzhun on 3/23/17.
 */

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    private Context mContext;
    private StreakModel streakModel;
    private GradientDrawable gd;

    public SectionAdapter(Context context, StreakModel streakModel) {
        mContext = context;
        this.streakModel = streakModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_section, parent, false);
        gd = new GradientDrawable();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemLabelTextView.setText(String.valueOf(streakModel.getItems().get(position)));

        if (streakModel.getStatus().equals("Finished")) {
            gd.setColor(Color.parseColor("#0b5999"));
            holder.itemLabelTextView.setTextColor(Color.WHITE);
        } else {
            gd.setColor((Color.WHITE));
            holder.itemLabelTextView.setTextColor(Color.parseColor("#e4e4e4"));
        }
        gd.setCornerRadius(45);
        holder.itemLabelTextView.setBackgroundDrawable(gd);


//        GradientDrawable gd = new GradientDrawable();
//        gd.setCornerRadius(25);
//
//        if (mItemsNumber.get(position).equals("finished")) {
//            gd.setColor(Color.parseColor("#acacac")); // origin: #cdced2
//        } else if (mItemsNumber.get(position).equals("in progress")) {
//            gd.setColor(Color.parseColor("#cdced2"));
//        }

        //holder.itemLabelTextView.setBackgroundDrawable(gd);

    }

    @Override
    public int getItemCount() {
        return streakModel.getItems().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemLabelTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemLabelTextView = (TextView) itemView.findViewById(R.id.tv_day_n);
        }
    }
}
