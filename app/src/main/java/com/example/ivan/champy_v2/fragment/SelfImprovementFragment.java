package com.example.ivan.champy_v2.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
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

import static java.lang.Math.round;

public class SelfImprovementFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String TAG = "SelfImprovementFragment";
    public int position, size, days = 21;
    public String duration = "", description = "", challenge_id = "", status = "", name = "";
    public SessionManager sessionManager;
    public TextView tvGoal, tvDays;
    public EditText etGoal, etDays;
    public ChallengeController cc;
    public ViewPager viewPager;
    public Typeface typeface;
    public SQLiteDatabase db;
    public Snackbar snackbar;
    public DBHelper dbHelper;
    public Cursor c;

    public static SelfImprovementFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SelfImprovementFragment fragment = new SelfImprovementFragment();
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
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.item_row, container, false);
        Log.i(TAG, "onCreateView");
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        final Bundle args = this.getArguments();
        c = db.query("selfimprovement", null, null, null, null, null, null);
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

        if (duration != null && !duration.isEmpty()) {
            days = Integer.parseInt(duration) / 86400;
        }

        tvDays.setText("" + days);
        tvDays.setTypeface(typeface);
        tvGoal.setText(description);
        tvGoal.setTypeface(typeface);

        Glide.with(getContext()).load(R.drawable.points).override(120, 120).into((ImageView)view.findViewById(R.id.imageViewAcceptButton));
        ImageButton buttonAccept = (ImageButton) getActivity().findViewById(R.id.imageButtonAcceptSelfImprovement);

        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        CHSetupUI chSetupUI = new CHSetupUI();
        chSetupUI.setupUI(getActivity().findViewById(R.id.selfimprovement), getActivity());
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
        buttonAccept.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick");

        //                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.i(TAG, "onClickDialog");
        description = etGoal.getText().toString();
        duration = etDays.getText().toString();
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        c = db.query("selfimprovement", null, null, null, null, null, null);
        position = viewPager.getCurrentItem();
        size = sessionManager.getSelfSize();
//                        switch (which){
//                            case DialogInterface.BUTTON_POSITIVE:
        snackbar = Snackbar.make(view, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClickSnackBar: ");
                cc = new ChallengeController(getContext(), getActivity(), 0, 0, 0);
                if (position == size) {
                    if (checkInputUserData(description, duration, view)) {
                        days = Integer.parseInt(duration);
                        cc.createNewSelfImprovementChallenge(description, days);
                    }
                } else {
                    if (c.moveToFirst()) {
                        int coldescription = c.getColumnIndex("description");
                        int colchallenge_id = c.getColumnIndex("challenge_id");
                        int o = 0;
                        do {
                            o++;
                            if (o > position + 1) break;
                            if (o == position + 1) {
                                description = c.getString(coldescription);
                                challenge_id = c.getString(colchallenge_id);
                            }
                        } while (c.moveToNext());
                    }
                    c.close();

                    if (cc.isActive(description)) {
                        snackbar = Snackbar.make(view, "This challenge is active!", Snackbar.LENGTH_SHORT);
                    } else {
                        cc.sendSingleInProgressForSelf(challenge_id);
                        snackbar = Snackbar.make(view, "Challenge Created!", Snackbar.LENGTH_SHORT);
                    }
                    snackbar.show();
                }
            }
        });
        snackbar.show();
//                                break;
//                            case DialogInterface.BUTTON_NEGATIVE:
//                                break;
//                        }
//                    }



//                DialogFragment builder = new DialogFragment().getDialog();
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle(R.string.areYouSure)
//                        .setMessage(R.string.youWannaCreateThisChall)
//                        .setIcon(R.drawable.self_blue)
//                        .setCancelable(false)
//                        .setPositiveButton(R.string.yes, dialogClickListener)
//                        .setNegativeButton(R.string.no,  dialogClickListener).show();
//
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
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


    // check user input data @description @days @isActive
    private boolean checkInputUserData(String description, String duration, View view) {
//        if (!duration.isEmpty()) {
//            days = Integer.parseInt(duration);
//        }
        if (!cc.isActive(description) && !description.isEmpty() && !description.startsWith(" ") && !duration.isEmpty() && days != 0) {
            snackbar = Snackbar.make(view, "Challenge created!", Snackbar.LENGTH_SHORT);
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

//    // check for isActive challenge
//    private boolean isActive(String description) {
//        DBHelper dbHelper = new DBHelper(getActivity());
//        final SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
//        description = description + " during this period";
//        boolean ok = false;
//        if (c.moveToFirst()) {
//            int coldescription = c.getColumnIndex("description");
//            do {
//                if (c.getString(c.getColumnIndex("status")).equals("started")) {
//                    if (c.getString(coldescription).equals(description)) {
//                        ok = true;
//                    }
//                }
//            } while (c.moveToNext());
//        }
//        c.close();
//        return ok;
//    }


}