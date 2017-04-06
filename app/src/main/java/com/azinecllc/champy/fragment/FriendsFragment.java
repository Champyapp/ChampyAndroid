package com.azinecllc.champy.fragment;

import android.content.ContentValues;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.FriendsAdapter;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.helper.CHCheckTableForExist;
import com.azinecllc.champy.interfaces.NewUser;
import com.azinecllc.champy.interfaces.RecyclerFriendsClickListener;
import com.azinecllc.champy.model.FriendModel;
import com.azinecllc.champy.model.user.Data;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;

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

import static com.azinecllc.champy.utils.Constants.API_URL;

public class FriendsFragment extends Fragment {

    public static final String TAG = "FriendsFragment";
    private static final String ARG_PAGE = "ARG_PAGE";
    private SwipeRefreshLayout gSwipeRefreshLayout;
    private CHCheckTableForExist checkTableForExist;
    private SessionManager sessionManager;
    private List<FriendModel> friendsList;
    private OfflineMode offlineMode;
    private RecyclerView rvContacts;
    private FriendsAdapter adapter;
    private SQLiteDatabase db;
    private Retrofit retrofit;
    private DBHelper dbHelper;
    private ContentValues cv;
    private View gView;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getContext());
        offlineMode = OfflineMode.getInstance();
        sessionManager = SessionManager.getInstance(getContext());
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        cv = new ContentValues();
        dbHelper = DBHelper.getInstance(getContext());
        db = dbHelper.getWritableDatabase();
        checkTableForExist = new CHCheckTableForExist(db);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.item_recycler_friends, container, false);

        friendsList = new ArrayList<>();
        Cursor c = db.query("mytable", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex("name");
            int photoColIndex = c.getColumnIndex("photo");
            int index = c.getColumnIndex("user_id");
            int challenges = c.getColumnIndex("challenges");
            int wins = c.getColumnIndex("wins");
            int total = c.getColumnIndex("total");
            int level = c.getColumnIndex("level");
            int idColIndex = c.getColumnIndex("id");
            do {
                if (!checkTableForExist.isInOtherTable(c.getString(index)))
                    friendsList.add(new FriendModel(
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


        rvContacts = (RecyclerView) itemView.findViewById(R.id.recycler_view);
        adapter = new FriendsAdapter(friendsList, getContext(), getActivity());

        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);

        gSwipeRefreshLayout = (SwipeRefreshLayout) itemView.findViewById(R.id.swipe_to_refresh);
        gSwipeRefreshLayout.setOnRefreshListener(() -> refreshOtherView(gSwipeRefreshLayout, gView));
        this.gView = itemView;

        ///////////////////////////////////////////////////////////////////////////////////////////
        //  need to check sessionManager.isLoggedInWithFacebook                                  //
        LoginButton loginButton = (LoginButton) itemView.findViewById(R.id.login_button);
        TextView tvConnectWithFB = (TextView) itemView.findViewById(R.id.text_view_connect_facebook);
        if (friendsList.isEmpty()) {
            loginButton.setVisibility(View.VISIBLE);
            tvConnectWithFB.setVisibility(View.VISIBLE);
            gSwipeRefreshLayout.setEnabled(false);
            loginButton.setOnClickListener(v -> Toast.makeText(getContext(), "Login", Toast.LENGTH_SHORT).show());
        } else {
            loginButton.setVisibility(View.GONE);
            tvConnectWithFB.setVisibility(View.GONE);
            gSwipeRefreshLayout.setEnabled(true);
        }
        ///////////////////////////////////////////////////////////////////////////////////////////


        if (sessionManager.getRefreshOthers().equals("true")) {
            refreshOtherView(gSwipeRefreshLayout, gView);
        }

        return itemView;

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: ");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
    }




    private void refreshOtherView(SwipeRefreshLayout swipeRefreshLayout, View view) {
        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    friendsList.clear();
                    NewUser newUser = retrofit.create(NewUser.class);
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("mytable", null, null);
                    GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                        @Override
                        public void onCompleted(JSONArray array, GraphResponse response) {
                            if (array.length() == 0) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            for (int i = 0; i < array.length(); i++) {
                                try {
                                    String fb_id = array.getJSONObject(i).getString("id");
                                    String jwtString = Jwts.builder()
                                            .setHeaderParam("alg", "HS256")
                                            .setHeaderParam("typ", "JWT")
                                            .setPayload("{\n" + "  \"facebookId\": \"" + fb_id + "\"\n" + "}")
                                            .signWith(SignatureAlgorithm.HS256, "secret")
                                            .compact();

                                    Call<User> call = newUser.getUserInfo(jwtString);
                                    call.enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(Response<User> response, Retrofit r) {
                                            if (response.isSuccess()) {
                                                Data data = response.body().getData();
                                                String photo = null;

                                                if (data.getPhoto() != null) {
                                                    photo = API_URL + data.getPhoto().getMedium();
                                                } else {
                                                    try {
                                                        URL profile_pic = new URL(
                                                                "https://graph.facebook.com/"
                                                                        + fb_id
                                                                        + "/picture?type=large");
                                                        photo = profile_pic.toString();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                String name = data.getName();
                                                cv.put("user_id", data.get_id());
                                                cv.put("name", name);
                                                cv.put("photo", photo);
                                                cv.put("total", data.getAllChallengesCount().toString());
                                                cv.put("wins", data.getSuccessChallenges().toString());
                                                cv.put("challenges", data.getInProgressChallenges().toString());
                                                cv.put("level", data.getLevel().getNumber().toString());

                                                // отображаем друзей в списке
                                                if (!checkTableForExist.isInOtherTable(data.get_id())) {
                                                    db.insert("mytable", null, cv);
                                                    friendsList.add(new FriendModel(name, photo, data.get_id(),
                                                            data.getInProgressChallenges().toString(),
                                                            data.getSuccessChallenges().toString(),
                                                            data.getAllChallengesCount().toString(),
                                                            data.getLevel().getNumber().toString()
                                                    ));
                                                }

                                                rvContacts.setAdapter(adapter);
                                                swipeRefreshLayout.setRefreshing(false);
                                            }

                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            Toast.makeText(getContext(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    request.executeAsync();
                }
            });
        }

        swipeRefreshLayout.setRefreshing(false);
        sessionManager.setRefreshOthers("false");
    }




}