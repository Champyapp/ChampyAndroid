package com.example.ivan.champy_v2;

import android.content.Context;
import android.view.View;

/**
 * Created by ivan on 09.12.15.
 */
public abstract class CustomPagerAdapter {
    private Context context;

    public CustomPagerAdapter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public abstract View getView(int position, View convertView);

    public abstract int dataCount();
}
