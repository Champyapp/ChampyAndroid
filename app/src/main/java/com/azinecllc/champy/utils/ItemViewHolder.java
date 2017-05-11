package com.azinecllc.champy.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.model.FriendModel;

/**
 * @autor SashaKhyzhun
 * Created on 5/11/17.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public TextView friendName;
//    public TextView friendInProgress;
//    public TextView friendTotal;

    public ItemViewHolder(View itemView) {
        super(itemView);

        friendName = (TextView) itemView.findViewById(R.id.text_view_user_name);
//        friendInProgress = (TextView) itemView.findViewById(R.id.country_iso);
    }

    public void bind(FriendModel friendModel) {
        friendName.setText(friendModel.getName());
//        friendInProgress.setText(friendModel.getisoCode());
    }

}
