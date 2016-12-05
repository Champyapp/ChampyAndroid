package com.example.ivan.champy_v2.fragment;

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

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.controller.ChallengeController;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHSetupUI;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;

public class SelfImprovementFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    public int position, size, daysCount, newDaysCount, days = 21, o = 0;
    public String duration, description, challenge_id, status, name;
    public Typeface typeface;
    public TextView tvGoal, tvDays, etDays;
    public EditText etGoal;
    public ViewPager viewPager;
    public SessionManager sessionManager;
    public ChallengeController cc;
    public Snackbar snackbar;
    public DBHelper dbHelper;
    public Cursor c;
    public SQLiteDatabase db;

    public static SelfImprovementFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SelfImprovementFragment fragment = new SelfImprovementFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.item_card, container, false);
        dbHelper = DBHelper.getInstance(getContext());
        db = dbHelper.getWritableDatabase();
        final Bundle args = this.getArguments();
        c = db.query("selfimprovement", null, null, null, null, null, null);
        position = args.getInt(ARG_PAGE);
        if (c.moveToFirst()) {
            int colchallenge_id = c.getColumnIndex("challenge_id");
            int coldescription = c.getColumnIndex("description");
            int colduration = c.getColumnIndex("duration");
            int nameColIndex = c.getColumnIndex("name");
            int colstatus = c.getColumnIndex("status");
            int idColIndex = c.getColumnIndex("id");
            try {
                do {
                    o++;
                    if (o > position + 1) break;
                    if (o == position + 1) {
                        challenge_id = c.getString(colchallenge_id);
                        description = c.getString(coldescription);
                        duration = c.getString(colduration);
                        name = c.getString(nameColIndex);
                        status = c.getString(colstatus);
                    }
                } while (c.moveToNext());
            } finally {
                c.close();
            }
        }

        sessionManager = new SessionManager(getContext());
        size = sessionManager.getSelfSize();
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        tvGoal = (TextView)view.findViewById(R.id.goal_text);
        tvDays = (TextView)view.findViewById(R.id.days_text);
        etDays = (TextView)view.findViewById(R.id.et_days);
        etGoal = (EditText)view.findViewById(R.id.et_goal);
        TextView textDays = (TextView)view.findViewById(R.id.textDays);
        TextView tvEveryDay = (TextView)view.findViewById(R.id.tvEveryDaySelf);
        ImageButton buttonPlus = (ImageButton) view.findViewById(R.id.imageButtonPlus);
        ImageButton buttonMinus = (ImageButton) view.findViewById(R.id.imageButtonMinus);
        View line = view.findViewById(R.id.line);

        if (duration != null && !duration.isEmpty()) days = Integer.parseInt(duration) / 86400;

        tvDays.setText(days + " days");
        tvGoal.setText(description);
        tvDays.setTypeface(typeface);
        tvGoal.setTypeface(typeface);
        tvEveryDay.setTypeface(typeface);
        textDays.setTypeface(typeface);
        textDays.setVisibility(View.INVISIBLE);

        ImageButton buttonAccept = (ImageButton) getActivity().findViewById(R.id.imageButtonAccept);
        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        CHSetupUI chSetupUI = new CHSetupUI();
        chSetupUI.setupUI(view, getActivity());

        if (position == size) {
            etGoal.setTypeface(typeface);
            etDays.setTypeface(typeface);
            etDays.setHint("21");
            etDays.setVisibility(View.VISIBLE);
            etGoal.setVisibility(View.VISIBLE);
            tvDays.setVisibility(View.INVISIBLE);
            line.setVisibility(View.INVISIBLE);
            textDays.setVisibility(View.VISIBLE);
            buttonMinus.setVisibility(View.VISIBLE);
            buttonPlus.setVisibility(View.VISIBLE);
        }

        final String token = sessionManager.getToken();
        final String userId = sessionManager.getUserId();
        cc = new ChallengeController(getContext(), getActivity(), token, userId);

        OfflineMode offlineMode = new OfflineMode();
        offlineMode.isConnectedToRemoteAPI(getActivity());
        buttonAccept.setOnClickListener(this);
        buttonMinus.setOnClickListener(this);
        buttonPlus.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButtonAccept:
                position = viewPager.getCurrentItem();
                size = sessionManager.getSelfSize();

                snackbar = Snackbar.make(view, R.string.are_you_sure, Snackbar.LENGTH_LONG).setAction(R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (position == size) {
                            description = etGoal.getText().toString();
                            duration = etDays.getText().toString();
                            try {
                                days = Integer.parseInt(duration);
                                if (!cc.isActive(description) && !description.isEmpty() && !description.startsWith(" ")) {
                                    cc.createNewSelfImprovementChallenge(description, days);
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
                            c = db.query("selfimprovement", null, null, null, null, null, null);
                            if (c.moveToFirst()) {
                                int colchallenge_id = c.getColumnIndex("challenge_id");
                                int coldescription = c.getColumnIndex("description");
                                int colduration = c.getColumnIndex("duration");
                                int colstatus = c.getColumnIndex("status");
                                int colname = c.getColumnIndex("name");
                                o = 0;
                                try {
                                    do {
                                        o++;
                                        if (o > position + 1) break;
                                        if (o == position + 1) {
                                            challenge_id = c.getString(colchallenge_id);
                                            description = c.getString(coldescription);
                                            duration = c.getString(colduration);
                                            status = c.getString(colstatus);
                                            name = c.getString(colname);
                                        }
                                    } while (c.moveToNext());
                                } finally {
                                    c.close();
                                }
                            }

                            try {
                                if (!cc.isActive(description)) {
                                    cc.sendSingleInProgressForSelf(challenge_id);
                                    snackbar = Snackbar.make(view, R.string.challenge_created, Snackbar.LENGTH_SHORT);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            c.close();
            db.close();
            Log.d("SelfImprovementFragment", "onDestroyView: db.isOpen? = " + db.isOpen());
            Log.d("SelfImprovementFragment", "onDestroyView: db.isClosed? = " + c.isClosed());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}