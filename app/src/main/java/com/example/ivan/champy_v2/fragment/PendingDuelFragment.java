package com.example.ivan.champy_v2.fragment;

import android.content.Context;
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
import android.widget.TextView;

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.controller.ChallengeController;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHSetupUI;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;

import java.io.IOException;

import static java.lang.Math.round;

public class PendingDuelFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    public int position, size, inProgressCount, days = 21, o = 0;
    public String versus, duration, description, challenge_id, status, recipient;
    public TextView tvGoal, tvDays, tvUserVsUser, everyDayForTheNext;
    public ImageButton btnAccept, btnCancel;
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
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PendingDuel", "onCreate: ");
        sessionManager = new SessionManager(getContext());
        final String token = sessionManager.getToken();
        final String userId = sessionManager.getUserId();
        cc = new ChallengeController(getContext(), getActivity(), token, userId);
        inProgressCount = Integer.parseInt(sessionManager.getChampyOptions().get("challenges"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.item_pending_duel, container, false);
        dbHelper = DBHelper.getInstance(getContext());
        db = dbHelper.getWritableDatabase();
        final Bundle args = this.getArguments();
        c = db.query("pending_duel", null, null, null, null, null, null);
        position = args.getInt(ARG_PAGE);

        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int versusColIndex = c.getColumnIndex("versus");
            int coldescription = c.getColumnIndex("description");
            int colduration = c.getColumnIndex("duration");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            int colrecipient = c.getColumnIndex("recipient");
            o = 0;
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

        int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int x = round(screenWidth/100);

        btnAccept = (ImageButton)view.findViewById(R.id.btn_accept);
        btnCancel = (ImageButton)view.findViewById(R.id.btn_cancel);
        btnAccept.getLayoutParams().height = x*10;
        btnAccept.getLayoutParams().width = x*10;
        btnCancel.getLayoutParams().height = x*10;
        btnCancel.getLayoutParams().width = x*10;

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        viewPager = (ViewPager)getActivity().findViewById(R.id.pager_pending_duel);
        everyDayForTheNext = (TextView)view.findViewById(R.id.tvEveryDayPending);
        tvUserVsUser  = (TextView)view.findViewById(R.id.tvYouVsSomebody);
        tvDays = (TextView)view.findViewById(R.id.textViewDuring);
        tvGoal = (TextView)view.findViewById(R.id.tv_goal);


        if (recipient.equals("true")) {
            tvUserVsUser.setText("from " + versus);
            btnAccept.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            tvUserVsUser.setText(getContext().getResources().getString(R.string.waiting_for_your_recipient) + "\n " + versus);
            btnAccept.setVisibility(View.INVISIBLE);
        }

        if (duration != null && !duration.isEmpty()) days = Integer.parseInt(duration) / 86400;

        tvDays.setText(days + " days");
        tvGoal.setText(description);
        tvDays.setTypeface(typeface);
        tvGoal.setTypeface(typeface);
        tvUserVsUser.setTypeface(typeface);
        everyDayForTheNext.setTypeface(typeface);


        CHSetupUI chSetupUI= new CHSetupUI();
        chSetupUI.setupUI(view, getActivity());

        offlineMode = OfflineMode.getInstance();
        offlineMode.isConnectedToRemoteAPI(getActivity());
        btnAccept.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        position = viewPager.getCurrentItem();
        c = db.query("pending_duel", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int coldescription = c.getColumnIndex("description");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            int colrecipient = c.getColumnIndex("recipient");
            o = 0;
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
            case R.id.btn_accept:
                snackbar = Snackbar.make(view, getString(R.string.are_you_sure), Snackbar.LENGTH_LONG).setAction(getString(R.string.yes), vAccept -> {
                    try {
                        if (!cc.isActive(description) && recipient.equals("true") && inProgressCount < 5) {
                            cc.joinToChallenge(challenge_id);
                            snackbar = Snackbar.make(vAccept, getString(R.string.challenge_created), Snackbar.LENGTH_SHORT);
                        } else {
                            snackbar = Snackbar.make(vAccept, getString(R.string.cant_create_this_challenge), Snackbar.LENGTH_SHORT);
                        }
                        snackbar.show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                });
                snackbar.show();
                break;

            case R.id.btn_cancel:
                snackbar = Snackbar.make(view, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes", vCancel -> {
                    try {
                        cc.rejectInviteForPendingDuel(challenge_id);
                        snackbar = Snackbar.make(vCancel, getString(R.string.challenge_created), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                    }
                });
                snackbar.show();
                break;
        }

    }



}
