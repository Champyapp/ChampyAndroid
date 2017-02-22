//package com.azinecllc.champy.service;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.util.Log;
//
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;
//
//import java.net.URISyntaxException;
//
//import static com.azinecllc.champy.utils.Constants.API_URL;
//
///**
// * Created by SashaKhyzhun on 2/22/17.
// */
//
//public class MySocketService extends Service {
//
//    private static final String TAG = "MySocketService";
////    private Socket mSocket;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        Log.i(TAG, "onBind: ");
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        Log.i(TAG, "onCreate: ");
//        super.onCreate();
////        try {
////            mSocket = IO.socket(API_URL);
////        } catch (URISyntaxException e) {
////            throw new RuntimeException(e);
////        }
////
////        mSocket.connect();
//    }
//
//    @Override
//    public void onDestroy() {
//        Log.i(TAG, "onDestroy: ");
//        super.onDestroy();
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        Log.i(TAG, "onUnbind: ");
//        return super.onUnbind(intent);
//    }
//
//}
