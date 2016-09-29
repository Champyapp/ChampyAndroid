package com.example.ivan.champy_v2.activity;

import android.app.Activity;
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

import com.android.debug.hv.ViewServer;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.helper.AppSync;
import com.example.ivan.champy_v2.helper.CHImageModule;
import com.example.ivan.champy_v2.helper.CHInitializeLogin;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.User.Data;
import com.example.ivan.champy_v2.model.User.LoginData;
import com.example.ivan.champy_v2.model.User.User;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "LoginActivity";
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private CallbackManager mCallbackManager;
    private String user_email, path_to_pic, name, fb_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.facebook.samples.hellofacebook", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, "KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.d(TAG, "KeyHash: not working" );
        }
        setContentView(R.layout.activity_login);

        try { initLayout();} catch (IOException e) { e.printStackTrace(); }

        mCallbackManager = CallbackManager.Factory.create();
        mTokenTracker  = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                String log = (newToken == null) ? "newToken = null" : "newToken != null";
                Log.i(TAG, log);
            }
        };
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldprofile, Profile newprofile) {
                if (newprofile != null) {
                    name = newprofile.getName();
                    fb_id = newprofile.getId();
                }
            }
        };
        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
        TextView textView = (TextView)findViewById(R.id.login_text);
        Typeface typeface = Typeface.createFromAsset(LoginActivity.this.getAssets(), "fonts/bebasneue.ttf");
        textView.setTypeface(typeface);
        ImageButton button = (ImageButton)findViewById(R.id.login_button);
        button.setOnClickListener(this);
        ViewServer.get(this).addWindow(this);
    }


    @Override
    public void onClick(View v) {
        OfflineMode offlineMode = new OfflineMode();
        Activity activity = LoginActivity.this;
        offlineMode.isConnectedToRemoteAPI(activity);
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile, email, user_friends"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                        try {
                            user_email = jsonObject.getString("email");
                            fb_id = jsonObject.getString("id");
                            name = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
                            Log.i(TAG, "UserEmail: " + user_email);
                            Log.i(TAG, "UserName: " + name);
                            Log.i(TAG, "Facebook: " + fb_id);
                            try {
                                URL profile_pic = new URL("https://g...content-available-to-author-only...k.com/" + fb_id + "/picture?type=large");
                                path_to_pic = profile_pic.toString();
                                Log.i(TAG, "PathToPic: " + path_to_pic);
                            } catch (MalformedURLException e) { e.printStackTrace(); }

                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String token_android;
                                        InstanceID instanceID = InstanceID.getInstance(LoginActivity.this);
                                        token_android = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("token", token_android);
                                        jsonObject.put("timeZone", "-2");
                                        String json = jsonObject.toString();

                                        Log.i(TAG, "JSON: " + json);
                                        Log.i(TAG, "GCM: "  + token_android);

                                        getUserData(fb_id, path_to_pic, json);
                                        registerUser(fb_id, name, user_email, json);

                                    } catch (Exception e) { Log.i(TAG, "Failed to complete token refresh", e); }
                                }
                            };
                            new Thread(runnable).start();

                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: canceled login");
            }

            @Override
            public void onError(FacebookException exception) { Log.i(TAG, "onError: " + exception); }
        });
    }


    public void getUserData(final String fb_id, final String path_to_pic, String gcm) throws JSONException {
        AppSync sync = new AppSync(fb_id, gcm, path_to_pic, this);
        sync.getToken(fb_id, gcm);
        sync.getUserProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
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
        ViewServer.get(this).removeWindow(this);
    }


    private void initLayout() throws IOException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Activity activity = LoginActivity.this;
                CHInitializeLogin CHInitializeLogin = new CHInitializeLogin(activity, getApplicationContext(), new CHImageModule(getApplicationContext()));
                CHInitializeLogin.Init();
            }
        });
    }


    private void registerUser(String facebookId, String name, String email, final String gcm) throws JSONException {
        final String API_URL = "http://46.101.213.24:3007";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("facebookId", fb_id);
        jsonObject.put("AndroidOS", gcm);
        String string2 = "{facebookId:'"+fb_id+"', AndroidOS:{token:'"+gcm+"', timeZone:2}";
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
                    String token_android;

                    Data data = decodedResponse.getData(); // data == user
                    String email = data.getEmail();
                    String user_name = data.getName();
                    String id = data.get_id();
                    String pushN = data.getProfileOptions().getPushNotifications().toString();
                    String newChallReq = data.getProfileOptions().getNewChallengeRequests().toString();
                    String acceptedYour = data.getProfileOptions().getAcceptedYourChallenge().toString();
                    String challegeEnd = data.getProfileOptions().getChallengeEnd().toString();

                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    sessionManager.setRefreshPending("false");
                    sessionManager.setRefreshFriends("false");
                    sessionManager.createUserLoginSession(user_name, email, fb_id, path_to_pic, jwtString, id, pushN, newChallReq, acceptedYour, challegeEnd, "true", gcm);
                    sessionManager.setChampyOptions(
                            data.getAllChallengesCount().toString(),
                            data.getSuccessChallenges().toString(),
                            data.getInProgressChallengesCount().toString(),
                            data.getLevel().getNumber().toString());
                    String api_path = null;
                    if (data.getPhoto() != null) {
                        // Context.getFilesDir().getPath()
                        String path =  "/data/data/com.example.ivan.champy_v2/app_imageDir/";
                        File f = new File(path, "profile.jpg");
                        if (!f.exists()) {
                            com.example.ivan.champy_v2.model.User.Photo photo = data.getPhoto();
                            api_path = API_URL + photo.getLarge();
                        }
                    }
                    Intent intent = new Intent(LoginActivity.this, RoleControllerActivity.class);
                    if (api_path == null) intent.putExtra("path_to_pic", path_to_pic);
                    else {
                        intent.putExtra("path_to_pic", api_path);
                        sessionManager.change_avatar(api_path);
                    }
                    intent.putExtra("name", user_name);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "VSE huynya: " + t);
            }
        });

    }

}

