package com.azinecllc.champy.interfaces;

import android.view.View;

import com.azinecllc.champy.model.CreateChallengeModel;

/**
 * @autor SashaKhyzhun
 * Created on 3/27/17.
 */

public interface RecyclerChallengesClickListener {
    void onItemClick(View v, CreateChallengeModel currentItem);
}
