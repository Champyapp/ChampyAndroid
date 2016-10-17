package com.example.ivan.champy_v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ivan.champy_v2.activity.RoleControllerActivity;
import com.facebook.login.LoginManager;

import java.util.HashMap;

public class SessionManager {

    private static final String IS_USER_LOGIN = "IsUserLoggedIn"; // All Shared Preferences Keys
    private static final String PREFER_NAME = "Champy_pref";  // SharedPreference file name
    private static final String KEY_PATH = "path_to_pic";
    private static final String KEY_ID = "facebook_id"; // Users facebook id (make v
    private static final String KEY_EMAIL = "email"; // Email address (make variable public to access from outside)
    private static final String KEY_NAME = "name"; // Friend_json name (make variable public to access from outside)
    private static final String KEY_GCM = "gcm";
    private int PRIVATE_MODE = 0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    // Constructor
    public SessionManager(Context context){
        this.context = context;
        pref = this.context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
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

        Log.i("YO", "LOGGED ID");

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
        return pref.getString("facebook_id", null);
    }

    //user id
    public String getObjectId() {
        return pref.getString("id", null);
    }

    public String getRefreshPending() {
        return pref.getString("pendingRefresh", null);
    }

    public void set_duel_pending(String count) {
        editor.putString("duel_pending", count);
        editor.commit();
    }

    public String get_duel_pending() {
        return pref.getString("duel_pending", null);
    }

    public void setRefreshFriends(String refresh) {
        editor.putString("friendsRefresh", refresh);
        editor.commit();
    }

    public String getRefreshFriends() {
        return pref.getString("friendsRefresh", null);
    }

    public String getPathToPic() {
        return pref.getString(KEY_PATH, null);
    }

    public String getGCM() {
        return pref.getString(KEY_GCM, null);
    }

    public void setSelfSize(int size) {
        editor.putInt("SelfSize", size);
        editor.commit();
    }

    public int getSelfSize() {
        return pref.getInt("SelfSize", 0);
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
        //Use HashMap to store user credentials
        HashMap<String, String> user = new HashMap<>();
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
        Intent intent = new Intent(activity, RoleControllerActivity.class);
        activity.startActivity(intent);
    }

}
