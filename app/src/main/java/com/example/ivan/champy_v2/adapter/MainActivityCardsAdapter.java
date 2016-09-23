package com.example.ivan.champy_v2.adapter;

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

        String itemType = currentCard.getType();
        String itemGoal = currentCard.getGoal();
        String itemChallengeName = currentCard.getChallengeName();
        itemWakeUpTime = currentCard.getWakeUpTime();
        String itemVersus = "with " + currentCard.getVersus();
        String itemSenderProgress = currentCard.getSenderProgress();
        String itemGetUpdated = currentCard.getUpdated();
        String itemGetProgress = currentCard.getSenderProgress();
        //Log.i(TAG, "getView: " + itemGoal + " = " + toArrayOfStrings(itemSenderProgress)[0]);
        TextView tvRecipientName = (TextView) tempView.findViewById(R.id.tvRecipientName);
        ImageView challengeLogo      = (ImageView)tempView.findViewById(R.id.imageViewChallengeLogo);
        TextView tvChallengeDescription = (TextView) tempView.findViewById(R.id.tvChallengeDescription);

        buttonDoneForToday = (Button) tempView.findViewById(R.id.buttonDoneForToday);
        buttonDoneForToday.getLayoutParams().width = x*10;
        buttonDoneForToday.getLayoutParams().height = x*10;
        buttonShare = (Button) tempView.findViewById(R.id.buttonShare);
        buttonShare.getLayoutParams().width = x*10;
        buttonShare.getLayoutParams().height = x*10;

        if (currentCard.getUpdated() != null && currentCard.getUpdated().equals("true") || currentCard.getType().equals("Wake Up")){
            buttonDoneForToday.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.icon_share));
            buttonDoneForToday.setVisibility(View.INVISIBLE);
            buttonShare.setVisibility(View.VISIBLE);
        }


        switch (itemType) {
            case "Wake Up":
                challengeLogo.setImageResource(R.drawable.wakeup_white);
                tvChallengeDescription.setText(itemChallengeName);
                //buttonDoneForToday.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.icon_share));
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
                if (currentCard.getType().equals("Wake Up")) break;
                String id = currentCard.getId();
                try {
                    cc.doneForToday(id, token, userId);
                    snackbar = Snackbar.make(view, "Well done!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.buttonShare:
                String message = "I'm improving myself and compete my challenge for today!\nTry and you: www.champyapp.com";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                getContext().startActivity(Intent.createChooser(share, "How would you like to share?"));
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

