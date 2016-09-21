package com.example.ivan.champy_v2.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHSetupUI;

import java.io.IOException;

public class PendingDuelFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String TAG = "PendingDuelFragment";
    public int position, size, days = 21, o = 0;
    public String versus, duration, description, challenge_id, status, recipient;
    public TextView tvGoal, tvDays, tvUserVsUser;
    public ImageButton buttonAcceptBattle, buttonCancelBattle;
    public OfflineMode offlineMode;
    public ViewPager viewPager;
    public Typeface typeface;
    public SessionManager sessionManager;
    public ChallengeController cc;
    public Snackbar snackbar;
    public DBHelper dbHelper;
    public Cursor c;
    public SQLiteDatabase db;


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
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        final Bundle args = this.getArguments();
        c = db.query("pending_duel", null, null, null, null, null, null);
        position = args.getInt(ARG_PAGE);
        //Log.i("stat", "Status: " + position);
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

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        tvDays = (TextView)view.findViewById(R.id.textViewDuring);
        tvGoal = (TextView)view.findViewById(R.id.tv_goal);
        tvUserVsUser = (TextView)view.findViewById(R.id.tvYouVsSomebody);
        buttonAcceptBattle = (ImageButton)getActivity().findViewById(R.id.imageButtonAcceptBattle);
        buttonCancelBattle = (ImageButton)getActivity().findViewById(R.id.imageButtonCancelBattle);
        offlineMode = new OfflineMode();
        viewPager = (ViewPager)getActivity().findViewById(R.id.pager_pending_duel);
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

        if (duration != null && !duration.isEmpty()) {
            days = Integer.parseInt(duration) / 86400;
        }

        tvDays.setText(days + "");
        tvGoal.setText(description);
        tvDays.setTypeface(typeface);
        tvGoal.setTypeface(typeface);

        buttonAcceptBattle.setOnClickListener(this);
//            @Override
//            public void onClick(View view) {
//                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        description = "";
//                        challenge_id = "";
//                        recipient = "";
//                        position = viewPager.getCurrentItem();
//                        switch (which){
//                            case DialogInterface.BUTTON_POSITIVE:
//                                Cursor c = db.query("pending_duel", null, null, null, null, null, null);
//
//                                if (c.moveToFirst()) {
//                                    int coldescription = c.getColumnIndex("description");
//                                    int colchallenge_id = c.getColumnIndex("challenge_id");
//                                    int colrecipient = c.getColumnIndex("recipient");
//                                    do {
//                                        o++;
//                                        if (o > position + 1) break;
//                                        if (o == position + 1) {
//                                            description = c.getString(coldescription);
//                                            challenge_id = c.getString(colchallenge_id);
//                                            recipient = c.getString(colrecipient);
//                                        }
//                                    } while (c.moveToNext());
//                                }
//                                c.close();
//
//                                if (checkRecipientAndActive(description, recipient) && offlineMode.isConnectedToRemoteAPI(getActivity())) {
//                                    cc = new ChallengeController(getContext(), getActivity(), 0, 0, 0);
//                                    cc.joinToChallenge(challenge_id);
//                                    Log.i("OnClickButtonAccept", "onResponse: VSE OK");
//                                }
//                                break;
//                            case DialogInterface.BUTTON_NEGATIVE:
//                                break;
//                        }
//                    }
//                };

//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
//                builder.setTitle(R.string.areYouSure)
//                        .setMessage(R.string.youWannaAcceptRequest)
//                        .setIcon(R.drawable.duel_blue)
//                        .setCancelable(false)
//                        .setPositiveButton(R.string.yes, dialogClickListener)
//                        .setNegativeButton(R.string.no,  dialogClickListener).show();
//            }
//        });
        buttonCancelBattle.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
            switch (view.getId()) {
                case R.id.imageButtonAcceptBattle:
                    Log.i(TAG, "onClick: buttonAccept");
                    description = "";
                    challenge_id = "";
                    recipient = "";
                    position = viewPager.getCurrentItem();
                    c = db.query("pending_duel", null, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        int coldescription = c.getColumnIndex("description");
                        int colchallenge_id = c.getColumnIndex("challenge_id");
                        int colrecipient = c.getColumnIndex("recipient");
                        do {
                            o++;
                            if (o > position + 1) break;
                            if (o == position + 1) {
                                description = c.getString(coldescription);
                                challenge_id = c.getString(colchallenge_id);
                                recipient = c.getString(colrecipient);
                            }
                        } while (c.moveToNext());
                    }
                    c.close();

                    cc = new ChallengeController(getContext(), getActivity(), 0, 0, 0);
                    snackbar = Snackbar.make(view, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.i(TAG, "onClickSnackBar: ");
                            if (checkRecipientAndActive(description, recipient, view) && offlineMode.isConnectedToRemoteAPI(getActivity())) {
                                cc.joinToChallenge(challenge_id);
                                snackbar = Snackbar.make(view, "Challenge Accepted!", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                Log.i("OnClickButtonAccept", "onResponse: VSE OK");
                            }
                        }
                    });
                    snackbar.show();
                    break;

                case R.id.imageButtonCancelBattle:
                    c = db.query("pending_duel", null, null, null, null, null, null);
                    position = viewPager.getCurrentItem();
                    if (c.moveToFirst()) {
                        int colchallenge_id = c.getColumnIndex("challenge_id");
                        do {
                            o++;
                            if (o > position + 1) break;
                            if (o == position + 1) {
                                challenge_id = c.getString(colchallenge_id);
                            }
                        } while (c.moveToNext());
                    }
                    c.close();

                    cc = new ChallengeController(getContext(), getActivity(), 0, 0, 0);
                    snackbar = Snackbar.make(view, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.i(TAG, "onClickSnackBar: ");
                            try {
                                cc.rejectInviteForPendingDuel(challenge_id);
                                snackbar = Snackbar.make(view, "Challenge Canceled!", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                Log.i("OnClickButtonCancel", "onResponse: VSE OK");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    snackbar.show();
                    break;
            }
        }
    }



    private boolean checkRecipientAndActive(String description, String recipient, View view) {

        if (!cc.isActive(description) && recipient.equals("true")) {
            snackbar = Snackbar.make(view, "Challenge created!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            Log.i("OnClickAccept", "onResponse: VSE OK");
            return true;
        } else if (cc.isActive(description)) {
            snackbar = Snackbar.make(view, "This challenge is active!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            Log.i("OnClickAccept", "onResponse: IS ACTIVE");
            return false;
        } else {
            snackbar = Snackbar.make(view, "You can't accept your challenge!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            Log.i("OnClickAccept", "onResponse: YOU ARE SENDER");
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
