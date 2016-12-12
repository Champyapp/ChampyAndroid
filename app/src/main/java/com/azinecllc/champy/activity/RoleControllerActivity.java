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
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;

/**
 * Splash screen with GenerateCards method
 */

public class RoleControllerActivity extends AppCompatActivity implements View.OnClickListener {

    public final String TAG = RoleControllerActivity.class.getSimpleName();
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

        sessionManager = new SessionManager(getApplicationContext());
        offlineMode = OfflineMode.getInstance();
        String uId = sessionManager.getUserId();
        String uToken = sessionManager.getToken();
        cc = new ChallengeController(getApplicationContext(), this, uToken, uId);

        typeface = android.graphics.Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
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
                cc.refreshCardsForPendingDuel();
            } else {
                Intent goToActivity = new Intent(this, LoginActivity.class);
                startActivity(goToActivity);
            }
        } else {
            lostInternet = (TextView)findViewById(R.id.tvLostInternetConnection);
            imageReload = (ImageView)findViewById(R.id.imageRetry);
            lostInternet.setTypeface(typeface);

            lostInternet.setVisibility(View.VISIBLE);
            imageReload.setVisibility(View.VISIBLE);
            imageReload.setOnClickListener(this);

            spinner = findViewById(R.id.loadingPanel);
            spinner.setVisibility(View.INVISIBLE);
            //findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        }

        //spinner.setVisibility(View.INVISIBLE);

    }


}
