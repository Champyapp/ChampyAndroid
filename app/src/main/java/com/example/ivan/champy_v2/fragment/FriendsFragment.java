package com.example.ivan.champy_v2.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.adapter.FriendsAdapter;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.interfaces.CustomItemClickListener;
import com.example.ivan.champy_v2.model.friend.Datum;
import com.example.ivan.champy_v2.model.friend.Friend_;
import com.example.ivan.champy_v2.model.friend.Owner;
import com.example.ivan.champy_v2.model.FriendModel;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class FriendsFragment extends Fragment {

    private static final String API_URL = "http://46.101.213.24:3007";
    private static final String TAG = "FriendsFragment";
    private static final String ARG_PAGE = "ARG_PAGE";

    private View gView;
    private SwipeRefreshLayout gSwipeRefreshLayout;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        Log.d(TAG, "onCreateView: ");
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);
        final List<FriendModel> friends = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("friends", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int inProgressChallengesCountIndex = c.getColumnIndex("inProgressChallengesCount");
            int allChallengesCount = c.getColumnIndex("allChallengesCount");
            int successChallenges = c.getColumnIndex("successChallenges");
            int photoColIndex = c.getColumnIndex("photo");
            int nameColIndex = c.getColumnIndex("name");
            int level = c.getColumnIndex("level");
            int index = c.getColumnIndex("user_id");
            int idColIndex = c.getColumnIndex("id");
            do {
                friends.add(new FriendModel(
                        c.getString(nameColIndex),
                        API_URL + c.getString(photoColIndex),
                        c.getString(index),
                        c.getString(inProgressChallengesCountIndex),
                        c.getString(successChallenges),
                        c.getString(allChallengesCount),
                        c.getString(level)));
            } while (c.moveToNext());
        }
        c.close();

        final RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        final FriendsAdapter adapter = new FriendsAdapter(friends, getContext(), getActivity(), new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FriendModel friend = friends.get(position);
            }
        });


        SessionManager sessionManager = new SessionManager(getActivity());
        //sessionManager.setRefreshFriends("true");
        String checkRefresh = sessionManager.getRefreshFriends();

        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);


        gSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
        gSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFriendsView(gSwipeRefreshLayout, gView);
            }
        });
        this.gView = view;

//        refreshFriendsView(gSwipeRefreshLayout, gView);

        Bundle friendRequestExtra = getActivity().getIntent().getExtras();
        if (friendRequestExtra != null) {
            refreshFriendsView(gSwipeRefreshLayout, gView);
        }

        if (checkRefresh.equals("true")) {
            refreshFriendsView(gSwipeRefreshLayout, gView);
            sessionManager.setRefreshFriends("false");
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

        mSocket.on("Relationship:new:accepted", modifiedRelationship);
        mSocket.on("Relationship:new:removed", modifiedRelationship);
        mSocket.on("Relationship:created:accepted", modifiedRelationship);
        mSocket.on("Relationship:created:removed", modifiedRelationship);

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


    private void refreshFriendsView(final SwipeRefreshLayout swipeRefreshLayout, final View view) {
        swipeRefreshLayout.setRefreshing(true);
        final String API_URL = "http://46.101.213.24:3007";
        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        final String id = user.get("id");
        String token = user.get("token");
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("friends", null, null);
        final ContentValues cv = new ContentValues();

        final com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);

        // Проверка на оффлайн вкладке FriendsActivity
        OfflineMode offlineMode = new OfflineMode();
        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {

            Call<com.example.ivan.champy_v2.model.friend.Friend> call = friends.getUserFriends(id, token);
            call.enqueue(new Callback<com.example.ivan.champy_v2.model.friend.Friend>() {
                @Override
                public void onResponse(Response<com.example.ivan.champy_v2.model.friend.Friend> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        List<Datum> data = response.body().getData();

                        // get friends from response.
                        for (int i = 0; i < data.size(); i++) {
                            Datum datum = data.get(i);
                            if (datum.getFriend() != null && datum.getStatus().toString().equals("true")) {
                                if (datum.getOwner().get_id().equals(id)) {
                                    Friend_ friend = datum.getFriend();
                                    cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.getId());
                                    cv.put("inProgressChallengesCount", friend.getInProgressChallengesCount());
                                    cv.put("allChallengesCount", friend.getAllChallengesCount());
                                    cv.put("successChallenges", friend.getSuccessChallenges());
                                    db.insert("friends", null, cv);
                                } else {
                                    Owner friend = datum.getOwner();
                                    cv.put("name", friend.getName());
                                    if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                    else cv.put("photo", "");
                                    cv.put("user_id", friend.get_id());
                                    cv.put("inProgressChallengesCount", friend.getInProgressChallengesCount());
                                    cv.put("allChallengesCount", friend.getAllChallengesCount());
                                    cv.put("successChallenges", friend.getSuccessChallenges());
                                    db.insert("friends", null, cv);
                                }
                            }
                        }

                        // put friends from response on screen
                        final List<FriendModel> newfriends = new ArrayList<>();
                        Cursor c = db.query("friends", null, null, null, null, null, null);
                        if (c.moveToFirst()) {
                            int inProgressChallengesCountIndex = c.getColumnIndex("inProgressChallengesCount");
                            int allChallengesCount = c.getColumnIndex("allChallengesCount");
                            int successChallenges = c.getColumnIndex("successChallenges");
                            int photoColIndex = c.getColumnIndex("photo");
                            int nameColIndex = c.getColumnIndex("name");
                            int idColIndex = c.getColumnIndex("id");
                            int index = c.getColumnIndex("user_id");
                            int level = c.getColumnIndex("level");
                            do {
                                newfriends.add(new FriendModel(
                                        c.getString(nameColIndex),
                                        API_URL + c.getString(photoColIndex),
                                        c.getString(index),
                                        "" + c.getString(inProgressChallengesCountIndex),
                                        "" + c.getString(successChallenges),
                                        "" + c.getString(allChallengesCount),
                                        "0"));
                            } while (c.moveToNext());
                        }
                        c.close();

                        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
                        final FriendsAdapter adapter = new FriendsAdapter(newfriends, getContext(), getActivity(), new CustomItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                FriendModel friend = newfriends.get(position);
                            }
                        });

                        rvContacts.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Log.d(TAG, "refreshFriendsView: finish refreshing");
                }

                @Override
                public void onFailure(Throwable t) { }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
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
                    refreshFriendsView(gSwipeRefreshLayout, gView);
                }});
            Log.d(TAG, "Sockets: modifiedRelationship");
        }
    };

//    public static FriendsFragment newInstance(int page) {
//        Bundle args = new Bundle();
//        args.putInt(ARG_PAGE, page);
//        FriendsFragment fragment = new FriendsFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }

//    public void UpdateFriendsList() {
//        final String API_URL = "http://46.101.213.24:3007";
//        SessionManager sessionManager = new SessionManager(getActivity());
//        HashMap<String, String> user;
//        user = sessionManager.getUserDetails();
//        final String id = user.get("id");
//        String token = user.get("token");
//        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
//        DBHelper dbHelper = new DBHelper(getActivity());
//        final SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.delete("friends", null, null);
//        final ContentValues cv = new ContentValues();
//
//        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);
//        Call<com.example.ivan.champy_v2.model.FriendModel.FriendModel> call = friends.getUserFriends(id, token);
//        call.enqueue(new Callback<com.example.ivan.champy_v2.model.FriendModel.FriendModel>() {
//            @Override
//            public void onResponse(Response<com.example.ivan.champy_v2.model.FriendModel.FriendModel> response, Retrofit retrofit) {
//                if (response.isSuccess()) {
//                    List<Datum> data = response.body().getData();
//                    for (int i = 0; i < data.size(); i++) {
//                        Datum datum = data.get(i);
//
//                        if (datum.getFriend() != null) {
//                            if (datum.getStatus().toString().equals("true")) {
//                                if (datum.getOwner().get_id().equals(id)) {
//
//                                    Friend_ friend = datum.getFriend();
//                                    cv.put("name", friend.getName());
//                                    if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
//                                    else cv.put("photo", "");
//                                    cv.put("user_id", friend.getId());
//                                    db.insert("friends", null, cv);
//
//                                } else {
//                                    Owner friend = datum.getOwner();
//                                    cv.put("name", friend.getName());
//
//                                    if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
//                                    else cv.put("photo", "");
//                                    cv.put("user_id", friend.get_id());
//                                    db.insert("friends", null, cv);
//                                }
//                            }
//                        }
//                    }
//                    final List<com.example.ivan.champy_v2.model.FriendModel> newfriends = new ArrayList<>();
//                    Cursor c = db.query("friends", null, null, null, null, null, null);
//                    if (c.moveToFirst()) {
//                        int idColIndex = c.getColumnIndex("id");
//                        int nameColIndex = c.getColumnIndex("name");
//                        int photoColIndex = c.getColumnIndex("photo");
//                        int index = c.getColumnIndex("user_id");
//                        do {
//                            Log.i("newusers", "NewUser: " + c.getString(nameColIndex) + " Photo: " + c.getString(photoColIndex));
//                            newfriends.add(new com.example.ivan.champy_v2.model.FriendModel(
//                                    c.getString(nameColIndex),
//                                    API_URL + c.getString(photoColIndex),
//                                    c.getString(index),
//                                    "0", "0", "0" ,"0"));
//                        } while (c.moveToNext());
//                    }
//                    c.close();
//
//                    Log.i("stat", "FriendsActivity :" + newfriends.toString());
//
//                    RecyclerView rvContacts = (RecyclerView) getActivity().findViewById(R.id.rvContacts);
//                    final FriendsAdapter adapter = new FriendsAdapter(newfriends, getContext(), getActivity(), new CustomItemClickListener() {
//                        @Override
//                        public void onItemClick(View view, int position) {
//                            com.example.ivan.champy_v2.model.FriendModel friend = newfriends.get(position);
//                        }
//                    });
//                    rvContacts.setAdapter(adapter);
//                    rvContacts.getAdapter().notifyDataSetChanged();
//                    rvContacts.invalidate();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//
//            }
//        });
//    }

}