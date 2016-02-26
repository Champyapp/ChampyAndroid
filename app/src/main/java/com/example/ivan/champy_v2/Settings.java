package com.example.ivan.champy_v2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class Settings extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String url = user.get("path_to_pic");
        String name = user.get("name");

        ImageView profile = (ImageView) headerLayout.findViewById(R.id.profile_image);
        TextView textView = (TextView) headerLayout.findViewById(R.id.textView);
        textView.setText(name);
        Typeface typeface = Typeface.createFromAsset(Settings.this.getAssets(), "fonts/bebasneue.ttf");

        textView = (TextView)findViewById(R.id.name);
        textView.setText(name);
        textView.setTypeface(typeface);

        textView = (TextView)findViewById(R.id.textView17);
        textView.setTypeface(typeface);
        textView = (TextView)findViewById(R.id.textView10);
        textView.setTypeface(typeface);
        textView = (TextView)findViewById(R.id.textView18);
        textView.setTypeface(typeface);

        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .into(profile);
        profile = (ImageView) findViewById(R.id.img_profile);
        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .into(profile);

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

                ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton4);
                imageButton.setVisibility(View.VISIBLE);

                findViewById(R.id.view11).setVisibility(View.VISIBLE);

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /* if (editText.getText().toString() != ""){
                            SessionManager sessionManager = new SessionManager(getApplicationContext());
                            sessionManager.change_name(editText.getText().toString());

                            TextView textView = (TextView)findViewById(R.id.name);
                            textView.setText(editText.getText().toString());
                        }*/
                        change_name.setVisibility(View.VISIBLE);
                        TextView textView1 = (TextView) findViewById(R.id.textView16);
                        textView1.setVisibility(View.GONE);

                        final EditText editText = (EditText) findViewById(R.id.new_name);
                        editText.setVisibility(View.GONE);

                        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton4);
                        imageButton.setVisibility(View.GONE);

                        findViewById(R.id.view11).setVisibility(View.GONE);

                    }
                });
            }
        });
        TextView terms = (TextView)findViewById(R.id.terms);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, Terms.class);
                startActivity(intent);
            }
        });
        TextView privacy = (TextView)findViewById(R.id.privacy);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, Privacy.class);
                startActivity(intent);
            }
        });

        TextView contact = (TextView)findViewById(R.id.contact_us);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, Contact_us.class);
                startActivity(intent);
            }
        });

        ViewServer.get(this).addWindow(this);
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            OfflineMode offlineMode = new OfflineMode();
            if (offlineMode.isInternetAvailable(this)) {
                LoginManager.getInstance().logOut();
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.logoutUser();
                Intent intent = new Intent(Settings.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Bye Bye!!!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "Lost internet connection!", Toast.LENGTH_LONG).show();
        }
        if (id == R.id.challenges) {
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
        }
        if (id == R.id.friends){
            Intent intent = new Intent(Settings.this, Friends.class);
            startActivity(intent);
        }
        else if (id == R.id.share) {
            String message = "Check out Champy - it helps you improve and compete with your friends!";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, message);

            startActivity(Intent.createChooser(share, "How would you like to share?"));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
}
