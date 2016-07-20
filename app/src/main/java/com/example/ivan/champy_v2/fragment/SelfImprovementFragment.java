package com.example.ivan.champy_v2.fragment;

import android.content.ContentValues;
import android.content.Intent;
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
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHSetupUI;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.CreateChallenge;
import com.example.ivan.champy_v2.interfaces.SingleInProgress;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;
import com.example.ivan.champy_v2.model.active_in_progress.Datum;

import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static java.lang.Math.round;

// TODO: 13.07.2016 показати до і після мене цей клас на review
public class SelfImprovementFragment extends Fragment implements View.OnClickListener {

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
                //if (!c.getString(nameColIndex).equals("User_Challenge")) {
                    if (o > position + 1) break;
                    if (o == position + 1) {
                        name = c.getString(nameColIndex);
                        description = c.getString(coldescription);
                        duration = c.getString(colduration);
                        challenge_id = c.getString(colchallenge_id);
                    }
                //}
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

        tvGoal.setText(description);
        tvDays.setText("" + days);
        tvGoal.setTypeface(typeface);
        tvDays.setTypeface(typeface);
        //tvGoal.setVisibility(View.VISIBLE);
        //tvDays.setVisibility(View.VISIBLE);
        //etGoal.setVisibility(View.INVISIBLE);
        //etDays.setVisibility(View.INVISIBLE);

        Glide.with(getContext()).load(R.drawable.points).override(120, 120).into((ImageView)view.findViewById(R.id.imageViewAcceptButton));

        if (!isActive(description)){
            //final int[] finalposition = new int[1];
            final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
            CHSetupUI chSetupUI = new CHSetupUI();
            chSetupUI.setupUI(getActivity().findViewById(R.id.selfimprovement), getActivity());
            chSetupUI.setupUI(view, getActivity());
            if (position == size) {
                etGoal.setTypeface(typeface);
                etDays.setTypeface(typeface);
                //if (duration != null && duration != "") {
                //    days = Integer.parseInt(duration) / 86400;
                //}
                //etDays.setText("" + days);
                //tvGoal.setVisibility(View.INVISIBLE);
                etGoal.setText(description);
                etDays.setHint("21");
                etDays.setVisibility(View.VISIBLE);
                etGoal.setVisibility(View.VISIBLE);
                tvDays.setVisibility(View.INVISIBLE);
            }

            ImageButton buttonAccept = (ImageButton) getActivity().findViewById(R.id.imageButtonAcceptSelfImprovement);
            //buttonAccept.setVisibility(View.VISIBLE);
            buttonAccept.setOnClickListener(this);
            buttonAccept.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String name = "";
                    String duration = "";
                    String description = "";
                    String challenge_id = "";
                    int days = 21;

                    EditText etGoal = (EditText) view.findViewById(R.id.et_goal);
                    EditText etDays = (EditText) view.findViewById(R.id.et_days);
                    description = etGoal.getText().toString();
                    duration = etDays.getText().toString();

                    if (!etDays.getText().toString().isEmpty()){
                        days = Integer.parseInt(duration);
                    }

                    Cursor c = db.query("selfimprovement", null, null, null, null, null, null);
                    int position = viewPager.getCurrentItem();
                    int size = sessionManager.getSelfSize();
                    OfflineMode offlineMode = new OfflineMode();
                    if (position == size) {
                        // тут би треба було days присваювати значення, яке вводить юзер
                        if ((!isActive(description)) && offlineMode.isConnectedToRemoteAPI(getActivity()) && (!description.isEmpty()) &&
                                (!description.startsWith(" ")) && (days != 0) && (!duration.isEmpty()) && (!isActive(description))) {

                            days = Integer.parseInt(duration);
                            Create_new_challenge(description, days);
                            Toast.makeText(getActivity(), "Challenge created", Toast.LENGTH_SHORT).show();
                            return true;
                        } else {
                            if (isActive(description)) {
                                Toast.makeText(getContext(), "Already exist", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            if (description.isEmpty() || description.startsWith(" ")) {
                                Toast.makeText(getContext(), "Goal is empty!", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            if (duration.isEmpty() || days == 0) {
                                Toast.makeText(getContext(), "Min 1 day!", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                    }
                    else {
                        // тут би треба було days присваювати значення, яке вводить юзер
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

                        if ((!isActive(description)) && offlineMode.isConnectedToRemoteAPI(getActivity())
                                && (!description.equals("active")) && (!description.isEmpty()) && (!description.startsWith(" "))
                                && (days != 0) && (!duration.isEmpty())) {
                            Toast.makeText(getActivity(), "Challenge created", Toast.LENGTH_SHORT).show();
                            StartSingleInProgress(challenge_id);
                            return true;
                        } else {
                            //if ((description.isEmpty() || description.startsWith(" ")) && (days == 0 || duration.isEmpty())) {
                            //    Toast.makeText(getContext(), "Card is empty!", Toast.LENGTH_SHORT).show();
                            //    return true;
                            //}
                            if (isActive(description)) {
                                Toast.makeText(getContext(), "This challenge is active", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            if (description.isEmpty() || description.startsWith(" ")) {
                                Toast.makeText(getContext(), "Goal is empty!", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            if (duration.isEmpty() || days == 0) {
                                Toast.makeText(getContext(), "Min 1 day!", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                    }
                    return true;
                }
            });
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity(), "Tap & Hold", Toast.LENGTH_SHORT).show();
    }


    public void Create_new_challenge(String description, int days) {
        String type_id = "567d51c48322f85870fd931a";
        final SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        String duration = "" + (days * 86400);
        String details = description + " during this period";

        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call =
                createChallenge.createChallenge(
                        "User_Challenge",
                        type_id,
                        description,
                        details,
                        duration,
                        token
                );
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    String challenge = response.body().getData().get_id();
                    Log.i("stat", "Status: " + challenge);
                    StartSingleInProgress(challenge);
                    Log.i("stat", "Status: Challenge Created");

                } else Log.i("stat", "Status: Error Creating");
            }

            @Override
            public void onFailure(Throwable t) {}
        });

    }


    public void StartSingleInProgress(final String challenge) {
        final SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");

        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = singleinprogress.start_single_in_progress(challenge, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    //Log.i("stat", "Status: Starting OK");
                    ContentValues cv = new ContentValues();
                    com.example.ivan.champy_v2.single_inprogress.SingleInProgress data = response.body();
                    cv.put("challenge_id", data.getData().get_id());
                    //Log.d("myLogs", "Added: " + data.getData().get_id());
                    cv.put("updated", "false");
                    DBHelper dbHelper = new DBHelper(getActivity());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.insert("updated", null, cv);
                    generate();
                } //else Log.i("stat", "Status: Starting WRONG" + response.code());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    public void generate() {
        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("myChallenges", null, null);
        final ContentValues cv = new ContentValues();
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        String id = user.get("id");
        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        final long unixTime = System.currentTimeMillis() / 1000L;
        final String update = "0"; //1457019726
        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(id, update, token);
        call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);
                        Challenge challenge = datum.getChallenge();
                        String desctiption = challenge.getDetails();
                        String duration = "";
                        if (datum.getEnd() != null) {
                            int end = datum.getEnd();
                            int days = round((end - unixTime) / 86400);
                            duration = "" + days;
                        }
                        String challenge_id = datum.get_id();
                        // добавить дуели?
                        if (challenge.getDescription().equals("Wake Up")) {
                            cv.put("name", "Wake Up");
                        } else if (challenge.getDescription().equals("Self Improvement")) {
                            cv.put("name", "Self Improvement");
                        } else {
                          cv.put("name", "Duel");
                        }

                        cv.put("description", desctiption);
                        cv.put("duration", duration);
                        cv.put("challenge_id", challenge_id);
                        cv.put("status", datum.getStatus());
                        String updated = find(challenge_id);
                        cv.put("updated", updated);
                        db.insert("myChallenges", null, cv);
                    }
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


    /*public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    return false;
                }

            });
        }

    }*/


    public String find(String challenge_id) {
        DBHelper dbHelper = new DBHelper(getActivity());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("updated", null, null, null, null, null, null);
        String ok = "false";
        int o = 0;
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            do {
                if (c.getString(colchallenge_id).equals(challenge_id)){
                    ok = c.getString(c.getColumnIndex("updated"));
                    Log.i("stat", "Find: "+ok);
                    break;
                }
            } while (c.moveToNext());
        } else
            Log.i("stat", "0 rows");
        c.close();
        return ok;
    }


    public boolean isActive(String description) {
        DBHelper dbHelper = new DBHelper(getActivity());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        final Bundle args = this.getArguments();
        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
        int position = args.getInt(ARG_PAGE);
        Log.i("stat", "Status: " + position);
        description = description + " during this period";
        boolean ok = false;
        int o = 0;
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int coldescription = c.getColumnIndex("description");
            int colduration = c.getColumnIndex("duration");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            do {
                if (c.getString(c.getColumnIndex("status")).equals("started")){
                    Log.i("stat", "Equals: "+c.getString(coldescription)+" "+description);
                    if (c.getString(coldescription).equals(description)) {
                        Log.i("stat", "Equals: true");
                        ok = true;
                    }
                }
            } while (c.moveToNext());
        } else
            Log.i("stat", "0 rows");
        c.close();

        return ok;
    }
}