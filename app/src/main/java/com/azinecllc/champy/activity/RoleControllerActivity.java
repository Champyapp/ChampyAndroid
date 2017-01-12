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

import io.jsonwebtoken.lang.RuntimeEnvironment;

/**
 * Splash screen with GenerateCards method
 */

public class RoleControllerActivity extends AppCompatActivity implements View.OnClickListener {

    private SessionManager sessionManager;
    private ChallengeController cc;
    private OfflineMode offlineMode;
    private Typeface typeface;
    private TextView lostInternet;
    private ImageView imageReload;
    private View spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_controller);
        spinner = findViewById(R.id.loadingPanel);

        sessionManager = SessionManager.getInstance(getApplicationContext());
        offlineMode = OfflineMode.getInstance();

        typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        TextView tvChampy = (TextView)findViewById(R.id.tvChampy);
        tvChampy.setTypeface(typeface);


        checkIfLoggedInAndMakeRedirect();


    }

    @Override
    public void onClick(View v) {
        if (offlineMode.isConnectedToRemoteAPI(this)) {
            spinner.setVisibility(View.VISIBLE);
            lostInternet.setVisibility(View.INVISIBLE);
            imageReload.setVisibility(View.INVISIBLE);
            checkIfLoggedInAndMakeRedirect();
        }
    }


    private void checkIfLoggedInAndMakeRedirect() {
        if (offlineMode.isConnectedToRemoteAPI(this)) {
            if (sessionManager.isUserLoggedIn()) {
                final String uId = sessionManager.getUserId();
                final String uToken = sessionManager.getToken();

                cc = new ChallengeController(getApplicationContext(), this, uToken, uId);
                cc.refreshCardsForPendingDuel(new Intent(this, MainActivity.class));

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        typeface = null;
//        if(lostInternet != null) {
//            lostInternet.destroyDrawingCache();
//            lostInternet = null;
//        }
//        imageReload.destroyDrawingCache();
//        imageReload = null;
//        spinner.destroyDrawingCache();
//        spinner = null;
//        cc = null;
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


}
