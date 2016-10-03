package com.example.ivan.champy_v2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.helper.NotificationController;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_controller);

        try {
            mSocket = IO.socket("http://46.101.213.24:3007");
        } catch (URISyntaxException e) { throw new RuntimeException(e); }

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        Intent goToActivity;
        if (sessionManager.isUserLoggedIn()) {
            mSocket.on("connect", onConnect);
            mSocket.on("connected", onConnected);
            mSocket.connect();
            goToActivity = new Intent(this, MainActivity.class);
//            goToActivity = new Intent(this, SettingsActivity.class);
            Log.i("RoleController", "Login Status: TRUE, go to MainActivity...");
        } else {
            mSocket.off();
            mSocket.disconnect();
            NotificationController controller = new NotificationController(getApplicationContext());
            controller.deactivateDailyNotificationReminder();
            goToActivity = new Intent(this, LoginActivity.class);
            Log.i("RoleController", "Login Status: FALSE, go to LoginActivity...");
        }
        startActivity(goToActivity);

    }


}
