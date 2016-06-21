package com.example.ivan.champy_v2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.RecyclerViewHolder;
import com.example.ivan.champy_v2.model.SelfImprovement_model;

import java.util.ArrayList;

/**
 * Created by SONU on 25/09/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    // RecyclerView will extend to recyclerView adapter
    private ArrayList<SelfImprovement_model> arrayList;
    private Context context;

    public RecyclerViewAdapter(Context context, ArrayList<SelfImprovement_model> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        final SelfImprovement_model model = arrayList.get(position);
        RecyclerViewHolder mainHolder = (RecyclerViewHolder) holder;// holder
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.item_row, viewGroup, false);
        RecyclerViewHolder listHolder = new RecyclerViewHolder(mainGroup);
        return listHolder;

    }

}