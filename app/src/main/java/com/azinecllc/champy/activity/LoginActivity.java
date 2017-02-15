package com.azinecllc.champy.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.DailyRemindController;
import com.azinecllc.champy.controller.UserController;
import com.azinecllc.champy.helper.AppSync;
import com.azinecllc.champy.interfaces.NewUser;
import com.azinecllc.champy.model.user.Data;
import com.azinecllc.champy.model.user.LoginData;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public View spinner;
    private String userEmail, userPicture, userName, userFBID;
    private AccessTokenTracker mTokenTracker;
    private CallbackManager mCallbackManager;
    private ProfileTracker mProfileTracker;
    private OfflineMode offlineMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        getFacebookHashKey(); // must be above "setContentView"
        setContentView(R.layout.activity_login);

        /************************ for API >= 23 *************************/
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        /****************************************************************/

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
        TextView loginText = (TextView)findViewById(R.id.login_text);
        spinner = findViewById(R.id.loadingPanel);
        loginText.setTypeface(typeface);

        initFacebookTokenTracker();

        ImageButton buttonLogin = (ImageButton)findViewById(R.id.login_button);
        offlineMode = OfflineMode.getInstance();
        buttonLogin.setOnClickListener(this);

        ViewServer.get(this).addWindow(this);
    }

    @Override
    public void onClick(View v) {
        offlineMode.isConnectedToRemoteAPI(this);
        LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("public_profile, email, user_friends"));
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
                        userEmail = (object.getString("email") != null)
                                ? object.getString("email")
                                : userFBID + "@champy.com";
                        try {
                            URL profile_pic = new URL("https://graph.facebook.com/" + userFBID + "/picture?type=large");
                            userPicture = profile_pic.toString();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        new Thread(() -> {
                            try {
                                String token_android;
                                InstanceID instanceID = InstanceID.getInstance(LoginActivity.this);
                                token_android = instanceID.getToken(
                                        getString(R.string.gcm_defaultSenderId),
                                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("token", token_android);
                                jsonObject.put("timeZone", "-2");
                                String json = jsonObject.toString();

                                AppSync sync = new AppSync(userFBID, json, userPicture, LoginActivity.this, token_android);
                                sync.getUserProfile();

                                registerUser(userFBID, userName, userEmail, json, token_android, userPicture);
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }).start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
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
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Profile profile = Profile.getCurrentProfile();
        ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
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
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                } //else { //permission denied, boo! Disable the functionality that depends on this permission.}
                return;
            }
        }
    }


    private void getFacebookHashKey() {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = getPackageManager().getPackageInfo("com.azinecllc.champy", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    private void initFacebookTokenTracker() {
        mCallbackManager = CallbackManager.Factory.create();
        mTokenTracker  = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {}
        };
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldprofile, Profile newprofile) {
                if (newprofile != null) {
                    userName = newprofile.getName();
                    userFBID = newprofile.getId();
                }
            }
        };
        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
    }


    private void registerUser(String fbID, String name, String email, String gcm, String token_android, String fb_photo) throws JSONException {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("facebookId", userFBID);
        jsonObject.put("AndroidOS", gcm);
        String string = jsonObject.toString();
        final String jwtString = Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT")
                .setPayload(string).signWith(SignatureAlgorithm.HS256, "secret").compact();

        NewUser newUser = retrofit.create(NewUser.class);
        Call<User> call = newUser.register(new LoginData(fbID, name, email));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                User decodedResponse = response.body();
                if (response.isSuccess()) {
                    Data user = decodedResponse.getData(); // data == user
                    String email = user.getEmail();
                    String user_name = user.getName();
                    String id = user.get_id();
                    String pushN = user.getProfileOptions().getPushNotifications().toString();
                    String newChallReq = user.getProfileOptions().getNewChallengeRequests().toString();
                    String acceptedYour = user.getProfileOptions().getAcceptedYourChallenge().toString();
                    String challengeEnd = user.getProfileOptions().getChallengeEnd().toString();

                    SessionManager sessionManager = SessionManager.getInstance(getApplicationContext());
                    sessionManager.setRefreshPending("true");
                    sessionManager.setRefreshFriends("true");
                    sessionManager.setRefreshOthers ("true");
                    sessionManager.createUserLoginSession(
                            user_name, email, userFBID, userPicture,
                            jwtString, id, pushN, newChallReq, acceptedYour,
                            challengeEnd, "true", "true", gcm, token_android);

                    sessionManager.setChampyOptions(
                            user.getAllChallengesCount().toString(),
                            user.getSuccessChallenges().toString(),
                            user.getInProgressChallengesCount().toString(),
                            user.getLevel().getNumber().toString());

                    UserController userController = new UserController(sessionManager, retrofit);
                    userController.updatePushIdentifier();
                    userController.uploadPhotoForAPI(fb_photo);

                    DailyRemindController drc = new DailyRemindController(getApplicationContext());
                    drc.enableDailyNotificationReminder();

                    Intent goToRoleActivity = new Intent(LoginActivity.this, RoleControllerActivity.class);
                    startActivity(goToRoleActivity);
                }
            }

            @Override
            public void onFailure(Throwable t) {}
        });

    }



}
