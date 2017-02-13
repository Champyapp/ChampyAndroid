package com.azinecllc.champy.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.StrictMode;

import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.interfaces.NewUser;
import com.azinecllc.champy.model.friend.Datum;
import com.azinecllc.champy.model.friend.Friend;
import com.azinecllc.champy.model.friend.Friend_;
import com.azinecllc.champy.model.friend.Owner;
import com.azinecllc.champy.model.user.Data;
import com.azinecllc.champy.model.user.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;

/**
 * Class helper to get user who has installed champy
 */
public class CHGetFacebookFriends {

    private Retrofit retrofit;
    private SQLiteDatabase db;

    public CHGetFacebookFriends(Context context, Retrofit retrofit) {
        this.retrofit = retrofit;
        DBHelper dbHelper = DBHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();
    }

    // OTHER TABLE. method which get friends and their data
    public void getUserFacebookFriends(final String gcm) {
        NewUser newUser = retrofit.create(NewUser.class);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        db.delete("mytable", null, null);
        ContentValues cv = new ContentValues();
        CHCheckTableForExist checkTableForExist = new CHCheckTableForExist(db);
        GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray array, GraphResponse response) {
                for (int i = 0; i < array.length(); i++) {
                    try {
                        final String fb_id = array.getJSONObject(i).getString("id");
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
                                    String photo;
                                    if (data.getPhoto() != null) {
                                        photo = API_URL + data.getPhoto().getMedium();
                                    } else {
                                        Uri uri = Uri.parse("android.resource://com.azinecllc.champy/drawable/ic_champy_circle");
                                        photo = uri.toString();
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
                                    }
                                }
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


    public void getUserPending(final String userId, String token) {
        ContentValues cv = new ContentValues();
        db.delete("pending", null, null);
        com.azinecllc.champy.interfaces.Friends friends = retrofit.create(com.azinecllc.champy.interfaces.Friends.class);
        Call<Friend> callGetUserFriends = friends.getUserFriends(userId, token);
        callGetUserFriends.enqueue(new Callback<Friend>() {
            @Override
            public void onResponse(Response<Friend> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Datum> data = response.body().getData();

                    for (int i = 0; i < data.size(); i++) {
                        Datum datum = data.get(i);
                        String status = datum.getStatus().toString();
                        if ((datum.getFriend() != null) && (datum.getOwner() != null) && status.equals("false")) {

                            String photo;
                            if (datum.getOwner().get_id().equals(userId)) {
                                Friend_ recipientFriend = datum.getFriend();
                                photo = (recipientFriend.getPhoto() != null) ? recipientFriend.getPhoto().getMedium() : "";
                                cv.put("name", recipientFriend.getName());
                                cv.put("photo", photo);
                                cv.put("user_id", recipientFriend.getId());
                                cv.put("inProgressChallengesCount", recipientFriend.getInProgressChallengesCount());
                                cv.put("allChallengesCount", recipientFriend.getAllChallengesCount());
                                cv.put("successChallenges", recipientFriend.getSuccessChallenges());
                                cv.put("owner", "false");
                                db.insert("pending", null, cv);
                            } else {
                                Owner ownerFriend = datum.getOwner();
                                photo = (ownerFriend.getPhoto() != null) ? ownerFriend.getPhoto().getMedium() : "";
                                cv.put("name", ownerFriend.getName());
                                cv.put("photo", photo);
                                cv.put("user_id", ownerFriend.get_id());
                                cv.put("inProgressChallengesCount", ownerFriend.getInProgressChallengesCount());
                                cv.put("allChallengesCount", ownerFriend.getAllChallengesCount());
                                cv.put("successChallenges", ownerFriend.getSuccessChallenges());
                                cv.put("owner", "true");
                                db.insert("pending", null, cv);
                            }

                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });


    }


}
