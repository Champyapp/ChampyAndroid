package com.example.ivan.champy_v2;

import android.util.Log;

import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.User.Data;
import com.example.ivan.champy_v2.model.User.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by ivan on 05.02.16.
 */
public class Friend {
    private String mName;
    private String mPicture;
    private String mID;
   // private List<Friend> friends;

    public Friend(String name, String picture, String ID) {
        mName = name;
        mPicture = picture;
        mID = ID;
    }

    public String getName() {
        return mName;
    }

    public String getPicture() {
        return mPicture;
    }

    public String  getID(){ return  mID; }

    public void setID(String id) { mID = id; }

    private static int lastFriendId = 0;

    /*public List<Friend> getFriends()
    {
        return friends;
    }

    public void add_user(String name, String picture)
    {
        friends.add(new Friend(name, picture));
    }*/

    public static List<Friend> createFriendsList() {
        final String API_URL = "http://46.101.213.24:3007";



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final NewUser newUser = retrofit.create(NewUser.class);

        final List<Friend> friends = new ArrayList<Friend>();
        final GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray array, GraphResponse response) {
                       for (int i=0; i<array.length(); i++){
                           try {
                               final String fb_id = array.getJSONObject(i).getString("id");
                               final String user_name = array.getJSONObject(i).getString("name");
                               final String jwtString = Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setPayload("{\n" +
                                       "  \"facebookId\": \"" + fb_id + "\"\n" +
                                       "}").signWith(SignatureAlgorithm.HS256, "secret").compact();
                               Call<User> call = newUser.getUserInfo(jwtString);
                               call.enqueue(new Callback<User>() {
                                   @Override
                                   public void onResponse(Response<User> response, Retrofit retrofit) {
                                       if (response.isSuccess()){
                                           Data data = response.body().getData();
                                           String photo = null;
                                           if (data.getPhoto() != null) photo = API_URL+data.getPhoto().getMedium();
                                           else {
                                               try {
                                                   URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                                   photo = profile_pic.toString();
                                               } catch (MalformedURLException e) {
                                                   e.printStackTrace();
                                               }

                                           }
                                           String name = data.getName();
                                           friends.add(new Friend(name, photo, data.get_id()));
                                       }
                                       else {
                                           URL profile_pic = null;
                                           String photo = null;
                                           try {
                                               profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                                photo = profile_pic.toString();
                                           } catch (MalformedURLException e) {
                                               e.printStackTrace();
                                           }
                                           friends.add(new Friend(user_name, photo, "not"));

                                       }
                                   }

                                   @Override
                                   public void onFailure(Throwable t) {

                                   }
                               });
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }

                       }
                    }
                });

        request.executeAsync();


        /*for (int i=0; i<20; i++)
        {
            friends.add(new Friend("My friend number "+i, "http://loremflickr.com/320/240?random="+(i+1), 0));
        }*/
        Log.i("stat", "Friends :"+friends);
        return friends;

    }
}
