package com.example.ivan.champy_v2.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ivan.champy_v2.Friend;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.adapter.OtherAdapter;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHCheckTableForExist;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.User.Data;
import com.example.ivan.champy_v2.model.User.User;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

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
 * Класс отвечает за OTHER в разделе FriendsActivity
 */
public class OtherFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String TAG = "OtherFragment";
    private CHCheckTableForExist checkTableForExist;
    private int mPage;
    public View gView;
    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        final List<Friend> friends = new ArrayList<Friend>();
        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        checkTableForExist = new CHCheckTableForExist(getContext());
        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        final String id = user.get("id");

        Cursor c = db.query("mytable", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int challenges = c.getColumnIndex("challenges");
            int photoColIndex = c.getColumnIndex("photo");
            int nameColIndex = c.getColumnIndex("name");
            int idColIndex = c.getColumnIndex("id");
            int index = c.getColumnIndex("user_id");
            int total = c.getColumnIndex("total");
            int level = c.getColumnIndex("level");
            int wins = c.getColumnIndex("wins");
            do {
                if (!checkTableForExist.isInOtherTable(c.getString(index)))
                    friends.add(new Friend(
                            c.getString(nameColIndex),
                            c.getString(photoColIndex),
                            c.getString(index),
                            c.getString(challenges),
                            c.getString(wins),
                            c.getString(total),
                            c.getString(level)));
            } while (c.moveToNext());
        }
        c.close();

        final RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        final OtherAdapter adapter = new OtherAdapter(friends, getContext(), getActivity());

        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView(swipeRefreshLayout, gView);
            }
        });
        this.gView = view;

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void refreshView(final SwipeRefreshLayout swipeRefreshLayout, final View view) {
        // Проверка на оффлайн вкладке OTHERS
        OfflineMode offlineMode = new OfflineMode();
        offlineMode.isConnectedToRemoteAPI(getActivity());
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                final String API_URL = "http://46.101.213.24:3007";
                final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
                final NewUser newUser = retrofit.create(NewUser.class);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                DBHelper dbHelper = new DBHelper(getActivity());
                final SQLiteDatabase db = dbHelper.getWritableDatabase();
                int clearCount = db.delete("mytable", null, null);
                final ContentValues cv = new ContentValues();
                final List<Friend> newFriends = new ArrayList<>();

                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
                    final GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                        @Override
                        public void onCompleted(JSONArray  array, GraphResponse response) {
                            if (array.length() == 0) {
                                Toast.makeText(getContext(), "No one of your friends has not installed Champy!", Toast.LENGTH_SHORT).show();
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            for (int i = 0; i < array.length(); i++) {
                                try {
                                    // jwt - Json Web Token...
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
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                String name = data.getName();
                                                cv.put("name", name);
                                                cv.put("photo", photo);
                                                cv.put("challenges", "" + data.getAllChallengesCount());
                                                cv.put("wins", "" + data.getSuccessChallenges());
                                                cv.put("total", "" + data.getScore());
                                                cv.put("level", "" + data.getLevel().getNumber());

                                                // отображаем друзей в списке
                                                if (!checkTableForExist.isInOtherTable(data.get_id())) {
                                                    db.insert("mytable", null, cv);
                                                    newFriends.add(new Friend(
                                                            name,
                                                            photo,
                                                            data.get_id(),
                                                            "" + data.getAllChallengesCount(),
                                                            "" + data.getSuccessChallenges(),
                                                            "" + data.getScore(),
                                                            "" + data.getLevel().getNumber()
                                                    ));
                                                } else {
                                                    Log.d("stat", "DBase: not added " + user_name);
                                                }
                                                swipeRefreshLayout.setRefreshing(false);
                                            } /*else {
                                                // отображение всего у человека, который не установил champy
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
                                                cv.put("challenges", "0");
                                                cv.put("wins", "0");
                                                cv.put("total", "0");
                                                cv.put("level", "0");
                                                newFriends.add(new Friend(user_name, photo, null, "0", "0", "0", "0"));
                                                db.insert("mytable", null, cv);

                                                RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
                                                OtherAdapter adapter1 = new OtherAdapter(newFriends, getContext(), getActivity());
                                                rvContacts.setAdapter(adapter1);
                                                swipeRefreshLayout.setRefreshing(false);
                                            }*/
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
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });
        Log.d(TAG, "refreshFriendsView: finished");
    }

    // this method works like "if not in "pending" & "friends" -> insert into "other"



//    public static OtherFragment newInstance(int page) {
//        Bundle args = new Bundle();
//        args.putInt(ARG_PAGE, page);
//        OtherFragment fragment = new OtherFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }


}