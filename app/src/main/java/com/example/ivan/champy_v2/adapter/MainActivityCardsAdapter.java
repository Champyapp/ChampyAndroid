package com.example.ivan.champy_v2.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
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

public class MainActivityCardsAdapter extends CustomPagerAdapter implements View.OnClickListener {

    private ArrayList<SelfImprovement_model> arrayList;
    public static final String TAG = "CardsAdapterMain";
    private String token, userId;
    private Snackbar snackbar;
    private Button buttonDoneForToday, buttonGiveUp;
    private ChallengeController cc;
    private SelfImprovement_model currentCard;
    private String itemWakeUpTime, itemChallengeName, itemGoal, itemType, itemVersus, itemSenderProgress;

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
        currentCard = arrayList.get(position);
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

        cc = new ChallengeController(getContext(), (Activity) getContext(), 0 , 0, 0);
        Typeface typeface = android.graphics.Typeface.createFromAsset(getContext().getAssets(), "fonts/bebasneue.ttf");

        TextView tvChallengeType = (TextView) tempView.findViewById(R.id.tvChallengeType);
        tvChallengeType.setTextSize((float)(y*1.3));
        tvChallengeType.setTypeface(typeface);
        tvChallengeType.setText(currentCard.getType());

        itemType   = currentCard.getType();
        itemGoal   = currentCard.getGoal();
        itemChallengeName   = currentCard.getChallengeName();
        itemWakeUpTime = currentCard.getWakeUpTime();
        itemVersus = "with " + currentCard.getVersus();
        itemSenderProgress = currentCard.getSenderProgress();
        //Log.i(TAG, "getView: " + itemGoal + " = " + toArrayOfStrings(itemSenderProgress)[0]);
        TextView tvRecipientName = (TextView) tempView.findViewById(R.id.tvRecipientName);
        ImageView challengeLogo      = (ImageView)tempView.findViewById(R.id.imageViewChallengeLogo);
        TextView tvChallengeDescription = (TextView) tempView.findViewById(R.id.tvChallengeDescription);

        switch (itemType) {
            case "Wake Up":
                challengeLogo.setImageResource(R.drawable.wakeup_white);
                tvChallengeDescription.setText(itemChallengeName);
                break;
            case "Duel":
                tvRecipientName.setText(itemVersus);
                tvRecipientName.setTypeface(typeface);
                tvChallengeDescription.setText(itemGoal);
                challengeLogo.setImageResource(R.drawable.duel_white);
                break;
            case "Self-Improvement":
                tvChallengeDescription.setText(itemGoal);
                challengeLogo.setImageResource(R.drawable.self_white);
                break;
        }


        tvChallengeDescription.setTextSize((float) (y*1.5));
        tvChallengeDescription.setTypeface(typeface);


        TextView tvDuration = (TextView) tempView.findViewById(R.id.textViewDuration);
        tvDuration.setText(currentCard.getDays() + " Days");
        tvDuration.setTextSize(y*2);
        tvDuration.setTypeface(typeface);

        buttonGiveUp = (Button) tempView.findViewById(R.id.buttonGiveUp);
        buttonGiveUp.getLayoutParams().width = x*10;
        buttonGiveUp.getLayoutParams().height = x*10;

        CurrentUserHelper user = new CurrentUserHelper(getContext());
        token = user.getToken();
        userId = user.getUserObjectId();

        buttonDoneForToday = (Button) tempView.findViewById(R.id.buttonDoneForToday);
        buttonDoneForToday.getLayoutParams().width = x*10;
        buttonDoneForToday.getLayoutParams().height = x*10;

        //final Button finalButton = buttonDoneForToday;
        if (currentCard.getUpdated() != null){
            if (!currentCard.getType().equals("Wake Up")) {
                if (currentCard.getUpdated().equals("false")) {
                    buttonDoneForToday.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.icon_done_for_today));
//                        tvDoneForToday.setVisibility(View.VISIBLE);
                }
            }
        }


        buttonGiveUp.setOnClickListener(this);
        buttonDoneForToday.setOnClickListener(this);

        return tempView;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.buttonGiveUp:
                snackbar = Snackbar.make(view, "Are you sure?", Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (currentCard.getType().equals("Wake Up")) {
                                int intentId = Integer.parseInt(itemWakeUpTime);
                                cc.give_up(currentCard.getId(), intentId, token, userId);
                            } else {
                                cc.give_up(currentCard.getId(), 0, token, userId);
                            }
                            snackbar = Snackbar.make(view, "Looser!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                snackbar.show();
            break;

            case R.id.buttonDoneForToday:
                String id = currentCard.getId();
                SQLiteDatabase localSQLiteDatabase = new DBHelper(getContext()).getWritableDatabase();
                ContentValues localContentValues = new ContentValues();
                localContentValues.put("updated", "true");
                localSQLiteDatabase.update("myChallenges", localContentValues, "challenge_id = ?", new String[]{id});
                int i = localSQLiteDatabase.update("updated", localContentValues, "challenge_id = ?", new String[]{id});
                try {
                    cc.doneForToday(id, token, userId);
                    snackbar = Snackbar.make(view, "Well done!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buttonDoneForToday.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.icon_share));
                break;
        }



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

