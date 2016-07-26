package com.example.ivan.champy_v2.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.activity.PendingDuelActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.R;

import org.w3c.dom.Text;

import java.io.IOException;

import static java.lang.Math.round;

/**
 * Fragment отвечающий за принятие или отмену дуели (то самое секретное меню)
 */
public class PendingDuelFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";


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
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        TextView tvDays = (TextView)view.findViewById(R.id.textViewDuring);
        TextView tvGoal = (TextView)view.findViewById(R.id.tv_goal);
        TextView tvUserVsUser = (TextView)view.findViewById(R.id.tvYouVsSomebody);

        if (recipient.equals("true")) {
            tvUserVsUser.setText(versus + " vs YOU");
        } else {
            tvUserVsUser.setText("YOU vs " + versus);
            //view.findViewById(R.id.imageButtonCancelBattle).setVisibility(View.INVISIBLE);
            //view.findViewById(R.id.imageButtonAcceptBattle).setVisibility(View.INVISIBLE);
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

        /*ImageButton buttonCancelBattle = (ImageButton) getActivity().findViewById(R.id.imageButtonCancelBattle);
        buttonCancelBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                OfflineMode offlineMode = new OfflineMode();
                                if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
                                    Toast.makeText(getContext(), "Coming soon", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to cancel request?")
                        .setPositiveButton("Yes!", dialog)
                        .setNegativeButton("No!",  dialog).show();
            }
        });*/



        /*ImageButton buttonAcceptBattle = (ImageButton)findViewById(R.id.imageButtonAcceptBattle);
        buttonAcceptBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //ChallengeController challengeController = new ChallengeController(getContext(), getActivity(), 0, 0);
                                //challengeController.generate();
                                //Toast.makeText(getContext(), "Challenge Accepted", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage("Are you sure you want to accept request?")
                        .setPositiveButton("Yes!", dialog)
                        .setNegativeButton("No!",  dialog).show();
            }
        });*/

        return view;
    }

}
