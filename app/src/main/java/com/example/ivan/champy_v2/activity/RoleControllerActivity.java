package com.example.ivan.champy_v2.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.utils.ChallengeController;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;

public class RoleControllerActivity extends AppCompatActivity implements View.OnClickListener {

    public final String TAG = getClass().getName();
    private SessionManager sessionManager;
    private OfflineMode offlineMode;
    private Typeface typeface;
    private TextView lostInternet;
    private ImageView imageReload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_controller);

        typeface = android.graphics.Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
        TextView tvChampy = (TextView)findViewById(R.id.tvChampy);
        tvChampy.setTypeface(typeface);

        sessionManager = new SessionManager(getApplicationContext());
        offlineMode = new OfflineMode();

        checkIfLoggedInAndMakeRedirect();


    }

    @Override
    public void onClick(View v) {
        if (offlineMode.isConnectedToRemoteAPI(this)) {
            lostInternet.setVisibility(View.INVISIBLE);
            imageReload.setVisibility(View.INVISIBLE);
            checkIfLoggedInAndMakeRedirect();
        }
    }


    private void checkIfLoggedInAndMakeRedirect() {
        if (offlineMode.isConnectedToRemoteAPI(this)) {
            if (sessionManager.isUserLoggedIn()) {
                CurrentUserHelper user = new CurrentUserHelper(getApplicationContext());
                String uId = user.getUserObjectId();
                String uToken = user.getToken();
                Log.d(TAG, "inProgressCount: " + user.getInProgressCount());
                ChallengeController cc = new ChallengeController(getApplicationContext(), this, uToken, uId);
                cc.generateCardsForMainActivity();
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
        }

    }

}
