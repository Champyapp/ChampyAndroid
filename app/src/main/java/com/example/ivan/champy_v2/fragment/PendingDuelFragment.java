package com.example.ivan.champy_v2.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.R;

/**
 * Created by ivan on 14.03.16.
 */
public class PendingDuelFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    public static PendingDuelFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);

        PendingDuelFragment fragment = new PendingDuelFragment();
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.item_pending_duel, container, false);
        String versus = "";
        String duration = "";
        String description = "";
        String challenge_id = "";
        String status = "";
        String recipient = "";

        DBHelper dbHelper = new DBHelper(getActivity());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        final Bundle args = this.getArguments();
        Cursor c = db.query("pending_duel", null, null, null, null, null, null);
        int position = args.getInt(ARG_PAGE);
        Log.i("stat", "Status: " + position);
        int o = 0;
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int versusColIndex = c.getColumnIndex("versus");
            int coldescription = c.getColumnIndex("description");
            int colduration = c.getColumnIndex("duration");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            int colrecipient = c.getColumnIndex("recipient");
            do {
                o++;
                if (o > position + 1) break;
                if (o == position + 1) {
                    versus = c.getString(versusColIndex);
                    description = c.getString(coldescription);
                    duration = c.getString(colduration);
                    challenge_id = c.getString(colchallenge_id);
                    recipient = c.getString(colrecipient);
                }
            } while (c.moveToNext());
        }
        c.close();
        TextView textView = (TextView)view.findViewById(R.id.tvYouVsSomebody);
        if (recipient.equals("true")) {
            textView.setText(versus + " vs YOU");
        } else {
            textView.setText("YOU vs " + versus);
            view.findViewById(R.id.imageButtonCancelBattle).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.imageButtonAcceptBattle).setVisibility(View.INVISIBLE);
        }

        Glide.with(getContext()).load(R.drawable.points).override(120, 120).into((ImageView) view.findViewById(R.id.imageViewAcceptButton));
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        textView.setTypeface(typeface);


      //  int days = Integer.parseInt(duration);
        int days = 21;
        textView = (TextView)view.findViewById(R.id.textViewDuring);
        textView.setText("During " + days + "days");

        TextView etGoal = (TextView)view.findViewById(R.id.tv_goal);
        etGoal.setText(description);
        return view;
    }

}
