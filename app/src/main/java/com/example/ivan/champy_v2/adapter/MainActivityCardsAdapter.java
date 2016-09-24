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


public class MainActivityCardsAdapter extends CustomPagerAdapter /*implements View.OnClickListener*/ {

    private ArrayList<SelfImprovement_model> arrayList;
    public static final String TAG = "CardsAdapterMain";
    private String token, userId;
    private Snackbar snackbar;

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
        final SelfImprovement_model currentCard = arrayList.get(position);
        ImageView cardImage = (ImageView)tempView.findViewById(R.id.cardImage);
        Typeface typeface = android.graphics.Typeface.createFromAsset(getContext().getAssets(), "fonts/bebasneue.ttf");
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


        TextView tvChallengeType = (TextView) tempView.findViewById(R.id.tvChallengeType);
        tvChallengeType.setText(currentCard.getType());
        tvChallengeType.setTextSize((float)(y*1.3));
        tvChallengeType.setTypeface(typeface);

        String itemGoal = currentCard.getGoal();
        ImageView imageChallengeLogo = (ImageView)tempView.findViewById(R.id.imageViewChallengeLogo);

        switch (currentCard.getType()) {
            case "Wake Up":
                imageChallengeLogo.setImageResource(R.drawable.wakeup_white);
                itemGoal = currentCard.getChallengeName();
                break;
            case "Duel":
//                itemGoal = item.getGoal();
                imageChallengeLogo.setImageResource(R.drawable.duel_white);
                break;
            case "Self-Improvement":
                imageChallengeLogo.setImageResource(R.drawable.self_white);
                break;
        }



        TextView tvChallengeDescription = (TextView) tempView.findViewById(R.id.tvChallengeDescription);
        tvChallengeDescription.setText(itemGoal);
        tvChallengeDescription.setTextSize(y*2);
        tvChallengeDescription.setTypeface(typeface);

        TextView tvDuration = (TextView) tempView.findViewById(R.id.textViewDuration);
        tvDuration.setText(currentCard.getDays() + " DAYS TO GO");
        tvDuration.setTextSize(y*2);

        final Button buttonGiveUp = (Button) tempView.findViewById(R.id.buttonGiveUp);
        buttonGiveUp.getLayoutParams().width  = x*10;
        buttonGiveUp.getLayoutParams().height = x*10;
        final Button buttonDone = (Button) tempView.findViewById(R.id.buttonDoneForToday);
        buttonDone.getLayoutParams()  .width  = x*10;
        buttonDone.getLayoutParams()  .height = x*10;
        final Button buttonShare = (Button) tempView.findViewById(R.id.buttonShare);
        buttonShare.getLayoutParams() .width  = x*10;
        buttonShare.getLayoutParams() .height = x*10;


        if (currentCard.getUpdated() != null){
            if (!currentCard.getType().equals("Wake Up")) { //?
                if (currentCard.getUpdated().equals("false")) {
                    buttonShare.setVisibility(View.INVISIBLE);
                    buttonDone.setVisibility(View.VISIBLE);
                }
            }
        }


        CurrentUserHelper user = new CurrentUserHelper(getContext());
        token = user.getToken();
        userId = user.getUserObjectId();

        final ChallengeController challengeController = new ChallengeController(getContext(), (Activity) getContext(), 0 , 0, 0);

        buttonGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (currentCard.getType().equals("Wake Up")) {
                           int intentId = Integer.parseInt(currentCard.getGoal());
                           challengeController.give_up(currentCard.getId(), intentId, token, userId);
                    } else challengeController.give_up(currentCard.getId(), 0, token, userId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = currentCard.getId();
                SQLiteDatabase localSQLiteDatabase = new DBHelper(getContext()).getWritableDatabase();
                ContentValues localContentValues = new ContentValues();
                localContentValues.put("updated", "true");
                localSQLiteDatabase.update("myChallenges", localContentValues, "challenge_id = ?", new String[]{id});
                int i = localSQLiteDatabase.update("updated", localContentValues, "challenge_id = ?", new String[]{id});
                try {
                    challengeController.doneForToday(id, token, userId);
                    buttonDone.setVisibility(View.INVISIBLE);
                    buttonShare.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "I'm done for today with my challenge: " + currentCard.getGoal() + "\nTry and you - www.champyapp.com/download";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                getContext().startActivity(Intent.createChooser(share, "How would you like to share?"));
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

