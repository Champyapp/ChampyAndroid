package com.example.ivan.champy_v2.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.activity.PendingDuelActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.duel.Challenge;
import com.example.ivan.champy_v2.duel.Data;
import com.example.ivan.champy_v2.helper.CHSetupUI;

import java.io.IOException;

import static java.lang.Math.round;

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
        //Log.i("stat", "Status: " + position);
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
        final SessionManager sessionManager = new SessionManager(getContext());
        int size = sessionManager.getSelfSize();
        final Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        final TextView tvDays = (TextView)view.findViewById(R.id.textViewDuring);
        final TextView tvGoal = (TextView)view.findViewById(R.id.tv_goal);
        TextView tvUserVsUser = (TextView)view.findViewById(R.id.tvYouVsSomebody);
        final ImageButton buttonAcceptBattle = (ImageButton)getActivity().findViewById(R.id.imageButtonAcceptBattle);
        final ImageButton buttonCancelBattle = (ImageButton)getActivity().findViewById(R.id.imageButtonCancelBattle);

        final ViewPager viewPager = (ViewPager)getActivity().findViewById(R.id.pager_pending_duel);
        CHSetupUI chSetupUI = new CHSetupUI();
        chSetupUI.setupUI(getActivity().findViewById(R.id.pending_duel), getActivity());
        chSetupUI.setupUI(view, getActivity());

        if (recipient.equals("true")) {
            tvUserVsUser.setText(versus + " want to \nchallenge with you");
        } else {
            tvUserVsUser.setText("Your challenge with \n" + versus);
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


        buttonAcceptBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String versus = "";
                        String duration = "";
                        String description = "";
                        String challenge_id = "";
                        String status = "";
                        String recipient = "";
                        String index = "";
                        String type_id = "567d51c48322f85870fd931b";
                        int position = viewPager.getCurrentItem();
                        int size = sessionManager.getSelfSize();

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Cursor c = db.query("pending_duel", null, null, null, null, null, null);
                                //int position = args.getInt(ARG_PAGE);
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
                                            index = c.getString(idColIndex);
                                        }
                                    } while (c.moveToNext());
                                }
                                int days = Integer.parseInt(duration);
                                c.close();
                                String inProgressId = "";
                                if (checkRecipientAndActive(description, recipient)) {
                                    ChallengeController cc = new ChallengeController(getContext(), getActivity(), 0, 0);
                                    cc.joinToChallenge(challenge_id);
                                }

                                Log.i("OnCreateView", "Status: VSE OK"
                                        + "\n       challenge_id = " + challenge_id
                                        + "\n       description  = " + description
                                        + "\n       duration     = " + duration
                                        + "\n       versus       = " + versus
                                        + "\n       recipient    = " + recipient
                                        + "\n       id           = " + index);
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
                        .setIcon(R.drawable.duel_blue)
                        .setCancelable(false)
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No",  dialogClickListener).show();

            }
        });


        buttonCancelBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String versus = "";
                        String duration = "";
                        String description = "";
                        String challenge_id = "";
                        String status = "";
                        String recipient = "";
                        String index = "";
                        String type_id = "567d51c48322f85870fd931b";
                        int position = viewPager.getCurrentItem();
                        int size = sessionManager.getSelfSize();

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Cursor c = db.query("pending_duel", null, null, null, null, null, null);
                                //int position = args.getInt(ARG_PAGE);
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
                                            index = c.getString(idColIndex);
                                        }
                                    } while (c.moveToNext());
                                }
                                int days = Integer.parseInt(duration);
                                c.close();
                                OfflineMode offlineMode = new OfflineMode();
                                offlineMode.isConnectedToRemoteAPI(getActivity());
                                Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                                ChallengeController cc = new ChallengeController(getContext(), getActivity(), 0, 0);
                                try {
                                    cc.rejectInviteForPendingDuel(challenge_id);
                                    Intent goToMainActivity = new Intent(getContext(), MainActivity.class);
                                    startActivity(goToMainActivity);
                                } catch (IOException e) {
                                    e.printStackTrace();
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


    private boolean checkRecipientAndActive(String description, String recipient) {

        if (!isActive(description) && recipient.equals("true")) {
            Toast.makeText(getContext(), "Challenge Accepted", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isActive(description)) {
            Toast.makeText(getContext(), "This challenge is active", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(getContext(), "You can't accept this challenge because you're Sender!", Toast.LENGTH_SHORT).show();
            return false;
        }

    }


    public boolean isActive(String description) {
        DBHelper dbHelper = new DBHelper(getActivity());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        final Bundle args = this.getArguments();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        int position = args.getInt(ARG_PAGE);
        Log.i("stat", "Status: " + position);
        description = description + " during this period";
        boolean ok = false;
        int o = 0;
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int coldescription = c.getColumnIndex("description");
            int colduration = c.getColumnIndex("duration");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            do {
                if (c.getString(c.getColumnIndex("status")).equals("started")){
                    if (c.getString(coldescription).equals(description)){
                        ok = true;
                    }
                }
            } while (c.moveToNext());
        }
        c.close();

        return ok;
    }


}
