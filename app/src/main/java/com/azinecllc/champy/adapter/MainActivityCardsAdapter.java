package com.azinecllc.champy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.model.SelfImprovement_model;
import com.azinecllc.champy.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static com.azinecllc.champy.utils.Constants.oneDay;
import static com.azinecllc.champy.utils.Constants.twoDays;

public class MainActivityCardsAdapter extends MainActivityCardPagerAdapter {

    private ArrayList<SelfImprovement_model> arrayList;
    private Snackbar snackbar;

    public MainActivityCardsAdapter(Context context, ArrayList<SelfImprovement_model> mArrayList) {
        super(context);
        this.arrayList = mArrayList;
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

        String itemGoal = currentCard.getGoal();
        String itemProgress = currentCard.getProgress();
        String itemType = currentCard.getType();
        String itemNeedsToCheck = currentCard.getNeedsToCheck();
        String itemInProgressId = currentCard.getId();
        String[] challengeProgress = itemProgress.replace("[", "").replace("]", "").split(" ");

        TextView tvChallengeType = (TextView) tempView.findViewById(R.id.tvChallengeType);
        tvChallengeType.setText(currentCard.getType());
        tvChallengeType.setTextSize((float)(y*1.7));
        tvChallengeType.setTypeface(typeface);

        TextView tvChallengeDescription = (TextView) tempView.findViewById(R.id.tvChallengeDescription);
        tvChallengeDescription.setText(itemGoal);
        tvChallengeDescription.setTextSize(y*2);
        tvChallengeDescription.setTypeface(typeface);

        Button buttonGiveUp = (Button) tempView.findViewById(R.id.buttonGiveUp);
        buttonGiveUp.getLayoutParams().width  = x*7;
        buttonGiveUp.getLayoutParams().height = x*7;

        Button buttonDone = (Button) tempView.findViewById(R.id.buttonDoneForToday);
        buttonDone.getLayoutParams()  .width  = x*7;
        buttonDone.getLayoutParams()  .height = x*7;

        Button buttonShare = (Button) tempView.findViewById(R.id.buttonShare);
        buttonShare.getLayoutParams() .width  = x*7;
        buttonShare.getLayoutParams() .height = x*7;

        TextView tvEveryDayForTheNext = (TextView) tempView.findViewById(R.id.tvEveryDayForTheNext);
        tvEveryDayForTheNext.setTypeface(typeface);
        tvEveryDayForTheNext.setTextSize((float)(y*1.3));

        TextView tvDuration = (TextView) tempView.findViewById(R.id.textViewDuration);
        tvDuration.setTypeface(typeface);
        tvDuration.setTextSize(y*2);

//        try {
//            String TAG = "INFO ABOUT CARD:";
//            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//            Log.d(TAG, "getRecipient: " + currentCard.getRecipient());          // false;
//            Log.d(TAG, "getChallengeName: " + currentCard.getChallengeName());  // Wake up at 08:55
//            Log.d(TAG, "getGoal: " + currentCard.getGoal());                    // 0855
//            Log.d(TAG, "getId: " + currentCard.getId());                        // 58605a9645af8ed13f56b8c1
//            Log.d(TAG, "getName: " + currentCard.getName());                    // null
//            Log.d(TAG, "getWakeUpTime: " + currentCard.getWakeUpTime());        // [1482735221, 1482821621]
//            Log.d(TAG, "getType: " + currentCard.getType());                    // Wake Up
//            Log.d(TAG, "getConstDuration: " + currentCard.getConstDuration());  // 2 days
//            Log.d(TAG, "getDays: " + currentCard.getDays());                    // 1-2 (current)
//            Log.d(TAG, "getProgress: " + currentCard.getProgress());            // [UnixTime]
//            Log.d(TAG, "getStatus: " + currentCard.getStatus());                // started
//            Log.d(TAG, "getVersus: " + currentCard.getVersus());                // not duel
//            Log.d(TAG, "getNeedsToCheck: " + currentCard.getNeedsToCheck());    // true / false
//        } catch (Exception e) { e.printStackTrace(); }

        if (itemNeedsToCheck.equals("true")) {
            tvDuration.setText(getContext().getResources().getString(R.string.done_for_today));
            buttonDone.setVisibility(View.VISIBLE);
            buttonShare.setVisibility(View.INVISIBLE);
            tvEveryDayForTheNext.setVisibility(View.INVISIBLE);
        } else {
            tvDuration.setText(String.format("%s", currentCard.getDays() + getContext().getResources().getString(R.string.daysToGo)));
            buttonShare.setVisibility(View.VISIBLE);
            buttonDone.setVisibility(View.INVISIBLE);
            tvEveryDayForTheNext.setVisibility(View.VISIBLE);
        }


        SessionManager sessionManager = SessionManager.getInstance(getContext());
        final String uID = sessionManager.getUserId();
        final String token = sessionManager.getToken();
        ChallengeController cc = new ChallengeController(getContext(), (Activity) getContext(), token, uID);

        /*************************** last check-in time for buttons view *************************/
        long now = System.currentTimeMillis() / 1000;
        long myProgress = 0;
        long progressMidNight = 0;
        if (!challengeProgress[challengeProgress.length - 1].equals("")) {
            myProgress = Long.parseLong(challengeProgress[challengeProgress.length - 1]);
            Date date = new Date(myProgress * 1000);
            progressMidNight = myProgress
                    - (date.getHours()  * 60 * 60)
                    - (date.getMinutes()* 60)
                    - (date.getSeconds());
        }

        if (myProgress != 0L && now > progressMidNight + oneDay) {
            if (!itemType.equals("Wake Up")) {
                tvDuration.setText(getContext().getResources().getString(R.string.done_for_today));
                buttonShare.setVisibility(View.INVISIBLE);
                buttonDone.setVisibility(View.VISIBLE);
                tvEveryDayForTheNext.setVisibility(View.INVISIBLE);
            }
        }

        switch (itemType) {
            case "Wake Up":
                imageChallengeLogo.setImageResource(R.drawable.ic_wakeup_white);
                tvChallengeDescription.setText(currentCard.getChallengeName());
                //~~~~~~~~~~
                if (myProgress != 0) {
                    if (now > myProgress + oneDay) {
                        tvDuration.setText(getContext().getResources().getString(R.string.done_for_today));
                        buttonDone.setVisibility(View.VISIBLE);
                        buttonShare.setVisibility(View.INVISIBLE);
                        tvEveryDayForTheNext.setVisibility(View.INVISIBLE);
                        if (now > myProgress + oneDay + (10 * 60)) {
                            tvDuration.setText(String.format("%s", currentCard.getDays() + getContext().getResources().getString(R.string.daysToGo)));
                            buttonShare.setVisibility(View.VISIBLE);
                            buttonDone.setVisibility(View.INVISIBLE);
                            tvEveryDayForTheNext.setVisibility(View.VISIBLE);
                        }
                    }
                }
//                else {
//                    String[] wakeArray = currentCard.getWakeUpTime().replace("[", "").replace("]", "").split(",");
//                    System.out.println(Arrays.toString(wakeArray));
//                    System.out.println(wakeArray[0]);
//                    int firstAlarm = Integer.parseInt(wakeArray[0]);
//                    if (now > firstAlarm) {
//                        tvDuration.setText(getContext().getResources().getString(R.string.done_for_today));
//                        buttonDone.setVisibility(View.VISIBLE);
//                        buttonShare.setVisibility(View.INVISIBLE);
//                        tvEveryDayForTheNext.setVisibility(View.INVISIBLE);
//                    } else {
//                        tvDuration.setText(String.format("%s", currentCard.getDays() + getContext().getResources().getString(R.string.daysToGo)));
//                        buttonShare.setVisibility(View.VISIBLE);
//                        buttonDone.setVisibility(View.INVISIBLE);
//                        tvEveryDayForTheNext.setVisibility(View.VISIBLE);
                        //}
//                    }
//                }
                //~~~~~~~~~~
                break;
            case "Duel":
                imageChallengeLogo.setImageResource(R.drawable.ic_duel_white);
                TextView tvRecipientName = (TextView) tempView.findViewById(R.id.tvRecipientName);
                tvRecipientName.setText(String.format("%s", "with " + currentCard.getVersus()));
                tvRecipientName.setTypeface(typeface);
                break;
            case "Self-Improvement":
                imageChallengeLogo.setImageResource(R.drawable.ic_self_white);
                break;
        }

        /************************************* Clicks ********************************************/

        Intent goMain = new Intent(getContext(), MainActivity.class);

        buttonGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar = Snackbar.make(v, getContext().getString(R.string.are_you_sure), Snackbar.LENGTH_LONG).setAction(getContext().getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            int i = (itemType.equals("Wake Up")) ? Integer.parseInt(currentCard.getGoal()) : 0;
                            cc.give_up(itemInProgressId, i, goMain);
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
                    buttonDone.setVisibility(View.INVISIBLE);
                    buttonShare.setVisibility(View.VISIBLE);
                    tvDuration.setVisibility(View.VISIBLE);
                    cc.doneForToday(itemInProgressId, itemGoal, goMain);
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
                        message = getContext().getString(R.string.share_text)
                                + currentCard.getType() + " challenge '"
                                + currentCard.getGoal() + "'" + getContext().getString(R.string.champyapp_link);
                        break;
                    case "Duel":
                        message = getContext().getString(R.string.share_text)
                                + currentCard.getType() + " challenge '"
                                + currentCard.getGoal() + "' with "
                                + currentCard.getVersus() + getContext().getString(R.string.champyapp_link);
                        break;
                    case "Wake Up":
                        message = getContext().getString(R.string.share_text)
                                + currentCard.getChallengeName()
                                + " challenge" + getContext().getString(R.string.champyapp_link);
                        break;
                    default:
                        message = getContext().getString(R.string.share_text)
                                + getContext().getString(R.string.champyapp_link);

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


}

