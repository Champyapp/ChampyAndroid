package com.example.ivan.champy_v2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.Photo;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.interfaces.Update_user;
import com.example.ivan.champy_v2.model.User.Delete;
import com.example.ivan.champy_v2.model.User.Profile_data;
import com.example.ivan.champy_v2.model.User.User;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final private String API_URL = "http://46.101.213.24:3007";
    final private String TAG = "myLogs";
    HashMap<String, String> map = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager1 = new SessionManager(getApplicationContext());
        if (!sessionManager1.isUserLoggedIn()) {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        setupUI(findViewById(R.id.settings_layout));



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);
        int count = check_pending();
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
        view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        if (count == 0) hideItem();

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        final String name = user.get("name");
        String id = user.get("id");
        String token = user.get("token");
        String pushN = user.get("pushN");
        String newChallReq = user.get("newChallReq");
        String acceptedYour = user.get("acceptedYour");
        String challengeEnd = user.get("challengeEnd");
        map.put("joinedChampy", "true");
        map.put("friendRequests", "true");
        map.put("challengeConfirmation", "true");
        map.put("challengeEnd", challengeEnd);
        map.put("reminderTime", "17");
        map.put("challengesForToday", "true");
        map.put("acceptedYourChallenge", acceptedYour);
        map.put("newChallengeRequests", newChallReq);
        map.put("pushNotifications", pushN);

        Switch switch1 = (Switch) findViewById(R.id.switch1);
        if (pushN.equals("true")) {
            switch1.setChecked(true);
        }
        else {
            switch1.setChecked(false);
        }
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) map.put("pushNotifications", "true");
                else map.put("pushNotifications", "false");
            }
        });
        Switch switch2 = (Switch) findViewById(R.id.switch2);
        if (newChallReq.equals("true")) {
            switch2.setChecked(true);
        }
        else {
            switch2.setChecked(false);
        }
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Status: " + isChecked);
                if (isChecked) map.put("newChallengeRequests", "true");
                else map.put("newChallengeRequests", "false");
            }
        });

        Switch switch3 = (Switch) findViewById(R.id.switch3);
        if (acceptedYour.equals("true")) {
            switch3.setChecked(true);
        }
        else {
            switch3.setChecked(false);
        }
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) map.put("acceptedYourChallenge", "true");
                else map.put("acceptedYourChallenge", "false");
            }
        });

        Switch switch4 = (Switch) findViewById(R.id.switch4);
        if (challengeEnd.equals("true")){
            switch4.setChecked(true);
        }
        else {
            switch4.setChecked(false);
        }
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) map.put("challengeEnd", "true");
                else map.put("challengeEnd", "false");
            }
        });


        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);

        ImageView profile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        TextView textView = (TextView) headerLayout.findViewById(R.id.tvUserName);
        textView.setText(name);
        Typeface typeface = Typeface.createFromAsset(SettingsActivity.this.getAssets(), "fonts/bebasneue.ttf");

        textView = (TextView)findViewById(R.id.name);
        textView.setText(name);
        textView.setTypeface(typeface);

        textView = (TextView)findViewById(R.id.textView9);
        textView.setText("Level "+sessionManager.getChampyOptions().get("level")+ " Champy");

        textView = (TextView)findViewById(R.id.textView17);
        textView.setTypeface(typeface);
        textView = (TextView)findViewById(R.id.textView10);
        textView.setTypeface(typeface);
        textView = (TextView)findViewById(R.id.textView18);
        textView.setTypeface(typeface);


        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(profile);
        profile = (ImageView) findViewById(R.id.img_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isConnectedToRemoteAPI(SettingsActivity.this)) {
                    Update_profile(map);
                    Intent intent = new Intent(SettingsActivity.this, Photo.class);
                    startActivity(intent);
                }
            }
        });
        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).override(130, 130).into(profile);

        try {
            Drawable dr = Init("/data/data/com.example.ivan.champy_v2/app_imageDir/");
            ImageView imageView = (ImageView) headerLayout.findViewById(R.id.slide_background);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageDrawable(dr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final TextView change_name = (TextView)findViewById(R.id.textView11);
        change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_name.setVisibility(View.INVISIBLE);
                TextView textView1 = (TextView) findViewById(R.id.textView16);
                textView1.setVisibility(View.VISIBLE);

                final EditText editText = (EditText) findViewById(R.id.new_name);
                editText.setVisibility(View.VISIBLE);
                editText.setText(name);

                ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton4);
                imageButton.setVisibility(View.VISIBLE);

                findViewById(R.id.view11).setVisibility(View.VISIBLE);

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OfflineMode offlineMode = new OfflineMode();
                        if (!offlineMode.isInternetAvailable(SettingsActivity.this)){
                            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_warn, 0);
                            Toast.makeText(SettingsActivity.this, "Lost Internet Connection! Try again later!", Toast.LENGTH_SHORT).show();
                        } if (editText.getText().toString().trim().length() > 50){
                            Toast.makeText(SettingsActivity.this, "Name is too long!!!", Toast.LENGTH_SHORT).show();
                        } else if (editText.getText().toString().isEmpty() || editText.getText().toString().startsWith(" ")
                                || editText.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Name field is empty!", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            String newName = editText.getText().toString().trim();
                            SessionManager sessionManager = new SessionManager(getApplicationContext());
                            sessionManager.change_name(newName);

                            Set_new_name(newName);

                            TextView textView = (TextView)findViewById(R.id.name);
                            textView.setText(editText.getText().toString());

                            change_name.setVisibility(View.VISIBLE);
                            TextView textView1 = (TextView) findViewById(R.id.textView16);
                            textView1.setVisibility(View.GONE);

                            final EditText editText = (EditText) findViewById(R.id.new_name);
                            editText.setVisibility(View.GONE);

                            ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton4);
                            imageButton.setVisibility(View.GONE);

                            findViewById(R.id.view11).setVisibility(View.GONE);
                        }

                    }
                });
            }
        });
        TextView delete = (TextView)findViewById(R.id.delete_acc);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isConnectedToRemoteAPI(SettingsActivity.this)) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                if (offlineMode.isConnectedToRemoteAPI(SettingsActivity.this)) {
                                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                                    HashMap<String, String> user = new HashMap<>();
                                    user = sessionManager.getUserDetails();
                                    String id = user.get("id");
                                    String token = user.get("token");

                                    Toast.makeText(getApplicationContext(), "Bye Bye!!!", Toast.LENGTH_SHORT).show();

                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl(API_URL)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build();

                                    Update_user update_user = retrofit.create(Update_user.class);
                                    Call<Delete> call = update_user.delete_user(id, token);
                                    Log.i("Token", "Token: " + token);

                                    call.enqueue(new Callback<Delete>() {
                                        @Override
                                        public void onResponse(Response<Delete> response, Retrofit retrofit) {
                                            if (response.isSuccess()) {
                                                Log.i("Status", "Status: OK");
                                                String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
                                                File file = new File(path, "blured2.jpg");
                                                DBHelper dbHelper = new DBHelper(getApplicationContext());
                                                final SQLiteDatabase db = dbHelper.getWritableDatabase();
                                                int clearCount = db.delete("pending", null, null);
                                                clearCount = db.delete("pending_duel", null, null);
                                                db.delete("myChallenges", null, null);
                                                file.delete();
                                            } else Log.i("Status", "Status: " + response.code());
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            Log.i("Status", "Status: " + t);
                                        }
                                    });
                                    sessionManager.logoutUser();
                                    LoginManager.getInstance().logOut();
                                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    break;
                                }

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                }

            }
        });
        TextView terms = (TextView)findViewById(R.id.terms);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, TermsActivity.class);
                startActivity(intent);
            }
        });
        TextView privacy = (TextView)findViewById(R.id.privacy);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update_profile(map);
                Intent intent = new Intent(SettingsActivity.this, PrivacyActivity.class);
                startActivity(intent);
            }
        });

        TextView contact = (TextView)findViewById(R.id.contact_us);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update_profile(map);
                Intent intent = new Intent(SettingsActivity.this, ContactUsActivity.class);
                startActivity(intent);
            }
        });
        TextView avatar = (TextView)findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isConnectedToRemoteAPI(SettingsActivity.this)) {
                    Update_profile(map);
                    Intent intent = new Intent(SettingsActivity.this, Photo.class);
                    startActivity(intent);
                }
            }
        });

        ViewServer.get(this).addWindow(this);
    }


    private void Set_new_name(String newName) {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String id = user.get("id");
        String token = user.get("token");


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_user_name(id, token, newName);
        Log.i("Token" , "Token: "+token);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
               if (response.isSuccess()){
                   Log.i("Status", "Status: OK");
                   recreate();
               }
                else Log.i("Status" , "Status: "+response.code());
            }
            @Override
            public void onFailure(Throwable t) {
                Log.i("Status" , "Status: "+t);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (findViewById(R.id.view11).getVisibility() == View.VISIBLE) {
            findViewById(R.id.textView11).setVisibility(View.VISIBLE);
            TextView textView1 = (TextView)findViewById(R.id.textView16);
            textView1.setVisibility(View.GONE);

            final EditText editText = (EditText)findViewById(R.id.new_name);
            editText.setVisibility(View.GONE);

            ImageButton imageButton = (ImageButton)findViewById(R.id.imageButton4);
            imageButton.setVisibility(View.GONE);

            findViewById(R.id.view11).setVisibility(View.GONE);
        }
        else {
            Log.d(TAG, "Status: Back");
            Update_profile(map);
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        OfflineMode offlineMode = new OfflineMode();
        if (offlineMode.isConnectedToRemoteAPI(this)) {
            switch (item.getItemId()) {
                case R.id.challenges:
                    Update_profile(map);
                    Intent goToChallenges = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(goToChallenges);
                    break;
                case R.id.friends:
                    Update_profile(map);
                    Intent goToFriends = new Intent(SettingsActivity.this, FriendsActivity.class);
                    startActivity(goToFriends);
                    break;
                case R.id.history:
                    Update_profile(map);
                    Intent goToHistory = new Intent(SettingsActivity.this, HistoryActivity.class);
                    startActivity(goToHistory);
                    break;
                case R.id.pending_duels:
                    Update_profile(map);
                    Intent goToPendingDuel = new Intent(SettingsActivity.this, PendingDuelActivity.class);
                    startActivity(goToPendingDuel);
                    break;
                case R.id.share:
                    Update_profile(map);
                    String message = "Check out Champy - it helps you improve and compete with your friends!";
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(share, "How would you like to share?"));
                    break;
                case R.id.nav_logout:
                    if (offlineMode.isConnectedToRemoteAPI(this)) {
                        Update_profile(map);
                        LoginManager.getInstance().logOut();
                        SessionManager sessionManager = new SessionManager(getApplicationContext());
                        sessionManager.logoutUser();
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public int check_pending() {
        DBHelper dbHelper = new DBHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        Cursor c = db.query("pending_duel", null, null, null, null, null, null);
        int o = 0;
        if (c.moveToFirst()) {

            do {
                o++;
            } while (c.moveToNext());
        } else
            Log.i("stat", "kwo0 rows");
        c.close();
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.set_duel_pending("" + o);
        Log.d(TAG, "O: " + o);
        return o;
    }


    private void hideItem() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.pending_duels).setVisible(false);
    }


    private Drawable Init(String path) throws FileNotFoundException {
        File file = new File(path, "blured2.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

        Log.d("TAG", "x_y" + bitmap.getWidth() + " " + bitmap.getHeight());
        Drawable dr = new BitmapDrawable(getResources(), bitmap);
        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);


        ImageView imageView = (ImageView) findViewById(R.id.back_settings);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageDrawable(dr);

        return dr;

    }


    public void Update_profile(HashMap<String, String> map){
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String id = user.get("id");
        String token = user.get("token");

        Log.d(TAG, "Status: "+map);

        sessionManager.toogle1(map.get("pushNotifications"));
        sessionManager.toogle2(map.get("newChallengeRequests"));
        sessionManager.toogle3(map.get("acceptedYourChallenge"));
        sessionManager.toogle4(map.get("challengeEnd"));

        final String API_URL = "http://46.101.213.24:3007";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Update_user update_user = retrofit.create(Update_user.class);
        Profile_data profile_data = new Profile_data(map);
        Call<User> call = update_user.update_profile_oprions(id, token, profile_data);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                Log.d(TAG, "Response code: " + response.code());
                User decodedResponse = response.body();
                if (response.isSuccess()) {
                    Log.d("TAG", "Status: Profile updated");
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "VSE huynya");
            }
        });
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(SettingsActivity.this);
                    return false;
                }

            });
        }

    }


}
