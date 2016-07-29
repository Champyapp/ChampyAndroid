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
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.duel.Duel;
import com.example.ivan.champy_v2.interfaces.CreateChallenge;
import com.example.ivan.champy_v2.interfaces.SingleInProgress;

import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

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
        Log.i("stat", "Status: " + position);
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
        final Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        final TextView tvDays = (TextView)view.findViewById(R.id.textViewDuring);
        final TextView tvGoal = (TextView)view.findViewById(R.id.tv_goal);
        TextView tvUserVsUser = (TextView)view.findViewById(R.id.tvYouVsSomebody);
        final ImageButton buttonAcceptBattle = (ImageButton)getActivity().findViewById(R.id.imageButtonAcceptBattle);
        final ImageButton buttonCancelBattle = (ImageButton)getActivity().findViewById(R.id.imageButtonCancelBattle);

        if (recipient.equals("true")) {
            tvUserVsUser.setText(versus + " want to \nchallenge with you");
        } else {
            tvUserVsUser.setText("You'r challenge with \n" + versus);
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
                        int mDays = 0;

                        if (!duration.isEmpty()) { mDays = Integer.parseInt(duration); }

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                final Bundle args = getArguments();
                                Cursor c = db.query("pending_duel", null, null, null, null, null, null);
                                int position = args.getInt(ARG_PAGE);
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
                                description = tvGoal.getText().toString();
                                duration    = tvDays.getText().toString();

                                createNewDuelChallenge(description, mDays, recipient);
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


    private void createNewDuelChallenge(String description, int days, final String recipient) {
        final SessionManager sessionManager = new SessionManager(getContext());
        String type_id = "567d51c48322f85870fd931b";
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        final String token = user.get("token");
        final String API_URL = "http://46.101.213.24:3007";
        String details = description + " during this period";
        String duration = "" + (days * 86400);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.example.ivan.champy_v2.create_challenge.CreateChallenge> call =
                createChallenge.createChallenge(
                        "User_Challenge",
                        type_id,
                        description,
                        details,
                        duration,
                        token);

        call.enqueue(new Callback<com.example.ivan.champy_v2.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("Duel", "onResponse 'createDuelChallenge': OKAY");
                    String challenge = response.body().getData().get_id();
                    startSingleInProgress(challenge, recipient);
                } else {
                    Log.i("DUEL", "onResponse 'createDuelChallenge': NE OKAY \n ERROR = " + response.code() + response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });


    }


    private void startSingleInProgress(final String challenge, final String recipient) {
        final SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        final String API_URL = "http://46.101.213.24:3007";
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        /*Call<Duel> call = singleinprogress.Join(challenge, token);
        call.enqueue(new Callback<Duel>() {
            @Override
            public void onResponse(Response<Duel> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i("DUEL", "onResponse 'startSingleInProgress': OKAY");
                } else {
                    Log.i("DUEL", "onResponse 'startSingleInProgress': NE OKAY \n ERROR = " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("Duel", "onResponse 'startSingleInProgress': WTF");
            }
        });*/


    }




}
