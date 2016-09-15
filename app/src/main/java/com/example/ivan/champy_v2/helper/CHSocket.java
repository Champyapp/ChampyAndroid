package com.example.ivan.champy_v2.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.ivan.champy_v2.ChallengeController;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class CHSocket {

    public static final String TAG = "CHSockets";
    private Socket mSocket;
    Activity activity;
    Context context;


    public CHSocket(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }


    public void tryToConnect() {
        try {
            Log.i(TAG, "tryToConnect: trying...");
            mSocket = IO.socket("http://46.101.213.24:3007");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public void connectAndEmmit() {
        mSocket.on("connect",                               onConnect);
        mSocket.on("connected",                             onConnected);

        mSocket.on("InProgressChallenge:accepted",          onGenerateNewChallenge);
        mSocket.on("InProgressChallenge:won",               onGenerateNewChallenge);
//        mSocket.on("InProgressChallenge:failed",            onGenerateNewChallenge);
//        mSocket.on("InProgressChallenge:new",               onRefreshPending);
//        mSocket.on("InProgressChallenge:checked",           onNewChallenge);
//        mSocket.on("InProgressChallenge:updated",           onNewChallenge);
//        mSocket.on("InProgress:finish",                     onNewChallenge);
//        mSocket.on("InProgressChallenge:recipient:checked", onNewChallenge);
//        mSocket.on("InProgressChallenge:sender:checked",    onNewChallenge);

        mSocket.connect();
    }




    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            CurrentUserHelper currentUser = new CurrentUserHelper(context);
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

    private Emitter.Listener onGenerateNewChallenge = new Emitter.Listener()  {
        @Override
        public void call(final Object... args) {
            try {
                ChallengeController cc = new ChallengeController(context, activity, 0, 0, 0);
                cc.generateCardsForMainActivity();
                Log.i(TAG, "Sockets: generateNewChallenge success!");
            } catch (Exception e) {
                Log.i(TAG, "Sockets: ERROR: " + e);
            }
        }
    };

//    private Emitter.Listener onRefreshPending = new Emitter.Listener()  {
//        @Override
//        public void call(final Object... args) {
//            try {
//                ChallengeController cc = new ChallengeController(context, activity, 0, 0, 0);
//                cc.refreshCardsForPendingDuel();
//                Log.i(TAG, "Sockets: generateNewChallenge success!");
//            } catch (Exception e) {
//                Log.i(TAG, "Sockets: ERROR: " + e);
//            }
//        }
//    };


}
