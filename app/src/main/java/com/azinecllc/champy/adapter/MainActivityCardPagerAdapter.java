package com.azinecllc.champy.adapter;

import android.content.Context;
import android.view.View;

/**
 * this class-adapter is helper for our cards in main activity. actually for adapter for main activity.
 */
public abstract class MainActivityCardPagerAdapter {

    private Context context;

    MainActivityCardPagerAdapter(Context context) {
        this.context = context;
    }

    public abstract View getView(int position, View convertView);

    public abstract int dataCount();

    public Context getContext() {
        return context;
    }

    // need?
    public void setContext(Context context) {
        this.context = context;
    }

}
