package com.example.ivan.champy_v2.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
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
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.model.SelfImprovement_model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MainActivityCardsAdapter extends MainActivityCardPagerAdapter {

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
        final ChallengeController cc = new ChallengeController(getContext(), (Activity) getContext(), 0 , 0, 0);
        final SelfImprovement_model currentCard = arrayList.get(position);
        ImageView cardImage = (ImageView)tempView.findViewById(R.id.cardImage);
        ImageView imageChallengeLogo = (ImageView)tempView.findViewById(R.id.imageViewChallengeLogo);
        Typeface typeface = android.graphics.Typeface.createFromAsset(getContext().getAssets(), "fonts/bebasneue.ttf");

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int x = size.x / 100;
        int y = size.y / 100;
        cardImage.getLayoutParams().width  = x*65;
        cardImage.getLayoutParams().height = y*50;
        if (y > 10) y = 10;

        final TextView tvChallengeType = (TextView) tempView.findViewById(R.id.tvChallengeType);
        tvChallengeType.setText(currentCard.getType());
        tvChallengeType.setTextSize((float)(y*1.3));
        tvChallengeType.setTypeface(typeface);

        String itemSenderProgress = currentCard.getSenderProgress();
        String itemUpdate = currentCard.getUpdated();
        String itemGoal = currentCard.getGoal();
        String itemType = currentCard.getType();
        final String itemInProgressId = currentCard.getId();
        String[] senderProgress = toArrayOfStrings(itemSenderProgress);

        switch (itemType) {
            case "Wake Up":
                imageChallengeLogo.setImageResource(R.drawable.wakeup_white);
                itemGoal = currentCard.getChallengeName();
                break;
            case "Duel":
                imageChallengeLogo.setImageResource(R.drawable.duel_white);
                TextView tvRecipientName = (TextView)tempView.findViewById(R.id.tvRecipientName);
                tvRecipientName.setText(currentCard.getVersus());
                tvRecipientName.setTypeface(typeface);
                break;
            case "Self-Improvement":
                imageChallengeLogo.setImageResource(R.drawable.self_white);
                break;
        }


        final TextView tvChallengeDescription = (TextView) tempView.findViewById(R.id.tvChallengeDescription);
        tvChallengeDescription.setText(itemGoal);
        tvChallengeDescription.setTextSize(y*2);
        tvChallengeDescription.setTypeface(typeface);
        final Button buttonGiveUp = (Button) tempView.findViewById(R.id.buttonGiveUp);
        buttonGiveUp.getLayoutParams().width  = x*7;
        buttonGiveUp.getLayoutParams().height = x*7;
        final Button buttonDone = (Button) tempView.findViewById(R.id.buttonDoneForToday);
        buttonDone.getLayoutParams()  .width  = x*7;
        buttonDone.getLayoutParams()  .height = x*7;
        final Button buttonShare = (Button) tempView.findViewById(R.id.buttonShare);
        buttonShare.getLayoutParams() .width  = x*7;
        buttonShare.getLayoutParams() .height = x*7;

        final TextView tvEveryDayForTheNext = (TextView) tempView.findViewById(R.id.tvEveryDayForTheNext);
        final TextView tvDuration = (TextView) tempView.findViewById(R.id.textViewDuration);

        if (itemType.equals("Wake Up") || itemUpdate.equals("true")) { //?
            tvDuration.setText("" + currentCard.getDays() + getContext().getResources().getString(R.string.daysToGo));
            buttonShare.setVisibility(View.VISIBLE);
            buttonDone.setVisibility(View.INVISIBLE);
            tvEveryDayForTheNext.setVisibility(View.VISIBLE);
        } else {
            tvDuration.setText(getContext().getResources().getString(R.string.done_for_today));
            buttonDone.setVisibility(View.VISIBLE);
            buttonShare.setVisibility(View.INVISIBLE);
            tvEveryDayForTheNext.setVisibility(View.INVISIBLE);
        }
        tvDuration.setTypeface(typeface);
        tvDuration.setTextSize(y*2);
        CurrentUserHelper user = new CurrentUserHelper(getContext());
        userId = user.getUserObjectId();
        token  = user.getToken();

//        /**
//         * My algorithm for displaying buttons inside cards view and opportunity for check challenge
//         * @param longSenderProgress it's last element of the "Sender Progress" (api: 'at"). We are can
//         *                           take in from ChallengeController -> GenerateCardsForMainActivity()
//         *                           method. We use it for compare.
//         * @param checkInPlusOneDay it's parsed long of 'longSenderProgress' plus one day in seconds.
//         *                          We use it to give the user time for relaxing. If current time
//         *                          more than checkInPlusOneDay then we make button "doneForToday"
//         *                          isActive and now we are ready to rewrite our 'senderProgress'
//         *  it's parsed long of 'longSenderProgress' plus one day and one hour in seconds.
//         *  We use it to give user time for press the button "done for today". If user did it
//         *  then we are rewrite senderProgress and make the button "doneForToday" not active
//         *  else - autoSurrender
//         */

        long now = Calendar.getInstance().getTimeInMillis() / 1000;
        long longSenderProgress = 0;
        long senderProgressMidNight = 0;
        final long oneDay = 86400L;
        try {
            longSenderProgress = Long.parseLong(senderProgress[0]); // our last checkIn in seconds
            Log.d(TAG, "getView: longSenderProgress: " + longSenderProgress);
            Date date = new Date(longSenderProgress * 1000); // convert last checkIn in date format
            senderProgressMidNight = longSenderProgress - (date.getHours() * 60 * 60) - (date.getMinutes() * 60) - (date.getSeconds());
        } catch (RuntimeException e) { e.printStackTrace(); }


        if (longSenderProgress != 0 && now > senderProgressMidNight + oneDay) {
            if (!itemType.equals("Wake Up")) {
                tvDuration.setText(getContext().getResources().getString(R.string.done_for_today));
                buttonShare.setVisibility(View.INVISIBLE);
                buttonDone.setVisibility(View.VISIBLE);
            }

            if (now > senderProgressMidNight + oneDay + oneDay) {
                try {
                    if (itemType.equals("Wake Up")) {
                        int i = Integer.parseInt(currentCard.getWakeUpTime());
                           cc.give_up(itemInProgressId, i, token, userId);
                    } else cc.give_up(itemInProgressId, 0, token, userId);
                } catch (IOException | NumberFormatException e) { e.printStackTrace(); }
            }
        }



        buttonGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar = Snackbar.make(v, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (currentCard.getType().equals("Wake Up")) {
                                int i = Integer.parseInt(currentCard.getWakeUpTime());
                                   cc.give_up(itemInProgressId, i, token, userId);
                            } else cc.give_up(itemInProgressId, 0, token, userId);
                        } catch (IOException | NumberFormatException e) { e.printStackTrace(); }
                    }
                });
                snackbar.show();
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // TODO: 26.09.2016 replace this piece of code inside DoneForToday method in cc.
                    ////////////////////////////////////////////////////////////////////////
                    DBHelper dbHelper = new DBHelper(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put("updated", "true");
                    db.update("myChallenges", cv, "challenge_id = ?", new String[]{itemInProgressId});
                    db.update("updated", cv, "challenge_id = ?", new String[]{itemInProgressId});
                    ////////////////////////////////////////////////////////////////////////
                    cc.doneForToday(itemInProgressId, token, userId);
                    buttonDone.setVisibility(View.INVISIBLE);
                    buttonShare.setVisibility(View.VISIBLE);
                    snackbar = Snackbar.make(v, "Well done!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } catch (IOException e) { e.printStackTrace(); }
            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = getContext().getString(R.string.share_text1) + currentCard.getGoal() + getContext().getString(R.string.share_text2);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                getContext().startActivity(Intent.createChooser(share, getContext().getString(R.string.how_would_you_like_to_share)));
            }
        });

        return tempView;
    }


    @Override
    public int dataCount() {
        return arrayList.size();
    }


    private String[] toArrayOfStrings(String arg) {
        String a = arg.replace("[", "");
        String b = a.replace("]","");
        return b.split(", ");
    }

}

