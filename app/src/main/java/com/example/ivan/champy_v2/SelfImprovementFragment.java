package com.example.ivan.champy_v2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

/**
 * Created by ivan on 14.03.16.
 */
public class SelfImprovementFragment extends Fragment{
    public static final String ARG_PAGE = "ARG_PAGE";

    public static SelfImprovementFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);

        SelfImprovementFragment fragment = new SelfImprovementFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("stat", "Status: Created");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.item_row, container, false);
        Bundle args = this.getArguments();
        int position = args.getInt(ARG_PAGE);
        ViewPager viewPager = (ViewPager )getActivity().findViewById(R.id.pager);
        String name = "";
        String duration = "";
        String description = "";
        String challenge_id = "";
        DBHelper dbHelper = new DBHelper(getActivity());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        Cursor c = db.query("selfimprovement", null, null, null, null, null, null);
        Log.i("stat", "Status: "+position);
        int o = 0;
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int coldescription = c.getColumnIndex("description");
            int colduration = c.getColumnIndex("duration");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            do {
                o++;
                if (o == position+1) {
                    name = c.getString(nameColIndex);
                    description = c.getString(coldescription);
                    duration = c.getString(colduration);
                    challenge_id = c.getString(colchallenge_id);
                    break;
                }
            } while (c.moveToNext());
        } else
            Log.i("stat", "0 rows");
        c.close();
        Log.i("stat", "Name: "+description);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        EditText editText = (EditText)view.findViewById(R.id.goal);
        editText.setText(description);
        editText.setTypeface(typeface);
        editText = (EditText)view.findViewById(R.id.days);
        editText.setTypeface(typeface);
        int days = 0;
        if (duration != "") {
            days = Integer.parseInt(duration)/86400;
        }
        editText.setText("" + days);

        TextView textView = (TextView)view.findViewById(R.id.textView8);
        textView.setTypeface(typeface);

        Glide.with(getContext())
                .load(R.drawable.points)
                .override(120,120)
                .into((ImageView) view.findViewById(R.id.imageView14));
        editText = (EditText)view.findViewById(R.id.goal);
        description = editText.getText().toString();
        editText = (EditText)view.findViewById(R.id.days);
        days = Integer.parseInt(editText.getText().toString());
        Log.i("stat", "Description: "+description);
        ImageButton imageButton = (ImageButton)getActivity().findViewById(R.id.imageButton5);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Tap & Hold", Toast.LENGTH_SHORT).show();
            }
        });
        final String finalDescription = description;
        final int finalDays = days;
        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();
                cv.put("name", "Self Improvement");
                cv.put("description", finalDescription);
                cv.put("duration", "" + finalDays);
                db.insert("myChallenges", null, cv);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                return true;
            }
        });

        return view;
    }
}
