package com.example.ivan.champy_v2.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.activity.MainActivity;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.model.SelfImprovement_model;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.round;

public class MainActivityCardsAdapter extends CustomPagerAdapter {

    private Activity activity;
    private ArrayList<SelfImprovement_model> arrayList;
    public static final String TAG = "CardsAdapterMain";
    private String token, userId;

    public MainActivityCardsAdapter(Context context, ArrayList<SelfImprovement_model> marrayList, Activity activity) {
        super(context);
        this.arrayList = marrayList;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView) {
        View tempView = convertView;
        if(tempView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            tempView = inflater.inflate(R.layout.single_card_fragment_self, null, false);
        }
        final SelfImprovement_model item = arrayList.get(position);
        ImageView cardImage = (ImageView)tempView.findViewById(R.id.cardImage);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int x = size.x / 100;
        int y = size.y / 100;
        cardImage.getLayoutParams().width  = x*65;
        cardImage.getLayoutParams().height = y*50;
        if (y > 10) y = 10;

        final ChallengeController challengeController = new ChallengeController(getContext(), (Activity) getContext(), 0 , 0, 0);
        Typeface typeface = android.graphics.Typeface.createFromAsset(getContext().getAssets(), "fonts/bebasneue.ttf");

        TextView tvChallengeType = (TextView) tempView.findViewById(R.id.tvChallengeType);
        tvChallengeType.setTextSize((float)(y*1.3));
        tvChallengeType.setTypeface(typeface);
        tvChallengeType.setText(item.getType());

        String itemType   = item.getType();
        String itemGoal   = item.getGoal();
        String itemChallengeName   = item.getChallengeName();
        final String itemWakeUpTime = item.getWakeUpTime();
        String itemVersus = "with " + item.getVersus();
        String itemSenderProgress = item.getSenderProgress();
        //Log.i(TAG, "getView: " + itemGoal + " = " + toArrayOfStrings(itemSenderProgress)[0]);
        TextView tvRecipientName = (TextView) tempView.findViewById(R.id.tvRecipientName);
        ImageView imageView      = (ImageView)tempView.findViewById(R.id.imageViewChallengeLogo);
        TextView tvChallengeDescription = (TextView) tempView.findViewById(R.id.tvChallengeDescription);

        switch (itemType) {
            case "Wake Up":
                imageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.wakeup_white));
                tvChallengeDescription.setText(itemChallengeName);
                break;
            case "Duel":
                tvRecipientName.setText(itemVersus);
                tvRecipientName.setTypeface(typeface);
                tvChallengeDescription.setText(itemGoal);
                imageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.duel_white));
                break;
            case "Self-Improvement":
                tvChallengeDescription.setText(itemGoal);
                imageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.self_white));
                break;
        }


        tvChallengeDescription.setTextSize((float) (y*1.5));
        tvChallengeDescription.setTypeface(typeface);


        TextView tvDuration = (TextView) tempView.findViewById(R.id.textViewDuration);
        tvDuration.setText(item.getDays() + " Days");
        tvDuration.setTextSize(y*2);
        tvDuration.setTypeface(typeface);

        Button buttonGiveUp = (Button) tempView.findViewById(R.id.buttonGiveUp);
        buttonGiveUp.getLayoutParams().width = x*10;
        buttonGiveUp.getLayoutParams().height = x*10;

        CurrentUserHelper user = new CurrentUserHelper(getContext());
        token = user.getToken();
        userId = user.getUserObjectId();

        buttonGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                    try {
                                        if (item.getType().equals("Wake Up")) {
                                            int intentId = Integer.parseInt(itemWakeUpTime);
                                            challengeController.give_up(item.getId(), intentId, token, userId);
                                        } else {
                                            challengeController.give_up(item.getId(), 0, token, userId);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.areYouSure)
                        .setMessage(R.string.youWantToGiveUp)
                        .setIcon(R.drawable.ic_action_warn)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();

            }
        });


        final Button buttonDoneForToday = (Button) tempView.findViewById(R.id.buttonDoneForToday);
        buttonDoneForToday.getLayoutParams().width = x*10;
        buttonDoneForToday.getLayoutParams().height = x*10;

        //final Button finalButton = buttonDoneForToday;
        if (item.getUpdated() != null){
            if (!item.getType().equals("Wake Up")) {
                if (item.getUpdated().equals("false")) {
                    buttonDoneForToday.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.icon_done_for_today));
//                        tvDoneForToday.setVisibility(View.VISIBLE);
                }
            }
        }

        buttonDoneForToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = item.getId();
                SQLiteDatabase localSQLiteDatabase = new DBHelper(getContext()).getWritableDatabase();
                ContentValues localContentValues = new ContentValues();
                localContentValues.put("updated", "true");
                localSQLiteDatabase.update("myChallenges", localContentValues, "challenge_id = ?", new String[]{id});
                int i = localSQLiteDatabase.update("updated", localContentValues, "challenge_id = ?", new String[]{id});
                try {
                    challengeController.doneForToday(id, token, userId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buttonDoneForToday.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.icon_share));
            }
        });

        return tempView;
    }

    @Override
    public int dataCount() {
        return arrayList.size();
    }


    public String[] toArrayOfStrings(String arg){
        String a = arg.substring(1);
        String b = a.replaceFirst("]","");
        return b.split(", ");
    }

}

