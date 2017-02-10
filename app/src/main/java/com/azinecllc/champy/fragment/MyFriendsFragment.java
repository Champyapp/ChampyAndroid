package com.azinecllc.champy.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.FriendsActivityPagerAdapter;
import com.azinecllc.champy.adapter.FriendsAdapter;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.model.FriendModel;
import com.azinecllc.champy.model.friend.Datum;
import com.azinecllc.champy.model.friend.Friend_;
import com.azinecllc.champy.model.friend.Owner;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;
import static com.azinecllc.champy.utils.Constants.path;

public class MyFriendsFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private String id, token;
    private View gView;
    private SwipeRefreshLayout gSwipeRefreshLayout;
    private SQLiteDatabase db;
    private SessionManager sessionManager;
    private Socket mSocket;

    public static MyFriendsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        MyFriendsFragment fragment = new MyFriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = SessionManager.getInstance(getContext());
        token = sessionManager.getToken();
        id = sessionManager.getUserId();

        DBHelper dbHelper = DBHelper.getInstance(getContext());
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);

        final List<FriendModel> friends = new ArrayList<>();
        Cursor c = db.query("friends", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int photoColIndex = c.getColumnIndex("photo");
            int index = c.getColumnIndex("user_id");
            int winsCount = c.getColumnIndex("successChallenges");
            int allCount = c.getColumnIndex("allChallengesCount");
            int inProgressCount = c.getColumnIndex("inProgressChallengesCount");
            int level = c.getColumnIndex("level");
            do {
                friends.add(new FriendModel(
                        c.getString(nameColIndex),
                        API_URL + c.getString(photoColIndex),
                        c.getString(index),
                        c.getString(inProgressCount),
                        c.getString(winsCount),
                        c.getString(allCount),
                        c.getString(level)));
            } while (c.moveToNext());
        }
        c.close();

        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        FriendsAdapter adapter = new FriendsAdapter(friends, getContext(), getActivity(), (view1, position) -> {
            FriendModel friend = friends.get(position);
        });


        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);


        gSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        gSwipeRefreshLayout.setOnRefreshListener(() -> refreshFriendsView(gSwipeRefreshLayout, gView));
        this.gView = view;

        if (sessionManager.getRefreshFriends().equals("true")) {
            refreshFriendsView(gSwipeRefreshLayout, gView);
            sessionManager.setRefreshFriends("false");
        }


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mSocket = IO.socket(API_URL);
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mSocket.off();
        mSocket.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


    private void refreshFriendsView(final SwipeRefreshLayout swipeRefreshLayout, final View view) {
        swipeRefreshLayout.setRefreshing(true);
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        final DBHelper dbHelper = DBHelper.getInstance(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        int clearCount = db.delete("friends", null, null);

        final com.azinecllc.champy.interfaces.Friends friends = retrofit.create(com.azinecllc.champy.interfaces.Friends.class);

        // Проверка на оффлайн вкладке FriendsActivity
        OfflineMode offlineMode = OfflineMode.getInstance();
        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
            Call<com.azinecllc.champy.model.friend.Friend> call = friends.getUserFriends(id, token);
            call.enqueue(new Callback<com.azinecllc.champy.model.friend.Friend>() {
                @Override
                public void onResponse(Response<com.azinecllc.champy.model.friend.Friend> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        List<Datum> data = response.body().getData();

                        // get friends from response.
                        for (int i = 0; i < data.size(); i++) {
                            Datum datum = data.get(i);
                            if (datum.getFriend() != null && datum.getStatus().toString().equals("true")) {
                                if (datum.getOwner().get_id().equals(id)) {
                                    Friend_ friend = datum.getFriend();
                                    String friendPhoto = (friend.getPhoto() != null) ? friend.getPhoto().getMedium() : "";
                                    cv.put("name", friend.getName());
                                    cv.put("photo", friendPhoto);
                                    cv.put("user_id", friend.getId());
                                    cv.put("inProgressChallengesCount", friend.getInProgressChallengesCount());
                                    cv.put("allChallengesCount", friend.getAllChallengesCount());
                                    cv.put("successChallenges", friend.getSuccessChallenges());
                                    db.insert("friends", null, cv);
                                } else {
                                    Owner friend = datum.getOwner();
                                    String friendPhoto = (friend.getPhoto() != null) ? friend.getPhoto().getMedium() : "";
                                    cv.put("name", friend.getName());
                                    cv.put("photo", friendPhoto);
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
                            int nameColIndex = c.getColumnIndex("name");
                            int photoColIndex = c.getColumnIndex("photo");
                            int idColIndex = c.getColumnIndex("id");
                            int index = c.getColumnIndex("user_id");
                            int winsCount = c.getColumnIndex("successChallenges");
                            int allCount = c.getColumnIndex("allChallengesCount");
                            int inProgress = c.getColumnIndex("inProgressChallengesCount");
                            int level = c.getColumnIndex("level");
                            do {
                                newfriends.add(new FriendModel(
                                        c.getString(nameColIndex),
                                        API_URL + c.getString(photoColIndex),
                                        c.getString(index),
                                        "" + c.getString(inProgress),
                                        "" + c.getString(winsCount),
                                        "" + c.getString(allCount),
                                        "0"));
                            } while (c.moveToNext());
                        }
                        c.close();

                        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
                        final FriendsAdapter adapter = new FriendsAdapter(newfriends, getContext(), getActivity(), (view1, position) -> {
                            FriendModel friend = newfriends.get(position);
                        });

                        rvContacts.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getContext(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mSocket.emit("ready", sessionManager.getToken());
        }
    };

    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            //Log.d("Sockets", "Sockets: connected!");
        }
    };

    protected Emitter.Listener modifiedRelationship = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshFriendsView(gSwipeRefreshLayout, gView);
                }
            });
        }
    };

}