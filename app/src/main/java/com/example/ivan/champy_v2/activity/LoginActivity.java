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
import com.example.ivan.champy_v2.helper.CHImage;
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

public class LoginActivity extends AppCompatActivity {

    private boolean ok;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private CallbackManager mCallbackManager;
    private String user_email, path_to_pic, name, fb_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        final SessionManager sessionManager = new SessionManager(getApplicationContext());
//        if (sessionManager.isUserLoggedIn()) {
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.facebook.samples.hellofacebook", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("LoginActivity", "KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.d("LoginActivity", "KeyHash: not working" );
        }
        setContentView(R.layout.activity_login);

        try {
            initLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCallbackManager = CallbackManager.Factory.create();
        mTokenTracker  = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                if (newToken == null) {
                }
                else {
                }
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

        //Log.i("KeyHash", "trying");
        ImageButton button = (ImageButton)findViewById(R.id.login_button);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = LoginActivity.this;
                OfflineMode offlineMode = new OfflineMode();
                offlineMode.isConnectedToRemoteAPI(activity);
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile, email, user_friends"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        final AccessToken accessToken = loginResult.getAccessToken();
                        Profile profile = Profile.getCurrentProfile();
                        final String[] URL = {""};
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //Log.i("LoginActivity", "OnClick response: " + response.toString());
                                try {
                                    user_email = object.getString("email");
                                    fb_id = object.getString("id");
                                    name = object.getString("first_name") + " " + object.getString("last_name");
                                    Log.i("LoginActivity", "UserEmail: " + user_email);
                                    Log.i("LoginActivity", "UserName: " + name);
                                    Log.i("LoginActivity", "Facebook: " + fb_id);
                                    try {
                                        URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                        path_to_pic = profile_pic.toString();
                                        Log.i("LoginActivity", "PathToPic: " + path_to_pic);
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
                                                Log.i("LoginActivity", "JSON: " + json);
                                                Log.i("LoginActivity", "GCM: "  + token_android);

                                                getUserData(fb_id, path_to_pic, json);
                                                registerUser(fb_id, name, user_email, json);

                                            } catch (Exception e) {
                                                Log.i("LoginActivity", "Failed to complete token refresh", e);
                                            }
                                        }
                                    }).start();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
            }
        };
        button.setOnClickListener(onClickListener);
        ViewServer.get(this).addWindow(this);
    }

    public void getUserData(final String fb_id, final String path_to_pic, String gcm) throws JSONException {

        AppSync sync = new AppSync(fb_id, gcm, path_to_pic, this);
        sync.getToken(fb_id, gcm);
        sync.getUserProfile();
    }

//    public void getUserFriendsInfo(final String gcm) {
//        final String API_URL = "http://46.101.213.24:3007";
//        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
//        final NewUser newUser = retrofit.create(NewUser.class);
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        DBHelper dbHelper = new DBHelper(this);
//        final SQLiteDatabase db = dbHelper.getWritableDatabase();
//        int clearCount = db.delete("mytable", null, null);
//        final ContentValues cv = new ContentValues();
//
//        final GraphRequest request = GraphRequest.newMyFriendsRequest(
//                AccessToken.getCurrentAccessToken(),
//                new GraphRequest.GraphJSONArrayCallback() {
//                    @Override
//                    public void onCompleted(JSONArray array, GraphResponse response) {
//                        for (int i = 0; i < array.length(); i++) {
//                            try {
//                                final String fb_id = array.getJSONObject(i).getString("id");
//                                final String user_name = array.getJSONObject(i).getString("name");
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("facebookId", fb_id);
//                                jsonObject.put("AndroidOS", gcm);
//                                String string = jsonObject.toString();
//                                final String jwtString = Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setPayload(string).signWith(SignatureAlgorithm.HS256, "secret").compact();
//                                Call<User> call = newUser.getUserInfo(jwtString);
//                                call.enqueue(new Callback<User>() {
//                                    @Override
//                                    public void onResponse(Response<User> response, Retrofit retrofit) {
//                                        if (response.isSuccess()) {
//                                            Data data = response.body().getData();
//                                            String photo = null;
//
//                                            if (data.getPhoto() != null)
//                                                photo = API_URL + data.getPhoto().getMedium();
//                                            else {
//                                                try {
//                                                    URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
//                                                    photo = profile_pic.toString();
//                                                } catch (MalformedURLException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                            String name = data.getName();
//                                            cv.put("name", name);
//                                            cv.put("photo", photo);
//                                            cv.put("user_id", data.get_id());
//                                            cv.put("challenges", ""+data.getAllChallengesCount());
//                                            cv.put("wins", ""+data.getSuccessChallenges());
//                                            cv.put("total", ""+data.getScore());
//                                            cv.put("level", ""+data.getLevel().getNumber());
//                                            if (!checkPendingFriends(data.get_id())) db.insert("mytable", null, cv);
//                                            else Log.d("LoginActivity", "GetFriends | DBase: not added");
//                                        } else {
//                                            URL profile_pic = null;
//                                            String photo = null;
//                                            try {
//                                                profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
//                                                photo = profile_pic.toString();
//                                            } catch (MalformedURLException e) {
//                                                e.printStackTrace();
//                                            }
//                                            cv.put("name", user_name);
//                                            cv.put("photo", photo);
//                                            cv.put("challenges", "0");
//                                            cv.put("wins", "0");
//                                            cv.put("total", "0");
//                                            cv.put("level", "0");
//                                            db.insert("mytable", null, cv);
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(Throwable t) {}
//                                });
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//
//                        // intent go to RoleActivity
//
//                    }
//                });
//        request.executeAndWait();
//    }
//
//
//    // get pending
//    public Boolean checkPendingFriends(String id) {
//        DBHelper dbHelper = new DBHelper(this);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Boolean ok = false;
//        Cursor c = db.query("pending", null, null, null, null, null, null);
//        if (c.moveToFirst()) {
//            int index = c.getColumnIndex("user_id");
//            do {
//                String user_id = c.getString(index);
//                if (user_id.equals(id)) {
//                    ok = true;
//                    break;
//                }
//            } while (c.moveToNext());
//        }
//        c.close();
//        return ok;
//    }

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


    public void initLayout() throws IOException {
        CHInitializeLogin CHInitializeLogin = new CHInitializeLogin(this, getApplicationContext(), new CHImageModule(getApplicationContext()));
        CHInitializeLogin.Init();
    }


    private void registerUser(String facebookId, String name, String email, final String gcm) throws JSONException {
        final String API_URL = "http://46.101.213.24:3007";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
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
                Log.d("LoginActivity", "Response Status: register " + response.code());
                User decodedResponse = response.body();
                if (response.isSuccess()) {
                    if (response.isSuccess()) {
                    /* Log.d("TAG", "Status: " + decodedResponse.getDescription());
                    Log.d("TAG", "Status: "+jwtString);*/
                        String token_android;

                        Data data = decodedResponse.getData(); // data == user
                        String email = data.getEmail();
                        String user_name = data.getName();
                        String id = data.get_id();
                        String pushN = data.getProfileOptions().getPushNotifications().toString();
                        String newChallReq = data.getProfileOptions().getNewChallengeRequests().toString();
                        String acceptedYour = data.getProfileOptions().getAcceptedYourChallenge().toString();
                        String challegeEnd = data.getProfileOptions().getChallengeEnd().toString();
                        //Log.d("LoginActivity", "ID: " + id);
                        //Log.d("LoginActivity", "FB: " + fb_id);

                        // "http://graph.facebook.com/" + fb_id + "/picture?type=large&redirect=true&width=500&height=500"
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
                        String facebookPhotoUrl = "http://graph.facebook.com/" + fb_id + "/picture?type=large&redirect=true&width=500&height=500";
                        if (data.getPhoto() != null) {
                            String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
                            File f = new File(path, "profile.jpg");
                            if (!f.exists()) {
                                com.example.ivan.champy_v2.model.User.Photo photo = data.getPhoto();
                                api_path = API_URL + photo.getLarge();
                                //Log.i("LoginActivity", "Image: " + api_path);
                            }
                        } else {
                            CHImage imageSaver = new CHImage();
                            try {
                                imageSaver.saveImage(facebookPhotoUrl, "/data/data/com.example.ivan.champy_v2/app_imageDir/profile.jpg", false, getApplicationContext());

                            } catch (IOException e) {
                                e.printStackTrace();
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
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("LoginActivity", "VSE huynya");
            }
        });

    }


//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Log.d("LoginActivity", "DownLoadImageTask: doInBackground --- url display: " + urldisplay);
//
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            Log.i("Icon :", mIcon11.toString());
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            // Do your staff here to save image
//            String string = saveToInternalStorage(result);
//            loadImageFromStorage(string);
//        }
//
//    }
//
//
//    private String saveToInternalStorage(Bitmap bitmapImage){
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        // path to /data/data/yourapp/app_data/imageDir
//        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//        // Create imageDir
//        File mypath = new File(directory,"profile.jpg");
//
//        Log.d(TAG, "MY_PATH: "+mypath.toString());
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(mypath);
//            // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return directory.getAbsolutePath();
//    }
//
//
//    public void loadImageFromStorage(String path) {
//        try {
//            File f=new File(path, "profile.jpg");
//            File file = new File(path, "blured2.jpg");
//            if (file.exists()) {
//                return;
//            } else {
//                file.createNewFile();
//                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//
//                Blur blur = new Blur();
//
//                Bitmap blured = blur.blurRenderScript(getApplicationContext(), b, 10);
//
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                blured.compress(Bitmap.CompressFormat.PNG, 0, bos);
//                byte[] bitmapdata = bos.toByteArray();
//
//                FileOutputStream fos = new FileOutputStream(file);
//                fos.write(bitmapdata);
//                fos.flush();
//                fos.close();
//                Log.d(TAG, "Image: Blured");
//            }
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void Upload_photo(String path, String id, String token) {
//        final String API_URL = "http://46.101.213.24:3007";
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(API_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        File f=new File(path);
//
//        Log.d(TAG, "Status: " + f);
//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), f);
//
//        Update_user update_user = retrofit.create(Update_user.class);
//        Call<User> call = update_user.update_photo(id, token, requestBody);
//        Log.d(TAG, "Status: RUN");
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Response<User> response, Retrofit retrofit) {
//                if (response.isSuccess()) {
//                    Log.d(TAG, "Status: photo_uploaded");
//                } else Log.d(TAG, "Status :" + response.code());
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Log.d(TAG, "Status: "+t);
//            }
//        });
//    }

}

