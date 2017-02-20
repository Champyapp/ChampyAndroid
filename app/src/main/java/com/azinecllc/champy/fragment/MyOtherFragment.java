package com.azinecllc.champy.fragment;

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
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.OtherAdapter;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.helper.CHCheckTableForExist;
import com.azinecllc.champy.interfaces.NewUser;
import com.azinecllc.champy.model.FriendModel;
import com.azinecllc.champy.model.user.Data;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
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
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;

public class MyOtherFragment extends Fragment {

    public static final String TAG = "OtherFriendsss";
    private static final String ARG_PAGE = "ARG_PAGE";
    private SwipeRefreshLayout gSwipeRefreshLayout;
    private CHCheckTableForExist checkTableForExist;
    private SessionManager sessionManager;
    private List<FriendModel> friends;
    private OfflineMode offlineMode;
    private RecyclerView rvContacts;
    private OtherAdapter adapter;
    private SQLiteDatabase db;
    private Retrofit retrofit;
    private DBHelper dbHelper;
    private ContentValues cv;
    private Socket mSocket;
    private View gView;

    public static MyOtherFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        MyOtherFragment fragment = new MyOtherFragment();
        fragment.setArguments(args);
        return fragment;
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
        final View view = inflater.inflate(R.layout.item_recycler, container, false);

        //sessionManager = SessionManager.getInstance(getContext());
        friends = new ArrayList<>();
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
                    friends.add(new FriendModel(
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


        rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        adapter = new OtherAdapter(friends, getContext(), getActivity(), retrofit);

        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);

        gSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        gSwipeRefreshLayout.setOnRefreshListener(() -> refreshOtherView(gSwipeRefreshLayout, gView));
        this.gView = view;

        if (sessionManager.getRefreshOthers().equals("true")) {
            refreshOtherView(gSwipeRefreshLayout, gView);
        }

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");

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
    public void onStop() {
        super.onStop();
        Date now = new Date(System.currentTimeMillis());
        Log.i(TAG, "onStop: Sockets disconnected "
                + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds());

        mSocket.disconnect();
        //mSocket.off();
        //mSocket.off("connect", onConnect);
        //mSocket.off("connected", onConnected);
        mSocket.off("Relationship:new:accepted", modifiedRelationship);
        mSocket.off("Relationship:new:removed", modifiedRelationship);
        mSocket.off("Relationship:created:accepted", modifiedRelationship);
        mSocket.off("Relationship:created:removed", modifiedRelationship);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Glide.clear(gView);
        cv.clear();
        friends.clear();
        gView.destroyDrawingCache();
        cv = null;
        gView = null;
        adapter = null;
        friends = null;
        //mSocket = null;
        retrofit = null;
        checkTableForExist = null;
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


    private void refreshOtherView(final SwipeRefreshLayout swipeRefreshLayout, final View view) {
        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    friends.clear();
                    NewUser newUser = retrofit.create(NewUser.class);
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("mytable", null, null);
                    GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken
                            .getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                        @Override
                        public void onCompleted(JSONArray array, GraphResponse response) {
                            if (array.length() == 0) {
                                Toast.makeText(getContext(), R.string.noOneHasInstalledChampy, Toast.LENGTH_LONG).show();
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
                                                    friends.add(new FriendModel(name, photo, data.get_id(),
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


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "Sockets call: onConnect");
            mSocket.emit("ready", sessionManager.getToken());
        }
    };

    private Emitter.Listener onConnected = args -> Log.d(TAG, "Sockets call: onConnected~");

    protected Emitter.Listener modifiedRelationship = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Sockets run: modifiedRelationship");
                    refreshOtherView(gSwipeRefreshLayout, gView);
                }
            });
        }
    };


}