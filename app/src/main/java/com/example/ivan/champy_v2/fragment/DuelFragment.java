package com.example.ivan.champy_v2.fragment;

import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ivan.champy_v2.controller.ChallengeController;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHSetupUI;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;

public class DuelFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String TAG = "DuelFragment";
    public int position, size, daysCount, newDaysCount, days = 21, o = 0;
    public String name, duration, description, challenge_id, status, friend_id, token, userId;
    public SessionManager sessionManager;
    public ChallengeController cc;
    public TextView tvGoal, tvDays, etDays;
    public EditText etGoal;
    public ViewPager viewPager;
    public Typeface typeface;
    public SQLiteDatabase db;
    public Snackbar snackbar;
    public DBHelper dbHelper;
    public ContentValues cv;
    public Cursor c;


    public static DuelFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        DuelFragment fragment = new DuelFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.item_card, container, false);
        Log.d(TAG, "onCreateView");
        Bundle extras = getActivity().getIntent().getExtras();
        friend_id = (extras == null) ? null : extras.getString("id");
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        final Bundle args = this.getArguments();
        c = db.query("duel", null, null, null, null, null, null);
        position = args.getInt(ARG_PAGE);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int coldescription = c.getColumnIndex("description");
            int colduration = c.getColumnIndex("duration");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            do {
                o++;
                if (o > position + 1) break;
                if (o == position + 1) {
                    challenge_id = c.getString(colchallenge_id);
                    description = c.getString(coldescription);
                    duration = c.getString(colduration);
                    name = c.getString(nameColIndex);
                }
            } while (c.moveToNext());
        }
        c.close();

        sessionManager = new SessionManager(getContext());
        String token = sessionManager.getToken();
        String userId = sessionManager.getUserId();
        cc = new ChallengeController(getContext(), getActivity(), token, userId);
        size = sessionManager.getSelfSize();
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        tvGoal = (TextView)view.findViewById(R.id.goal_text);
        tvDays = (TextView)view.findViewById(R.id.days_text);
        etGoal = (EditText)view.findViewById(R.id.et_goal);
        etDays = (TextView)view.findViewById(R.id.et_days);
        View line = view.findViewById(R.id.line);
        TextView tvEveryDay = (TextView)view.findViewById(R.id.tvEveryDaySelf);
        TextView textDays   = (TextView)view.findViewById(R.id.textDays);
        ImageButton buttonPlus = (ImageButton) view.findViewById(R.id.imageButtonPlus);
        ImageButton buttonMinus = (ImageButton) view.findViewById(R.id.imageButtonMinus);

        if (duration != null && !duration.isEmpty()) days = Integer.parseInt(duration) / 86400;

        tvDays.setText(days + " days");
        tvGoal.setText(description);
        tvDays.setTypeface(typeface);
        tvGoal.setTypeface(typeface);
        tvEveryDay.setTypeface(typeface);
        textDays.setTypeface(typeface);
        textDays.setVisibility(View.INVISIBLE);
        ImageButton imageButtonAccept = (ImageButton) getActivity().findViewById(R.id.ok);

        viewPager = (ViewPager) getActivity().findViewById(R.id.pager_duel);
        CHSetupUI chSetupUI = new CHSetupUI();
        chSetupUI.setupUI(view, getActivity());

        if (position == size) {
            etGoal.setTypeface(typeface);
            etDays.setTypeface(typeface);
            etDays.setHint("21");
            etDays.setVisibility(View.VISIBLE);
            etGoal.setVisibility(View.VISIBLE);
            tvDays.setVisibility(View.INVISIBLE);
            tvGoal.setVisibility(View.INVISIBLE);
            line.setVisibility(View.INVISIBLE);
            textDays.setVisibility(View.VISIBLE);
            buttonMinus.setVisibility(View.VISIBLE);
            buttonPlus.setVisibility(View.VISIBLE);
        }

        OfflineMode offlineMode = new OfflineMode();
        offlineMode.isConnectedToRemoteAPI(getActivity());
        imageButtonAccept.setOnClickListener(this);
        buttonMinus.setOnClickListener(this);
        buttonPlus.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok:
                snackbar = Snackbar.make(view, R.string.are_you_sure, Snackbar.LENGTH_LONG).setAction(R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        position = viewPager.getCurrentItem();
                        size = sessionManager.getSelfSize();
                        if (position == size) {
                            description = etGoal.getText().toString();
                            duration = etDays.getText().toString();
                            try {
                                days = Integer.parseInt(duration);
                                if (!cc.isActive(description) && !description.isEmpty() && !description.startsWith(" ")) {
                                    cc.createNewDuelChallenge(description, days, friend_id);
                                    snackbar = Snackbar.make(view, R.string.challenge_created, Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                } else {
                                    snackbar = Snackbar.make(view, R.string.cant_create_this_challenge, Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                            } catch (NullPointerException | NumberFormatException e) {
                                e.printStackTrace();
                            }

                        } else {
                            c = db.query("duel", null, null, null, null, null, null);
                            if (c.moveToFirst()) {
                                int colchallenge_id = c.getColumnIndex("challenge_id");
                                int coldescription = c.getColumnIndex("description");
                                int colduration = c.getColumnIndex("duration");
                                int nameColIndex = c.getColumnIndex("name");
                                int idColIndex = c.getColumnIndex("id");
                                o = 0;
                                do {
                                    o++;
                                    if (o > position + 1) break;
                                    if (o == position + 1) {
                                        challenge_id = c.getString(colchallenge_id);
                                        description = c.getString(coldescription);
                                        duration = c.getString(colduration);
                                        name = c.getString(nameColIndex);
                                        break;
                                    }
                                } while (c.moveToNext());
                            }
                            c.close();

                            try {
                                if (!cc.isActive(description)) {
                                    cc.sendSingleInProgressForDuel(challenge_id, friend_id);
                                    snackbar = Snackbar.make(view, "Sent duel request", Snackbar.LENGTH_SHORT);
                                } else {
                                    snackbar = Snackbar.make(view, R.string.cant_create_this_challenge, Snackbar.LENGTH_SHORT);
                                }
                                snackbar.show();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                snackbar.show();
                break;

            case R.id.imageButtonPlus:
                daysCount = Integer.parseInt(etDays.getText().toString());
                if (daysCount < 1000) {
                    newDaysCount = daysCount + 1;
                    etDays.setText(String.valueOf(newDaysCount));
                }
                break;
            case R.id.imageButtonMinus:
                daysCount = Integer.parseInt(etDays.getText().toString());
                if (daysCount > 1) {
                    newDaysCount = daysCount - 1;
                    etDays.setText(String.valueOf(newDaysCount));
                }
                break;
        }
    }

}