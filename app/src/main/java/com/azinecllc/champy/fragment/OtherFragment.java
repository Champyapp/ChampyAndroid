package com.azinecllc.champy.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

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

import static com.azinecllc.champy.utils.Constants.API_URL;

/**
 * Класс отвечает за OTHER в разделе FriendsActivity
 */
public class OtherFragment extends Fragment {

    //2.48 //2.22 //1.39 //1.63
    private static final String ARG_PAGE = "ARG_PAGE";
    private final String TAG = "OtherFragment";
    private int mPage;
    private Retrofit retrofit;
    private List<FriendModel> friends;
    private Socket mSocket;
    private View gView;
    private SwipeRefreshLayout gSwipeRefreshLayout;
    private CHCheckTableForExist checkTableForExist;
    private SessionManager sessionManager;
    private OfflineMode offlineMode;
    private OtherAdapter adapter;
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private ContentValues cv;

    public static OtherFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        OtherFragment fragment = new OtherFragment();
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
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);

        friends = new ArrayList<>();
        Cursor c = db.query("mytable", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int nameColIndex  = c.getColumnIndex("name");
            int photoColIndex = c.getColumnIndex("photo");
            int index         = c.getColumnIndex("user_id");
            int challenges    = c.getColumnIndex("challenges");
            int wins          = c.getColumnIndex("wins");
            int total         = c.getColumnIndex("total");
            int level         = c.getColumnIndex("level");
            int idColIndex    = c.getColumnIndex("id");
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


        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        adapter = new OtherAdapter(friends, getContext(), getActivity(), retrofit);

        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);

        gSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
        gSwipeRefreshLayout.setOnRefreshListener(() -> refreshOtherView(gSwipeRefreshLayout, gView));
        this.gView = view;

        if (sessionManager.getRefreshOthers().equals("true")) {
//            new loadOtherFromAsync().doInBackground();
            refreshOtherView(gSwipeRefreshLayout, gView);
        }

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mSocket = IO.socket(API_URL);
            Log.d(TAG, "onStart: Sockets are connected");
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
        Log.d(TAG, "onStop: Sockets off & disconnect");
        mSocket.off();
        mSocket.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Glide.clear(gView);
        cv.clear();
        friends.clear();
        gView.destroyDrawingCache();
        gSwipeRefreshLayout.destroyDrawingCache();
        cv = null;
        gView = null;
        adapter = null;
        friends = null;
        mSocket = null;
        retrofit = null;
        checkTableForExist = null;
        gSwipeRefreshLayout = null;
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


//    private class loadOtherFromAsync extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            refreshOtherView(gSwipeRefreshLayout, gView);
//            return null;
//        }
//    }


    private void refreshOtherView(final SwipeRefreshLayout swipeRefreshLayout, final View view) {
        // Проверка на оффлайн вкладке OTHERS
        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.post(() -> {
                NewUser newUser = retrofit.create(NewUser.class);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int clearCount = db.delete("mytable", null, null);
                friends.clear();
                final GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), (array, response) -> {
                    if (array.length() == 0) {
                        Toast.makeText(getContext(), R.string.noOneHasInstalledChampy, Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            final String fb_id = array.getJSONObject(i).getString("id");
                            final String jwtString = Jwts.builder()
                                    .setHeaderParam("alg", "HS256")
                                    .setHeaderParam("typ", "JWT")
                                    .setPayload("{\n"+"  \"facebookId\": \"" + fb_id + "\"\n" + "}")
                                    .signWith(SignatureAlgorithm.HS256, "secret")
                                    .compact();

                            Call<User> call = newUser.getUserInfo(jwtString);
                            call.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Response<User> response, Retrofit retrofit1) {
                                    if (response.isSuccess()) {
                                        Data data = response.body().getData();
                                        String photo = null;

                                        if (data.getPhoto() != null) {
                                            photo = API_URL + data.getPhoto().getMedium();
                                        } else {
                                            try {
                                                URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                                photo = profile_pic.toString();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        String name = data.getName();
                                        cv.put("user_id",    data.get_id());
                                        cv.put("name",       name);
                                        cv.put("photo",      photo);
                                        cv.put("total",      data.getAllChallengesCount().toString());
                                        cv.put("wins",       data.getSuccessChallenges().toString());
                                        cv.put("challenges", data.getInProgressChallenges().toString());
                                        cv.put("level",      data.getLevel().getNumber().toString());

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
                                        swipeRefreshLayout.setRefreshing(false);

                                    }
//                                    else {
//                                        // отображение всего у человека, который не установил champy
//                                        URL profile_pic = null;
//                                        String photo = null;
//                                        try {
//                                            profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
//                                            photo = profile_pic.toString();
//                                        } catch (MalformedURLException e) {
//                                            e.printStackTrace();
//                                        }
//                                        cv.put("name", user_name);
//                                        cv.put("photo", photo);
//                                        cv.put("ic_score_progress", "0");
//                                        cv.put("ic_score_wins", "0");
//                                        cv.put("ic_score_total", "0");
//                                        cv.put("level", "0");
//                                        newFriends.add(new FriendModel(user_name, photo, null, "0", "0", "0", "0"));
//                                        db.insert("mytable", null, cv);
//
//                                        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
//                                        OtherAdapter adapter1 = new OtherAdapter(newFriends, getContext(), getActivity());
//                                        rvContacts.setAdapter(adapter1);
//                                        gSwipeRefreshLayout.setRefreshing(false);
//                                    }

                                    RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
                                    adapter = new OtherAdapter(friends, getContext(), getActivity(), retrofit);
                                    rvContacts.setAdapter(adapter);
                                    gSwipeRefreshLayout.setRefreshing(false);
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Toast.makeText(getContext(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                });
                request.executeAsync();
            });
            Runtime.getRuntime().runFinalization();
            Runtime.getRuntime().gc();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            sessionManager.setRefreshOthers("false");
        }

    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mSocket.emit("ready", sessionManager.getToken());
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
                    refreshOtherView(gSwipeRefreshLayout, gView);
                }});
            Log.d(TAG, "Sockets: modifiedRelationship");
        }
    };



}