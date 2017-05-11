package com.azinecllc.champy.fragment;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.FriendsAdapter;
import com.azinecllc.champy.controller.DailyRemindController;
import com.azinecllc.champy.controller.UserController;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.helper.CHCheckTableForExist;
import com.azinecllc.champy.helper.CHGetFacebookFriends;
import com.azinecllc.champy.helper.CHSaveAndUploadPhoto;
import com.azinecllc.champy.interfaces.NewUser;
import com.azinecllc.champy.model.FriendModel;
import com.azinecllc.champy.model.user.Data;
import com.azinecllc.champy.model.user.LoginData;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.UserProfileUtil;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.azinecllc.champy.utils.Constants.API_URL;

public class FriendsFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    public static final String TAG = "FriendsFragment";
    private static final String ARG_PAGE = "ARG_PAGE";
    private String userEmail, userPicture, userName, userFBID;
    private SwipeRefreshLayout gSwipeRefreshLayout;
    private CHCheckTableForExist checkTableForExist;
    private CallbackManager mCallbackManager;
    private SessionManager sessionManager;
    private List<FriendModel> friendsList;
    private TextView tvConnectWithFB;
    private OfflineMode offlineMode;
    private RecyclerView rvContacts;
    private LoginButton loginButton;
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
        setHasOptionsMenu(true);
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


        adapter = new FriendsAdapter(friendsList, getContext(), getActivity());
        rvContacts = (RecyclerView) itemView.findViewById(R.id.recycler_view);
        rvContacts.setHasFixedSize(true); // to improve performance.
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);

        gSwipeRefreshLayout = (SwipeRefreshLayout) itemView.findViewById(R.id.swipe_to_refresh);
        gSwipeRefreshLayout.setOnRefreshListener(() -> refreshOtherView(gSwipeRefreshLayout, gView));
        this.gView = itemView;


        ///////////////////////////////////////////////////////////////////////////////////////////
        loginButton = (LoginButton) itemView.findViewById(R.id.login_button);
        tvConnectWithFB = (TextView) itemView.findViewById(R.id.text_view_connect_facebook);
        toggleCurrentView();
        ///////////////////////////////////////////////////////////////////////////////////////////


        if (sessionManager.getRefreshOthers().equals("true")) {
            refreshOtherView(gSwipeRefreshLayout, gView);
        }

        return itemView;

    }

    @Override
    public void onClick(View v) {
        System.out.println("onClick, bro");
        if (!offlineMode.isConnectedToRemoteAPI(getActivity())) {
            return;
        }
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("LoginManager.SUCCESS");
                if (!loginResult.getAccessToken().getDeclinedPermissions().isEmpty()) {
                    Toast.makeText(getActivity(), "No Permissions Granted", Toast.LENGTH_LONG).show();
                    return;
                }

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                    try {
                        userFBID = object.getString("id");
                        userName = object.getString("first_name") + " " + object.getString("last_name");
                        try {
                            URL profile_pic = new URL("https://graph.facebook.com/" + userFBID + "/picture?type=large");
                            userPicture = profile_pic.toString();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        userEmail = (object.getString("email") != null) ? object.getString("email") : userFBID + "@facebook.com";
                    } catch (JSONException e) {
                        userEmail = userFBID + "@facebook.com";
                        e.printStackTrace();
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                InstanceID instanceID = InstanceID.getInstance(getContext());
                                String androidToken = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("token", androidToken);
                                jsonObject.put("timeZone", "-2");
                                String gcm = jsonObject.toString();

                                System.out.println("HELLO MOTTO");
                                singInUser(userFBID, gcm, userPicture, androidToken);
                                registerUser(userFBID, userName, userEmail, gcm, androidToken, userPicture);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }).start();

                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();

                refreshOtherView(gSwipeRefreshLayout, gView);

                loginButton.setVisibility(View.GONE);
                tvConnectWithFB.setVisibility(View.GONE);
                gSwipeRefreshLayout.setEnabled(true);


            }

            @Override
            public void onCancel() {
                System.out.println("LoginManager.CANCEL");
                LoginManager.getInstance().logOut();
                Toast.makeText(getActivity(), "Login status: Denied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                System.out.println("LoginManager.ERROR");
                Toast.makeText(getActivity(), "Login status: Failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                adapter.setFilter(friendsList);
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true; // Return true to expand action view
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<FriendModel> filteredModelList = filter(friendsList, newText);
        adapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<FriendModel> filter(List<FriendModel> models, String query) {
        query = query.toLowerCase();
        final List<FriendModel> filteredModelList = new ArrayList<>();
        for (FriendModel model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
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
                    GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONArrayCallback() {
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

    /**
     * Method to login user in case when we have already created profile. Here we create new Session
     * for user, set current status bar, load photo from api and store it locally. We need this because
     * after each 'log out' we have cleaned out session manager (plus case if 2 user on once device)
     * and after each action we redirect user to SplashScreen.
     *
     * @param facebookID - current user's facebook ID
     * @param gcm        - user's Google Cloud Messaging ID
     * @param pictureFB  - facebook picture (path to picture)
     * @param androidTok - unique android token for GCM. We need this to get notification from the server
     * @throws JSONException - we can expect this exception because we can get NPE in any value
     *                       In this case we can handle it.
     */
    private void singInUser(String facebookID, String gcm, String pictureFB, String androidTok) throws JSONException {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        String jwt = getToken(facebookID, gcm);
        NewUser newUser = retrofit.create(NewUser.class);
        Call<User> callGetUserInfo = newUser.getUserInfo(jwt);
        callGetUserInfo.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Data data = response.body().getData();
                    String userID = data.get_id();
                    String userName = data.getName();
                    String userEmail = data.getEmail();
                    String pushN = data.getProfileOptions().getPushNotifications().toString();
                    String challengeEnd = data.getProfileOptions().getChallengeEnd().toString();
                    String acceptedYour = data.getProfileOptions().getAcceptedYourChallenge().toString();
                    String newChallengeReq = data.getProfileOptions().getNewChallengeRequests().toString();
                    String challengesForToday = data.getProfileOptions().getChallengesForToday().toString();
                    String total = data.getAllChallengesCount().toString();
                    String wins = data.getSuccessChallenges().toString();
                    String inProgress = data.getInProgressChallenges().toString();
                    String userPicture = (data.getPhoto() != null) ? API_URL + data.getPhoto().getLarge() : pictureFB;
//                    String pictureAPI = null;
//                    if (data.getPhoto() != null) {
//                        String root = Environment.getExternalStorageDirectory().toString(); // path
//                        String path = "/data/data/com.azinecllc.champy/app_imageDir/";
//                        File photoFile = new File(root + path, "profile.jpg");
//                        // не достаточно ли просто провеить (data.getPhoto != null) ?
//                        if (!photoFile.exists()) {
//                            com.azinecllc.champy.model.user.Photo photo = data.getPhoto();
//                            pictureAPI = API_URL + photo.getLarge();
//                        }
//                    }

                    sessionManager.setRefreshFriends("true");
                    sessionManager.setRefreshPending("false");
                    sessionManager.setRefreshOthers("true");
                    sessionManager.setChampyOptions(total, wins, inProgress, "0");
                    sessionManager.createUserLoginSession(
                            true, userName, userEmail, userFBID, userPicture, jwt, userID, pushN, newChallengeReq,
                            acceptedYour, challengeEnd, challengesForToday, "true", gcm, androidTok
                    );

                    UserController userController = new UserController(sessionManager, retrofit);
                    userController.updatePushIdentifier();

                    CHGetFacebookFriends getFbFriends = new CHGetFacebookFriends(getContext(), retrofit);
                    getFbFriends.getUserFacebookFriends(gcm);
                    getFbFriends.getUserPending(userID, jwt); // todo: remove later.

                    UserProfileUtil.setProfilePicture(getActivity(), userPicture);
                    UserProfileUtil.setUserNameAndEmail(getActivity(), userName, userEmail);
                    //UserProfileUtil.setBackgroundPicture(getActivity(), userPicture);
                    //Intent goToRoleActivity = new Intent(getContext(), RoleControllerActivity.class);
                    //startActivity(goToRoleActivity);

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }

        });


    }

    /**
     * Method to register user in Server DataBase. Here we create new Session for user, download photo
     * from facebook, create locally this file and upload on API. Also we enable all notifications,
     * set status bar equals to 0, set value 'refreshFriends' true for all pages, enable daily reminder
     * and after each action we redirect user to SplashScreen.
     *
     * @param fbId       - current user's facebook ID
     * @param name       - current user's name from Facebook (userFirstName + " " + userLastName)
     * @param email      - userEmail from Facebook
     * @param gcm        - user's Google Cloud Messaging ID
     * @param androidTok - unique android token for GCM. We need this to get notification from the server
     * @param picture    - current user's profile picture from Facebook (path to picture)
     * @throws JSONException - we can expect this exception because we can get NPE in any value
     *                       In this case we can handle it.
     */
    private void registerUser(String fbId, String name, String email,
                              String gcm, String androidTok, String picture) throws JSONException {
        String jwt = getToken(fbId, gcm);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NewUser newUser = retrofit.create(NewUser.class);
        Call<User> call = newUser.register(new LoginData(fbId, name, email));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                User decodedResponse = response.body();
                if (response.isSuccess()) {
                    Data user = decodedResponse.getData(); // data == user
                    String userEmail = user.getEmail();
                    String userName = user.getName();
                    String userID = user.get_id();
                    String pushN = user.getProfileOptions().getPushNotifications().toString();
                    String newChallReq = user.getProfileOptions().getNewChallengeRequests().toString();
                    String acceptedYour = user.getProfileOptions().getAcceptedYourChallenge().toString();
                    String challengeEnd = user.getProfileOptions().getChallengeEnd().toString();

                    sessionManager.setRefreshPending("true");
                    sessionManager.setRefreshFriends("true");
                    sessionManager.setRefreshOthers("true");
                    sessionManager.createUserLoginSession(
                            true, userName, userEmail, userFBID, picture,
                            jwt, userID, pushN, newChallReq, acceptedYour,
                            challengeEnd, "true", "true", gcm, androidTok);

                    Log.i("TAG", "onResponse: " + picture);
                    sessionManager.setChampyOptions(
                            user.getAllChallengesCount().toString(),
                            user.getSuccessChallenges().toString(),
                            user.getInProgressChallenges().toString(),
                            user.getLevel().getNumber().toString());

                    /** here I upload photo on API and update push identifier */
                    CHSaveAndUploadPhoto a = new CHSaveAndUploadPhoto(getContext(), retrofit);
                    a.execute(picture); // async, don't forget to destroy thread.

                    DailyRemindController drc = new DailyRemindController(getContext());
                    drc.enableDailyNotificationReminder(12);

                    UserProfileUtil.setProfilePicture(getActivity(), picture);
                    UserProfileUtil.setUserNameAndEmail(getActivity(), userName, userEmail);
                    //Intent goToRoleActivity = new Intent(getContext(), RoleControllerActivity.class);
                    //startActivity(goToRoleActivity);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });

    }

    /**
     * Method to get java web token from user FB ID. This value need to make Call and create new User
     *
     * @param facebookID - user's facebook id
     * @param gcm        - user's Google Cloud Messaging ID
     * @return - clear java web token;
     * @throws JSONException - we can expect this exception because we can get NPE in any value
     *                       In this case we can handle it.
     */
    private String getToken(String facebookID, String gcm) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("facebookId", facebookID);
        jsonObject.put("AndroidOS", gcm);
        String string = jsonObject.toString();
        return Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .setPayload(string)
                .signWith(SignatureAlgorithm.HS256, "secret")
                .compact();
    }

    /**
     * Method which returns boolean value of granted permission. To work with storage we need to
     * have this permission and we had to check it in runtime
     *
     * @return value of granted permission
     */
    private boolean checkWriteExternalPermission() {
        int res = getActivity().checkCallingOrSelfPermission(READ_EXTERNAL_STORAGE);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    private void toggleCurrentView() {
        //Toast.makeText(getContext(), "login status: " + sessionManager.isUserLoggedIn(), Toast.LENGTH_SHORT).show();
        if (!sessionManager.isUserLoggedIn()) {
            loginButton.setVisibility(View.VISIBLE);
            tvConnectWithFB.setVisibility(View.VISIBLE);
            gSwipeRefreshLayout.setEnabled(false);

            mCallbackManager = CallbackManager.Factory.create();
            loginButton.setFragment(this);
            loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
            loginButton.setOnClickListener(this);

        } else {
            loginButton.setVisibility(View.GONE);
            tvConnectWithFB.setVisibility(View.GONE);
            gSwipeRefreshLayout.setEnabled(true);
        }
    }



}