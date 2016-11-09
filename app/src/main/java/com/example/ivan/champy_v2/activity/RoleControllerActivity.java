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
import com.example.ivan.champy_v2.helper.NotificationController;
import com.example.ivan.champy_v2.utils.ChallengeController;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;

public class RoleControllerActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "RoleControllerActivity";
    private SessionManager sessionManager;
    private OfflineMode offlineMode;
    private TextView lostInternet;
    private ImageView imageReload;
//    private Socket mSocket;
//    //public View spinner;
//
//    private Emitter.Listener onConnect = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            CurrentUserHelper currentUser = new CurrentUserHelper(getApplicationContext());
//            mSocket.emit("ready", currentUser.getToken());
//            Log.i(TAG, "Sockets: onConnect");
//        }
//    };
//    private Emitter.Listener onConnected = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            Log.i(TAG, "Sockets: onConnected");
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_controller);

        Typeface typeface = android.graphics.Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
        TextView tvChampy = (TextView)findViewById(R.id.tvChampy);
        tvChampy.setTypeface(typeface);

//        spinner = findViewById(R.id.loadingPanel);

//        try {
//            mSocket = IO.socket("http://46.101.213.24:3007");
//        } catch (URISyntaxException e) { throw new RuntimeException(e); }

        sessionManager = new SessionManager(getApplicationContext());
        offlineMode = new OfflineMode();

        if (offlineMode.isConnectedToRemoteAPI(this)) {
            checkIfLoggedInAndMakeRedirect();
        } else {
            lostInternet = (TextView)findViewById(R.id.tvLostInternetConnection);
            imageReload = (ImageView)findViewById(R.id.imageRetry);
            lostInternet.setTypeface(typeface);

            lostInternet.setVisibility(View.VISIBLE);
            imageReload.setVisibility(View.VISIBLE);
            imageReload.setOnClickListener(this);
        }


    }

    @Override
    public void onClick(View v) {
        if (offlineMode.isConnectedToRemoteAPI(this)) {
            lostInternet.setVisibility(View.INVISIBLE);
            imageReload.setVisibility(View.INVISIBLE);
            checkIfLoggedInAndMakeRedirect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //spinner.setVisibility(View.INVISIBLE);
    }


    private void checkIfLoggedInAndMakeRedirect() {
        if (sessionManager.isUserLoggedIn()) {
            CurrentUserHelper user = new CurrentUserHelper(getApplicationContext());
            String uId = user.getUserObjectId();
            String uToken = user.getToken();
            Log.d(TAG, "inProgressCount: " + user.getInProgressCount());
            ChallengeController cc = new ChallengeController(getApplicationContext(), this, uToken, uId);
            cc.generateCardsForMainActivity();
        } else {
            NotificationController controller = new NotificationController(getApplicationContext());
            controller.deactivateDailyNotificationReminder();
            Intent goToActivity = new Intent(this, LoginActivity.class);
            startActivity(goToActivity);
        }
    }

}
