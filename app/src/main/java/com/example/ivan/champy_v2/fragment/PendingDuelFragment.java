package com.example.ivan.champy_v2.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.AlarmReceiver;
import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.duel.Duel;
import com.example.ivan.champy_v2.interfaces.SingleInProgress;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;
import com.example.ivan.champy_v2.single_inprogress.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.example.ivan.champy_v2.model.SelfImprovement_model.generate;

/**
 * Fragment отвечающий за принятие или отмену дуели (то самое секретное меню)
 * table: pending_duel
 */
public class PendingDuelFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    Activity firstActivity;

    public static PendingDuelFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PendingDuelFragment fragment = new PendingDuelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        final TextView tvDays = (TextView)view.findViewById(R.id.textViewDuring);
        final TextView tvGoal = (TextView)view.findViewById(R.id.tv_goal);
        TextView tvUserVsUser = (TextView)view.findViewById(R.id.tvYouVsSomebody);

        if (recipient.equals("true")) {
            tvUserVsUser.setText(versus + " want to \nchallenge with you");
        } else {
            tvUserVsUser.setText("You'r challenge with \n" + versus);
            //view.findViewById(R.id.imageButtonCancelBattle).setVisibility(View.INVISIBLE);
            //view.findViewById(R.id.imageButtonAcceptBattle).setVisibility(View.INVISIBLE);
        }

        Glide.with(getContext()).load(R.drawable.points).override(200, 200).into((ImageView)view.findViewById(R.id.imageViewAcceptButton));
        tvUserVsUser.setTypeface(typeface);

        int days = 21;
        if (duration != null && !duration.isEmpty()) {
            days = Integer.parseInt(duration) / 86400;
        }

        tvDays.setText(days + "");
        tvGoal.setText(description);
        tvDays.setTypeface(typeface);
        tvGoal.setTypeface(typeface);

        ImageButton buttonAcceptBattle = (ImageButton)getActivity().findViewById(R.id.imageButtonAcceptBattle);
        final String finalChallenge_id = challenge_id;
        buttonAcceptBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String description = tvGoal.getText().toString();
                final int days = Integer.parseInt(tvDays.getText().toString());
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                OfflineMode offlineMode = new OfflineMode();
                                if (offlineMode.isConnectedToRemoteAPI(getActivity())){
                                    ChallengeController cc = new ChallengeController(getContext(), getActivity(), 0, 0);
                                    cc.Create_new_challenge(description, days, "567d51c48322f85870fd931b");
                                    Toast.makeText(getContext(), "Challenge Accepted", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setTitle("Are you sure")
                        .setMessage("You wanna accept request?")
                        .setIcon(R.drawable.challengecceptedmeme)
                        .setCancelable(false)
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No",  dialogClickListener).show();

            }
        });


        ImageButton buttonCancelBattle = (ImageButton)getActivity().findViewById(R.id.imageButtonCancelBattle);
        buttonCancelBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                OfflineMode offlineMode = new OfflineMode();
                                if (offlineMode.isConnectedToRemoteAPI(getActivity())){
                                    Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setTitle("Are you sure")
                        .setMessage("You wanna cancel request?")
                        .setIcon(R.drawable.duel_blue)
                        .setCancelable(false)
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No",  dialogClickListener).show();

            }
        });


        return view;
    }



    public boolean isActive(String description) {
        DBHelper dbHelper = new DBHelper(getActivity());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("pending_duel", null, null, null, null, null, null);
        description = description + " during this period";
        boolean ok = false;
        if (c.moveToFirst()) {
            int coldescription = c.getColumnIndex("description");
            do {
                if (c.getString(c.getColumnIndex("status")).equals("started")) {
                    if (c.getString(coldescription).equals(description)) {
                        ok = true;
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        return ok;
    }


}
