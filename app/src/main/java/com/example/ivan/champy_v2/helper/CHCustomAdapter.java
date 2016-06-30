package com.example.ivan.champy_v2.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.adapter.CustomPagerAdapter;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.model.SelfImprovement_model;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.round;

public class CHCustomAdapter extends CustomPagerAdapter {

    public CHCustomAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView) {
        return null;
    }

    @Override
    public int dataCount() {
        return 0;
    }
}