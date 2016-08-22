package com.example.ivan.champy_v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ivan.champy_v2.activity.LoginActivity;
import com.facebook.login.LoginManager;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "Champy_pref";
    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    // Friend_json name (make variable public to access from outside)
    public static final String KEY_NAME  = "name";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ID    = "facebook_id";
    public static final String KEY_PATH  = "path_to_pic";
    public static final String KEY_GCM  = "gcm";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    //Create login session
    public void createUserLoginSession(String name, String email, String facebook_id, String path_to_pic, String token, String id,
                                       String pushN, String newChallReq, String acceptedYour, String challengeEnd, String updateDB, String gcm) {
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_ID, facebook_id);

        editor.putString(KEY_PATH, path_to_pic);

        editor.putString("token", token);

        editor.putString("id", id);

        editor.putString("pushN", pushN);

        editor.putString("newChallReq", newChallReq);

        editor.putString("acceptedYour", acceptedYour);

        editor.putString("challengeEnd", challengeEnd);

        editor.putString("updateDB", updateDB);

        editor.putString(KEY_GCM, gcm);

        Log.i("YO", "LOGINED");

        // commit changes
        editor.commit();
    }

    public void toogle1(String t){
        editor.putString("pushN", t);
        editor.commit();
    }

    public void toogle2(String t){
        editor.putString("newChallReq", t);
        editor.commit();
    }

    public void toogle3(String t){
        editor.putString("acceptedYour", t);
        editor.commit();
    }

    public void toogle4(String t){
        editor.putString("challengeEnd", t);
        editor.commit();
    }

    public void change_name(String name){
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public void change_avatar(String url){
        editor.putString(KEY_PATH, url);
        editor.commit();
    }

    public void setRefreshPending(String refresh) {
        editor.putString("pendingRefresh", refresh);
        editor.commit();
    }

    public String getFacebookId() {
        String s = pref.getString("facebook_id", null);
        return s;
    }

    public String getRefreshPending() {
        String s = pref.getString("pendingRefresh", null);
        return s;
    }

    public void set_duel_pending(String count) {
        editor.putString("duel_pending", count);
        editor.commit();
    }

    public String get_duel_pending() {
        String s = pref.getString("duel_pending", null);
        return s;
    }

    public void setRefreshFriends(String refresh) {
        editor.putString("friendsRefresh", refresh);
        editor.commit();
    }

    public String getRefreshFriends() {
        String s = pref.getString("friendsRefresh", null);
        return s;
    }

    public String getGCM() {
        String s = pref.getString(KEY_GCM, null);
        return s;
    }

    public void setSelfSize(int size) {
        editor.putInt("SelfSize", size);
        editor.commit();
    }

    public int getSelfSize() {
        int s = pref.getInt("SelfSize", 0);
        return s;
    }

    public void setChampyOptions(String challenges, String wins, String total, String level){
        editor.putString("challenges", challenges);
        editor.putString("wins", wins);
        editor.putString("total", total);
        editor.putString("level", level);
        editor.commit();
    }

    public HashMap<String, String> getChampyOptions(){
        HashMap<String, String> champy = new HashMap<String, String>();
        champy.put("challenges", pref.getString("challenges", null));
        champy.put("wins",       pref.getString("wins",       null));
        champy.put("total",      pref.getString("total",      null));
        champy.put("level",      pref.getString("level",      null));
        return champy;
    }

    public HashMap<String, String> getUserDetails() {
        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME,       pref.getString(KEY_NAME,       null));
        user.put(KEY_EMAIL,      pref.getString(KEY_EMAIL,      null));
        user.put(KEY_ID,         pref.getString(KEY_EMAIL,      null));
        user.put(KEY_PATH,       pref.getString(KEY_PATH,       null));
        user.put("token",        pref.getString("token",        null));
        user.put("id",           pref.getString("id",           null));
        user.put("pushN",        pref.getString("pushN",        null));
        user.put("newChallReq",  pref.getString("newChallReq",  null));
        user.put("acceptedYour", pref.getString("acceptedYour", null));
        user.put("challengeEnd", pref.getString("challengeEnd", null));
        user.put("updateDB",     pref.getString("updateDB",     null));

        return user;
    }

    public void logoutUser(){
        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();
        // After logout redirect user to Login Activity
    }

    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public void logout(Activity activity) {
        LoginManager.getInstance().logOut();
        logoutUser();
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    public void change_token(String token){
        editor.putString("token", token);
        editor.commit();
    }

    public boolean checkLogin(){
        // Check login status
        if(!this.isUserLoggedIn()){

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

            return true;
        }
        return false;
    }

    public void setUpdateFalse() {
        editor.putString("updateDB", "false");
        editor.commit();
    }


}
