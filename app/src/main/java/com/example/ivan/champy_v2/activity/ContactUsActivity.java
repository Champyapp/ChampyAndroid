package com.example.ivan.champy_v2.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHLoadBlurredPhoto;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;

import java.io.File;
import java.io.FileNotFoundException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.ivan.champy_v2.utils.Constants.path;

public class ContactUsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private EditText inputSubject, inputMessage;
    private TextInputLayout inputLayoutSubject, inputLayoutMessage;
    private NavigationView navigationView;
    private SessionManager sessionManager;
    private String[] recipients = {"azinecllc@gmail.com"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        sessionManager = new SessionManager(getApplicationContext());
        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);
        String name = sessionManager.getUserName();

        try {
            ImageView imageView = (ImageView) headerLayout.findViewById(R.id.slide_background);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageDrawable(CHLoadBlurredPhoto.Init(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        final ImageView drawerUserPhoto = (ImageView) headerLayout.findViewById(R.id.profile_image);
        final TextView drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        drawerUserName.setText(name);
        drawerUserName.setTypeface(typeface);

        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(drawerUserPhoto);

        ViewServer.get(this).addWindow(this);

        inputLayoutSubject = (TextInputLayout)findViewById(R.id.input_layout_name);
        inputLayoutMessage = (TextInputLayout)findViewById(R.id.input_layout_email);
        inputSubject = (EditText)findViewById(R.id.input_name);
        inputMessage = (EditText)findViewById(R.id.input_email);

        final Button buttonSend = (Button) findViewById(R.id.buttonSend);
        inputSubject.addTextChangedListener(new MyTextWatcher(inputSubject));
        inputMessage.addTextChangedListener(new MyTextWatcher(inputMessage));
        buttonSend.setOnClickListener(this);

        final CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
        int count = checker.getPendingCount();
        if (count == 0) {
            checker.hideItem();
        } else {
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
            view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        }
    }

    @Override
    public void onClick(View v) {
        submitForm();
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
                    String message = getString(R.string.share_text2);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(share, getString(R.string.how_would_you_like_to_share)));
                    break;
                case R.id.nav_logout:
                    OfflineMode offlineMode = new OfflineMode();
                    if (offlineMode.isConnectedToRemoteAPI(this)) {
                        sessionManager.logout(this);
                    }
                    break;
            }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void submitForm() {
        if (!validateSubject()) {
            return;
        }
        if (!validateMessage()) {
            return;
        }
        sendEmail();
    }


    private void sendEmail() {
        Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE, Uri.parse("mailto:"));
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, recipients);
        email.putExtra(Intent.EXTRA_SUBJECT, inputSubject.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, inputMessage.getText().toString());

        try {
            if (!inputSubject.getText().toString().isEmpty() && !inputMessage.getText().toString().isEmpty()) {
                startActivity(Intent.createChooser(email, "Choose an email client from..."));
            }
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ContactUsActivity.this, "No email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validateSubject() {
        if (inputSubject.getText().toString().trim().isEmpty()) {
            inputLayoutSubject.setError("Complete your subject");
            requestFocus(inputSubject);
            return false;
        } else {
            inputLayoutSubject.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validateMessage() {
        String email = inputMessage.getText().toString().trim();

        if (email.isEmpty()) {
            inputLayoutMessage.setError("Complete your message");
            requestFocus(inputMessage);
            return false;
        } else {
            inputLayoutMessage.setErrorEnabled(false);
        }
        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateSubject();
                    break;
                case R.id.input_email:
                    validateMessage();
                    break;
            }
        }
    }

}
