package com.example.ivan.champy_v2;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.example.ivan.champy_v2.activity.TestActivity;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static java.lang.Math.max;
import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final String TAG = "myLogs";
    private FloatingActionMenu actionMenu;

    private CustomPagerBase pager;

    private int counter = 0;
    private int total = 30; // the total number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RelativeLayout cards = (RelativeLayout)findViewById(R.id.cards);
        CustomAdapter adapter = new CustomAdapter(this);
        pager = new CustomPagerBase(this,  cards, adapter);
        pager.preparePager(2);

        final ImageButton actionButton = (ImageButton)findViewById(R.id.imageButton);

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);


        SubActionButton button1 = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.wakeupcolor)).build();
        SubActionButton button2 = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.duelcolor)).build();
        SubActionButton button3 = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.selfimprovementcolor)).build();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int x = round(width/100);
        button1.getLayoutParams().height = x*20;
        button1.getLayoutParams().width = x*20;
        button2.getLayoutParams().height = x*20;
        button2.getLayoutParams().width = x*20;
        button3.getLayoutParams().height = x*20;
        button3.getLayoutParams().width = x*20;

        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .setRadius(350)
                .attachTo(actionButton)
                .build();

        FloatingActionButton.OnClickListener onClickListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "clicked");
                ImageView screen = (ImageView) findViewById(R.id.blured);

                if (screen.getDrawable() == null) {
                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content_main);
                relativeLayout.setDrawingCacheEnabled(true);
                relativeLayout.buildDrawingCache();
                Bitmap bm = relativeLayout.getDrawingCache();


                Blur blur = new Blur();
                if (bm == null) Log.d(TAG, "SUKAAAAA");
                else {

                    Bitmap blured = Blur.blurRenderScript(getApplicationContext(), bm, 25);

                    screen = (ImageView) findViewById(R.id.blured);

                    Drawable ob = new BitmapDrawable(getResources(), blured);
                    screen.setImageDrawable(ob);
                }
                }
                else Log.d(TAG, "Vse zaebok");
                RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.cards);
                Log.d("TAG", "menu " + actionMenu.isOpen());
                actionMenu.toggle(true);
                if (!actionMenu.isOpen()) {
                    screen.setVisibility(View.INVISIBLE);
                    relativeLayout.setVisibility(View.VISIBLE);
                }
                else {
                    screen.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.INVISIBLE);

                }
            }
        };
        actionButton.setOnClickListener(onClickListener2);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                actionMenu.close(true);
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
        Log.d(TAG, "Url :" + url);
        String name = user.get("name");

        if (url == null) {
            Log.d(TAG, "intent");
            Intent intent = getIntent();
            url = intent.getExtras().getString("path_to_pic");
            name = intent.getExtras().getString("name");
        }

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.slider);
        ImageView profile_image = (ImageView)headerLayout.findViewById(R.id.profile_image);
        TextView textView = (TextView)headerLayout.findViewById(R.id.textView);
        textView.setText(name);
        ImageView imageView = (ImageView)headerLayout.findViewById(R.id.slide_background);
        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "blured2.jpg");
        if (file.exists()) try {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageDrawable(Init(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        else new DownloadImageTask().execute(url);
        Glide.with(this)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .into(profile_image);

        BuildAnim();

        ViewServer.get(this).addWindow(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        actionMenu.close(true);
        ImageView screen = (ImageView) findViewById(R.id.blured);
        screen.setVisibility(View.INVISIBLE);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.cards);
        relativeLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
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
        if (offlineMode.isInternetAvailable(this)) Logout();
        else Toast.makeText(this, "Lost internet connection!", Toast.LENGTH_LONG).show();
    }
        if (id == R.id.friends) {
            Intent intent = new Intent(MainActivity.this, Friends.class);
            startActivity(intent);
        }
        if (id == R.id.history){
            Intent intent = new Intent(MainActivity.this, TestActivity.class);
            startActivity(intent);
        }
        if (id == R.id.settings){
            Intent intent = new Intent(MainActivity.this, Settings.class);
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

    public void Logout(){
        LoginManager.getInstance().logOut();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.logoutUser();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Bye Bye!!!", Toast.LENGTH_SHORT).show();
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
            return mIcon11;
        }


        private String saveToInternalSorage(Bitmap bitmapImage){
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
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

                    Bitmap bitmap = blured;


                    Drawable dr = new BitmapDrawable(getResources(), bitmap);
                    dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);
                    ImageView background = (ImageView) findViewById(R.id.slide_background);
                    background.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    background.setImageDrawable(dr);
                    background = (ImageView) findViewById(R.id.main_background);
                    background.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    background.setImageDrawable(dr);
                    bitmap = ((BitmapDrawable)dr).getBitmap();

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content_main);
                    relativeLayout.setDrawingCacheEnabled(true);
                    relativeLayout.buildDrawingCache();
                    Bitmap bm = relativeLayout.getDrawingCache();


                    blur = new Blur();

                    blured = blur.blurRenderScript(getApplicationContext(), bm, 25);

                    ImageView screen = (ImageView) findViewById(R.id.blured);

                    Drawable ob = new BitmapDrawable(getResources(), blured);
                    screen.setImageDrawable(ob);
                }




            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        protected void onPostExecute(Bitmap result) {
            // Do your staff here to save image
            saveToInternalSorage(result);
            loadImageFromStorage("/data/data/com.example.ivan.champy_v2/app_imageDir/");
        }
    }
   /* private void make_screen_blured()
    {

    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }

    public Drawable Init(String path) throws FileNotFoundException {
        File file = new File(path, "blured2.jpg");

        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        Drawable dr = new BitmapDrawable(getResources(), bitmap);
        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);
        bitmap = ((BitmapDrawable)dr).getBitmap();


       ImageView background = (ImageView)findViewById(R.id.main_background);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);
        background.setImageDrawable(dr);

        return dr;

    }
    public class CustomAdapter extends CustomPagerAdapter{

        public CustomAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView) {
            View tempView = convertView;
            if(tempView == null){
                LayoutInflater inflater = (LayoutInflater) getApplicationContext()

                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                tempView = inflater.inflate(R.layout.single_card_fragment, null, false);
            }
            ImageView cardImage = (ImageView)tempView.findViewById(R.id.cardImage);
            int x = round(getWindowManager().getDefaultDisplay().getWidth() / 100);
            int y = round(getWindowManager().getDefaultDisplay().getHeight() / 100);
            cardImage.getLayoutParams().width = x*65;
            cardImage.getLayoutParams().height = y*50;


            // cardImage.setImageDrawable(RecyclerView_Activity.this.getResources().getDrawable(R.drawable.card_image));
            Button button = (Button) tempView.findViewById(R.id.button3);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "YOOO");
                }
            });
            button.getLayoutParams().width = x*10;
            button.getLayoutParams().height = x*10;
            button = (Button) tempView.findViewById(R.id.button2);
            button.getLayoutParams().width = x*10;
            button.getLayoutParams().height = x*10;
            button = (Button) tempView.findViewById(R.id.button4);
            button.getLayoutParams().width = x*10;
            button.getLayoutParams().height = x*10;
            Log.d("TAG", "X: "+x*y);

           /* cardImage = (ImageView)tempView.findViewById(R.id.imageView);
            cardImage.getLayoutParams().width = x*15;
            cardImage.getLayoutParams().height = x *15;*/

            if (y > 10) y = 10;

            TextView textView  = (TextView) tempView.findViewById(R.id.textView12);
            textView.setTextSize((float)(y*1.3));

            textView = (TextView) tempView.findViewById(R.id.textView13);
            textView.setTextSize(y);

            textView = (TextView) tempView.findViewById(R.id.textView14);
            textView.setTextSize(y*2);

            textView = (TextView) tempView.findViewById(R.id.textView15);
            textView.setTextSize(y);

            return tempView;
        }

        @Override
        public int dataCount() {
            return 10;
        }

    }
    public void BuildAnim()
    {
        int width = getWindowManager().getDefaultDisplay().getWidth();
        make_responsive_score(width);
        ImageView mImageViewFilling = (ImageView) findViewById(R.id.imageview_score_animation);
        ((AnimationDrawable) mImageViewFilling.getBackground()).start();
        ImageView mImageViewFilling1 = (ImageView) findViewById(R.id.imageview_score_animation1);
        ((AnimationDrawable) mImageViewFilling1.getBackground()).start();
        ImageView mImageViewFilling2 = (ImageView) findViewById(R.id.imageview_score_animation2);
        ((AnimationDrawable) mImageViewFilling2.getBackground()).start();

        final TextView t1 = (TextView) findViewById(R.id.textView2);
        final TextView t2 = (TextView) findViewById(R.id.textView3);
        final TextView t3 = (TextView) findViewById(R.id.textView4);
        counter = 0;
        final int i1 = 20;
        final int i2 = 10;
        final int i3 = 33;
        total = max(max(i1, i2), i3);
        Log.d(TAG, "TOTAL: " + i2);

        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(0, i1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                t1.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animator.setDuration(1000);

        ValueAnimator animator1 = new ValueAnimator();
        animator1.setObjectValues(0, i2);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                t2.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator1.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animator1.setDuration(1000);

        ValueAnimator animator2 = new ValueAnimator();
        animator2.setObjectValues(0, i3);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                t3.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator2.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animator2.setDuration(1000);

        animator.start();
        animator1.start();
        animator2.start();



        final TextView mSwitcher1 = (TextView) findViewById(R.id.textView5);
        final TextView mSwitcher2 = (TextView) findViewById(R.id.textView6);
        final TextView mSwitcher3 = (TextView) findViewById(R.id.textView7);
        final ImageView imageView1 = (ImageView) findViewById(R.id.imageView2);
        final ImageView imageView2 = (ImageView) findViewById(R.id.imageView3);
        final ImageView imageView3 = (ImageView) findViewById(R.id.imageView4);
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(3000);

        mSwitcher1.setText("Challenges");
        mSwitcher1.startAnimation(in);

        mSwitcher2.setText("Wins");
        mSwitcher2.startAnimation(in);

        mSwitcher3.setText("Total");
        mSwitcher3.startAnimation(in);

        imageView1.setImageResource(R.drawable.challenges);
        imageView1.startAnimation(in);

        imageView2.setImageResource(R.drawable.wins);
        imageView2.startAnimation(in);

        imageView3.setImageResource(R.drawable.total);
        imageView3.startAnimation(in);

    }

    public void make_responsive_score(int width)
    {
        int x = round(width/100);

        ImageView imageView = (ImageView)findViewById(R.id.imageview_score_animation);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;
        imageView = (ImageView)findViewById(R.id.imageview_score_animation1);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;
        imageView = (ImageView)findViewById(R.id.imageview_score_animation2);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;

        imageView = (ImageView)findViewById(R.id.imageView3);
        imageView.getLayoutParams().width = x*5;
        imageView.getLayoutParams().height = x*5;
        imageView = (ImageView)findViewById(R.id.imageView4);
        imageView.getLayoutParams().width = x*5;
        imageView.getLayoutParams().height = x*5;
        imageView = (ImageView)findViewById(R.id.imageView2);
        imageView.getLayoutParams().width = x*5;
        imageView.getLayoutParams().height = x*5;

        ImageButton imageButton = (ImageButton)findViewById(R.id.imageButton);
        imageButton.getLayoutParams().width = x*20;
        imageButton.getLayoutParams().height = x*20;

        /*imageView = (ImageView)findViewById(R.id.profile_image);
        imageView.getLayoutParams().width = x*25;
        imageView.getLayoutParams().height = x*25;*/

        Float y = x*(float)3.5;
        TextView textView = (TextView)findViewById(R.id.textView2);
        textView.setTextSize(y);
        textView = (TextView)findViewById(R.id.textView3);
        textView.setTextSize(y);
        textView = (TextView)findViewById(R.id.textView4);
        textView.setTextSize(y);

        y = x*(float)1.5;
        textView = (TextView)findViewById(R.id.textView5);
        textView.setTextSize(y);
        textView = (TextView)findViewById(R.id.textView6);
        textView.setTextSize(y);
        textView = (TextView)findViewById(R.id.textView7);
        textView.setTextSize(y);

    }
}
