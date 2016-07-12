package com.example.ivan.champy_v2.fragment;

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
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.duel.Duel;
import com.example.ivan.champy_v2.helper.CHSetupUI;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.CreateChallenge;
import com.example.ivan.champy_v2.interfaces.SingleInProgress;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;
import com.example.ivan.champy_v2.model.active_in_progress.Datum;
import com.example.ivan.champy_v2.model.active_in_progress.Recipient;
import com.example.ivan.champy_v2.model.active_in_progress.Sender;

import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class DuelFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    public static DuelFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        DuelFragment fragment = new DuelFragment();
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
        String friend_id = "";
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras == null) {
        } else {
            friend_id = extras.getString("id");
        }
        DBHelper dbHelper = new DBHelper(getActivity());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        final Bundle args = this.getArguments();
        Cursor c = db.query("duel", null, null, null, null, null, null);
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
        Log.i("stat", "Name: " + name);
        if (isActive(description)) {
            Log.i("stat", "Status: Active");
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
            TextView textView = (TextView)view.findViewById(R.id.goal_text);
            textView.setText(description);
            textView.setTypeface(typeface);
            textView.setVisibility(View.VISIBLE);
            textView = (TextView)view.findViewById(R.id.days_text);
            int days = 0;
            if (duration != null && duration != "") {
                days = Integer.parseInt(duration) / 86400;
            }
            textView.setText("" + days);
            textView.setTypeface(typeface);
            textView.setVisibility(View.VISIBLE);
            EditText editText = (EditText) view.findViewById(R.id.et_goal);
            editText.setVisibility(View.INVISIBLE);
            editText = (EditText)view.findViewById(R.id.et_days);
            editText.setVisibility(View.INVISIBLE);
            Glide.with(getContext())
                    .load(R.drawable.points)
                    .override(120, 120)
                    .into((ImageView) view.findViewById(R.id.imageViewAcceptButton));
        }
        else {
            final int[] finalposition = new int[1];
            final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager_duel);
            CHSetupUI chSetupUI = new CHSetupUI();
            chSetupUI.setupUI(getActivity().findViewById(R.id.duel_back), getActivity());
            chSetupUI.setupUI(view, getActivity());

            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
            EditText editText = (EditText) view.findViewById(R.id.et_goal);
            editText.setText(description);
            editText.setTypeface(typeface);
            editText.setVisibility(View.VISIBLE);
            editText = (EditText) view.findViewById(R.id.et_days);
            editText.setTypeface(typeface);
            int days = 0;
            if (duration != null && duration != "") {
                days = Integer.parseInt(duration) / 86400;
            }
            editText.setText("" + days);
            editText.setVisibility(View.VISIBLE);
            TextView textView = (TextView) view.findViewById(R.id.tvDays);
            textView.setTypeface(typeface);
            textView = (TextView)view.findViewById(R.id.goal_text);
            textView.setVisibility(View.INVISIBLE);
            textView = (TextView)view.findViewById(R.id.days_text);
            textView.setVisibility(View.INVISIBLE);
            Glide.with(getContext())
                    .load(R.drawable.points)
                    .override(120, 120)
                    .into((ImageView) view.findViewById(R.id.imageViewAcceptButton));
            editText = (EditText) view.findViewById(R.id.et_goal);
            description = editText.getText().toString();
            editText = (EditText) view.findViewById(R.id.et_days);
            days = Integer.parseInt(editText.getText().toString());
            Log.i("stat", "Description: " + description);
            ImageButton imageButton = (ImageButton) getActivity().findViewById(R.id.ok);
            imageButton.setVisibility(View.VISIBLE);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Tap & Hold", Toast.LENGTH_SHORT).show();
                }
            });

            final String finalFriend_id = friend_id;
            imageButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String name = "";
                    String duration = "";
                    String description = "";
                    String challenge_id = "";
                    int days = 0;
                    EditText editTextGoal = (EditText) view.findViewById(R.id.et_goal);
                    description = editTextGoal.getText().toString();
                    editTextGoal = (EditText) view.findViewById(R.id.et_days);
                    Log.i("stat", "Descrition :"+description+ " " + description.length());
                    if (editTextGoal.getText().toString().equals("")){
                        Toast.makeText(getContext(), "Duration is empty!!!", Toast.LENGTH_SHORT).show();
                    } else days = Integer.parseInt(editTextGoal.getText().toString());
                    Cursor c = db.query("duel", null, null, null, null, null, null);
                    int position = viewPager.getCurrentItem();
                    final SessionManager sessionManager = new SessionManager(getContext());
                    int size = sessionManager.getSelfSize();

                    Log.i("stat", "Click: " + position + " " + size);
                    if (position == size) {
                        editTextGoal = (EditText) view.findViewById(R.id.et_goal);
                        description = editTextGoal.getText().toString();
                        Log.i("stat", "Click: clicked");
                        Log.i("stat", "Click: " + description);
                        editTextGoal = (EditText) view.findViewById(R.id.et_days);
                        if (editTextGoal.getText().toString().equals("") || days == 0) {
                            Toast.makeText(getContext(), "Min 1 day!", Toast.LENGTH_SHORT).show();
                        } else if (description.equals(" ") || description.startsWith(" ") || description.isEmpty()) {
                            Toast.makeText(getContext(), "Goal is empty!!!", Toast.LENGTH_SHORT).show();
                        } else if (name.equals("active")) {
                            Toast.makeText(getContext(), "This challenge is active", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            OfflineMode offlineMode = new OfflineMode();
                            if (offlineMode.isInternetAvailable(getActivity())) {
                                days = Integer.parseInt(editTextGoal.getText().toString());
                                Create_new_challenge(description, days, finalFriend_id);
                                Toast.makeText(getContext(), "Challenge created", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.i("stat", "Status: Poehali");
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
                        } else Log.i("stat", "0 rows");
                        c.close();
                        description = ((EditText) view.findViewById(R.id.et_goal)).getText().toString();
                        Log.i("stat", "Description :" + description + " " + description.length());
                        if (duration != null && duration != "") {
                            days = Integer.parseInt(duration) / 86400;
                        }
                        Log.i("stat", "Click: " + viewPager.getCurrentItem());

                        if (name.equals("active")) {
                            Toast.makeText(getContext(), "This challenge is active", Toast.LENGTH_SHORT).show();
                        } else if (description.equals("") || description.startsWith(" ")) {
                            Toast.makeText(getContext(), "Goal is empty!", Toast.LENGTH_SHORT).show();
                        } else if (days == 0) {
                            Toast.makeText(getContext(), "Min 1 day", Toast.LENGTH_SHORT).show();
                        } else {
                            OfflineMode offlineMode = new OfflineMode();
                            if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
                                StartSingleInProgress(challenge_id, finalFriend_id);
                                HashMap<String, String> user = new HashMap<>();
                                user = sessionManager.getUserDetails();
                                String token = user.get("token");
                                Log.i("stat", "Nam nado: " + challenge_id + " " + finalFriend_id + " " + token);
                            }
                        }
                    }
                    return true;
                }
            });}
        return view;
    }


    public void Create_new_challenge(String description, int days, final String friend_id) {
        String type_id = "567d51c48322f85870fd931b";
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
                    StartSingleInProgress(challenge, friend_id);
                    Log.i("stat", "Status: Challenge Created");

                } else Log.i("stat", "Status: Error Creating");
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }


    public void StartSingleInProgress(final String challenge, String recipient) {
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
        Call<com.example.ivan.champy_v2.duel.Duel> call =
                singleinprogress.Start_duel(
                        recipient,
                        challenge,
                        token
                );
        call.enqueue(new Callback<Duel>() {
            @Override
            public void onResponse(Response<Duel> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("stat", "Status: Starting OK");
                    ContentValues cv = new ContentValues();
                    Duel data = response.body();
                    cv.put("challenge_id", data.getData().getId());
                    Log.d("myLogs", "Added: " + data.getData().getId());
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
        final int clearCount = db.delete("pending_duel", null, null);
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
        final String id = user.get("id");
        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        final long unixTime = System.currentTimeMillis() / 1000L;
        final String update = "1457019726";
        Log.i("stat", "Nam nado: " + id + " " + update + " " + token);
        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(id, update, token);
        call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);
                        Recipient recipient = datum.getRecipient();
                        Sender sender = datum.getSender();
                        Challenge challenge = datum.getChallenge();
                        cv.clear();
                        if (challenge.getType().equals("567d51c48322f85870fd931b")) {
                            if (id.equals(recipient.getId())) {
                                cv.put("recipient", "true");
                                cv.put("versus", sender.getName());
                            }
                            if (id.equals(sender.get_id())) {
                                cv.put("recipient", "false");
                                cv.put("versus", recipient.getName());
                            }
                            cv.put("challenge_id", challenge.get_id());
                            cv.put("description", challenge.getDescription());
                            cv.put("duration", challenge.getDuration());
                            db.insert("pending_duel", null, cv);
                        }
                    }
                    Toast.makeText(getActivity(), "Sended duel Request!!!", Toast.LENGTH_SHORT).show();
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
