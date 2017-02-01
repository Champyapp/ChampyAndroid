package com.azinecllc.champy.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;

public class WakeUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TimePicker alarmTimePicker;
    private OfflineMode offlineMode;
    private ChallengeController cc;
    private Snackbar snackbar;
    private TextView etDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);

        final Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        final TextView tvIChallengeMySelf = (TextView) findViewById(R.id.tvChallengeToMySelf);
        final ImageButton buttonAccept = (ImageButton) findViewById(R.id.imageButtonAccept);
        final ImageButton buttonMinus = (ImageButton) findViewById(R.id.imageButtonMinus);
        final ImageButton buttonPlus = (ImageButton) findViewById(R.id.imageButtonPlus);
        final TextView tvEveryDay = (TextView) findViewById(R.id.tvEveryDayWakeUp);
        final TextView tvWakeUp = (TextView) findViewById(R.id.tvWakeUpChallenge);
        final TextView tvGoal = (TextView) findViewById(R.id.goal_text);
        final TextView tvDays = (TextView) findViewById(R.id.tvDays);
        etDays = (TextView) findViewById(R.id.etDays);

        tvIChallengeMySelf.setTypeface(typeface);
        tvEveryDay.setTypeface(typeface);
        tvWakeUp.setTypeface(typeface);
        etDays.setTypeface(typeface);
        tvDays.setTypeface(typeface);
        tvGoal.setTypeface(typeface);

        SessionManager sessionManager = SessionManager.getInstance(getApplicationContext());
        offlineMode = OfflineMode.getInstance();

        final String userID = sessionManager.getUserId();
        final String token = sessionManager.getToken();
        cc = new ChallengeController(this, this, token, userID);

        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);

        buttonAccept.setOnClickListener(this);
        buttonMinus.setOnClickListener(this);
        buttonPlus.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.imageButtonAccept:
                final int pickedHour = alarmTimePicker.getCurrentHour();
                final int picketMin = alarmTimePicker.getCurrentMinute();

                // this piece of code need only for check exist challenge
                String sHour   = String.format("%s", pickedHour); //"" + pickedHour;
                String sMinute = String.format("%s", picketMin);  // "" + picketMin;

                if (pickedHour < 10) sHour  = "0" + sHour;
                if (picketMin < 10) sMinute = "0" + sMinute;

                String fHour = sHour;
                String fMin = sMinute;

                final boolean isActive = cc.isActive(sHour + sMinute);
                if (offlineMode.isConnectedToRemoteAPI(WakeUpActivity.this)) {
                    snackbar = Snackbar.make(v, R.string.are_you_sure, Snackbar.LENGTH_LONG).setAction(R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isActive) {
                                int days = Integer.parseInt(etDays.getText().toString());
                                cc.createNewWakeUpChallenge(days, fHour, fMin);
                                snackbar = Snackbar.make(v, R.string.challenge_created, Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                snackbar = Snackbar.make(view, R.string.cant_create_this_challenge, Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        }
                    });
                    snackbar.show();
                }
                break;
            case R.id.imageButtonPlus:
                int daysCount = Integer.parseInt(etDays.getText().toString());
                int newDaysCount;
                if (daysCount < 1000) {
                    newDaysCount = daysCount + 1;
                    etDays.setText(String.valueOf(newDaysCount));
                }
                break;

            case R.id.imageButtonMinus:
                daysCount = Integer.parseInt(etDays.getText().toString());
                if (daysCount > 1) {
                    newDaysCount = daysCount - 1;
                    etDays.setText(String.valueOf(newDaysCount));
                }
                break;
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WakeUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }


}
