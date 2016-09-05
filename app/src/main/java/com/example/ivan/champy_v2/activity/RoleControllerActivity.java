package com.example.ivan.champy_v2.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.facebook.login.LoginManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class RoleControllerActivity extends AppCompatActivity {

    public static final String TAG = "RoleControllerActivity";
    private Socket mSocket;

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            CurrentUserHelper currentUser = new CurrentUserHelper(getApplicationContext());
            mSocket.emit("ready", currentUser.getToken());
            Log.i(TAG, "Sockets: onConnect");
        }
    };
    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "Sockets: onConnected");
        }
    };


//    private Emitter.Listener onNewChallenge = new Emitter.Listener()  {
//        @Override
//        public void call(final Object... args) {
//            String userId = user.get("id");
//            Log.i("call", "call: new challenge request for duel 1");
//            try {
//                sync.getUserInProgressChallenges(userId);
//                Log.i("call", "call: new challenge request for duel2");
//            } catch (Exception e) {
//                Log.i("call", "call: ERROR: " + e);
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_controller);

        try {
            //new ProgressTask().execute();
            mSocket = IO.socket("http://46.101.213.24:3007");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        Intent goToActivity;
        if (sessionManager.isUserLoggedIn()) {
            mSocket.on("connect", onConnect);
            mSocket.on("connected", onConnected);
            mSocket.connect();
            goToActivity = new Intent(this, MainActivity.class);
            Log.i("RoleController", "Login Status: TRUE, go to MainActivity...");
        } else {
            mSocket.disconnect();
            mSocket.off();
            goToActivity = new Intent(this, LoginActivity.class);
            Log.i("RoleController", "Login Status: FALSE, go to LoginActivity...");
        }
        startActivity(goToActivity);

    }


//    private class ProgressTask extends AsyncTask<Void,Void,Void> {
//
//        ProgressBar bar = (ProgressBar)findViewById(R.id.roleProgressBar);
//
//        @Override
//        protected void onPreExecute(){
//            bar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected Void doInBackground(Void... arg0) {
//            //my stuff is here
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            bar.setVisibility(View.GONE);
//        }
//    }

}
