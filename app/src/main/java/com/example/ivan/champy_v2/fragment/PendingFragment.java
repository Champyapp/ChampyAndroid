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
import com.example.ivan.champy_v2.adapter.PendingAdapter;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.interfaces.CustomItemClickListener;
import com.example.ivan.champy_v2.model.Pending_friend;
import com.example.ivan.champy_v2.model.friend.Datum;
import com.example.ivan.champy_v2.model.friend.Friend_;
import com.example.ivan.champy_v2.model.friend.Owner;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.example.ivan.champy_v2.utils.Constants.API_URL;

public class PendingFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private final String TAG = "PendingFragment";
    private String id = "", token = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private SessionManager sessionManager;
    private View gView;

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
        sessionManager = new SessionManager(getContext());
        id = sessionManager.getUserId();
        token = sessionManager.getToken();
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

            //try {
                do {
                    pendingFriends.add(new Pending_friend(
                            c.getString(nameColIndex),
                            API_URL + c.getString(photoColIndex),
                            c.getString(index),
                            c.getString(owner),
                            c.getString(successChallenges),
                            c.getString(allChallengesCount),
                            c.getString(inProgressChallengesCountIndex)
                    ));
                } while (c.moveToNext());
//            } finally {
//                c.close();
//                db.close();
//            }
        }
        c.close();
        final RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);




        final PendingAdapter adapter = new PendingAdapter(pendingFriends, getContext(), getActivity(), (view1, position) -> {
            Pending_friend friend = pendingFriends.get(position);
        });



        SessionManager sessionManager = new SessionManager(getActivity());
        String checkRefresh = sessionManager.getRefreshPending();


        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> refreshPendingView(swipeRefreshLayout, gView));
        this.gView = view;

        if (checkRefresh.equals("true")) {
            refreshPendingView(swipeRefreshLayout, gView);
            sessionManager.setRefreshPending("false");
        }

        return view;

    }


    @Override
    public void onStart() {
        super.onStart();
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
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Sockets off & disconnect");
        mSocket.off();
        mSocket.disconnect();
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
                            try {
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
                            } finally {
                                c.close();
                                db.close();
                            }
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


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mSocket.emit("ready", token);
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