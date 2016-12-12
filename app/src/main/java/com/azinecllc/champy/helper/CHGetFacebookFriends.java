package com.azinecllc.champy.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;

import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.interfaces.NewUser;
import com.azinecllc.champy.model.user.Data;
import com.azinecllc.champy.model.user.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;

/**
 * Class helper to get user who has installed champy
 */
public class CHGetFacebookFriends {

    private Context context;

    public CHGetFacebookFriends(Context context) {
        this.context = context;
    }

    // OTHER TABLE. method which get friends and their data
    public void getUserFacebookFriends(final String gcm) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        final NewUser newUser = retrofit.create(NewUser.class);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        DBHelper dbHelper = DBHelper.getInstance(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("mytable", null, null);
        final ContentValues cv = new ContentValues();
        final CHCheckTableForExist checkTableForExist = new CHCheckTableForExist(db);
        final GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray array, GraphResponse response) {
                for (int i = 0; i < array.length(); i++) {
                    try {
                        final String fb_id = array.getJSONObject(i).getString("id");
                        //final String user_name = array.getJSONObject(i).getString("name");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("facebookId", fb_id);
                        jsonObject.put("AndroidOS", gcm);
                        String string = jsonObject.toString();

                        final String jwtString = Jwts.builder()
                                .setHeaderParam("alg", "HS256")
                                .setHeaderParam("typ", "JWT")
                                .setPayload(string)
                                .signWith(SignatureAlgorithm.HS256, "secret")
                                .compact();

                        Call<User> call = newUser.getUserInfo(jwtString);
                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Response<User> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    Data data = response.body().getData();
                                    String photo = "";
                                    if (data.getPhoto() != null) {
                                        photo = API_URL + data.getPhoto().getMedium();
                                    } else {
                                        try {
                                            URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                            photo = profile_pic.toString();
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    final String name = data.getName();
                                    cv.put("name", name);
                                    cv.put("photo", photo);
                                    cv.put("user_id", data.get_id());
                                    cv.put("challenges", data.getAllChallengesCount());
                                    cv.put("wins", data.getSuccessChallenges());
                                    cv.put("total", data.getInProgressChallenges());
                                    cv.put("level", data.getLevel().getNumber());

                                    if (!checkTableForExist.isInOtherTable(data.get_id())) {
                                        db.insert("mytable", null, cv);
//                                        Log.d("GetFacebookFriends", "GetUserFriendsInfo | DBase: vse okay! " + name + " in other");
//                                    } else {
//                                        Log.d("GetFacebookFriends", "GetUserFriendsInfo | DBase: " + name + " in pending or friends");
                                    }
                                }
//                                else {
//                                    Log.d("AppSync", "GetUserFriendsInfo | onResponse: " + response.message());
//                                    URL profile_pic;
//                                    String photo = "";
//                                    try {
//                                        profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
//                                        photo = profile_pic.toString();
//                                    } catch (MalformedURLException e) { e.printStackTrace(); }
//                                    cv.put("name", user_name);
//                                    cv.put("photo", photo);
//                                    cv.put("challenges", "0");
//                                    cv.put("wins", "0");
//                                    cv.put("total", "0");
//                                    cv.put("level", "0");
//                                    db.insert("mytable", null, cv);
//                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {}
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        request.executeAndWait();
    }





}
