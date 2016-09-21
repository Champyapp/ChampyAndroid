package com.example.ivan.champy_v2.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach");
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

        buttonAcceptBattle = (ImageButton)getActivity().findViewById(R.id.imageButtonAcceptBattle);
        buttonCancelBattle = (ImageButton)getActivity().findViewById(R.id.imageButtonCancelBattle);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        viewPager = (ViewPager)getActivity().findViewById(R.id.pager_pending_duel);
        tvUserVsUser  = (TextView)view.findViewById(R.id.tvYouVsSomebody);
        tvDays = (TextView)view.findViewById(R.id.textViewDuring);
        tvGoal = (TextView)view.findViewById(R.id.tv_goal);
        CHSetupUI chSetupUI= new CHSetupUI();
        chSetupUI.setupUI(getActivity().findViewById(R.id.pending_duel), getActivity());
        chSetupUI.setupUI(view, getActivity());
        Glide.with(getContext()).load(R.drawable.points).override(200, 200).into((ImageView)view.findViewById(R.id.imageViewAcceptButton));
        offlineMode = new OfflineMode();
        offlineMode.isConnectedToRemoteAPI(getActivity());
        buttonAcceptBattle.setOnClickListener(this);
        buttonCancelBattle.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");

        if (recipient.equals("true")) {
            tvUserVsUser.setText("from " + versus);
        } else {
            tvUserVsUser.setText(R.string.waiting_for_your_recipient);
        }

        tvUserVsUser.setTypeface(typeface);

        if (duration != null && !duration.isEmpty()) {
            days = Integer.parseInt(duration) / 86400;
        }

        tvDays.setText(days + "");
        tvGoal.setText(description);
        tvDays.setTypeface(typeface);
        tvGoal.setTypeface(typeface);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
    }

    @Override
    public void onClick(View view) {
        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
            cc = new ChallengeController(getContext(), getActivity(), 0, 0, 0);
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

            switch (view.getId()) {
                case R.id.imageButtonAcceptBattle:
                    Log.i(TAG, "onClick: buttonAccept");

                    snackbar = Snackbar.make(view, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.i(TAG, "onClickSnackBar: ");
                            if (checkRecipientAndActive(description, recipient, view)) {
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
//                    c = db.query("pending_duel", null, null, null, null, null, null);
//                    position = viewPager.getCurrentItem();
//                    if (c.moveToFirst()) {
//                        int colchallenge_id = c.getColumnIndex("challenge_id");
//                        do {
//                            o++;
//                            if (o > position + 1) break;
//                            if (o == position + 1) {
//                                challenge_id = c.getString(colchallenge_id);
//                            }
//                        } while (c.moveToNext());
//                    }
//                    c.close();
//
                    snackbar = Snackbar.make(view, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.i(TAG, "onClickSnackBar: ");
                            try {
                                //cc = new ChallengeController(getContext(), getActivity(), 0, 0, 0);
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
            snackbar = Snackbar.make(view, "Challenge accepted!", Snackbar.LENGTH_SHORT);
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
