package com.example.ivan.champy_v2.fragment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class SelfImprovementFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";


    public static SelfImprovementFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SelfImprovementFragment fragment = new SelfImprovementFragment();
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
        final View view = inflater.inflate(R.layout.item_row, container, false);
        String name = "";
        String duration = "";
        String description = "";
        String challenge_id = "";
        String status = "";
        DBHelper dbHelper = new DBHelper(getActivity());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        final Bundle args = this.getArguments();
        Cursor c = db.query("selfimprovement", null, null, null, null, null, null);
        int position = args.getInt(ARG_PAGE);
        Log.i("stat", "Status: " + position);
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
        final SessionManager sessionManager = new SessionManager(getContext());
        int size = sessionManager.getSelfSize();
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        TextView tvGoal = (TextView)view.findViewById(R.id.goal_text);
        TextView tvDays = (TextView)view.findViewById(R.id.days_text);
        EditText etGoal = (EditText)view.findViewById(R.id.et_goal);
        EditText etDays = (EditText)view.findViewById(R.id.et_days);

        int days = 21;
        if (duration != null && !duration.isEmpty()) {
            days = Integer.parseInt(duration) / 86400;
        }

        tvDays.setText("" + days);
        tvDays.setTypeface(typeface);
        tvGoal.setText(description);
        tvGoal.setTypeface(typeface);

        Glide.with(getContext()).load(R.drawable.points).override(120, 120).into((ImageView)view.findViewById(R.id.imageViewAcceptButton));
        ImageButton buttonAccept = (ImageButton) getActivity().findViewById(R.id.imageButtonAcceptSelfImprovement);

        final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
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

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = "";
                        String duration = "";
                        String description = "";
                        String challenge_id = "";
                        int days;

                        EditText etGoal = (EditText) view.findViewById(R.id.et_goal);
                        EditText etDays = (EditText) view.findViewById(R.id.et_days);
                        description = etGoal.getText().toString();
                        duration = etDays.getText().toString();

                        if (!duration.isEmpty()){ days = Integer.parseInt(duration); }

                        Cursor c = db.query("selfimprovement", null, null, null, null, null, null);
                        int position = viewPager.getCurrentItem();
                        int size = sessionManager.getSelfSize();

                        ChallengeController cc = new ChallengeController(getContext(), getActivity(), 0, 0, 0);
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                if (position == size) {
                                    if ((checkInputUserData(description, duration))) {
                                        days = Integer.parseInt(duration);
                                        cc.createNewSelfImprovementChallenge(description, days);
                                    }
                                } else {
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
                                                break;
                                            }
                                        } while (c.moveToNext());
                                    }
                                    c.close();

                                    if (isActive(description)) {
                                        Toast.makeText(getContext(), "This challenge is active", Toast.LENGTH_SHORT).show();
                                    } else {
                                        cc.sendSingleInProgressForSelf(challenge_id);
                                        Toast.makeText(getActivity(), "Challenge created", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setTitle("Are you sure")
                        .setMessage("You wanna create this challenge?")
                        .setIcon(R.drawable.self_blue)
                        .setCancelable(false)
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No",  dialogClickListener).show();
            }
        });
        return view;
    }


    // проверяем данные для создания челенджа
    private boolean checkInputUserData(String description, String duration) {
        int days = 21;
        if (!duration.isEmpty()) {
            days = Integer.parseInt(duration);
        }
        if (!isActive(description) && !description.isEmpty() && !description.startsWith(" ") && !duration.isEmpty() && days != 0) {
            Toast.makeText(getActivity(), "Challenge created", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isActive(description)) {
            Toast.makeText(getContext(), "This challenge is active", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(getContext(), "Complete all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    // проверяем активный ли челендж
    private boolean isActive(String description) {
        DBHelper dbHelper = new DBHelper(getActivity());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        description = description + " during this period";
        boolean ok = false;
        if (c.moveToFirst()) {
            int coldescription = c.getColumnIndex("description");
            do {
                if (c.getString(c.getColumnIndex("status")).equals("started")) {
                    if (c.getString(coldescription).equals(description)) {
                        ok = true;
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        return ok;
    }


}