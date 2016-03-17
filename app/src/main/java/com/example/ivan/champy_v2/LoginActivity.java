package com.example.ivan.champy_v2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.model.Friend.Datum;
import com.example.ivan.champy_v2.model.Friend.Friend;
import com.example.ivan.champy_v2.model.Friend.Friend_;
import com.example.ivan.champy_v2.model.Friend.Owner;
import com.example.ivan.champy_v2.model.User.Data;
import com.example.ivan.champy_v2.model.User.LoginData;
import com.example.ivan.champy_v2.model.User.User;
import com.example.ivan.champy_v2.model.active_in_progress.Challenge;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static java.lang.Math.round;

public class LoginActivity extends AppCompatActivity {
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private CallbackManager mCallbackManager;
    private boolean ok;


    private String user_email, path_to_pic, name, fb_id;


    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SessionManager sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.isUserLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }


        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        try {
            Init();
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

        ImageButton button = (ImageButton)findViewById(R.id.login_button);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Activity activity = LoginActivity.this;
                OfflineMode offlineMode = new OfflineMode();
                if (!offlineMode.isInternetAvailable(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, "Lost internet connection!", Toast.LENGTH_LONG).show();
                    return;
                }

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
                                Log.i("LoginActivity", response.toString());
                                try {
                                    user_email = object.getString("email");
                                    fb_id = object.getString("id");
                                    name = object.getString("first_name") + " " + object.getString("last_name");

                                    Log.d(TAG, user_email);
                                    Log.d(TAG, name);
                                    try {
                                        URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                        Log.i("profile_pic", profile_pic + "");
                                        path_to_pic = profile_pic.toString();

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG, path_to_pic);
                                    getFriends();

                                    Register_User(fb_id, name, user_email);
                                    getUserData(fb_id, path_to_pic, accessToken);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
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

    }

    public void Init() throws IOException {
        InitializeLogin initializeLogin = new InitializeLogin(this, getApplicationContext(), new ImageModule(getApplicationContext()));
        initializeLogin.Init();
    }







    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {


        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Log.d(TAG, "lalala: " + urldisplay);

            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            Log.i("Icon :", mIcon11.toString());
            return mIcon11;
        }


        protected void onPostExecute(Bitmap result) {
            // Do your staff here to save image
            String string = saveToInternalSorage(result);
            loadImageFromStorage(string);
        }
    }

    private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        Log.d(TAG, "MY_PATH: "+mypath.toString());

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }



    private void Register_User(String facebookId, String name, String email)
    {

        final String API_URL = "http://46.101.213.24:3007";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final String jwtString = Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setPayload("{\n" +
                "  \"facebookId\": \"" + fb_id + "\"\n" +
                "}").signWith(SignatureAlgorithm.HS256, "secret").compact();

        NewUser newUser = retrofit.create(NewUser.class);

        Call<User> call = newUser.register(new LoginData(facebookId, name, email));

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                Log.d(TAG, "Status: register " + response.code());
                User decodedResponse = response.body();
                if (response.isSuccess()) {
                    if (response.isSuccess()) {
                 /*   Log.d("TAG", "Status: " + decodedResponse.getDescription());
                    Log.d("TAG", "Status: "+jwtString);*/

                        Data data = decodedResponse.getData();
                        String email = data.getEmail();
                        String user_name = data.getName();
                        String id = data.get_id();
                        String pushN = data.getProfileOptions().getPushNotifications().toString();
                        String newChallReq = data.getProfileOptions().getNewChallengeRequests().toString();
                        String acceptedYour = data.getProfileOptions().getAcceptedYourChallenge().toString();
                        String challegeEnd = data.getProfileOptions().getChallengeEnd().toString();
                        Log.d(TAG, "Status: " + id);
                        Log.d(TAG, "FB: " + fb_id);
                        SessionManager sessionManager = new SessionManager(getApplicationContext());
                        sessionManager.setRefreshPending("false");
                        sessionManager.createUserLoginSession(user_name, email, fb_id, path_to_pic, jwtString, id, pushN, newChallReq, acceptedYour, challegeEnd, "true");
                        sessionManager.setChampyOptions(data.getAllChallengesCount().toString(), data.getSuccessChallenges().toString(), data.getScore().toString(), data.getLevel().getNumber().toString());
                        String api_path = null;
                        if (data.getPhoto() != null) {
                            String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
                            File f = new File(path, "profile.jpg");
                            if (!f.exists()) {
                                com.example.ivan.champy_v2.model.User.Photo photo = data.getPhoto();
                                api_path = API_URL + photo.getLarge();
                                Log.d(TAG, "Image: " + api_path);
                            }
                        }

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                Log.d(TAG, "VSE huynya");
            }
        });
    }
    private void getUserData(final String fb_id, final String path_to_pic, final AccessToken accessToken)
    {
        final String API_URL = "http://46.101.213.24:3007";

        final String jwtString = Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setPayload("{\n" +
                "  \"facebookId\": \"" + fb_id + "\"\n" +
                "}").signWith(SignatureAlgorithm.HS256, "secret").compact();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NewUser newUser = retrofit.create(NewUser.class);
        Call<User> call = newUser.getUserInfo(jwtString);

        final String finalId = fb_id;
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                Log.d(TAG, "Status: get_user" + response.code());
                User decodedResponse = response.body();
                if (response.isSuccess()) {
                 /*   Log.d("TAG", "Status: " + decodedResponse.getDescription());
                    Log.d("TAG", "Status: "+jwtString);*/

                    Data data = decodedResponse.getData();
                    String email = data.getEmail();
                    final String user_name = data.getName();
                    final String id = data.get_id();
                    String pushN = data.getProfileOptions().getPushNotifications().toString();
                    String newChallReq = data.getProfileOptions().getNewChallengeRequests().toString();
                    String acceptedYour = data.getProfileOptions().getAcceptedYourChallenge().toString();
                    String challegeEnd = data.getProfileOptions().getChallengeEnd().toString();
                    Log.d(TAG, "Status: " + id + " " +fb_id);

                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    sessionManager.setRefreshPending("false");
                    sessionManager.setRefreshFriends("false");
                    sessionManager.createUserLoginSession(user_name, email, fb_id, path_to_pic, jwtString, id, pushN, newChallReq, acceptedYour, challegeEnd, "true");
                    sessionManager.setChampyOptions(data.getAllChallengesCount().toString(), data.getSuccessChallenges().toString(), data.getScore().toString(), data.getLevel().getNumber().toString());
                    DBHelper dbHelper = new DBHelper(LoginActivity.this);
                    final SQLiteDatabase db = dbHelper.getWritableDatabase();
                    int clearCount = db.delete("pending", null, null);
                    final ContentValues cv = new ContentValues();
                    final String  user_id = id;

                    com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);
                    Call<com.example.ivan.champy_v2.model.Friend.Friend> call = friends.getUserFriends(id, jwtString);
                    call.enqueue(new Callback<Friend>() {
                        @Override
                        public void onResponse(Response<Friend> response, Retrofit retrofit) {
                            if (response.isSuccess()){
                                List<Datum> data = response.body().getData();
                                Log.d(TAG, "Status: "+data.size());
                                for (int i=0; i<data.size(); i++){
                                    Datum datum = data.get(i);
                                    Log.d(TAG, "Status: "+response.body().toString());
                                    if (datum.getFriend() != null) {
                                    if (datum.getStatus().toString().equals("false")) {
                                        Log.d(TAG, "Status: "+datum.getOwner().getId());
                                        if (datum.getOwner().getId().equals(user_id)) {
                                            Friend_ friend = datum.getFriend();
                                            Log.d(TAG, "Status: "+friend);
                                            if (friend.getName() != null)
                                            cv.put("name", friend.getName());
                                            Log.d(TAG, "Status: "+friend.getPhoto());
                                            if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                            else cv.put("photo", "");
                                            Log.d(TAG, "Friend");
                                            cv.put("user_id", friend.getId());
                                            cv.put("owner", "false");
                                            db.insert("pending", null, cv);}
                                        else if (datum.getStatus().toString().equals("true")){
                                            Friend_ friend = datum.getFriend();
                                            Log.d(TAG, "Status: "+friend);
                                            if (friend.getName() != null)
                                                cv.put("name", friend.getName());
                                            Log.d(TAG, "Status: "+friend.getPhoto());
                                            if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                            else cv.put("photo", "");
                                            Log.d(TAG, "Friend");
                                            cv.put("user_id", friend.getId());
                                            cv.put("owner", "false");
                                            db.insert("friends", null, cv);
                                        }
                                        else {
                                            Owner friend = datum.getOwner();
                                            cv.put("name", friend.getName());
                                            if (friend.getPhoto() != null) cv.put("photo", friend.getPhoto().getMedium());
                                            else cv.put("photo", "");
                                            Log.d(TAG, "Owner");
                                            cv.put("user_id", friend.getId());
                                            cv.put("owner", "true");
                                            db.insert("pending", null, cv);
                                        }
                                    }
                                }
                                }
                            }
                        }
                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
                    clearCount = db.delete("myChallenges", null, null);
                    ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
                    final long unixTime = System.currentTimeMillis() / 1000L;
                    String update = "1457019726";
                    Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call1 = activeInProgress.getActiveInProgress(id, update, jwtString);
                    call1.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
                        @Override
                        public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                List<com.example.ivan.champy_v2.model.active_in_progress.Datum> data = response.body().getData();
                                for (int i = 0; i < data.size(); i++) {
                                    com.example.ivan.champy_v2.model.active_in_progress.Datum datum = data.get(i);
                                    Challenge challenge = datum.getChallenge();

                                    String desctiption = challenge.getDescription();
                                    int end = datum.getEnd();
                                    int days = round((end - unixTime) / 86400);
                                    String duration = "" + days;
                                    String challenge_id = challenge.get_id();
                                    cv.put("name", "Self Improvement");
                                    cv.put("description", desctiption);
                                    cv.put("duration", duration);
                                    cv.put("challenge_id", challenge_id);
                                    db.insert("myChallenges", null, cv);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });
                    String api_path = null;
                    if (data.getPhoto() != null){
                        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
                        File f=new File(path, "profile.jpg");
                        if (!f.exists()){
                            com.example.ivan.champy_v2.model.User.Photo photo = data.getPhoto();
                            api_path = API_URL+photo.getLarge();
                            Log.d(TAG, "Image: "+api_path);
                        }
                    }

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    if (api_path == null) intent.putExtra("path_to_pic", path_to_pic);
                    else {
                        intent.putExtra("path_to_pic", api_path);
                        sessionManager.change_avatar(api_path);
                    }
                    intent.putExtra("name", user_name) ;
                    startActivity(intent);
                }
                else Log.d("TAG", "Status: "+decodedResponse);
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "VSE huynya");
            }
        });

    }


    public void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            File file = new File(path, "blured2.jpg");
            if (file.exists()) {
                return;
            } else {
                file.createNewFile();
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

                Blur blur = new Blur();

                Bitmap blured = blur.blurRenderScript(getApplicationContext(), b, 10);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                blured.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
                Log.d(TAG, "Image: Blured");

            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Boolean getContact(String id) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Boolean ok = false;
        Cursor c = db.query("pending", null, null, null, null, null, null);
        if (c.moveToFirst()) {

            int index = c.getColumnIndex("user_id");
            do {
                String user_id = c.getString(index);
                if (user_id.equals(id)) {
                    ok = true;
                    break;
                }
            } while (c.moveToNext());
        } else
            Log.i("stat", "0 rows");
        c.close();
        return ok;
    }

    public void getFriends()
    {
        final String API_URL = "http://46.101.213.24:3007";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final NewUser newUser = retrofit.create(NewUser.class);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete("mytable", null, null);
        final ContentValues cv = new ContentValues();

        final GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray array, GraphResponse response) {
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                final String fb_id = array.getJSONObject(i).getString("id");
                                final String user_name = array.getJSONObject(i).getString("name");
                                final String jwtString = Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setPayload("{\n" +
                                        "  \"facebookId\": \"" + fb_id + "\"\n" +
                                        "}").signWith(SignatureAlgorithm.HS256, "secret").compact();
                                Call<User> call = newUser.getUserInfo(jwtString);
                                call.enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Response<User> response, Retrofit retrofit) {
                                        if (response.isSuccess()) {
                                            Data data = response.body().getData();
                                            String photo = null;

                                            if (data.getPhoto() != null)
                                                photo = API_URL + data.getPhoto().getMedium();
                                            else {
                                                try {
                                                    URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                                    photo = profile_pic.toString();
                                                } catch (MalformedURLException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            String name = data.getName();
                                            cv.put("name", name);
                                            cv.put("photo", photo);
                                            cv.put("user_id", data.get_id());
                                            if (!getContact(data.get_id()))
                                                db.insert("mytable", null, cv);
                                            else Log.d(TAG, "DBase: not added");
                                        } else {
                                            URL profile_pic = null;
                                            String photo = null;
                                            try {
                                                profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                                photo = profile_pic.toString();
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            cv.put("name", user_name);
                                            cv.put("photo", photo);
                                            db.insert("mytable", null, cv);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
        request.executeAndWait();
    }

}

