package com.example.ivan.champy_v2;

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

/**
 * Created by ivan on 14.03.16.
 */
public class SelfImprovementFragment extends Fragment{
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.item_row, container, false);
        final Bundle args = this.getArguments();
        final int[] finalposition = new int[1];
        final ViewPager viewPager = (ViewPager )getActivity().findViewById(R.id.pager);
        String name = "";
        String duration = "";
        String description = "";
        String challenge_id = "";
        DBHelper dbHelper = new DBHelper(getActivity());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
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
                if (o > position+1) break;
                if (o == position+1) {
                    name = c.getString(nameColIndex);
                    description = c.getString(coldescription);
                    duration = c.getString(colduration);
                    challenge_id = c.getString(colchallenge_id);
                    break;
                }
            } while (c.moveToNext());
        } else
            Log.i("stat", "0 rows");
        c.close();
        Log.i("stat", "Name: "+description);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        EditText editText = (EditText)view.findViewById(R.id.goal);
        editText.setText(description);
        editText.setTypeface(typeface);
        editText = (EditText)view.findViewById(R.id.days);
        editText.setTypeface(typeface);
        int days = 0;
        if (duration != null && duration != "") {
            days = Integer.parseInt(duration)/86400;
        }
        editText.setText("" + days);
        TextView textView = (TextView)view.findViewById(R.id.textView8);
        textView.setTypeface(typeface);

        Glide.with(getContext())
                .load(R.drawable.points)
                .override(120,120)
                .into((ImageView) view.findViewById(R.id.imageView14));
        editText = (EditText)view.findViewById(R.id.goal);
        description = editText.getText().toString();
        editText = (EditText)view.findViewById(R.id.days);
        days = Integer.parseInt(editText.getText().toString());
        Log.i("stat", "Description: " + description);
        ImageButton imageButton = (ImageButton)getActivity().findViewById(R.id.imageButton5);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Tap & Hold", Toast.LENGTH_SHORT).show();
            }
        });
        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EditText editText1 = (EditText) view.findViewById(R.id.goal);
                String name = "";
                String duration = "";
                String description = "";
                String challenge_id = "";
                int days = 0;
                Cursor c = db.query("selfimprovement", null, null, null, null, null, null);
                int position = viewPager.getCurrentItem();
                SessionManager sessionManager = new SessionManager(getContext());
                int size = sessionManager.getSelfSize();
                Log.i("stat", "Click: "+position+" "+size);
                if (position == size) {
                   description = editText1.getText().toString();
                   Log.i("stat", "Click: clicked");
                   Log.i("stat", "Click: "+description);
                   editText1 = (EditText)view.findViewById(R.id.days);
                   days = Integer.parseInt(editText1.getText().toString());
                   Create_new_challenge(description, days);
                } else {
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
                                break;
                            }
                        } while (c.moveToNext());
                    } else
                        Log.i("stat", "0 rows");
                    c.close();

                    if (duration != null && duration != "") {
                        days = Integer.parseInt(duration) / 86400;
                    }

                Log.i("stat", "Click: " + viewPager.getCurrentItem());
                Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();
                StartSingleInProgress(challenge_id);}
                return true;
            }
        });
        return view;
    }
    public void Create_new_challenge(String descritpion, int days) {
        String type_id = "567d51c48322f85870fd931a";
        final SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        String duration = ""+(days*86400);

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
                        descritpion,
                        descritpion,
                        duration,
                        token
                );
        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()){
                    String challenge = response.body().getData().get_id();
                    Log.i("stat", "Status: "+challenge);
                    StartSingleInProgress(challenge);
                    Log.i("stat", "Status: Challenge Created");
                    
                }
                else Log.i("stat", "Status: Error Creating");
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
        
    }
    public void StartSingleInProgress(String challenge)
    {
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
                 if (response.isSuccess()){
                     Log.i("stat", "Status: Starting OK");
                     generate();
                 } else Log.i("stat", "Status: Starting WRONG"+response.code());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
    public void generate()
    {
        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        //int clearCount = db.delete("myChallenges", null, null);
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
        String update = "1457019726";
        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(id, update, token);
        call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);
                        Challenge challenge = datum.getChallenge();

                        String desctiption = challenge.getDescription();
                        int end = datum.getEnd();
                        int days = round((end - unixTime) / 86400);
                        String duration = "" + days;
                        String challenge_id = challenge.get_id();
                        cv.put("name", "Self Improvement");
                        cv.put("description", desctiption);
                        cv.put("duration", duration);
                        cv.put("challenge_id", challenge_id);
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
}
