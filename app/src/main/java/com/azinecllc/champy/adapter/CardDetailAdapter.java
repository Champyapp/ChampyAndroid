package com.azinecllc.champy.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.model.SectionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SashaKhyzhun on 3/23/17.
 */

public class CardDetailAdapter extends RecyclerView.Adapter<CardDetailAdapter.ViewHolder> {

    private Context mContext;
    private List<SectionModel> mSections = new ArrayList<>();

    public CardDetailAdapter(Context context, List<SectionModel> items) {
        mContext = context;
        mSections = items;
    }

    @Override
    public CardDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_streak, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SectionModel section = mSections.get(position);

        if (position == mSections.size() - 1) {
            holder.borderView.setVisibility(View.INVISIBLE);
        } else {
            holder.borderView.setVisibility(View.VISIBLE);
        }

        holder.sectionLabelTextView.setText(section.getLabel());


        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        holder.sectionRecyclerView.setLayoutManager(layoutManager);
        SectionAdapter sectionAdapter = new SectionAdapter(mContext, section.getItems());
        holder.sectionRecyclerView.setAdapter(sectionAdapter);
    }

    @Override
    public int getItemCount() {
        return mSections.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sectionLabelTextView;
        private RecyclerView sectionRecyclerView;
        private View borderView;

        public ViewHolder(View itemView) {
            super(itemView);
            sectionLabelTextView = (TextView) itemView.findViewById(R.id.section_label);
            sectionRecyclerView = (RecyclerView) itemView.findViewById(R.id.section_rv);
            borderView = itemView.findViewById(R.id.border);
        }
    }
}
