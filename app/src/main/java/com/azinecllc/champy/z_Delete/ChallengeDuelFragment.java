//package com.azinecllc.champy.fragment;
//
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import com.azinecllc.champy.R;
//import com.azinecllc.champy.controller.ChallengeController;
//import com.azinecllc.champy.data.DBHelper;
//import com.azinecllc.champy.utils.OfflineMode;
//import com.azinecllc.champy.utils.SessionManager;
//
//public class ChallengeDuelFragment extends Fragment implements View.OnClickListener {
//
//    private static final String ARG_PAGE = "ARG_PAGE";
//    private int position, size, days = 21, o = 0;
//    private String name, duration, description, challenge_id, friend_id;
//    private SessionManager sessionManager;
//    private ChallengeController cc;
//    private TextView etDays;
//    private EditText etGoal;
//    private ViewPager viewPager;
//    private SQLiteDatabase db;
//    private Snackbar snackbar;
//    private Cursor c;
//
//
//    public static ChallengeDuelFragment newInstance(int page) {
//        Bundle args = new Bundle();
//        args.putInt(ARG_PAGE, page);
//        ChallengeDuelFragment fragment = new ChallengeDuelFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
//        final View view = inflater.inflate(R.layout.item_card_self_and_duel, container, false);
//        DBHelper dbHelper = DBHelper.getInstance(getContext());
//        Bundle extras = getActivity().getIntent().getExtras();
//        Bundle args = this.getArguments();
//        db = dbHelper.getWritableDatabase();
//        friend_id = (extras == null) ? null : extras.getString("id");
//        c = db.query("duel", null, null, null, null, null, null);
//        position = args.getInt(ARG_PAGE);
//        if (c.moveToFirst()) {
//            int nameColIndex    = c.getColumnIndex("name");
//            int coldescription  = c.getColumnIndex("description");
//            int colduration     = c.getColumnIndex("duration");
//            int colchallenge_id = c.getColumnIndex("challenge_id");
//            do {
//                o++;
//                if (o > position + 1) break;
//                if (o == position + 1) {
//                    name         = c.getString(nameColIndex);
//                    duration     = c.getString(colduration);
//                    description  = c.getString(coldescription);
//                    challenge_id = c.getString(colchallenge_id);
//                }
//            } while (c.moveToNext());
//        }
//        c.close();
//
//        sessionManager = SessionManager.getInstance(getContext());
//        String token = sessionManager.getToken();
//        String userId = sessionManager.getUserId();
//        cc = new ChallengeController(getContext(), getActivity());
//        size = sessionManager.getSelfSize();
//        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
//        View line = view.findViewById(R.id.line);
//        etGoal = (EditText)view.findViewById(R.id.et_goal);
//        etDays = (TextView)view.findViewById(R.id.et_days);
//        TextView tvGoal = (TextView) view.findViewById(R.id.goal_text);
//        TextView tvDays = (TextView) view.findViewById(R.id.days_text);
//        TextView tvEvery = (TextView) view.findViewById(R.id.tvEveryDaySelf);
//        TextView textDays = (TextView) view.findViewById(R.id.textDays);
//        ImageButton btnPlus = (ImageButton) view.findViewById(R.id.imageButtonPlus);
//        ImageButton btnMinus = (ImageButton) view.findViewById(R.id.imageButtonMinus);
//
//        if (duration != null && !duration.isEmpty()) days = Integer.parseInt(duration) / 86400;
//
//        tvDays.setText(String.format("%s", days + " days")); //days + " days");
//        tvGoal.setText(name);
//        tvDays.setTypeface(typeface);
//        tvGoal.setTypeface(typeface);
//        tvEvery.setTypeface(typeface);
//        textDays.setTypeface(typeface);
//        textDays.setVisibility(View.INVISIBLE);
//        ImageButton imageButtonAccept = (ImageButton) getActivity().findViewById(R.id.ok);
//
//        viewPager = (ViewPager) getActivity().findViewById(R.id.pager_duel);
//
//        if (position == size) {
//            etGoal.setTypeface(typeface);
//            etDays.setTypeface(typeface);
//            etDays.setHint("21");
//            etDays.setVisibility(View.VISIBLE);
//            etGoal.setVisibility(View.VISIBLE);
//            tvDays.setVisibility(View.INVISIBLE);
//            tvGoal.setVisibility(View.INVISIBLE);
//            line.setVisibility(View.INVISIBLE);
//            textDays.setVisibility(View.VISIBLE);
//            btnMinus.setVisibility(View.VISIBLE);
//            btnPlus.setVisibility(View.VISIBLE);
//        }
//
//        OfflineMode offlineMode = OfflineMode.getInstance();
//        offlineMode.isConnectedToRemoteAPI(getActivity());
//        imageButtonAccept.setOnClickListener(this);
//        btnMinus.setOnClickListener(this);
//        btnPlus.setOnClickListener(this);
//
//        return view;
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.ok:
//                snackbar = Snackbar.make(view, R.string.are_you_sure, Snackbar.LENGTH_LONG).setAction(R.string.yes, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        position = viewPager.getCurrentItem();
//                        size = sessionManager.getSelfSize();
//                        if (position == size) {
//                            name = etGoal.getText().toString();
//                            duration = etDays.getText().toString();
//                            try {
//                                days = Integer.parseInt(duration);
//                                if (!cc.isActive(name) && !name.isEmpty()) {
//                                    cc.createNewDuelChallenge(name, days, friend_id);
//                                    snackbar = Snackbar.make(view, (R.string.sent_duel_request), Snackbar.LENGTH_SHORT);
//                                    snackbar.show();
//                                } else {
//                                    snackbar = Snackbar.make(view, R.string.challenge_cant_create, Snackbar.LENGTH_SHORT);
//                                    snackbar.show();
//                                }
//                            } catch (NullPointerException | NumberFormatException e) {
//                                e.printStackTrace();
//                            }
//
//                        } else {
//                            c = db.query("duel", null, null, null, null, null, null);
//                            if (c.moveToFirst()) {
//                                int idColIndex      = c.getColumnIndex("id");
//                                int nameColIndex    = c.getColumnIndex("name");
//                                int colduration     = c.getColumnIndex("duration");
//                                int coldescription  = c.getColumnIndex("description");
//                                int colchallenge_id = c.getColumnIndex("challenge_id");
//                                o = 0;
//                                do {
//                                    o++;
//                                    if (o > position  + 1) break;
//                                    if (o == position + 1) {
//                                        name         = c.getString(nameColIndex);
//                                        duration     = c.getString(colduration);
//                                        description  = c.getString(coldescription);
//                                        challenge_id = c.getString(colchallenge_id);
//                                        break;
//                                    }
//                                } while (c.moveToNext());
//                            }
//                            c.close();
//
//                            try {
//                                if (!cc.isActive(name)) {
//                                    cc.sendSingleInProgressForDuel(challenge_id, friend_id);
//                                    snackbar = Snackbar.make(view, (R.string.sent_duel_request), Snackbar.LENGTH_SHORT);
//                                    snackbar.show();
//                                } else {
//                                    snackbar = Snackbar.make(view, R.string.challenge_cant_create, Snackbar.LENGTH_SHORT);
//                                    snackbar.show();
//                                }
//                            } catch (NullPointerException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//                snackbar.show();
//                break;
//
//            case R.id.imageButtonPlus:
//                int daysCount = Integer.parseInt(etDays.getText().toString());
//                int newDaysCount;
//                if (daysCount < 100) {
//                    newDaysCount = daysCount + 1;
//                    etDays.setText(String.valueOf(newDaysCount));
//                }
//                break;
//            case R.id.imageButtonMinus:
//                daysCount = Integer.parseInt(etDays.getText().toString());
//                if (daysCount > 1) {
//                    newDaysCount = daysCount - 1;
//                    etDays.setText(String.valueOf(newDaysCount));
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        //Runtime.getRuntime().runFinalization();
//        //Runtime.getRuntime().gc();
//    }
//
//
//}