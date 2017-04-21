package com.azinecllc.champy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.model.StreakModel;

/**
 * @autor SashaKhyzhun
 * Created on 3/23/17.
 * Adapter to control days inside the streak.
 */

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    private Context mContext;
    private StreakModel streakModel;
    private GradientDrawable gd;
    private int colorBgFinished = Color.parseColor("#0b5999");
    private int colorBgInProgress = Color.parseColor("#eeeeee");
    private int colorBgPending = Color.parseColor("#e4e4e4");
    private int colorTextPending = Color.parseColor("#e4e4e4");

    public SectionAdapter(Context context, StreakModel streakModel) {
        mContext = context;
        this.streakModel = streakModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_section, parent, false);
        gd = new GradientDrawable();
        gd.setCornerRadius(45);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //holder.itemLabelTextView.setText(String.valueOf(streakModel.getStreakSections().get(position)));
        holder.itemLabelTextView.setText(String.valueOf(streakModel.getStreakSections().get(position).getDayNumber()));

        int dayNumber = streakModel.getStreakSections().get(position).getDayNumber();
        int dayStatus = streakModel.getStreakSections().get(position).getDayStatus();
        int currDay = streakModel.getStreakSections().get(position).getDayCurrent();
        String streakStatus = streakModel.getStreakStatus();

        gd.setColor((streakStatus.equals("Finished") ? colorBgFinished : Color.WHITE));
        holder.itemLabelTextView.setTextColor((streakStatus.equals("Finished") ? Color.WHITE : colorTextPending));


        if (streakStatus.equals("In Progress")) {
            if (currDay > dayNumber) {
                gd.setColor(colorBgFinished);
            } else if (currDay == dayNumber) {
                gd.setColor(colorBgInProgress);
                holder.itemLabelTextView.setTextColor(mContext.getResources().getColor(R.color.primary_dark_indigo));
            } else if (currDay < dayNumber) {
                gd.setColor(Color.WHITE);
            }
        }
//        else if (streakStatus.equals("Finished")) {
//            gd.setColor(colorBgFinished);
//            holder.itemLabelTextView.setTextColor(Color.WHITE);
//        } else {
//            gd.setColor(Color.WHITE);
//            holder.itemLabelTextView.setTextColor(colorTextPending);
//        }


        holder.itemLabelTextView.setBackgroundDrawable(gd);

    }

    @Override
    public int getItemCount() {
        return streakModel.getStreakSections().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemLabelTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemLabelTextView = (TextView) itemView.findViewById(R.id.tv_day_n);
        }
    }
}
