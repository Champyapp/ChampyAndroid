package com.azinecllc.champy.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;

import java.io.IOException;

public class PendingDuelFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    public int position, size, inProgressCount, days = 21, o = 0;
    public String versus, duration, description, challenge_id, status, recipient;
    public TextView tvGoal, tvDays, tvUserVsUser, everyDayForTheNext;
    public ImageButton btnAccept, btnCancel, btnCancelC;
    public OfflineMode offlineMode;
    public ViewPager viewPager;
    public Typeface typeface;
    public SessionManager sessionManager;
    public ChallengeController cc;
    public Snackbar snackbar;
    public DBHelper dbHelper;
    public Cursor c;
    public SQLiteDatabase db;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = SessionManager.getInstance(getContext());
        final String token = sessionManager.getToken();
        final String userId = sessionManager.getUserId();
        cc = new ChallengeController(getContext(), getActivity(), token, userId);
        inProgressCount = Integer.parseInt(sessionManager.getChampyOptions().get("challenges"));
        dbHelper = DBHelper.getInstance(getContext());
        db = dbHelper.getWritableDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_pending_duel, container, false);

        final Bundle args = this.getArguments();
        c = db.query("pending_duel", null, null, null, null, null, null);
        position = args.getInt(ARG_PAGE);

        if (c.moveToFirst()) {
            int idColIndex      = c.getColumnIndex("id");
            int versusColIndex  = c.getColumnIndex("versus");
            int coldescription  = c.getColumnIndex("description");
            int colduration     = c.getColumnIndex("duration");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            int colrecipient    = c.getColumnIndex("recipient");
            o = 0;
            do {
                o++;
                if (o > position + 1) break;
                if (o == position + 1) {
                    versus       = c.getString(versusColIndex);
                    description  = c.getString(coldescription);
                    duration     = c.getString(colduration);
                    challenge_id = c.getString(colchallenge_id);
                    recipient    = c.getString(colrecipient);
                }
            } while (c.moveToNext());
        }

        c.close();

        btnAccept = (ImageButton)view.findViewById(R.id.btn_accept);
        btnCancel = (ImageButton)view.findViewById(R.id.btn_cancel);
        btnCancelC = (ImageButton) view.findViewById(R.id.btn_cancelc);

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        viewPager = (ViewPager)getActivity().findViewById(R.id.pager_pending_duel);
        everyDayForTheNext = (TextView)view.findViewById(R.id.tvEveryDayPending);
        tvUserVsUser  = (TextView)view.findViewById(R.id.tvYouVsSomebody);
        tvDays = (TextView)view.findViewById(R.id.textViewDuring);
        tvGoal = (TextView)view.findViewById(R.id.tv_goal);


        if (recipient.equals("true")) {
            tvUserVsUser.setText(String.format("%s", "from " + versus));
            btnAccept.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnCancelC.setVisibility(View.INVISIBLE);
        } else {
            tvUserVsUser.setText(getContext().getResources().getString(R.string.waiting_for_your_recipient) + "\n " + versus);
            btnAccept.setVisibility(View.INVISIBLE);
            btnCancel.setVisibility(View.INVISIBLE);
            btnCancelC.setVisibility(View.VISIBLE);
        }

        if (duration != null && !duration.isEmpty()) days = Integer.parseInt(duration) / 86400;

        tvDays.setText(String.format("%s", days + " days"));
        tvGoal.setText(description);
        tvDays.setTypeface(typeface);
        tvGoal.setTypeface(typeface);
        tvUserVsUser.setTypeface(typeface);
        everyDayForTheNext.setTypeface(typeface);

        offlineMode = OfflineMode.getInstance();
        offlineMode.isConnectedToRemoteAPI(getActivity());
        btnAccept.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnCancelC.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        position = viewPager.getCurrentItem();
        c = db.query("pending_duel", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int coldescription  = c.getColumnIndex("description");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            int colrecipient    = c.getColumnIndex("recipient");
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
                onClickAccept(view);
                break;
            case R.id.btn_cancel:
                onClickCancel(view);
                break;
            case R.id.btn_cancelc:
                onClickCancel(view);
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


    private void onClickCancel(View view) {
        snackbar = Snackbar.make(view, R.string.are_you_sure, Snackbar.LENGTH_LONG).setAction(R.string.yes, vCancel -> {
            try {
                cc.rejectInviteForPendingDuel(challenge_id);
                snackbar = Snackbar.make(view, R.string.challenge_canceled, Snackbar.LENGTH_SHORT);
                snackbar.show();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        });
        snackbar.show();
    }

    private void onClickAccept(View view) {
        snackbar = Snackbar.make(view, getString(R.string.are_you_sure), Snackbar.LENGTH_LONG).setAction(getString(R.string.yes), vAccept -> {
            try {
                if (!cc.isActive(description) && recipient.equals("true") && inProgressCount < 10) {
                    cc.joinToChallenge(challenge_id);
                    snackbar = Snackbar.make(view, R.string.challenge_created, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    snackbar = Snackbar.make(view, R.string.cant_create_this_challenge, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
        snackbar.show();
    }

    public static PendingDuelFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PendingDuelFragment fragment = new PendingDuelFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
