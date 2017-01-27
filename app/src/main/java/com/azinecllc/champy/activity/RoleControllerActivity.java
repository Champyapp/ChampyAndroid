package com.azinecllc.champy.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.controller.DailyRemindController;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;

/**
 * Splash screen with GenerateCards method
 */

public class RoleControllerActivity extends AppCompatActivity implements View.OnClickListener {

    private SessionManager sessionManager;
    private OfflineMode offlineMode;
    private Typeface typeface;
    private TextView lostInternet;
    private ImageView imageReload;
    private View spinner;
    private Intent goTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_controller);
        spinner = findViewById(R.id.loadingPanel);

        checkNotificationExtras();

        sessionManager = SessionManager.getInstance(getApplicationContext());
        offlineMode = OfflineMode.getInstance();

        typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        TextView tvChampy = (TextView)findViewById(R.id.tvChampy);
        tvChampy.setTypeface(typeface);


        checkIfLoggedInAndMakeRedirect(goTo);


    }

    @Override
    public void onClick(View v) {
        if (offlineMode.isConnectedToRemoteAPI(this)) {
            spinner.setVisibility(View.VISIBLE);
            lostInternet.setVisibility(View.INVISIBLE);
            imageReload.setVisibility(View.INVISIBLE);
            checkIfLoggedInAndMakeRedirect(goTo);
        }
    }


    private void checkIfLoggedInAndMakeRedirect(Intent goTo) {
        if (offlineMode.isConnectedToRemoteAPI(this)) {
            if (sessionManager.isUserLoggedIn()) {
                final String uID = sessionManager.getUserId();
                final String token = sessionManager.getToken();

                ChallengeController cc = new ChallengeController(getApplicationContext(), this, token, uID);
                cc.refreshCardsForPendingDuel(goTo);

                DailyRemindController reminder = new DailyRemindController(getApplicationContext());
                reminder.enableDailyNotificationReminder();


            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        } else {
            lostInternet = (TextView)findViewById(R.id.tvLostInternetConnection);
            lostInternet.setVisibility(View.VISIBLE);
            lostInternet.setTypeface(typeface);

            imageReload = (ImageView)findViewById(R.id.imageRetry);
            imageReload.setVisibility(View.VISIBLE);
            imageReload.setOnClickListener(this);

            spinner = findViewById(R.id.loadingPanel);
            spinner.setVisibility(View.INVISIBLE);
        }

    }


    private void checkNotificationExtras() {
        goTo = new Intent(this, MainActivity.class);
        Bundle bundle = getIntent().getExtras();
        String extras;
        if (bundle != null) {
            extras = bundle.getString("gcm");
            if (extras != null) {
                switch (extras) {
                    case "friend_request_confirmed":
                        goTo = new Intent(this, FriendsActivity.class);
                        goTo.putExtra("friend_request", "friend_request_confirmed");
                        break;
                    case "friend_request_incoming":
                        goTo = new Intent(this, FriendsActivity.class);
                        goTo.putExtra("friend_request", "friend_request_incoming");
                        break;
                    case "friend_request_removed":
                        goTo = new Intent(this, FriendsActivity.class);
                        goTo.putExtra("friend_request", "friend_request_removed");
                        break;
                    case "challenge_request_win":
                        goTo = new Intent(this, HistoryActivity.class);
                        goTo.putExtra("challenge_request_win", "true");
                        break;
                    case "challenge_request_confirmed":
                        goTo = new Intent(this, MainActivity.class);
                        break;
                    case "challenge_request_incoming":
                        goTo = new Intent(this, PendingDuelActivity.class);
                        break;
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


}
