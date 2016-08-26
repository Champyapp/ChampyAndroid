package com.example.ivan.champy_v2.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ivan.champy_v2.activity.FriendsActivity;
import com.example.ivan.champy_v2.adapter.FriendsAdapter;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.interfaces.CustomItemClickListener;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.adapter.PendingAdapter;
import com.example.ivan.champy_v2.model.Pending_friend;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.model.Friend.Datum;
import com.example.ivan.champy_v2.model.Friend.Friend_;
import com.example.ivan.champy_v2.model.Friend.Owner;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class PendingFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    final String API_URL = "http://46.101.213.24:3007";

    public View gView;
    public SwipeRefreshLayout gSwipeRefreshlayout;
    private int mPage;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://46.101.213.24:3007");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshView(gSwipeRefreshlayout, gView);
        }
    };


    public static PendingFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PendingFragment fragment = new PendingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Comm:", "onDestroy: viszlat");
    }



    @Override
    public void onStop() {
        super.onStop();
        mSocket.disconnect();
        Log.i("Comm:", "onDestroy: viszlat");
    }

    //
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            CurrentUserHelper currentUser = new CurrentUserHelper(getContext());
            mSocket.emit("ready", currentUser.getToken());
            Log.i("call", "call: minden fasza");
        }
    };
    //
    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i("call", "call: connected okay");
        }
    };
    //
    protected Emitter.Listener modifiedRelationship = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {

            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshView(gSwipeRefreshlayout, gView);
                    }
                });




            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("call", "new friend request");
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        Log.i("stat", "Created Pending");
        final View view = inflater.inflate(R.layout.fragment_first, container, false);
        final List<Pending_friend> friends = new ArrayList<>();

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
                Log.i("stat", "Status: "+c.getString(photoColIndex));
                friends.add(new Pending_friend(
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

        Log.i("stat", "Friends :" + friends);

        final RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        final PendingAdapter adapter = new PendingAdapter(friends, getContext(), getActivity(), new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Pending_friend friend = friends.get(position);
            }
        });
        //FloatingActionButton floatingActionButton = (FloatingActionButton)getActivity().findViewById(R.id.fabPlus);
        //floatingActionButton.attachToRecyclerView(rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);

        gSwipeRefreshlayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);

        gSwipeRefreshlayout.setOnRefreshListener(onRefreshListener);


        this.gView = view;
        return view;

    }




//    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
//        @Override
//        public void onRefresh() {
//            refreshView(gSwipeRefreshlayout, view);
//        }
//    }

    private void refreshView(final SwipeRefreshLayout swipeRefreshLayout, final View view) {
        CurrentUserHelper currentUser = new CurrentUserHelper(getContext());
        swipeRefreshLayout.setRefreshing(true);

        final String id =currentUser.getUserObjectId();
        String token = currentUser.getToken();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("pending", null, null);
        final ContentValues cv = new ContentValues();
        Log.d("PendingFragment", "ID :" + id + " Token: " + token);
        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);

        OfflineMode offlineMode = new OfflineMode();

        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
            Call<com.example.ivan.champy_v2.model.Friend.Friend> call = friends.getUserFriends(id, token);
            call.enqueue(new Callback<com.example.ivan.champy_v2.model.Friend.Friend>() {
                @Override
                public void onResponse(Response<com.example.ivan.champy_v2.model.Friend.Friend> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        List<Datum> data = response.body().getData();

                        for (int i = 0; i < data.size(); i++) {
                            Datum datum = data.get(i);
                            if ((datum.getFriend() != null) && (datum.getOwner() != null) && datum.getStatus().toString().equals("false")) {
                                String status = "";
//
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


                                }
                                cv.put("owner", "" + status);

                                db.insert("pending", null, cv);
                            }
                        }
                        final List<Pending_friend> newfriends = new ArrayList<Pending_friend>();
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
                                Log.i("newusers", "NewUser: " + c.getString(nameColIndex) + " Photo: " + c.getString(photoColIndex));
                                newfriends.add(new Pending_friend(
                                        c.getString(nameColIndex),
                                        API_URL + c.getString(photoColIndex),
                                        c.getString(index),
                                        c.getString(owner),
                                        c.getString(successChallenges),
                                        c.getString(allChallengesCount),
                                        c.getString(inProgressChallengesCountIndex)
                                        ));
                            } while (c.moveToNext());
                        }

                        c.close();


                        Log.i("stat", "FriendsActivity :" + newfriends.toString());

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
                    Log.i("Comm:", "onResponse: 12345");
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }



    @Override
    public void onStart() {
        super.onStart();

        mSocket.on("connect", onConnect);
        mSocket.on("connected", onConnected);

        mSocket.on("Relationship:new", modifiedRelationship);
        mSocket.on("Relationship:new:accepted", modifiedRelationship);
        mSocket.on("Relationship:new:removed", modifiedRelationship);
        mSocket.on("Relationship:accepted", modifiedRelationship);

        mSocket.on("Relationship:created", modifiedRelationship);
        mSocket.on("Relationship:created:accepted", modifiedRelationship);
        mSocket.on("Relationship:created:removed", modifiedRelationship);
        mSocket.on("Relationship:new:removed", modifiedRelationship);
        mSocket.connect();

    }

}