package com.example.ivan.champy_v2.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ContactUsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
        int count = checker.checkPending();
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
        view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        if (count == 0) checker.hideItem();

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);
        String name = user.get("name");

        ImageView profile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        TextView textView = (TextView) headerLayout.findViewById(R.id.tvUserName);
        textView.setText(name);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(profile);

        try {
            Drawable dr = Init("/data/data/com.example.ivan.champy_v2/app_imageDir/");
            ImageView imageView = (ImageView) headerLayout.findViewById(R.id.slide_background);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageDrawable(dr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ViewServer.get(this).addWindow(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
                case R.id.challenges:
                    Intent goToChallenges = new Intent(this, MainActivity.class);
                    startActivity(goToChallenges);
                    break;
                case R.id.friends:
                    Intent goToFriends = new Intent(this, FriendsActivity.class);
                    startActivity(goToFriends);
                    break;
                case R.id.pending_duels:
                    Intent goToPendingDuel = new Intent(this, PendingDuelActivity.class);
                    startActivity(goToPendingDuel);
                    break;
                case R.id.history:
                    Intent goToHistory = new Intent(this, HistoryActivity.class);
                    startActivity(goToHistory);
                    break;
                case R.id.settings:
                    Intent goToSettings = new Intent(this, SettingsActivity.class);
                    startActivity(goToSettings);
                    break;
                case R.id.share:
                    String message = "Check out Champy - it helps you improve and compete with your friends!";
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(share, "How would you like to share?"));
                    break;
                case R.id.nav_logout:
                    OfflineMode offlineMode = new OfflineMode();
                    SessionManager sessionManager = new SessionManager(this);
                    if (offlineMode.isConnectedToRemoteAPI(this)) {
                        sessionManager.logout(this);
                    }
                    break;
            }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


//    private Drawable Init(String path) throws FileNotFoundException {
//        File file = new File(path, "blured2.jpg");
//        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
//        Drawable dr = new BitmapDrawable(getResources(), bitmap);
//        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);
//        return dr;
//    }

    private Drawable Init(String path) throws FileNotFoundException {
        File file = new File(path, "blured2.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        Drawable dr = new BitmapDrawable(this.getResources(), bitmap);
        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);
        ImageView background = (ImageView) findViewById(R.id.contact_us_background);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);
        background.setImageDrawable(dr);
        return dr;
    }


    protected void sendEmail() {
        EditText subject = (EditText) findViewById(R.id.editText2);
        EditText body    = (EditText) findViewById(R.id.editText);

        // TODO: 12.09.2016 change this email for real;
        String[] recipients = {"skill.bereg@gmail.com"};
        Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, recipients);
        email.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, body.getText().toString());

        try {
            if (!subject.getText().toString().isEmpty() && !body.getText().toString().isEmpty()) {
                // the user can choose the email client
                startActivity(Intent.createChooser(email, "Choose an email client from..."));
            } else {
                Toast.makeText(ContactUsActivity.this, "Complete empty fields", Toast.LENGTH_SHORT).show();
            }
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ContactUsActivity.this, "No email client installed.", Toast.LENGTH_SHORT).show();
        }

//        subject.setText("");
//        body.setText("");
//        Toast.makeText(ContactUsActivity.this, "Sent!", Toast.LENGTH_SHORT).show();


    }


}