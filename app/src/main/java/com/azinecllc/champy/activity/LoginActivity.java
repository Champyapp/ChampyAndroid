package com.azinecllc.champy.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.DailyRemindController;
import com.azinecllc.champy.controller.UserController;
import com.azinecllc.champy.helper.CHSaveAndUploadPhoto;
import com.azinecllc.champy.helper.CHGetFacebookFriends;
import com.azinecllc.champy.interfaces.NewUser;
import com.azinecllc.champy.model.user.Data;
import com.azinecllc.champy.model.user.LoginData;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.azinecllc.champy.utils.Constants.API_URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String userEmail, userPicture, userName, userFBID;
    private CallbackManager mCallbackManager;
    private SessionManager sessionManager;
    private OfflineMode offlineMode;
    private View spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //getFacebookHashKey(); // must be above "setContentView"
        setContentView(R.layout.activity_login);

        TextView loginText = (TextView)findViewById(R.id.login_text);
        spinner = findViewById(R.id.loadingPanel);

        //initFacebookTokenTracker();
        offlineMode = OfflineMode.getInstance();
        sessionManager = SessionManager.getInstance(getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton buttonLogin = (LoginButton) findViewById(R.id.login_button);
        buttonLogin.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        buttonLogin.setOnClickListener(this);

        if (!checkWriteExternalPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onClick(View v) {
        if (!offlineMode.isConnectedToRemoteAPI(this)) {
            return;
        }
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (!loginResult.getAccessToken().getDeclinedPermissions().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "No Permissions Granted", Toast.LENGTH_LONG).show();
                    return;
                }

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                    spinner.setVisibility(View.VISIBLE);
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
                                InstanceID instanceID = InstanceID.getInstance(LoginActivity.this);
                                String androidToken = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("token", androidToken);
                                jsonObject.put("timeZone", "-2");
                                String gcm = jsonObject.toString();

                                singInUser(userFBID, gcm, userPicture, androidToken);
                                registerUser(userFBID, userName, userEmail, gcm, androidToken, userPicture);
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }).start();

                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
                spinner.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();
                Toast.makeText(LoginActivity.this, "Login status: Denied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this, "Login status: Failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.isFinishing();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 1: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the contacts-related task you need to do.
//                } //else { //permission denied, boo! Disable the functionality that depends on this permission.}
//                return;
//            }
//        }
//    }


//    /**
//     * Initialize facebook has key. We need this, because without this facebook-login will crash.
//     */
//    private void getFacebookHashKey() {
//        try {
//            @SuppressLint("PackageManagerGetSignatures")
//            PackageInfo info = getPackageManager().getPackageInfo("com.azinecllc.champy", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//            }
//        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Initialize facebook token tracker. This shit needs to track if token was changed. We never
//     * change it in Champy, but without this shit facebook-login will crash.
//     */
//    private void initFacebookTokenTracker() {
//        mCallbackManager = CallbackManager.Factory.create();
//        mTokenTracker  = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {}
//        };
//        mProfileTracker = new ProfileTracker() {
//            @Override
//            protected void onCurrentProfileChanged(Profile oldprofile, Profile newprofile) {
//                if (newprofile != null) {
//                    userName = newprofile.getName();
//                    userFBID = newprofile.getId();
//                }
//            }
//        };
//        mTokenTracker.startTracking();
//        mProfileTracker.startTracking();
//    }

    /**
     * Method to login user in case when we have already created profile. Here we create new Session
     * for user, set current status bar, load photo from api and store it locally. We need this because
     * after each 'log out' we have cleaned out session manager (plus case if 2 user on once device)
     * and after each action we redirect user to SplashScreen.
     *
     * @param facebookID - current user's facebook ID
     * @param gcm        - user's Google Cloud Messaging ID
     * @param pictureFB    - facebook picture (path to picture)
     * @param androidTok - unique android token for GCM. We need this to get notification from the server
     * @throws JSONException - we can expect this exception because we can get NPE in any value
     *                       In this case we can handle it.
     */
    public void singInUser(String facebookID, String gcm, String pictureFB, String androidTok) throws JSONException {
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
                            userName, userEmail, userFBID, userPicture, jwt, userID, pushN, newChallengeReq,
                            acceptedYour, challengeEnd, challengesForToday, "true", gcm, androidTok
                    );

                    UserController userController = new UserController(sessionManager, retrofit);
                    userController.updatePushIdentifier();

                    CHGetFacebookFriends getFbFriends = new CHGetFacebookFriends(getApplicationContext(), retrofit);
                    getFbFriends.getUserFacebookFriends(gcm);
                    getFbFriends.getUserPending(userID, jwt);

                    Intent goToRoleActivity = new Intent(LoginActivity.this, RoleControllerActivity.class);
                    startActivity(goToRoleActivity);

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }

        });


    }

    /**
     * Method to register user in Server DataBase. Here we create new Session for user, download photo
     * from facebook, create locally this file and upload on API. Also we enable all notifications,
     * set status bar equals to 0, set value 'refreshFriends' true for all pages, enable daily reminder
     * and after each action we redirect user to SplashScreen.
     * @param fbID - current user's facebook ID
     * @param name - current user's name from Facebook (userFirstName + " " + userLastName)
     * @param email - userEmail from Facebook
     * @param gcm - user's Google Cloud Messaging ID
     * @param androidTok - unique android token for GCM. We need this to get notification from the server
     * @param picture - current user's profile picture from Facebook (path to picture)
     * @throws JSONException - we can expect this exception because we can get NPE in any value
     *                         In this case we can handle it.
     */
    private void registerUser(String fbID, String name, String email, String gcm, String androidTok, String picture) throws JSONException {
        String jwt = getToken(fbID, gcm);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        NewUser newUser = retrofit.create(NewUser.class);
        Call<User> call = newUser.register(new LoginData(fbID, name, email));
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
                    sessionManager.setRefreshOthers ("true");
                    sessionManager.createUserLoginSession(
                            userName, userEmail, userFBID, picture, jwt, userID, pushN, newChallReq,
                            acceptedYour, challengeEnd, "true", "true", gcm, androidTok);

                    Log.i("TAG", "onResponse: " + picture);
                    sessionManager.setChampyOptions(
                            user.getAllChallengesCount().toString(),
                            user.getSuccessChallenges().toString(),
                            user.getInProgressChallenges().toString(),
                            user.getLevel().getNumber().toString());

                    /** here I upload photo on API and update push identifier */
                    CHSaveAndUploadPhoto a = new CHSaveAndUploadPhoto(getApplicationContext(), retrofit);
                    a.execute(picture); // async, don't forget to destroy thread.

                    DailyRemindController drc = new DailyRemindController(getApplicationContext());
                    drc.enableDailyNotificationReminder(12);

                    Intent goToRoleActivity = new Intent(LoginActivity.this, RoleControllerActivity.class);
                    startActivity(goToRoleActivity);
                }
            }

            @Override
            public void onFailure(Throwable t) {}
        });

    }

    /**
     * Method to get java web token from user FB ID. This value need to make Call and create new User
     * @param facebookID - user's facebook id
     * @param gcm - user's Google Cloud Messaging ID
     * @return - clear java web token;
     * @throws JSONException - we can expect this exception because we can get NPE in any value
     *                         In this case we can handle it.
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
        int res = checkCallingOrSelfPermission(READ_EXTERNAL_STORAGE);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


}
