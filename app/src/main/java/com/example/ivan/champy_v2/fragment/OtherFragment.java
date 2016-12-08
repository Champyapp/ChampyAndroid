package com.example.ivan.champy_v2.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.adapter.OtherAdapter;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.helper.CHCheckTableForExist;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.FriendModel;
import com.example.ivan.champy_v2.model.user.Data;
import com.example.ivan.champy_v2.model.user.User;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
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

import static com.example.ivan.champy_v2.utils.Constants.API_URL;

/**
 * Класс отвечает за OTHER в разделе FriendsActivity
 */
public class OtherFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private final String TAG = "OtherFragment";
    private int mPage;
    private View gView;
    private List<FriendModel> friends;
    private Socket mSocket;
    private String checkRefresh = "";
    private SwipeRefreshLayout gSwipeRefreshLayout;
    private CHCheckTableForExist checkTableForExist;
    private SessionManager sessionManager;
    private OfflineMode offlineMode;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        checkRefresh = "true";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getContext());
        offlineMode = OfflineMode.getInstance();
        dbHelper = DBHelper.getInstance(getContext());
        sessionManager = new SessionManager(getContext());
        cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        checkTableForExist = new CHCheckTableForExist(db);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);
        Log.d(TAG, "onCreateView: ");

        friends = new ArrayList<>();
//        Cursor c = db.query("mytable", null, null, null, null, null, null);
//        if (c.moveToFirst()) {
//            int nameColIndex = c.getColumnIndex("name");
//            int photoColIndex = c.getColumnIndex("photo");
//            int index = c.getColumnIndex("user_id");
//            int challenges = c.getColumnIndex("challenges");
//            int wins = c.getColumnIndex("wins");
//            int total = c.getColumnIndex("total");
//            int level = c.getColumnIndex("level");
//            int idColIndex = c.getColumnIndex("id");
//            do {
//                if (!checkTableForExist.isInOtherTable(c.getString(index)))
//                    friends.add(new FriendModel(
//                            c.getString(nameColIndex),
//                            c.getString(photoColIndex),
//                            c.getString(index),
//                            c.getString(challenges),
//                            c.getString(wins),
//                            c.getString(total),
//                            c.getString(level)));
//            } while (c.moveToNext());
//        }
//        c.close();
        final RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        final OtherAdapter adapter = new OtherAdapter(friends, getContext(), getActivity());

        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);

        gSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
        gSwipeRefreshLayout.setOnRefreshListener(() -> new refreshOtherView().doInBackground() );
        this.gView = view;

//        Bundle friendRequestExtra = getActivity().getIntent().getExtras();
//        if (friendRequestExtra != null) {
//            //refreshOtherView(gSwipeRefreshLayout, gView);
//            new refreshOtherView().doInBackground();
//        }

        if (checkRefresh.equals("true")) { new refreshOtherView().doInBackground(); }

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
    

    private class refreshOtherView extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            refreshOtherView(gSwipeRefreshLayout, gView);
            return null;
        }
    }

    private void refreshOtherView(final SwipeRefreshLayout swipeRefreshLayout, final View view) {
        // Проверка на оффлайн вкладке OTHERS
        if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.post(() -> {
                final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
                final NewUser newUser = retrofit.create(NewUser.class);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                final SQLiteDatabase db = dbHelper.getWritableDatabase();
                int clearCount = db.delete("mytable", null, null);
                final List<FriendModel> newFriends = new ArrayList<>();

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
                                            } catch (Exception e) { e.printStackTrace(); }
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
                                        if (!checkTableForExist.isInOtherTable(data.get_id())) {
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
                                        }
                                        swipeRefreshLayout.setRefreshing(false);
                                        checkRefresh = "false";
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
//                                        cv.put("challenges", "0");
//                                        cv.put("wins", "0");
//                                        cv.put("total", "0");
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
                                    OtherAdapter otherAdapter = new OtherAdapter(newFriends, getContext(), getActivity());
                                    rvContacts.setAdapter(otherAdapter);
                                    gSwipeRefreshLayout.setRefreshing(false);
                                }

                                @Override
                                public void onFailure(Throwable t) { }
                            });
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                });
                request.executeAsync();
            });
        } else { swipeRefreshLayout.setRefreshing(false); checkRefresh = "false"; }

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