package com.azinecllc.champy.interfaces;

import android.view.View;
import android.widget.Adapter;

import com.azinecllc.champy.model.Cards;

/**
 * @autor SashaKhyzhun
 * Created on 3/20/17.
 */

public interface OnCardClickListener {
    void onClick(View v, Cards item);
}
