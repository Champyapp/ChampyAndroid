package com.example.ivan.champy_v2.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.model.friend.Datum;
import com.example.ivan.champy_v2.model.friend.Friend;
import com.example.ivan.champy_v2.model.friend.Friend_;
import com.example.ivan.champy_v2.model.friend.Owner;
import com.example.ivan.champy_v2.utils.Constants;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by SashaKhyzhun on 11/23/16.
 */

public class CHGetPendingFriends {

    private Context context;
    private ContentValues cv;
    private SQLiteDatabase db;
    public CHGetPendingFriends(Context context) {
        this.context = context;
    }


    public void getUserPending(final String userId, String token) {
        cv = new ContentValues();
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("pending", null, null);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);
        Call<Friend> callGetUserFriends = friends.getUserFriends(userId, token);
        callGetUserFriends.enqueue(new Callback<Friend>() {
            @Override
            public void onResponse(Response<Friend> response, Retrofit retrofit) {
                if (response.isSuccess()){
                    List<Datum> data = response.body().getData();

                    for (int i = 0; i < data.size(); i++) {
                        Datum datum = data.get(i);
                        String status = datum.getStatus().toString();
                        if ((datum.getFriend() != null) && (datum.getOwner() != null) && status.equals("false")) {

                            if (datum.getOwner().get_id().equals(userId)) {
                                Friend_ recipientFriend = datum.getFriend();
                                cv.put("name", recipientFriend.getName());
                                //if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                //else cv.put("photo", "");
                                String friendPhoto = (recipientFriend.getPhoto() != null) ? recipientFriend.getPhoto().getMedium() : "";
                                cv.put("photo", friendPhoto);
                                cv.put("user_id", recipientFriend.getId());
                                cv.put("inProgressChallengesCount", recipientFriend.getInProgressChallengesCount());
                                cv.put("allChallengesCount", recipientFriend.getAllChallengesCount());
                                cv.put("successChallenges", recipientFriend.getSuccessChallenges());
                                cv.put("owner", "false");
                                db.insert("pending", null, cv);
                            } else {
                                Owner ownerFriend = datum.getOwner();
                                cv.put("name", ownerFriend.getName());
                                //if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                //else cv.put("photo", "");
                                String ownerPhoto = (ownerFriend.getPhoto() != null) ? ownerFriend.getPhoto().getMedium() : "";
                                cv.put("photo", ownerPhoto);
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
            public void onFailure(Throwable t) { }
        });


    }

}
