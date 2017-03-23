package com.azinecllc.champy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * @autor SashaKhyzhun
 * Created on 3/23/17.
 */

public class CardDetailAdapter extends RecyclerView.Adapter<CardDetailAdapter.ViewHolder> {


    @Override
    public CardDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(CardDetailAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);

        }

    }


}
