package com.example.ivan.champy_v2;

import android.app.Activity;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
        Log.i("stat", "Status: Created");
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
        // достаем карточки с базы данных
        if (c.moveToLast()) {
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
            } while (c.moveToPrevious());
        } /*else {
            Log.i("stat", "0 rows");
        }*/
        c.close();
        Log.i("stat", "Name: " + name);
        final SessionManager sessionManager = new SessionManager(getContext());
        final int[] finalposition = new int[1];
        final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        setupUI(getActivity().findViewById(R.id.selfimprovement));
        setupUI(view);
        int size = sessionManager.getSelfSize();

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        TextView tvGoal = (TextView) view.findViewById(R.id.goal_text);
        TextView tvDays = (TextView) view.findViewById(R.id.days_text);
        final EditText etGoal = (EditText) view.findViewById(R.id.et_goal);
        final EditText etDays = (EditText) view.findViewById(R.id.et_days);

        Glide.with(getContext())
                .load(R.drawable.points)
                .override(120, 120)
                .into((ImageView) view.findViewById(R.id.imageViewAcceptButton));

        if (position != size) {
            int days = 0;
            if (duration != null && duration != "") {
                days = Integer.parseInt(duration) / 86400;
            }
            tvGoal.setText(description);
            tvGoal.setTypeface(typeface);
            tvGoal.setVisibility(View.VISIBLE);

            tvDays.setText("" + days);
            tvDays.setTypeface(typeface);
            tvDays.setVisibility(View.VISIBLE);

            etGoal.setVisibility(View.INVISIBLE);
            etDays.setVisibility(View.INVISIBLE);
        } else {
            tvGoal.setText(description);
            tvGoal.setVisibility(View.VISIBLE);
            tvGoal.setTypeface(typeface);

            tvDays.setVisibility(View.VISIBLE);
            tvDays.setTypeface(typeface);

            etGoal.setVisibility(View.VISIBLE);
            etDays.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(R.drawable.points)
                    .override(120, 120)
                    .into((ImageView) view.findViewById(R.id.imageViewAcceptButton));
        }

        ImageButton imageButtonAccept = (ImageButton) getActivity().findViewById(R.id.imageButtonAcceptSelfImprovement);
        imageButtonAccept.setVisibility(View.VISIBLE);

        imageButtonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Tap & Hold", Toast.LENGTH_SHORT).show();
            }
        });

        imageButtonAccept.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String name = "";
                String duration = "";
                String description = "";
                String challenge_id = "";
                int days = 0;
                EditText editTextDays = (EditText) view.findViewById(R.id.et_days);
                duration = editTextDays.getText().toString();

                EditText editTextGoal = (EditText) view.findViewById(R.id.et_goal);
                description = editTextGoal.getText().toString();

                //Log.i("stat", "Description :" + description + " " + description.length());
                if (!editTextDays.getText().toString().equals("")) {
                    days = Integer.parseInt(editTextDays.getText().toString());
                }
                Cursor c = db.query("selfimprovement", null, null, null, null, null, null);
                int position = viewPager.getCurrentItem();


                int size = sessionManager.getSelfSize();

                //Log.i("stat", "Click: " + position + " " + size);
                // якшо (position = size) - значить я стою на пустому (останьому)
                if (position == size) {
                    //editTextGoal = (EditText) view.findViewById(R.id.et_goal);
                    //description = etGoal.getText().toString();
                    //Log.i("stat", "Click: clicked");
                    //Log.i("stat", "Click: " + description);
                    //editTextGoal = (EditText) view.findViewById(R.id.et_days);
                    // тут всьо чотко, я виправив
                    if (description.equals("") && (days == 0 || duration.equals(""))) {
                        Toast.makeText(getContext(), "Card is empty!", Toast.LENGTH_SHORT).show();
                    } else if (description.isEmpty() || description.startsWith(" ")) {
                        Toast.makeText(getContext(), "Goal is empty!", Toast.LENGTH_SHORT).show();
                    } else if (duration.equals("") || days == 0) {
                        Toast.makeText(getContext(), "Min 1 day!", Toast.LENGTH_SHORT).show();
                    } else {
                        OfflineMode offlineMode = new OfflineMode();
                        if (offlineMode.isInternetAvailable(getActivity())) {
                            days = Integer.parseInt(duration);
                            Create_new_challenge(description, days);
                            Toast.makeText(getActivity(), "Challenge created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else { // тут вже создані карточки
                    Log.i("stat", "Status: Poehali");
                    int o = 0;
                    // опять карточки з бд
                    /*if (c.moveToFirst()) {
                        int idColIndex = c.getColumnIndex("id");
                        int nameColIndex = c.getColumnIndex("name");
                        int coldescription = c.getColumnIndex("description");
                        int colduration = c.getColumnIndex("duration");
                        int colchallenge_id = c.getColumnIndex("challenge_id");
                        // ручками перевіряєм
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
                    } else Log.i("stat", "0 rows");
                    c.close();*/
                    /*description = ((EditText) view.findViewById(R.id.et_goal)).getText().toString();
                    Log.i("stat", "Description :" + description + " " + description.length());
                    if (duration != null && duration != "") {
                        days = Integer.parseInt(duration) / 86400;
                    }
                    Log.i("stat", "Click: " + viewPager.getCurrentItem());
                    // тут всьо чотко, я виправив
                    if (description.equals("") && (days == 0 || etGoal.getText().toString().equals(""))) { // descriptions = days
                        Toast.makeText(getContext(), "Card is empty", Toast.LENGTH_SHORT).show();
                    } else if (description.isEmpty() || description.equals(" ") || description.startsWith(" ")) { // descriptions = goal
                        Toast.makeText(getContext(), "Goal is empty!", Toast.LENGTH_SHORT).show();
                    } else if (etGoal.getText().toString().equals("") || days == 0) {
                        Toast.makeText(getContext(), "Min 1 day", Toast.LENGTH_SHORT).show();
                    } else {
                        OfflineMode offlineMode = new OfflineMode();
                        if (offlineMode.isInternetAvailable(getActivity())) {
                            days = Integer.parseInt(etGoal.getText().toString());
                            Create_new_challenge(description, days);
                            Toast.makeText(getActivity(), "Challenge created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                        }
                    }*/
                }
                return true;
            }
        });
        return view;
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
            public void onFailure(Throwable t) {

            }
        });

    }

    public void StartSingleInProgress(final String challenge) {
        final SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");

        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call =
                singleinprogress.start_single_in_progress(
                        challenge,
                        token
                );
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("stat", "Status: Starting OK");
                    ContentValues cv = new ContentValues();
                    com.example.ivan.champy_v2.single_inprogress.SingleInProgress data = response.body();
                    cv.put("challenge_id", data.getData().get_id());
                    Log.d("myLogs", "Added: " + data.getData().get_id());
                    cv.put("updated", "false");
                    DBHelper dbHelper = new DBHelper(getActivity());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.insert("updated", null, cv);
                    generate();
                } else Log.i("stat", "Status: Starting WRONG" + response.code());
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
        final String update = "1457019726";
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
                            duration = "" + days;}
                        String challenge_id = datum.get_id();
                        if (challenge.getDescription().equals("Wake Up")) {
                            cv.put("name", "Wake Up");
                        }
                        else {
                            cv.put("name", "Self Improvement");
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

    public static void hideSoftKeyboard(Activity activity) {
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

    }

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
                if (c.getString(c.getColumnIndex("status")).equals("created")){
                    Log.i("stat", "Equals: "+c.getString(coldescription)+" "+description);
                    if (c.getString(coldescription).equals(description)){
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
