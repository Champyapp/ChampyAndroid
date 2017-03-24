package com.azinecllc.champy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azinecllc.champy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SashaKhyzhun on 3/23/17.
 */

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    private Context mContext;
    private List<Integer> mItems = new ArrayList<>();

    public SectionAdapter(Context context, List<Integer> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemLabelTextView.setText(String.valueOf(mItems.get(position).toString()));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemLabelTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemLabelTextView = (TextView) itemView.findViewById(R.id.item_label);
        }
    }
}
