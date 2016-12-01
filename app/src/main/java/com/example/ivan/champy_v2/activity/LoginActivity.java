package com.example.ivan.champy_v2.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.controller.DailyRemindController;
import com.example.ivan.champy_v2.helper.AppSync;
import com.example.ivan.champy_v2.helper.UpdatePushIdentifier;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.user.Data;
import com.example.ivan.champy_v2.model.user.LoginData;
import com.example.ivan.champy_v2.model.user.User;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

import static com.example.ivan.champy_v2.utils.Constants.API_URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "LoginActivity";
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private CallbackManager mCallbackManager;
    private String user_email, path_to_pic, name, fb_id;
    public View spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        getFacebookHashKey(); // mast be above "setContentView"

        setContentView(R.layout.activity_login);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
        TextView loginText = (TextView)findViewById(R.id.login_text);
        spinner = findViewById(R.id.loadingPanel);
        loginText.setTypeface(typeface);

        initFacebookTokenTracker();

        ImageButton buttonLogin = (ImageButton)findViewById(R.id.login_button);
        buttonLogin.setOnClickListener(this);
        ViewServer.get(this).addWindow(this);
    }

    @Override
    public void onClick(View v) {
        OfflineMode offlineMode = new OfflineMode();
        offlineMode.isConnectedToRemoteAPI(this);
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Collections.singletonList("public_profile, email, user_friends"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (!loginResult.getAccessToken().getDeclinedPermissions().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Can't get permissions from Facebook", Toast.LENGTH_LONG).show();
                    return;
                }
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        spinner.setVisibility(View.VISIBLE);
                        try {
                            user_email = object.getString("email");
                            fb_id = object.getString("id");
                            name = object.getString("first_name") + " " + object.getString("last_name");
                            try {
                                URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                path_to_pic = profile_pic.toString();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        String token_android;
                                        InstanceID instanceID = InstanceID.getInstance(LoginActivity.this);
                                        token_android = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("token", token_android);
                                        jsonObject.put("timeZone", "-2");
                                        String json = jsonObject.toString();
                                        getUserData(fb_id, path_to_pic, json, token_android);
                                        // make if statement here /\  -  \/
                                        registerUser(fb_id, name, user_email, json, token_android);
                                    } catch (Exception e) {
                                        Log.e(TAG, "error: ", e);
                                    }
                                }
                            }).start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onCompleted: No Permission For Email Or Friends List");
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
                spinner.setVisibility(View.INVISIBLE);
//                spinner = findViewById(R.id.loadingPanel);
//                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();
                Toast.makeText(LoginActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onCancel: LOGIN CANCELED");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "onError: LOGIN FAILED");
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
//        spinner.setVisibility(View.INVISIBLE);
        ViewServer.get(this).removeWindow(this);
    }


    private void getFacebookHashKey() {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = getPackageManager().getPackageInfo("com.facebook.samples.hellofacebook", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, "KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.d(TAG, "KeyHash: not working" );
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
                if (newprofile != null) { name = newprofile.getName(); fb_id = newprofile.getId(); }
            }
        };
        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
    }


    private void getUserData(final String fb_id, final String path_to_pic, String gcm, String token_android) throws JSONException {
        AppSync sync = new AppSync(fb_id, gcm, path_to_pic, this, token_android);
        //sync.getToken(fb_id, gcm);
        sync.getUserProfile();
    }


    private void registerUser(String facebookId, String name, String email, final String gcm, String token_android) throws JSONException {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("facebookId", fb_id);
        jsonObject.put("AndroidOS", gcm);
        String string = jsonObject.toString();
        final String jwtString = Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setPayload(string).signWith(SignatureAlgorithm.HS256, "secret").compact();

        NewUser newUser = retrofit.create(NewUser.class);
        Call<User> call = newUser.register(new LoginData(facebookId, name, email));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                Log.d(TAG, "Response Status: register " + response.code());
                User decodedResponse = response.body();
                if (response.isSuccess()) {
                    Data user = decodedResponse.getData(); // data == user
                    String email = user.getEmail();
                    String user_name = user.getName();
                    String id = user.get_id();
                    String pushN = user.getProfileOptions().getPushNotifications().toString();
                    String newChallReq = user.getProfileOptions().getNewChallengeRequests().toString();
                    String acceptedYour = user.getProfileOptions().getAcceptedYourChallenge().toString();
                    String challegeEnd = user.getProfileOptions().getChallengeEnd().toString();

                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    sessionManager.setRefreshPending("false");
                    sessionManager.setRefreshFriends("false");
                    sessionManager.createUserLoginSession(
                            user_name, email, fb_id, path_to_pic,
                            jwtString, id, pushN, newChallReq, acceptedYour,
                            challegeEnd, "true", "true", gcm, token_android);

                    sessionManager.setChampyOptions(
                            user.getAllChallengesCount().toString(),
                            user.getSuccessChallenges().toString(),
                            user.getInProgressChallengesCount().toString(),
                            user.getLevel().getNumber().toString());

                    UpdatePushIdentifier pushIdentifier = new UpdatePushIdentifier();
                    pushIdentifier.updatePushIdentifier(sessionManager);

                    DailyRemindController dailyRemind = new DailyRemindController(getApplicationContext());
                    dailyRemind.enableDailyNotificationReminder();


                    String api_path = null;
                    // if user has photo on facebook
                    if (user.getPhoto() != null) {
                        // we make a path for 'file'
                        @SuppressLint("SdCardPath")
                        String sdCardPath = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
                        // also we make a new 'file'
                        File profilePhoto = new File(sdCardPath, "profile.jpg");
                        // if user's photo file is not exist (it's mean in ChampyApp and it's must be true)
                        if (!profilePhoto.exists()) {
                            // so, we make path for user's photo
                            com.example.ivan.champy_v2.model.user.Photo photo = user.getPhoto();
                            api_path = API_URL + photo.getLarge();
                        }
                    }

                    Intent goToRoleControllerActivity = new Intent(LoginActivity.this, RoleControllerActivity.class);
                    // if we could not do the above written code and api = null
                    if (api_path == null) {
                        // then we trying to take user's photo from facebook and push her to api
                        goToRoleControllerActivity.putExtra("path_to_pic", path_to_pic);
                        sessionManager.change_avatar(path_to_pic);
                    } else {
                        // else we get existence photo from file
                        goToRoleControllerActivity.putExtra("path_to_pic", api_path);
                        sessionManager.change_avatar(api_path);
                    }

                    goToRoleControllerActivity.putExtra("name", user_name);
                    startActivity(goToRoleControllerActivity);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "VSE huynya");
            }
        });

    }



}
