package com.example.ivan.champy_v2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by SONU on 25/09/15.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder  {
    public EditText goal;
    public EditText days;
    public TextView reward;


    public RecyclerViewHolder(View view) {
        super(view);
        // Find all views ids

        this.goal = (EditText) view
                .findViewById(R.id.et_goal);

        this.days = (EditText) view.findViewById(R.id.et_days);



    }



}