package com.example.ivan.champy_v2.fragment;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.create_challenge.Data;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.duel.Duel;
import com.example.ivan.champy_v2.helper.CHSetupUI;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.CreateChallenge;
import com.example.ivan.champy_v2.interfaces.SingleInProgress;
import com.example.ivan.champy_v2.model.active_in_progress.Datum;

import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import rx.Single;

import static java.lang.Math.round;

/**
 * Fragment отвечающий за принятие или отмену дуели (то самое секретное меню)
 * table: pending_duel
 */
public class PendingDuelFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    Activity firstActivity;

    public static PendingDuelFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PendingDuelFragment fragment = new PendingDuelFragment();
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
        final View view = inflater.inflate(R.layout.item_pending_duel, container, false);
        String versus = "";
        String duration = "";
        String description = "";
        String challenge_id = "";
        String status = "";
        String recipient = "";

        DBHelper dbHelper = new DBHelper(getActivity());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        final Bundle args = this.getArguments();
        Cursor c = db.query("pending_duel", null, null, null, null, null, null);
        int position = args.getInt(ARG_PAGE);
        //Log.i("stat", "Status: " + position);
        int o = 0;
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int versusColIndex = c.getColumnIndex("versus");
            int coldescription = c.getColumnIndex("description");
            int colduration = c.getColumnIndex("duration");
            int colchallenge_id = c.getColumnIndex("challenge_id");
            int colrecipient = c.getColumnIndex("recipient");
            do {
                o++;
                if (o > position + 1) break;
                if (o == position + 1) {
                    versus = c.getString(versusColIndex);
                    description = c.getString(coldescription);
                    duration = c.getString(colduration);
                    challenge_id = c.getString(colchallenge_id);
                    recipient = c.getString(colrecipient);
                }
            } while (c.moveToNext());
        }
        c.close();
        final SessionManager sessionManager = new SessionManager(getContext());
        int size = sessionManager.getSelfSize();
        final Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        final TextView tvDays = (TextView)view.findViewById(R.id.textViewDuring);
        final TextView tvGoal = (TextView)view.findViewById(R.id.tv_goal);
        TextView tvUserVsUser = (TextView)view.findViewById(R.id.tvYouVsSomebody);
        final ImageButton buttonAcceptBattle = (ImageButton)getActivity().findViewById(R.id.imageButtonAcceptBattle);
        final ImageButton buttonCancelBattle = (ImageButton)getActivity().findViewById(R.id.imageButtonCancelBattle);

        final ViewPager viewPager = (ViewPager)getActivity().findViewById(R.id.pager_pending_duel);
        CHSetupUI chSetupUI = new CHSetupUI();
        chSetupUI.setupUI(getActivity().findViewById(R.id.pending_duel), getActivity());
        chSetupUI.setupUI(view, getActivity());

        if (recipient.equals("true")) {
            tvUserVsUser.setText(versus + " want to \nchallenge with you");
        } else {
            tvUserVsUser.setText("Your challenge with \n" + versus);
        }

        Glide.with(getContext()).load(R.drawable.points).override(200, 200).into((ImageView)view.findViewById(R.id.imageViewAcceptButton));
        tvUserVsUser.setTypeface(typeface);

        int days = 21;
        if (duration != null && !duration.isEmpty()) {
            days = Integer.parseInt(duration) / 86400;
        }

        tvDays.setText(days + "");
        tvGoal.setText(description);
        tvDays.setTypeface(typeface);
        tvGoal.setTypeface(typeface);

        buttonAcceptBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String versus = "";
                        String duration = "";
                        String description = "";
                        String challenge_id = "";
                        String status = "";
                        String recipient = "";
                        String index = "";
                        int position = viewPager.getCurrentItem();
                        int size = sessionManager.getSelfSize();
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                final Bundle args = getArguments();
                                Cursor c = db.query("pending_duel", null, null, null, null, null, null);
                                //int position = args.getInt(ARG_PAGE);
                                int o = 0;
                                if (c.moveToFirst()) {
                                    int idColIndex = c.getColumnIndex("id");
                                    int versusColIndex = c.getColumnIndex("versus");
                                    int coldescription = c.getColumnIndex("description");
                                    int colduration = c.getColumnIndex("duration");
                                    int colchallenge_id = c.getColumnIndex("challenge_id");
                                    int colrecipient = c.getColumnIndex("recipient");
                                    do {
                                        o++;
                                        if (o > position + 1) break;
                                        if (o == position + 1) {
                                            versus = c.getString(versusColIndex);
                                            description = c.getString(coldescription);
                                            duration = c.getString(colduration);
                                            challenge_id = c.getString(colchallenge_id);
                                            recipient = c.getString(colrecipient);
                                            index = c.getString(idColIndex);
                                        }
                                    } while (c.moveToNext());
                                }
                                int days = Integer.parseInt(duration);
                                c.close();
                                if (recipient.equals("true")) {
                                    createNewChallenge(description, days);
                                    Log.i("OnCreateView", "Status: VSE OK"
                                            + "\n       challenge_id = " + challenge_id
                                            + "\n       description  = " + description
                                            + "\n       duration     = " + duration
                                            + "\n       versus       = " + versus
                                            + "\n       recipient    = " + recipient
                                            + "\n       id           = " + index);
                                } else {
                                    Log.i("OnCreateView", "Status: WTF" +
                                            "\nYou can't accept this challenge because you're Sender!" );
                                }
                                Toast.makeText(getContext(), "Challenge Accepted", Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setTitle("Are you sure")
                        .setMessage("You wanna accept request?")
                        .setIcon(R.drawable.challengecceptedmeme)
                        .setCancelable(false)
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No",  dialogClickListener).show();

            }
        });


        buttonCancelBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                OfflineMode offlineMode = new OfflineMode();
                                if (offlineMode.isConnectedToRemoteAPI(getActivity())){
                                    Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
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
                        .setMessage("You wanna cancel request?")
                        .setIcon(R.drawable.duel_blue)
                        .setCancelable(false)
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No",  dialogClickListener).show();

            }
        });


        return view;
    }


    private void createNewChallenge(final String description, int days) {
        final String type_id = "567d51c48322f85870fd931b";
        final SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        final String duration = "" + (days * 86400);
        final String details = description + " during this period";

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

                    String _id = ""; // тут треба брати _ID (не челенджа)
                    joinToChallenge(_id);
                    Log.i("CreateNewChallenge", "Status: VSE OK"
                            + "\n _ID         = " + _id
                            + "\n DESCRIPTION = " + description);
                } else {
                    Log.i("CreateNewChallenge", "Status: WTF"
                            + "\n       ERROR = " + response.code() + response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void joinToChallenge(final String _id) {
        final SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        final String mToken = token;
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();


        SingleInProgress singleInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> call = singleInProgress.Join(_id, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.single_inprogress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.single_inprogress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("JoinToChallenge", "Status: VSE OK"
                            + "\n     _ID         = " + _id
                            + "\n     TOKEN       = " + mToken);
                    //generate();
                } else {
                    Log.i("JoinToChallenge", "Status: WTF"
                            + "\n    ERROR        = " + response.code() + response.message()
                            + "\n    _ID          = " + _id
                            + "\n    TOKEN        = " + mToken);
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    //private void generate() { }

    /*
    call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
        @Override
        public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
            if (response.isSuccess()) {
                List<com.example.ivan.champy_v2.model.active_in_progress.Datum> data = response.body().getData();
                for (int i = 0; i < data.size(); i++) {
                    com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);
                    Challenge challenge = datum.getChallenge();
                    cv.clear();
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
                    Log.d(TAG, "Challenge: "+desctiption);
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
        }
    });*/



}
