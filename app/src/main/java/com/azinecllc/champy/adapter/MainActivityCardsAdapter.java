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

        final String itemGoal = currentCard.getGoal();
        final String itemProgress = currentCard.getProgress();
        final String itemType = currentCard.getType();
        final String itemNeedsToCheck = currentCard.getNeedsToCheck();
        final String itemInProgressId = currentCard.getId();
        final String[] challengeProgress = itemProgress.replace("[", "").replace("]", "").split(", ");
        String details = Arrays.toString(challengeProgress);

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

//        try {
//            String TAG = "INFO ABOUT CARD:";
//            Log.d(TAG, "getView: " + currentCard.getRecipient());      // false;
//            Log.d(TAG, "getView: " + currentCard.getChallengeName());  // Wake up at 08:55
//            Log.d(TAG, "getView: " + currentCard.getGoal());           // 0855
//            Log.d(TAG, "getView: " + currentCard.getId());             // 58605a9645af8ed13f56b8c1
//            Log.d(TAG, "getView: " + currentCard.getName());           // null
//            Log.d(TAG, "getView: " + currentCard.getWakeUpTime());     // [1482735221, 1482821621]
//            Log.d(TAG, "getView: " + currentCard.getType());           // Wake Up
//            Log.d(TAG, "getView: " + currentCard.getConstDuration());  // 2 days
//            Log.d(TAG, "getView: " + currentCard.getDays());           // 1-2 (current)
//            Log.d(TAG, "getView: " + currentCard.getProgress());       // [UnixTime]
//            Log.d(TAG, "getView: " + currentCard.getStatus());         // started
//            Log.d(TAG, "getView: " + currentCard.getVersus());         // not duel
//            Log.d(TAG, "getView: " + currentCard.getNeedsToCheck());   // true / false
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

        switch (itemType) {
            case "Wake Up":
                imageChallengeLogo.setImageResource(R.drawable.ic_wakeup_white);
                tvChallengeDescription.setText(currentCard.getChallengeName());
                //~~~~~~~~~~
//                tvDuration.setText(String.format("%s", currentCard.getDays() + getContext().getResources().getString(R.string.daysToGo)));
//                buttonShare.setVisibility(View.VISIBLE);
//                buttonDone.setVisibility(View.INVISIBLE);
//                tvEveryDayForTheNext.setVisibility(View.VISIBLE);
                //~~~~~~~~~~
                break;
            case "Duel":
                imageChallengeLogo.setImageResource(R.drawable.ic_duel_white);
                TextView tvRecipientName = (TextView)tempView.findViewById(R.id.tvRecipientName);
                tvRecipientName.setText(String.format("%s", "with " + currentCard.getVersus()));
                tvRecipientName.setTypeface(typeface);
                break;
            case "Self-Improvement":
                imageChallengeLogo.setImageResource(R.drawable.ic_self_white);
                break;
        }

        SessionManager sessionManager = SessionManager.getInstance(getContext());
        final String userId = sessionManager.getUserId();
        final String token = sessionManager.getToken();
        final ChallengeController cc = new ChallengeController(getContext(), (Activity) getContext(), token, userId);



        buttonGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar = Snackbar.make(v, getContext().getString(R.string.are_you_sure), Snackbar.LENGTH_LONG).setAction(getContext().getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            int i = (itemType.equals("Wake Up")) ? Integer.parseInt(currentCard.getGoal()) : 0;
                            cc.give_up(itemInProgressId, i, new Intent(getContext(), MainActivity.class));
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
                    cc.doneForToday(itemInProgressId, details, itemGoal);
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


//    private String[] toArrayOfStrings(String arg) {
//        String a = arg.replace("[", "");
//        String b = a.replace("]","");
//        return b.split(", ");
//    }

//    try {
//        Log.d(TAG, "getView: " + currentCard.getRecipient());
//        Log.d(TAG, "getView: " + currentCard.getChallengeName());
//        Log.d(TAG, "getView: " + currentCard.getGoal());
//        Log.d(TAG, "getView: " + currentCard.getId());
//        Log.d(TAG, "getView: " + currentCard.getName());
//        Log.d(TAG, "getView: " + currentCard.getWakeUpTime());
//        Log.d(TAG, "getView: " + currentCard.getType());
//        Log.d(TAG, "getView: " + currentCard.getConstDuration());
//        Log.d(TAG, "getView: " + currentCard.getDays());
//        Log.d(TAG, "getView: " + currentCard.getProgress());
//        Log.d(TAG, "getView: " + currentCard.getStatus());
//        Log.d(TAG, "getView: " + currentCard.getVersus());
//    } catch (Exception e) { e.printStackTrace(); }


}

