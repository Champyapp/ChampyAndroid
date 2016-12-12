package com.azinecllc.champy.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
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

import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.model.SelfImprovement_model;
import com.azinecllc.champy.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.azinecllc.champy.utils.Constants.oneDay;

public class MainActivityCardsAdapter extends MainActivityCardPagerAdapter {

    private final String TAG = "CardsAdapterMain";
    private ArrayList<SelfImprovement_model> arrayList;
    private Snackbar snackbar;
    private SessionManager sessionManager;

    public MainActivityCardsAdapter(Context context, ArrayList<SelfImprovement_model> mArrayList, SessionManager session) {
        super(context);
        this.arrayList = mArrayList;
        this.sessionManager = session;
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
        ImageView imageChallengeLogo = (ImageView)tempView.findViewById(R.id.imageViewChallengeLogo);
        final Typeface typeface = android.graphics.Typeface.createFromAsset(getContext().getAssets(), "fonts/bebasneue.ttf");
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int x = size.x / 100;
        int y = size.y / 100;
        cardImage.getLayoutParams().width  = x*65;
        cardImage.getLayoutParams().height = y*50;
        if (y > 10) y = 10;

        String itemGoal = currentCard.getGoal();
        final String itemProgress = currentCard.getProgress();
        final String itemType = currentCard.getType();
        final String itemNeedsToCheck = currentCard.getNeedsToCheck();
        final String itemInProgressId = currentCard.getId();
        final String[] challengeProgress = toArrayOfStrings(itemProgress);

        final TextView tvChallengeType = (TextView) tempView.findViewById(R.id.tvChallengeType);
        tvChallengeType.setText(currentCard.getType());
        tvChallengeType.setTextSize((float)(y*1.7));
        tvChallengeType.setTypeface(typeface);
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
        tvEveryDayForTheNext.setTypeface(typeface);
        tvEveryDayForTheNext.setTextSize((float)(y*1.3));
        final TextView tvDuration = (TextView) tempView.findViewById(R.id.textViewDuration);
        tvDuration.setTypeface(typeface);
        tvDuration.setTextSize(y*2);

        if (itemNeedsToCheck.equals("true")) {
            tvDuration.setText(getContext().getResources().getString(R.string.done_for_today));
            buttonDone.setVisibility(View.VISIBLE);
            buttonShare.setVisibility(View.INVISIBLE);
            tvEveryDayForTheNext.setVisibility(View.INVISIBLE);
        } else {
            tvDuration.setText("" + currentCard.getDays() + getContext().getResources().getString(R.string.daysToGo));
            buttonShare.setVisibility(View.VISIBLE);
            buttonDone.setVisibility(View.INVISIBLE);
            tvEveryDayForTheNext.setVisibility(View.VISIBLE);
        }

        switch (itemType) {
            case "Wake Up":
                imageChallengeLogo.setImageResource(R.drawable.wakeup_white);
                itemGoal = currentCard.getChallengeName();
                tvChallengeDescription.setText(itemGoal);
                break;
            case "Duel":
                imageChallengeLogo.setImageResource(R.drawable.duel_white);
                TextView tvRecipientName = (TextView)tempView.findViewById(R.id.tvRecipientName);
                tvRecipientName.setText("with " + currentCard.getVersus());
                tvRecipientName.setTypeface(typeface);
                break;
            case "Self-Improvement":
                imageChallengeLogo.setImageResource(R.drawable.self_white);
                break;
        }

        final String userId = sessionManager.getUserId();
        final String token = sessionManager.getToken();
        final ChallengeController cc = new ChallengeController(getContext(), (Activity) getContext(), token, userId);


        /*************************** last check-in time for auto surrender ************************/
        long now = Calendar.getInstance().getTimeInMillis() / 1000;
        long myProgress = 0;
        long progressMidNight = 0;
        try {
            myProgress = Long.parseLong(challengeProgress[challengeProgress.length - 1]); // our last checkIn in seconds
            Date date = new Date(myProgress * 1000); // convert last checkIn in date format
            progressMidNight = myProgress - (date.getHours() * 60 * 60) - (date.getMinutes() * 60) - (date.getSeconds());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if (myProgress != 0L && now > progressMidNight + oneDay) {
            // it's mean "self" and "duel"
            DBHelper dbHelper = DBHelper.getInstance(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("updated", "false"); // was true
            db.update("updated",      cv, "challenge_id = ?", new String[]{itemInProgressId});
            db.update("myChallenges", cv, "challenge_id = ?", new String[]{itemInProgressId});

            if (!itemType.equals("Wake Up")) {
                tvDuration.setText(getContext().getResources().getString(R.string.done_for_today));
                buttonShare.setVisibility(View.INVISIBLE);
                buttonDone.setVisibility(View.VISIBLE);
            }
            if (now > progressMidNight + oneDay + oneDay) {
                try {
                    if (itemType.equals("Wake Up")) {
                        int i = Integer.parseInt(currentCard.getWakeUpTime());
                        cc.give_up(itemInProgressId, i);
                    } else {
                        cc.give_up(itemInProgressId, 0);
                    }
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        /******************************************************************************************/


        buttonGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar = Snackbar.make(v, getContext().getString(R.string.are_you_sure), Snackbar.LENGTH_LONG).setAction(getContext().getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (currentCard.getType().equals("Wake Up")) {
                                int i = Integer.parseInt(currentCard.getGoal());
                                cc.give_up(itemInProgressId, i);
                            } else {
                                cc.give_up(itemInProgressId, 0);
                            }
                        } catch (IOException | NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
                snackbar.show();
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cc.doneForToday(itemInProgressId);
                    buttonDone.setVisibility(View.INVISIBLE);
                    buttonShare.setVisibility(View.VISIBLE);
                    snackbar = Snackbar.make(v, "Well done!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                switch (currentCard.getType()) {
                    case "Self-Improvement":
                        message = getContext().getString(R.string.share_text) + currentCard.getType()
                                + " challenge '" + currentCard.getGoal() + "'" + getContext().getString(R.string.champyapp_link);
                        break;
                    case "Duel":
                        message = getContext().getString(R.string.share_text) + currentCard.getType()
                                + " challenge '" + currentCard.getGoal() + "'" + getContext().getString(R.string.champyapp_link);
                        break;
                    case "Wake Up":
                        message = getContext().getString(R.string.share_text) + " "
                                + currentCard.getChallengeName() + " challenge" + getContext().getString(R.string.champyapp_link);
                        break;
                    default:
                        message = getContext().getString(R.string.share_text) + getContext().getString(R.string.champyapp_link);

                }
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

