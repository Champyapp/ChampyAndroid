package com.example.ivan.champy_v2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ivan.champy_v2.interfaces.*;
import com.example.ivan.champy_v2.interfaces.Friends;
import com.example.ivan.champy_v2.model.User.Data;
import com.example.ivan.champy_v2.model.User.User;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public Boolean getContact(String id) {
        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Boolean ok = false;
        Cursor c = db.query("pending", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int photoColIndex = c.getColumnIndex("photo");
            int index = c.getColumnIndex("user_id");
            do {
                String user_id = c.getString(index);
                if (user_id.equals(id)) {
                    ok = true;
                    break;
                }
            } while (c.moveToNext());
        } else
            Log.i("stat", "0 rows");
        c = db.query("friends", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int photoColIndex = c.getColumnIndex("photo");
            int index = c.getColumnIndex("user_id");
            do {
                String user_id = c.getString(index);
                if (user_id.equals(id)) {
                    ok = true;
                    break;
                }
            } while (c.moveToNext());
        } else
            Log.i("stat", "0 rows");
        c.close();
        return ok;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_first, container, false);
        final List<Friend> friends = new ArrayList<Friend>();

        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        final String id = user.get("id");

        Cursor c = db.query("mytable", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int photoColIndex = c.getColumnIndex("photo");
            int index = c.getColumnIndex("user_id");

            do {
                if (!getContact(c.getString(index))) friends.add(new Friend(c.getString(nameColIndex), c.getString(photoColIndex), c.getString(index)));
            } while (c.moveToNext());
        } else
            Log.i("stat", "0 rows");
        c.close();

        /*for (int i=0; i<20; i++)
        {
            friends.add(new Friend("My friend number "+i, "http://loremflickr.com/320/240?random="+(i+1), 0));
        }*/
        Log.i("stat", "Friends :"+friends);

        final RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        final ContactsAdapter adapter = new ContactsAdapter(friends, getContext(), getActivity());
        FloatingActionButton floatingActionButton = (FloatingActionButton)getActivity().findViewById(R.id.imageButton);
        floatingActionButton.attachToRecyclerView(rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);


        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {

                        final String API_URL = "http://46.101.213.24:3007";

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(API_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        final NewUser newUser = retrofit.create(NewUser.class);
                        final com.example.ivan.champy_v2.interfaces.Friends friend = retrofit.create(Friends.class);
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        DBHelper dbHelper = new DBHelper(getActivity());
                        final SQLiteDatabase db = dbHelper.getWritableDatabase();
                        int clearCount = db.delete("mytable", null, null);
                        final ContentValues cv = new ContentValues();
                        final List<Friend> newfriends = new ArrayList<Friend>();


                        final GraphRequest request = GraphRequest.newMyFriendsRequest(
                                AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONArrayCallback() {
                                    @Override
                                    public void onCompleted(JSONArray array, GraphResponse response) {
                                        for (int i = 0; i < array.length(); i++) {
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
                                                        if (response.isSuccess()) {
                                                            Data data = response.body().getData();
                                                            String photo = null;
                                                            cv.put("user_id", data.get_id());
                                                            if (data.getPhoto() != null)
                                                                photo = API_URL + data.getPhoto().getMedium();
                                                            else {
                                                                try {
                                                                    URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                                                    photo = profile_pic.toString();
                                                                } catch (MalformedURLException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                            String name = data.getName();
                                                            cv.put("name", name);
                                                            cv.put("photo", photo);

                                                            Log.i("Users", "user: " + user_name + " photo: " + photo);
                                                            if (!getContact(data.get_id())){
                                                                db.insert("mytable", null, cv);
                                                                newfriends.add(new Friend(name, photo, data.get_id()));
                                                            }
                                                            else
                                                                Log.i("stat", "DBase: not added" + user_name);

                                                        } else {
                                                            URL profile_pic = null;
                                                            String photo = null;
                                                            try {
                                                                profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                                                photo = profile_pic.toString();
                                                            } catch (MalformedURLException e) {
                                                                e.printStackTrace();
                                                            }
                                                            cv.put("name", user_name);
                                                            cv.put("photo", photo);
                                                            Log.i("Users", "user: " + user_name + " photo: " + photo);
                                                            newfriends.add(new Friend(user_name, photo, null));
                                                            db.insert("mytable", null, cv);
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
                                        RecyclerView rvContacts = (RecyclerView)view.findViewById(R.id.rvContacts);
                                        Log.i("stat", "Friends :" + newfriends.toString());
                                        ContactsAdapter adapter1 = new ContactsAdapter(newfriends, getContext(), getActivity());
                                        rvContacts.setAdapter(adapter1);
                                        swipeRefreshLayout.setRefreshing(false);

                                    }
                                });


                        request.executeAsync();
                    }
                });

            }
        });
        Log.i("stat", "DBase: view returned");
        return view;

    }






}