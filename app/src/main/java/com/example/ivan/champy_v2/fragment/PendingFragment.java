package com.example.ivan.champy_v2.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.adapter.OtherAdapter;
import com.example.ivan.champy_v2.adapter.PendingAdapter;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHCheckTableForExist;
import com.example.ivan.champy_v2.helper.CHGetFacebookFriends;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.interfaces.CustomItemClickListener;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.FriendModel;
import com.example.ivan.champy_v2.model.friend.Datum;
import com.example.ivan.champy_v2.model.friend.Friend_;
import com.example.ivan.champy_v2.model.friend.Owner;
import com.example.ivan.champy_v2.model.Pending_friend;
import com.example.ivan.champy_v2.model.user.Data;
import com.example.ivan.champy_v2.model.user.User;
import com.example.ivan.champy_v2.utils.Constants;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;
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

public class PendingFragment extends Fragment {

    public static final String API_URL = "http://46.101.213.24:3007";
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String TAG = "PendingFragment";

    private String id = "", token = "";
    public SwipeRefreshLayout swipeRefreshLayout;
    private CHCheckTableForExist chCheckTableForExist;
    public View gView;

    public int mPage;
    private Socket mSocket;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        CurrentUserHelper currentUser = new CurrentUserHelper(getContext());
        chCheckTableForExist = new CHCheckTableForExist(getContext());
        id = currentUser.getUserObjectId();
        token = currentUser.getToken();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        Log.d(TAG, "onCreateView: ");
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);
        final List<Pending_friend> pendingFriends = new ArrayList<>();

        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("pending", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int photoColIndex = c.getColumnIndex("photo");
            int index = c.getColumnIndex("user_id");
            int owner = c.getColumnIndex("owner");
            int inProgressChallengesCountIndex = c.getColumnIndex("inProgressChallengesCount");
            int successChallenges = c.getColumnIndex("successChallenges");
            int allChallengesCount = c.getColumnIndex("allChallengesCount");

            do {
                pendingFriends.add(new Pending_friend(
                                c.getString(nameColIndex),
                                API_URL+c.getString(photoColIndex),
                                c.getString(index),
                                c.getString(owner),
                                c.getString(successChallenges),
                                c.getString(allChallengesCount),
                                c.getString(inProgressChallengesCountIndex)
                        )
                );

            } while (c.moveToNext());
        }
        c.close();

        final RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        final PendingAdapter adapter = new PendingAdapter(pendingFriends, getContext(), getActivity(), new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Pending_friend friend = pendingFriends.get(position);
            }
        });

        SessionManager sessionManager = new SessionManager(getActivity());
        String checkRefresh = sessionManager.getRefreshPending();


        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPendingView(swipeRefreshLayout, gView);
            }
        });
        this.gView = view;

        Bundle friendRequestExtra = getActivity().getIntent().getExtras();
        if (friendRequestExtra != null) {
            refreshPendingView(swipeRefreshLayout, gView);
        }

        if (checkRefresh.equals("true")) {
            refreshPendingView(swipeRefreshLayout, gView);
            sessionManager.setRefreshPending("false");
        }

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

        try {
            mSocket = IO.socket("http://46.101.213.24:3007");
            Log.d(TAG, "onStart: Sockets are connected!");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.on("connect", onConnect);
        mSocket.on("connected", onConnected);

        mSocket.on("Relationship:new", modifiedRelationship);
        mSocket.on("Relationship:new:accepted", modifiedRelationship);
        mSocket.on("Relationship:new:removed", removedRelationship);
        mSocket.on("Relationship:accepted", modifiedRelationship);

        mSocket.on("Relationship:created", modifiedRelationship);
        mSocket.on("Relationship:created:accepted", modifiedRelationship);
        mSocket.on("Relationship:created:removed", removedRelationship);
        mSocket.connect();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Sockets off & disconnect");
        mSocket.off();
        mSocket.disconnect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }


    private void refreshPendingView(final SwipeRefreshLayout swipeRefreshLayout, final View view) {
        swipeRefreshLayout.setRefreshing(true);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("pending", null, null);
        final ContentValues cv = new ContentValues();
        OfflineMode offlineMode = new OfflineMode();
        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);

        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
            Call<com.example.ivan.champy_v2.model.friend.Friend> call = friends.getUserFriends(id, token);
            call.enqueue(new Callback<com.example.ivan.champy_v2.model.friend.Friend>() {
                @Override
                public void onResponse(Response<com.example.ivan.champy_v2.model.friend.Friend> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        List<Datum> data = response.body().getData();
                        // get data from response
                        for (int i = 0; i < data.size(); i++) {
                            Datum datum = data.get(i);
                            if ((datum.getFriend() != null) && (datum.getOwner() != null) && datum.getStatus().toString().equals("false")) {
                                String status;
                                if (datum.getOwner().get_id().equals(id)) {
                                    status = "false";
                                    Friend_ friend = datum.getFriend();
                                    cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.getId());
                                    cv.put("inProgressChallengesCount", friend.getInProgressChallengesCount());
                                    cv.put("allChallengesCount", friend.getAllChallengesCount());
                                    cv.put("successChallenges", friend.getSuccessChallenges());
                                    cv.put("owner", status);
                                } else {
                                    status = "true";
                                    Owner friend = datum.getOwner();
                                    cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.get_id());
                                    cv.put("inProgressChallengesCount", friend.getInProgressChallengesCount());
                                    cv.put("allChallengesCount", friend.getAllChallengesCount());
                                    cv.put("successChallenges", friend.getSuccessChallenges());
                                    cv.put("owner", status);
                                }

                                db.insert("pending", null, cv);

                            }
                        }
                        // put data in list
                        final List<Pending_friend> newfriends = new ArrayList<>();
                        Cursor c = db.query("pending", null, null, null, null, null, null);
                        if (c.moveToFirst()) {
                            int idColIndex = c.getColumnIndex("id");
                            int nameColIndex = c.getColumnIndex("name");
                            int photoColIndex = c.getColumnIndex("photo");
                            int index = c.getColumnIndex("user_id");
                            int owner = c.getColumnIndex("owner");
                            int inProgressChallengesCountIndex = c.getColumnIndex("inProgressChallengesCount");
                            int successChallenges = c.getColumnIndex("successChallenges");
                            int allChallengesCount = c.getColumnIndex("allChallengesCount");
                            do {
                                newfriends.add(new Pending_friend(
                                        c.getString(nameColIndex),
                                        API_URL + c.getString(photoColIndex),
                                        c.getString(index),
                                        c.getString(owner),
                                        "" + c.getString(successChallenges),
                                        "" + c.getString(allChallengesCount),
                                        "" + c.getString(inProgressChallengesCountIndex)
                                ));
                            } while (c.moveToNext());
                        }
                        c.close();

                        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
                        final PendingAdapter adapter = new PendingAdapter(newfriends, getContext(), getActivity(), new CustomItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Pending_friend friend = newfriends.get(position);
                            }
                        });
                        rvContacts.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Log.d(TAG, "refreshPendingView: finish refreshing");
                }

                @Override
                public void onFailure(Throwable t) { }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    public void refreshOtherView(final SwipeRefreshLayout swipeRefreshLayout, final View view) {
        // Проверка на оффлайн вкладке OTHERS
        final OfflineMode offlineMode = new OfflineMode();
        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    final Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
                    final NewUser newUser = retrofit.create(NewUser.class);
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    DBHelper dbHelper = new DBHelper(getActivity());
                    final SQLiteDatabase db = dbHelper.getWritableDatabase();
                    int clearCount = db.delete("mytable", null, null);
                    final ContentValues cv = new ContentValues();
                    final List<FriendModel> newFriends = new ArrayList<>();

                    if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
                        final GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(JSONArray array, GraphResponse response) {
                                if (array.length() == 0) {
                                    Toast.makeText(getContext(), R.string.noOneHasInstalledChampy, Toast.LENGTH_SHORT).show();
                                    swipeRefreshLayout.setRefreshing(false);
                                    return;
                                }
                                for (int i = 0; i < array.length(); i++) {
                                    try {
                                        // jwt - Json Web Token...
                                        final String fb_id = array.getJSONObject(i).getString("id");
                                        final String user_name = array.getJSONObject(i).getString("name");
                                        final String jwtString = Jwts.builder()
                                                .setHeaderParam("alg", "HS256")
                                                .setHeaderParam("typ", "JWT")
                                                .setPayload("{\n"+"  \"facebookId\": \"" + fb_id + "\"\n" + "}")
                                                .signWith(SignatureAlgorithm.HS256, "secret")
                                                .compact();

                                        Call<User> call = newUser.getUserInfo(jwtString);
                                        call.enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Response<User> response, Retrofit retrofit) {
                                                if (response.isSuccess()) {
                                                    Data data = response.body().getData();
                                                    String photo = null;

                                                    if (data.getPhoto() != null) {
                                                        photo = Constants.API_URL + data.getPhoto().getMedium();
                                                    }
                                                    else {
                                                        try {
                                                            URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                                            photo = profile_pic.toString();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    String name = data.getName();
                                                    cv.put("user_id", data.get_id());
                                                    cv.put("name", name);
                                                    cv.put("photo", photo);
                                                    cv.put("challenges", "" + data.getAllChallengesCount());
                                                    cv.put("wins", "" + data.getSuccessChallenges());
                                                    cv.put("total", "" + data.getInProgressChallenges());
                                                    cv.put("level", "" + data.getLevel().getNumber());

                                                    // отображаем друзей в списке
                                                    if (!chCheckTableForExist.isInOtherTable(data.get_id())) {
                                                        db.insert("mytable", null, cv);
                                                        newFriends.add(new FriendModel(
                                                                name,
                                                                photo,
                                                                data.get_id(),
                                                                "" + data.getAllChallengesCount(),
                                                                "" + data.getSuccessChallenges(),
                                                                "" + data.getInProgressChallenges(),
                                                                "" + data.getLevel().getNumber()
                                                        ));
                                                    } else {
                                                        Log.d(TAG, "DBase: not added | " + user_name + " in another table");
                                                    }
                                                    swipeRefreshLayout.setRefreshing(false);
                                                }
//                                            else {
//                                                // отображение всего у человека, который не установил champy
//                                                URL profile_pic = null;
//                                                String photo = null;
//                                                try {
//                                                    profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
//                                                    photo = profile_pic.toString();
//                                                } catch (MalformedURLException e) {
//                                                    e.printStackTrace();
//                                                }
//                                                cv.put("name", user_name);
//                                                cv.put("photo", photo);
//                                                cv.put("challenges", "0");
//                                                cv.put("wins", "0");
//                                                cv.put("total", "0");
//                                                cv.put("level", "0");
//                                                newFriends.add(new FriendModel(user_name, photo, null, "0", "0", "0", "0"));
//                                                db.insert("mytable", null, cv);
//
//                                                RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
//                                                OtherAdapter adapter1 = new OtherAdapter(newFriends, getContext(), getActivity());
//                                                rvContacts.setAdapter(adapter1);
//                                                gSwipeRefreshLayout.setRefreshing(false);
//                                            }

                                                RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
                                                OtherAdapter otherAdapter = new OtherAdapter(newFriends, getContext(), getActivity());
                                                rvContacts.setAdapter(otherAdapter);
                                                swipeRefreshLayout.setRefreshing(false);
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
            Log.d(TAG, "refreshOtherView: finished");
        }

//    public static OtherFragment newInstance(int page) {
//        Bundle args = new Bundle();
//        args.putInt(ARG_PAGE, page);
//        OtherFragment fragment = new OtherFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }

    }



    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            CurrentUserHelper currentUser = new CurrentUserHelper(getContext());
            mSocket.emit("ready", currentUser.getToken());
            Log.d(TAG, "Sockets: connecting...");
        }
    };
    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, "Sockets: connected!");
        }
    };
    protected Emitter.Listener modifiedRelationship = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshPendingView(swipeRefreshLayout, gView);
                }
            });
            Log.d(TAG, "Sockets: modifiedRelationship");
        }
    };
    protected Emitter.Listener removedRelationship = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshPendingView(swipeRefreshLayout, gView);
                }
            });
        }
    };

    public static PendingFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PendingFragment fragment = new PendingFragment();
        fragment.setArguments(args);
        return fragment;
    }

}