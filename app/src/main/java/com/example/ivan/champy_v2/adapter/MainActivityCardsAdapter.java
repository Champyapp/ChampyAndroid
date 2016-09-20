package com.example.ivan.champy_v2.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.model.SelfImprovement_model;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.round;

public class MainActivityCardsAdapter extends CustomPagerAdapter {

    Activity activity;
    private ArrayList<SelfImprovement_model> arrayList;
    public static final String TAG = "CardsAdapterMain";

    public MainActivityCardsAdapter(Context context, ArrayList<SelfImprovement_model> marrayList, Activity activity) {
        super(context);
        this.arrayList = marrayList;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView) {
        View tempView = convertView;
        final SelfImprovement_model item = arrayList.get(position);
        Typeface typeface = android.graphics.Typeface.createFromAsset(getContext().getAssets(), "fonts/bebasneue.ttf");
        String itemType = item.getType();
        String itemGoal = item.getGoal();
        String itemName = item.getName();
        String itemVersus = "with " + item.getVersus();
        String itemSenderProgress = item.getSenderProgress();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(tempView == null) {
            switch (itemType) {
                case "Duel":
                    tempView = inflater.inflate(R.layout.single_card_fragment_duel, null, false);
                    TextView tvRecipientName = (TextView) tempView.findViewById(R.id.tvRecipientName);
                    tvRecipientName.setTypeface(typeface);
                    tvRecipientName.setText(itemVersus);

                    //ImageView imageUser1     = (ImageView)tempView.findViewById(R.id.user1);
                    //ImageView imageUser2     = (ImageView)tempView.findViewById(R.id.user2);
                    break;
                case "Self-Improvement":
                    tempView = inflater.inflate(R.layout.single_card_fragment_self, null, false);

                    break;
                case "Wake Up":
                    tempView = inflater.inflate(R.layout.single_card_fragment_wake, null, false);

                    break;
            }

        }

        Log.i(TAG, "getView: " + itemName + " itemSenderProgress = " + toArrayOfStrings(itemSenderProgress)[0]);
        ImageView cardImage = (ImageView)tempView.findViewById(R.id.cardImage);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int x = size.x / 100;
        int y = size.y / 100;
        //int x = round(activity.getWindow().getWindowManager().getDefaultDisplay().getWidth() / 100);
        //int y = round(activity.getWindow().getWindowManager().getDefaultDisplay().getHeight() / 100);
        cardImage.getLayoutParams().width  = x*65;
        cardImage.getLayoutParams().height = y*50;
        if (y > 10) y = 10;



        TextView challengeType  = (TextView) tempView.findViewById(R.id.tvChallengeType);
        challengeType.setTextSize((float)(y*1.3));
        challengeType.setTypeface(typeface);


        TextView tvChallengeName = (TextView) tempView.findViewById(R.id.tvChallengeDescription);
        tvChallengeName.setText(itemName);
        tvChallengeName.setTextSize((float) (y*1.5));
        tvChallengeName.setTypeface(typeface);

        TextView tvDuration = (TextView) tempView.findViewById(R.id.textViewDuration);
        tvDuration.setText(item.getDays() + " DAYS TO GO");
        tvDuration.setTextSize(y*2);

        Button buttonGiveUp = (Button) tempView.findViewById(R.id.buttonGiveUp);
        buttonGiveUp.getLayoutParams().width = x*10;
        buttonGiveUp.getLayoutParams().height = x*10;

        Button buttonDoneForToday = (Button) tempView.findViewById(R.id.buttonDoneForToday);
        buttonDoneForToday.getLayoutParams().width = x*10;
        buttonDoneForToday.getLayoutParams().height = x*10;

        final ChallengeController challengeController = new ChallengeController(getContext(), (Activity) getContext(), 0 , 0, 0);

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
                                            int intentId = Integer.parseInt(item.getGoal());
                                            challengeController.give_up(item.getId(), intentId);
                                        } else {
                                            challengeController.give_up(item.getId(), 0);
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




        //final Button finalButton = buttonDoneForToday;
//        if (item.getUpdated() != null){
//            if (!item.getType().equals("Wake Up")) {
//                if (item.getUpdated().equals("false")) {
//                    buttonDoneForToday.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.icon_done_for_today));
//                        tvDoneForToday.setVisibility(View.VISIBLE);
//                }
//            }
//        }

        buttonDoneForToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = item.getId();

                long currentDayInMillis = System.currentTimeMillis() / 1000;

                SQLiteDatabase localSQLiteDatabase = new DBHelper(getContext()).getWritableDatabase();
                ContentValues localContentValues = new ContentValues();
                localContentValues.put("updated", "true");
                localSQLiteDatabase.update("myChallenges", localContentValues, "challenge_id = ?", new String[]{id});
                int i = localSQLiteDatabase.update("updated", localContentValues, "challenge_id = ?", new String[]{id});
                try {
                    challengeController.doneForToday(id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                buttonDoneForToday.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.icon_share));
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

