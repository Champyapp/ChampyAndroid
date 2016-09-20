package com.example.ivan.champy_v2.fragment;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHSetupUI;

//12.07.2016 тут логика окна дуелей с другом: friends -> make challenge -> here.
public class DuelFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String TAG = "DuelFragment";
    public int position, size, days = 21;
    public String name, duration, description, challenge_id, status, friend_id;
    public SessionManager sessionManager;
    public TextView tvGoal, tvDays;
    public EditText etGoal, etDays;
    public ChallengeController cc;
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
        Log.i(TAG, "onCreateView: ");
        Bundle extras = getActivity().getIntent().getExtras();
        friend_id = (extras == null) ? null : extras.getString("id");
//        if(extras == null) {
//        } else {
//            friend_id = extras.getString("id");
//        }
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        final Bundle args = this.getArguments();
        c = db.query("duel", null, null, null, null, null, null);
        position = args.getInt(ARG_PAGE);
        int o = 0;
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

//        days = (duration != null && !duration.isEmpty()) ? days = Integer.parseInt(duration) / 86400 : 0;
        if (duration != null && !duration.isEmpty()) {
            days = Integer.parseInt(duration) / 86400;
        }

        tvDays.setText("" + days);
        tvDays.setTypeface(typeface);
        tvGoal.setText(description);
        tvGoal.setTypeface(typeface);

        Glide.with(getContext()).load(R.drawable.points).override(120, 120).into((ImageView) view.findViewById(R.id.imageViewAcceptButton));
        ImageButton imageButtonAccept = (ImageButton) getActivity().findViewById(R.id.ok);

        viewPager = (ViewPager) getActivity().findViewById(R.id.pager_duel);
        CHSetupUI chSetupUI = new CHSetupUI();
        chSetupUI.setupUI(getActivity().findViewById(R.id.duel_back), getActivity());
        chSetupUI.setupUI(view, getActivity());

        if (position == size) {
            etGoal.setTypeface(typeface);
            etDays.setTypeface(typeface);
            etGoal.setText(description);
            etDays.setHint("21");
            etDays.setVisibility(View.VISIBLE);
            etGoal.setVisibility(View.VISIBLE);
            tvDays.setVisibility(View.INVISIBLE);
        }

        OfflineMode offlineMode = new OfflineMode();
        offlineMode.isConnectedToRemoteAPI(getActivity());
        imageButtonAccept.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick: ");
//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
        description = etGoal.getText().toString();
        duration = etDays.getText().toString();
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        c = db.query("duel", null, null, null, null, null, null);
        position = viewPager.getCurrentItem();
        size = sessionManager.getSelfSize();
//      switch (which) {
//        case DialogInterface.BUTTON_POSITIVE:
        snackbar = Snackbar.make(view, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClickSnackBar: ");
                cc = new ChallengeController(getContext(), getActivity(), 0, 0, 0);
                if (position == size) {
                    if (checkInputUserData(description, duration, view)) {
                        days = Integer.parseInt(duration);
                        cc.createNewDuelChallenge(description, days, friend_id);
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

                    if (cc.isActive(description)) {
                        snackbar = Snackbar.make(view, "This challenge is active!", Snackbar.LENGTH_SHORT);
                    } else {
                        cc.sendSingleInProgressForDuel(challenge_id, friend_id);
                        snackbar = Snackbar.make(view, "Sent duel request", Snackbar.LENGTH_SHORT);
                    }
                    snackbar.show();
                }
            }
        });
        snackbar.show();
    }

//                    case DialogInterface.BUTTON_NEGATIVE:
//                        break;
//                }
//            }
//        };
//
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
//        builder.setTitle(R.string.areYouSure)
//                .setMessage(R.string.youWannaSendDuelRequest)
//                .setIcon(R.drawable.duel_blue)
//                .setCancelable(false)
//                .setPositiveButton(R.string.yes, dialogClickListener)
//                .setNegativeButton(R.string.no, dialogClickListener).show();



    // check user input data @description @days @isActive
    private boolean checkInputUserData(String description, String duration, View view) {
        if (!duration.isEmpty()) {
            days = Integer.parseInt(duration);
        }
        if (!cc.isActive(description) && !description.isEmpty() && !description.startsWith(" ") && !duration.isEmpty() && days != 0) {
            snackbar = Snackbar.make(view, "Sent duel request!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return true;
        } else if (cc.isActive(description)) {
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