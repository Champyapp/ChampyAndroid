package com.example.ivan.champy_v2.fragment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
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

// TODO: 12.07.2016 тут логика окна дуелей с другом: friends -> make challenge -> here.
// TODO: 12.07.2016 переписать это так как SelfImprovementFragment.
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
        final SessionManager sessionManager = new SessionManager(getContext());
        int size = sessionManager.getSelfSize();
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        TextView tvGoal = (TextView)view.findViewById(R.id.goal_text);
        TextView tvDays = (TextView)view.findViewById(R.id.days_text);
        EditText etGoal = (EditText)view.findViewById(R.id.et_goal);
        EditText etDays = (EditText)view.findViewById(R.id.et_days);

        int days = 0;
        if (duration != null && !duration.isEmpty()) {
            days = Integer.parseInt(duration) / 86400;
        }

        tvDays.setText("" + days);
        tvDays.setTypeface(typeface);
        tvGoal.setText(description);
        tvGoal.setTypeface(typeface);

        Glide.with(getContext()).load(R.drawable.points).override(120, 120).into((ImageView) view.findViewById(R.id.imageViewAcceptButton));
        ImageButton imageButtonAccept = (ImageButton) getActivity().findViewById(R.id.ok);

        final int[] finalposition = new int[1];
        final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager_duel);
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
        final String finalFriend_id = friend_id;
        imageButtonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = "";
                        String duration = "";
                        String description = "";
                        String challenge_id = "";
                        int days = 0;

                        EditText etGoal = (EditText) view.findViewById(R.id.et_goal);
                        EditText etDays = (EditText) view.findViewById(R.id.et_days);
                        description = etGoal.getText().toString();
                        duration = etDays.getText().toString();

                        if (!duration.isEmpty()) {
                            days = Integer.parseInt(duration);
                        }

                        Cursor c = db.query("duel", null, null, null, null, null, null);
                        int position = viewPager.getCurrentItem();
                        int size = sessionManager.getSelfSize();

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (position == size) {
                                    if ((checkInputUserData(description, duration))) {
                                        days = Integer.parseInt(duration);
                                        Create_new_challenge(description, days, finalFriend_id);
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
                                    description = ((EditText) view.findViewById(R.id.et_goal)).getText().toString();

                                    if (isActive(description)) {
                                        Toast.makeText(getContext(), "This challenge is active", Toast.LENGTH_SHORT).show();
                                    } else {
                                        StartSingleInProgress(challenge_id, finalFriend_id);
                                        Toast.makeText(getActivity(), "Sent duel request", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setTitle("Are you sure")
                        .setMessage("You wanna send this request?")
                        .setIcon(R.drawable.challengecceptedmeme)
                        .setCancelable(false)
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No",  dialogClickListener).show();

            }
        });

        return view;
    }


    private boolean checkInputUserData(String description, String duration) {
        int days = 21;
        if (!duration.isEmpty()) {
            days = Integer.parseInt(duration);
        }
        if (!isActive(description) && !description.isEmpty() && !description.startsWith(" ") && !duration.isEmpty() && days != 0) {
            Toast.makeText(getActivity(), "Sent duel request", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isActive(description)) {
            Toast.makeText(getContext(), "This challenge is active", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(getContext(), "Complete all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
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
                    //Log.i("stat", "Status: Starting OK");
                    ContentValues cv = new ContentValues();
                    Duel duel = response.body();
                    cv.put("challenge_id", duel.getData().getId());
                    //Log.d("myLogs", "Added: " + duel.getData().getId());
                    cv.put("updated", "false"); // почему false?
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
        final int clearCount = db.delete("pending_duel", null, null);
        final ContentValues cv = new ContentValues();
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        final SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        final String id = user.get("id");
        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        final long unixTime = System.currentTimeMillis() / 1000L;
        final String update = "0"; //1457019726
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
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
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
