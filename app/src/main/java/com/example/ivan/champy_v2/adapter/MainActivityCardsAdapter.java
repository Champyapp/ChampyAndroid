package com.example.ivan.champy_v2.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
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
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.model.SelfImprovement_model;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.round;

public class MainActivityCardsAdapter extends CustomPagerAdapter /*implements View.OnClickListener*/ {

    private ArrayList<SelfImprovement_model> arrayList;
    public static final String TAG = "CardsAdapterMain";
    private String token, userId;
    private Snackbar snackbar;
    private Button buttonDoneForToday, buttonGiveUp, buttonShare;
    private ChallengeController cc;
    private SelfImprovement_model currentCard;
    private String itemWakeUpTime;

    public MainActivityCardsAdapter(Context context, ArrayList<SelfImprovement_model> marrayList) {
        super(context);
        this.arrayList = marrayList;
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
        //int x = round(activity.getWindow().getWindowManager().getDefaultDisplay().getWidth() / 100);
        //int y = round(activity.getWindow().getWindowManager().getDefaultDisplay().getHeight() / 100);
        cardImage.getLayoutParams().width  = x*65;
        cardImage.getLayoutParams().height = y*50;
        if (y > 10) y = 10;

        final ChallengeController challengeController = new ChallengeController(getContext(), (Activity) getContext(), 0 , 0, 0);

        TextView tvChallengeType = (TextView) tempView.findViewById(R.id.tvChallengeType);
        tvChallengeType.setText(item.getType());
        String itemGoal = item.getGoal();
        ImageView imageView = (ImageView)tempView.findViewById(R.id.imageViewChallengeLogo);

        switch (item.getType()) {
            case "Wake Up":
                imageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.wakeup_white));
                itemGoal = item.getChallengeName();
                break;
            case "Duel":
                itemGoal = item.getGoal();
                imageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.duel_white));
                break;
            case "Self-Improvement":
                imageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.self_white));
                break;
        }

        tvChallengeType.setTextSize((float)(y*1.3));
        Typeface typeface = android.graphics.Typeface.createFromAsset(getContext().getAssets(), "fonts/bebasneue.ttf");
        tvChallengeType.setTypeface(typeface);

        TextView tvChallengeDescription = (TextView) tempView.findViewById(R.id.tvChallengeDescription);
        tvChallengeDescription.setText(itemGoal);
        tvChallengeDescription.setTextSize(y);

        TextView tvDuration = (TextView) tempView.findViewById(R.id.textViewDuration);
        tvDuration.setText(item.getDays() + " DAYS TO GO");
        tvDuration.setTextSize(y*2);

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
                                        int intentId = Integer.parseInt(item.getGoal());
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


//    @Override
//    public void onClick(View view) {
//
//        switch (view.getId()) {
//            case R.id.buttonGiveUp:
//                snackbar = Snackbar.make(view, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        try {
//                            if (currentCard.getType().equals("Wake Up")) {
//                                int intentId = Integer.parseInt(itemWakeUpTime);
//                                cc.give_up(currentCard.getId(), intentId, token, userId);
//                            } else {
//                                cc.give_up(currentCard.getId(), 0, token, userId);
//                            }
//                            snackbar = Snackbar.make(view, "Looser!", Snackbar.LENGTH_SHORT);
//                            snackbar.show();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                snackbar.show();
//            break;
//
//            case R.id.buttonDoneForToday:
//                if (currentCard.getType().equals("Wake Up")) break;
//                String inProgressId = currentCard.getId();
//                try {
//                    cc.doneForToday(inProgressId, token, userId);
//                    //buttonDoneForToday.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.icon_share));
//                    snackbar = Snackbar.make(view, "Well done!", Snackbar.LENGTH_SHORT);
//                    snackbar.show();
//                    buttonDoneForToday.setVisibility(View.INVISIBLE);
//                    buttonShare.setVisibility(View.VISIBLE);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                break;
//
//            case R.id.buttonShare:
//                String message = "I'm done for today with my challenge: " + currentCard.getGoal();
//                Intent share = new Intent(Intent.ACTION_SEND);
//                share.setType("text/plain");
//                share.putExtra(Intent.EXTRA_TEXT, message);
//                getContext().startActivity(Intent.createChooser(share, "How would you like to share?"));
//                break;
//        }
//
//
//
//    }

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

