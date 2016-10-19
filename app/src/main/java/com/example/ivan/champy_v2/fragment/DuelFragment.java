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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHSetupUI;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;

import java.util.HashMap;

public class DuelFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String TAG = "DuelFragment";
    public int position, size, days = 21, o = 0;
    public String name, duration, description, challenge_id, status, friend_id, token, userId;
    public SessionManager sessionManager;
    public ChallengeController cc;
    public TextView tvGoal, tvDays;
    public EditText etGoal, etDays;
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
        final View view = inflater.inflate(R.layout.item_row, container, false);
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
        size = sessionManager.getSelfSize();
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        tvGoal = (TextView)view.findViewById(R.id.goal_text);
        tvDays = (TextView)view.findViewById(R.id.days_text);
        etGoal = (EditText)view.findViewById(R.id.et_goal);
        etDays = (EditText)view.findViewById(R.id.et_days);
        TextView textDays   = (TextView)view.findViewById(R.id.tvDays);
        TextView tvLevel    = (TextView)view.findViewById(R.id.tvLevel1Chall);
        TextView tvEveryDay = (TextView)view.findViewById(R.id.tvEveryDaySelf);
        TextView tvPoint    = (TextView)view.findViewById(R.id.tvRewardPlus10Points);

        if (duration != null && !duration.isEmpty()) days = Integer.parseInt(duration) / 86400;

        tvDays.setText(days + " days");
        tvGoal.setText(description);
        tvDays.setTypeface(typeface);
        tvGoal.setTypeface(typeface);
        tvEveryDay.setTypeface(typeface);
        textDays.setTypeface(typeface);
        tvLevel.setTypeface(typeface);
        tvPoint.setTypeface(typeface);
        textDays.setVisibility(View.INVISIBLE);

        Glide.with(getContext()).load(R.drawable.points).override(120, 120).into((ImageView) view.findViewById(R.id.imageViewPoints));
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
            //tvGoal.setVisibility(View.INVISIBLE);
            textDays.setVisibility(View.VISIBLE);
        }

        OfflineMode offlineMode = new OfflineMode();
        offlineMode.isConnectedToRemoteAPI(getActivity());
        imageButtonAccept.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        description = etGoal.getText().toString();
        duration = etDays.getText().toString();
        c = db.query("duel", null, null, null, null, null, null);
        position = viewPager.getCurrentItem();
        size = sessionManager.getSelfSize();

        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        final String token  = user.get("token");
        final String userId = user.get("id");

        snackbar = Snackbar.make(view, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc = new ChallengeController(getContext(), getActivity(), 0, 0, 0);
                if (position == size) {
                    try {
                        if (checkInputUserData(description, duration, view))
                            days = Integer.parseInt(duration);
                            cc.createNewDuelChallenge(description, days, friend_id, token, userId);
                    } catch (NullPointerException e) { e.printStackTrace(); }

                } else {
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
                            cc.sendSingleInProgressForDuel(challenge_id, friend_id, token, userId);
                            snackbar = Snackbar.make(view, "Sent duel request", Snackbar.LENGTH_SHORT);
                        } else {
                            snackbar = Snackbar.make(view, "This challenge is active!", Snackbar.LENGTH_SHORT);
                        }
                        snackbar.show();
                    } catch (NullPointerException e) { e.printStackTrace(); }
                }
            }
        });
        snackbar.show();
    }


    // check user input data @description @days @isActive
    private boolean checkInputUserData(String name, String duration, View view) {
        if (!cc.isActive(name) && !name.isEmpty() && !name.startsWith(" ") && !duration.isEmpty() && !duration.equals("0")) {
            snackbar = Snackbar.make(view, "Sent duel request!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return true;
        } else if (cc.isActive(name)) {
            snackbar = Snackbar.make(view, "This challenge is active!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        } else {
            snackbar = Snackbar.make(view, "Complete all fields", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }
    }


}