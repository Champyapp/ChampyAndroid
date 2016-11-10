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

import com.android.debug.hv.ViewServer;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.helper.AppSync;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.User.Data;
import com.example.ivan.champy_v2.model.User.LoginData;
import com.example.ivan.champy_v2.model.User.User;
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
                            } catch (MalformedURLException e) { e.printStackTrace(); }
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
                                        getUserData(fb_id, path_to_pic, json);
                                        // make if statement here /\  -  \/
                                        registerUser(fb_id, name, user_email, json);
                                    } catch (Exception e) {Log.e(TAG, "error: ", e);}
                                }
                            }).start();
                        } catch (JSONException e) { e.printStackTrace(); }
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
            public void onCancel() {}

            @Override
            public void onError(FacebookException exception) {}
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


    private void getUserData(final String fb_id, final String path_to_pic, String gcm) throws JSONException {
        AppSync sync = new AppSync(fb_id, gcm, path_to_pic, this);
        //sync.getToken(fb_id, gcm);
        sync.getUserProfile();
    }


    private void registerUser(String facebookId, String name, String email, final String gcm) throws JSONException {
        final String API_URL = "http://46.101.213.24:3007";
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
                    sessionManager.createUserLoginSession(user_name, email, fb_id, path_to_pic, jwtString, id, pushN, newChallReq, acceptedYour, challegeEnd, "true", gcm);
                    sessionManager.setChampyOptions(
                            user.getAllChallengesCount().toString(),
                            user.getSuccessChallenges().toString(),
                            user.getInProgressChallengesCount().toString(),
                            user.getLevel().getNumber().toString());


                    String api_path = null;
                    // если у юзера есть фото на facebook-e
                    if (user.getPhoto() != null) {
                        // создаем путь к файлу
                        @SuppressLint("SdCardPath") String sdCardPath = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
                        //String internalPath = "/android/data/com.azinecllc.champy/Images";
                        // создаем файл по нашему пути
                        File profilePhoto = new File(sdCardPath, "profile.jpg");
                        // если такой фотки не существует (так и должно быть)
                        if (!profilePhoto.exists()) {
                            // то мы заливаем нашу фотку на API
                            com.example.ivan.champy_v2.model.User.Photo photo = user.getPhoto();
                            api_path = API_URL + photo.getLarge();
                        }
                    }

                    Intent goToRoleControllerActivity = new Intent(LoginActivity.this, RoleControllerActivity.class);
                    // если у нас не получилось сделать выше написаное и api_path = null
                    if (api_path == null) {
                        // то... мы стягиваем стандартную фотку facebook-а и заливаем её
                        goToRoleControllerActivity.putExtra("path_to_pic", path_to_pic);
                        sessionManager.change_avatar(path_to_pic);
                    } else {
                        // ну... а если получилось стянуть норм фотку, то заливаем её
                        goToRoleControllerActivity.putExtra("path_to_pic", api_path);
                        sessionManager.change_avatar(api_path);
                    }
//                    MyNotificationController notification = new MyNotificationController(getApplicationContext());
//                    notification.activateDailyNotificationReminder();
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
