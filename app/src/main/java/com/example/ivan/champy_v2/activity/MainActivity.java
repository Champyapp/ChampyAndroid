package com.example.ivan.champy_v2.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.Blur;
import com.example.ivan.champy_v2.ChallengeController;
import com.example.ivan.champy_v2.CustomPagerBase;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.adapter.MainActivityCardsAdapter;
import com.example.ivan.champy_v2.helper.AppSync;
import com.example.ivan.champy_v2.helper.CHBuildAnim;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHDownloadImageTask;
import com.example.ivan.champy_v2.helper.CHSocket;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.model.SelfImprovement_model;
import com.facebook.FacebookSdk;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainActivity";
    private long mLastClickTime = 0;
    public Activity activity;
    private SubActionButton buttonWakeUpChallenge, buttonDuelChallenge, buttonSelfImprovement;
    private FloatingActionMenu actionMenu;
    private CustomPagerBase pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_gradient));
        setSupportActionBar(toolbar);

        CHSocket sockets = new CHSocket(this, getApplicationContext());
        sockets.tryToConnect();
        sockets.connectAndEmmit();
//        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmSchedule.class);
//        intent.putExtra("alarm", "reset");
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));  // було 18
//        calendar.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));        //  було 6
//        calendar.set(Calendar.SECOND, 0);
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


        RelativeLayout cards = (RelativeLayout)findViewById(R.id.cards);
        MainActivityCardsAdapter adapter = new MainActivityCardsAdapter(this, SelfImprovement_model.generate(this), activity);
        if (adapter.dataCount() > 0) {
            pager = new CustomPagerBase(this,  cards, adapter);
            pager.preparePager(0);
        }

        final ImageButton actionButton = (ImageButton)findViewById(R.id.fabPlus);
        final SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        buttonWakeUpChallenge = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.wakeupcolor)).build();
        buttonDuelChallenge   = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.duel_yellow)).build();
        buttonSelfImprovement = itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.self_yellow)).build();

        int width = getWindowManager().getDefaultDisplay().getWidth();
        int x = round(width/100);

        buttonWakeUpChallenge.getLayoutParams().height = x*20;
        buttonWakeUpChallenge.getLayoutParams().width  = x*20;
        buttonDuelChallenge  .getLayoutParams().height = x*20;
        buttonDuelChallenge  .getLayoutParams().width  = x*20;
        buttonSelfImprovement.getLayoutParams().height = x*20;
        buttonSelfImprovement.getLayoutParams().width  = x*20;

        actionMenu = new FloatingActionMenu.Builder(this).addSubActionView(buttonWakeUpChallenge)
                .addSubActionView(buttonDuelChallenge).addSubActionView(buttonSelfImprovement)
                .setRadius(350).attachTo(actionButton).build();

        actionButton.setOnClickListener(initStupidClick());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                ImageView screen = (ImageView) findViewById(R.id.blurScreen);
                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.cards);
                relativeLayout.setVisibility(View.VISIBLE);
                screen.setVisibility(View.INVISIBLE);
                actionMenu.close(true);
            }
        };
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
        int count = checker.checkPending();
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
        view.setText("+" + (count > 0 ? String.valueOf(count) : null));
        if (count == 0) checker.hideItem();

        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String path_to_pic = user.get("path_to_pic");
        String name = user.get("name");

        if (path_to_pic == null) {
            Intent fromLogin = getIntent();
            path_to_pic = fromLogin.getExtras().getString("path_to_pic");
            name = fromLogin.getExtras().getString("name");
        }

        ImageView background    = (ImageView)headerLayout.findViewById(R.id.slide_background);
        ImageView profile_image = (ImageView)headerLayout.findViewById(R.id.profile_image);
        TextView  tvUserName    = (TextView) headerLayout.findViewById(R.id.tvUserName);
        tvUserName.setText(name);

        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "blured2.jpg");
        if (file.exists())
            try {
                background.setScaleType(ImageView.ScaleType.CENTER_CROP);
                background.setImageDrawable(initBackground(path));
            }   catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        else {
            CHDownloadImageTask chDownloadImageTask = new CHDownloadImageTask(getApplicationContext(), this);
            chDownloadImageTask.execute(path_to_pic);
        }

        file = new File(path, "profile.jpg");
        Uri uri = Uri.fromFile(file);
        Glide.with(this).load(uri).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(profile_image);
        ViewServer.get(this).addWindow(this);
    }


    private View.OnClickListener initStupidClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                /**
                 * Here we make our background is blurred
                 */
                ImageView blurScreen;
                RelativeLayout contentMain = (RelativeLayout) findViewById(R.id.content_main);
                contentMain.destroyDrawingCache();
                contentMain.buildDrawingCache();
                Bitmap bm = contentMain.getDrawingCache();
                Bitmap blured = Blur.blurRenderScript(getApplicationContext(), bm, 25);
                blurScreen = (ImageView) findViewById(R.id.blurScreen);
                Drawable ob = new BitmapDrawable(getResources(), blured);
                blurScreen.setImageDrawable(ob);
                RelativeLayout cardsLayout = (RelativeLayout) findViewById(R.id.cards);

                /**
                 * first we check action menu and if "is open" then we setup our inside click for FAB
                 */
                actionMenu.toggle(true);
                if (!actionMenu.isOpen()) {
                    blurScreen.setVisibility(View.INVISIBLE);
                    cardsLayout.setVisibility(View.VISIBLE);
                } else {
                    blurScreen.setVisibility(View.VISIBLE);
                    cardsLayout.setVisibility(View.INVISIBLE);
                    buttonSelfImprovement.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, SelfImprovementActivity.class);
                            startActivity(intent);
                        }
                    });
                    buttonDuelChallenge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                            startActivity(intent);
                        }
                    });
                    buttonWakeUpChallenge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, WakeUpActivity.class);
                            startActivity(intent);
                        }
                    });
                }

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CHBuildAnim chBuildAnim = new CHBuildAnim();
                chBuildAnim.buildAnim(MainActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        actionMenu.close(true);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.cards);
        relativeLayout.setVisibility(View.VISIBLE);
        ImageView screen = (ImageView) findViewById(R.id.blurScreen);
        screen.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.friends:
                Intent goToFriends = new Intent(this, FriendsActivity.class);
                startActivity(goToFriends);
                break;
            case R.id.history:
                Intent goToHistory = new Intent(this, HistoryActivity.class);
                startActivity(goToHistory);
                break;
            case R.id.settings:
                Intent goToSettings = new Intent(this, SettingsActivity.class);
                startActivity(goToSettings);
                break;
            case R.id.pending_duels:
                Intent goToPendingDuel = new Intent(this, PendingDuelActivity.class);
                startActivity(goToPendingDuel);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            ChallengeController cc = new ChallengeController(getApplicationContext(), activity, 0, 0, 0);
            cc.generateCardsForMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.duel, menu);
        return true;
    }


    public Drawable initBackground(String path) throws FileNotFoundException {
        File file = new File(path, "blured2.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

        Drawable dr = new BitmapDrawable(getResources(), bitmap);
        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);

        ImageView background = (ImageView)findViewById(R.id.main_background);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);
        background.setImageDrawable(dr);

        return dr;

    }

//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Log.d("DownLoadImageTask", "doInBackground: " + urldisplay);
//
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("DownLoadImageTask", "Error: " + e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        private String saveToInternalStorage(Bitmap bitmapImage){
//            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            // path to /data/data/yourapp/app_data/imageDir
//            String root = Environment.getExternalStorageDirectory().toString();
//            File myDir = new File(root + "/saved_images");
//            myDir.mkdirs();
//            Random generator = new Random();
//            int n = 10000;
//            n = generator.nextInt(n);
//            String fname = "Image-"+ n +".jpg";
//            File file = new File (myDir, fname);
//            if (file.exists()) {
//                file.delete();
//            }
//            try {
//                FileOutputStream out = new FileOutputStream(file);
//                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
//                out.flush();
//                out.close();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            CHUploadPhoto uploadPhoto = new CHUploadPhoto(getApplicationContext());
//            uploadPhoto.uploadPhotoForAPI(Uri.fromFile(file).getPath());
//
//            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//            // Create imageDir
//            File myPath = new File(directory,"profile.jpg");
//
//            Log.i("DownloadImageTask", "saveToInternalStorage | PhotoPath: " + myPath.toString());
//
//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream(myPath);
//                // Use the compress method on the BitMap object to write image to the OutputStream
//                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                fos.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return directory.getAbsolutePath();
//        }
//
//        public void loadImageFromStorage(String path) {
//            try {
//                File f = new File(path, "profile.jpg");
//                Uri uri = Uri.fromFile(f);
//                Log.i("DownloadImageTask", "LoadImageFromStorage: URI = " + uri);
//                Glide.with(getApplicationContext()).load(uri).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
//                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into((ImageView)findViewById(R.id.profile_image));
//                File file = new File(path, "blured2.jpg");
//                if (file.exists()) {
//                    return;
//                } else {
//                    file.createNewFile();
//                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//
//                    Blur blur = new Blur();
//
//                    Bitmap blured = blur.blurRenderScript(getApplicationContext(), b, 10);
//
//                    Bitmap bitmap = blured;
//
//                    Drawable dr = new BitmapDrawable(getResources(), bitmap);
//                    dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);
//                    ImageView background = (ImageView) findViewById(R.id.slide_background);
//                    background.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    background.setImageDrawable(dr);
//                    background = (ImageView) findViewById(R.id.main_background);
//                    background.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    background.setImageDrawable(dr);
//                    bitmap = ((BitmapDrawable)dr).getBitmap();
//
//                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
//                    byte[] bitmapdata = bos.toByteArray();
//
//                    FileOutputStream fos = new FileOutputStream(file);
//                    fos.write(bitmapdata);
//                    fos.flush();
//                    fos.close();
//
//                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content_main);
//                    relativeLayout.setDrawingCacheEnabled(true);
//                    relativeLayout.buildDrawingCache();
//                    Bitmap bm = relativeLayout.getDrawingCache();
//
//
//                    blur = new Blur();
//
//                    blured = blur.blurRenderScript(getApplicationContext(), bm, 25);
//
//                    ImageView screen = (ImageView) findViewById(R.id.blurScreen);
//
//                    Drawable ob = new BitmapDrawable(getResources(), blured);
//                    screen.setImageDrawable(ob);
//                }
//            }
//            catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            // Do your staff here to save image
//            saveToInternalStorage(result);
//            loadImageFromStorage("/data/data/com.example.ivan.champy_v2/app_imageDir/");
//        }
//
//    }
//
//
//
//    public class CustomAdapter extends CustomPagerAdapter {
//
//        private ArrayList<SelfImprovement_model> arrayList;
//        public CustomAdapter(Context context, ArrayList<SelfImprovement_model> marrayList) {
//            super(context);
//            this.arrayList = marrayList;
//        }
//
//        @Override
//        public View getView(int position, View convertView) {
//            View tempView = convertView;
//            if(tempView == null) {
//                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                tempView = inflater.inflate(R.layout.single_card_fragment_self, null, false);
//            }
//            final SelfImprovement_model item = arrayList.get(position);
//            ImageView cardImage = (ImageView)tempView.findViewById(R.id.cardImage);
//            int x = round(getWindowManager().getDefaultDisplay().getWidth() / 100);
//            int y = round(getWindowManager().getDefaultDisplay().getHeight() / 100);
//            cardImage.getLayoutParams().width  = x*65;
//            cardImage.getLayoutParams().height = y*50;
//            if (y > 10) y = 10;
//
//            final ChallengeController challengeController = new ChallengeController(MainActivity.this, MainActivity.this, 0 , 0, 0);
//
//            TextView tvSelfImprovement  = (TextView) tempView.findViewById(R.id.textViewSIC);
//            tvSelfImprovement.setText(item.getType());
//            String itemGoal = item.getGoal();
//            ImageView imageView = (ImageView)tempView.findViewById(R.id.imageViewChallengeLogo);
//
//            switch (item.getType()) {
//                case "Wake Up":
//                    imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.wakeup_white));
//                    itemGoal = item.getChallengeName();
//                    break;
//                case "Duel":
//                    itemGoal = item.getGoal();
//                    imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.duel_white));
//                    break;
//                case "Self-Improvement":
//                    imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.self_white));
//                    break;
//            }
//
//            tvSelfImprovement.setTextSize((float)(y*1.3));
//            Typeface typeface = android.graphics.Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
//            tvSelfImprovement.setTypeface(typeface);
//
//            TextView tvChallengeName = (TextView) tempView.findViewById(R.id.textViewChallengeName);
//            tvChallengeName.setText(itemGoal);
//            tvChallengeName.setTextSize(y);
//
//            TextView tvDuration = (TextView) tempView.findViewById(R.id.textViewDuration);
//            tvDuration.setText(item.getDays() + " DAYS TO GO");
//            tvDuration.setTextSize(y*2);
//
//            TextView tvLevelAndPoints = (TextView) tempView.findViewById(R.id.textViewLevelAndPoints);
//            tvLevelAndPoints.setTextSize(y);
//            /*final TextView tvDoneForToday = (TextView) tempView.findViewById(R.id.tvDoneForToday);
//            tvDoneForToday.setTextScaleX(y);*/
//
//            Button buttonGiveUp = (Button) tempView.findViewById(R.id.buttonGiveUp);
//            buttonGiveUp.getLayoutParams().width = x*10;
//            buttonGiveUp.getLayoutParams().height = x*10;
//
//            buttonGiveUp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch (which){
//                                case DialogInterface.BUTTON_POSITIVE:
//                                    OfflineMode offlineMode = new OfflineMode();
//                                    if (offlineMode.isConnectedToRemoteAPI(MainActivity.this)){
//                                        try {
//                                            if (item.getType().equals("Wake Up")) {
//                                                int intentId = Integer.parseInt(item.getGoal());
//                                                challengeController.give_up(item.getId(), intentId);
//                                            } else {
//                                                challengeController.give_up(item.getId(), 0);
//                                            }
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                    break;
//                                case DialogInterface.BUTTON_NEGATIVE:
//                                    break;
//                            }
//                        }
//                    };
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                    builder.setTitle(R.string.areYouSure)
//                            .setMessage(R.string.youWantToGiveUp)
//                            .setIcon(R.drawable.ic_action_warn)
//                            .setCancelable(false)
//                            .setPositiveButton(R.string.yes, dialogClickListener)
//                            .setNegativeButton(R.string.no, dialogClickListener).show();
//
//                }
//            });
//
//
//            final Button buttonDoneForToday = (Button) tempView.findViewById(R.id.buttonDoneForToday);
//            buttonDoneForToday.getLayoutParams().width = x*10;
//            buttonDoneForToday.getLayoutParams().height = x*10;
//
//            //final Button finalButton = buttonDoneForToday;
//            if (item.getUpdated() != null){
//                if (!item.getType().equals("Wake Up")) {
//                    if (item.getUpdated().equals("false")) {
//                        buttonDoneForToday.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.icon_done_for_today));
////                        tvDoneForToday.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//
//            buttonDoneForToday.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String id = item.getId();
//                    SQLiteDatabase localSQLiteDatabase = new DBHelper(MainActivity.this).getWritableDatabase();
//                    ContentValues localContentValues = new ContentValues();
//                    localContentValues.put("updated", "true");
//                    localSQLiteDatabase.update("myChallenges", localContentValues, "challenge_id = ?", new String[]{id});
//                    int i = localSQLiteDatabase.update("updated", localContentValues, "challenge_id = ?", new String[]{id});
//                    try {
//                        challengeController.doneForToday(id);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    buttonDoneForToday.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.icon_share));
//                    findViewById(R.id.tvDoneForToday).setVisibility(View.INVISIBLE);
//                }
//            });
//
//            return tempView;
//        }
//
//        @Override
//        public int dataCount() {
//            return arrayList.size();
//        }
//
//    }
}