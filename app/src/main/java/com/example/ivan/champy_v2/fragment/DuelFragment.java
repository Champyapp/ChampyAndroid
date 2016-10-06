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

//12.07.2016 тут логика окна дуелей с другом: friends -> make challenge -> here.
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.item_row, container, false);
        Log.i(TAG, "onCreateView");
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
                    name = c.getString(nameColIndex);
                    description = c.getString(coldescription);
                    duration = c.getString(colduration);
                    challenge_id = c.getString(colchallenge_id);
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

        if (duration != null && !duration.isEmpty()) days = Integer.parseInt(duration) / 86400;

        tvDays.setText("" + days);
        tvDays.setTypeface(typeface);
        tvGoal.setText(name);
        tvGoal.setTypeface(typeface);

        Glide.with(getContext()).load(R.drawable.points).override(120, 120).into((ImageView) view.findViewById(R.id.imageViewAcceptButton));
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
        }

        OfflineMode offlineMode = new OfflineMode();
        offlineMode.isConnectedToRemoteAPI(getActivity());
        imageButtonAccept.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick: ");
        name = etGoal.getText().toString();
        duration = etDays.getText().toString();
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        c = db.query("duel", null, null, null, null, null, null);
        position = viewPager.getCurrentItem();
        size = sessionManager.getSelfSize();
        CurrentUserHelper user = new CurrentUserHelper(getContext());
        token = user.getToken();
        userId = user.getUserObjectId();
        snackbar = Snackbar.make(view, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClickSnackBar: ");
                cc = new ChallengeController(getContext(), getActivity(), 0, 0, 0);
                if (position == size) {
                    if (checkInputUserData(name, duration, view)) {
                        days = Integer.parseInt(duration);
                        cc.createNewDuelChallenge(name, days, friend_id, token);
                    }
                } else {
                    if (c.moveToFirst()) {
                        int idColIndex = c.getColumnIndex("id");
                        int nameColIndex = c.getColumnIndex("name");
                        int coldescription = c.getColumnIndex("description");
                        int colduration = c.getColumnIndex("duration");
                        int colchallenge_id = c.getColumnIndex("challenge_id");
                        int o = 0;
                        do {
                            o++;
                            if (o > position + 1) break;
                            if (o == position + 1) {
                                name = c.getString(nameColIndex);
                                description = c.getString(coldescription);
                                duration = c.getString(colduration);
                                challenge_id = c.getString(colchallenge_id);
                                break;
                            }
                        } while (c.moveToNext());
                    }
                    c.close();

                    if (cc.isActive(name)) {
                        snackbar = Snackbar.make(view, "This challenge is active!", Snackbar.LENGTH_SHORT);
                    } else {
                        cc.sendSingleInProgressForDuel(challenge_id, friend_id, token);
                        snackbar = Snackbar.make(view, "Sent duel request", Snackbar.LENGTH_SHORT);
                    }
                    snackbar.show();
                }
            }
        });
        snackbar.show();
    }


    // check user input data @description @days @isActive
    private boolean checkInputUserData(String name, String duration, View view) {
        if (!cc.isActive(name) && !name.isEmpty() && !name.startsWith(" ") && !duration.isEmpty() && days != 0) {
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