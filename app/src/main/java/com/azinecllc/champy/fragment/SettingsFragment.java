package com.azinecllc.champy.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.AboutActivity;
import com.azinecllc.champy.activity.ContactUsActivity;
import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.activity.RoleControllerActivity;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.controller.DailyRemindController;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.helper.CHUploadPhoto;
import com.azinecllc.champy.interfaces.Update_user;
import com.azinecllc.champy.model.user.Delete;
import com.azinecllc.champy.model.user.Profile_data;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.Blur;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Random;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static com.azinecllc.champy.utils.Constants.API_URL;
import static com.azinecllc.champy.utils.Constants.CAMERA_REQUEST;
import static com.azinecllc.champy.utils.Constants.CROP_PIC;
import static com.azinecllc.champy.utils.Constants.SELECT_FILE;
import static com.azinecllc.champy.utils.Constants.path;

/**
 * Created by SashaKhyzhun on 2/7/17.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private DBHelper dbHelper;
    private Typeface typeface;
    private OfflineMode offline;
    private SessionManager session;
    private String userName, userID, userToken;
    private DailyRemindController reminder;
    private TextView tvChangeName, tvUserName;
    private CHUploadPhoto uploadPhoto;
    private HashMap<String, String> map = new HashMap<>();
    private HashMap<String, String> userDetails = new HashMap<>();
    private Retrofit retrofit;
    public Uri picUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        reminder = new DailyRemindController(getContext()); // here should be getContext()
        session = SessionManager.getInstance(context);
        dbHelper = DBHelper.getInstance(context);
        offline = OfflineMode.getInstance();
        userID = session.getUserId();
        userName = session.getUserName();
        userToken = session.getToken();
        userDetails = session.getUserDetails();
        uploadPhoto = new CHUploadPhoto(getContext());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewSettings = inflater.inflate(R.layout.content_settings, container, false);

        ImageView userImageProfile = (ImageView) viewSettings.findViewById(R.id.img_profile);

        File fileProfile = new File(path, "profile.jpg");
        Uri userPicturePath = Uri.fromFile(fileProfile);
        Glide.with(this)
                .load(userPicturePath)
                .bitmapTransform(new CropCircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(130, 130)
                .into(userImageProfile);

        initSwitches(viewSettings);

        TextView about = (TextView) viewSettings.findViewById(R.id.about);
        TextView avatar = (TextView) viewSettings.findViewById(R.id.avatar);
        TextView tvLegal = (TextView) viewSettings.findViewById(R.id.tvLegal);
        TextView delete = (TextView) viewSettings.findViewById(R.id.delete_acc);
        TextView tvGeneral = (TextView) viewSettings.findViewById(R.id.tvGeneral);
        TextView contactUs = (TextView) viewSettings.findViewById(R.id.contact_us);
        TextView tvNotif = (TextView) viewSettings.findViewById(R.id.tvNotifications);
        tvUserName = (TextView) viewSettings.findViewById(R.id.tvUserName);
        tvChangeName = (TextView) viewSettings.findViewById(R.id.tvName);

        tvUserName.setText(userName);
        tvUserName.setTypeface(typeface);
        tvLegal.setTypeface(typeface);
        tvNotif.setTypeface(typeface);
        tvGeneral.setTypeface(typeface);

        about.setOnClickListener(this);
        delete.setOnClickListener(this);
        avatar.setOnClickListener(this);
        contactUs.setOnClickListener(this);
        tvChangeName.setOnClickListener(this);


        return viewSettings;
    }

    @Override
    public void onClick(View v) {
        if (!offline.isConnectedToRemoteAPI(getActivity())) {
            return;
        }

        switch (v.getId()) {
            case R.id.tvName:
                LinearLayout layoutEditText = (LinearLayout) getActivity().findViewById(R.id.layoutEditText);
                TextView tvEnterYourName = (TextView) getActivity().findViewById(R.id.tvEnterNewName);
                EditText etNewName = (EditText) getActivity().findViewById(R.id.editTextNewName);
                Button buttonOK = (Button) getActivity().findViewById(R.id.buttonOk);
                View lineOfTheNed = getActivity().findViewById(R.id.view11);

                if (layoutEditText.getVisibility() == View.GONE) {
                    layoutEditText.setVisibility(View.VISIBLE);
                    tvEnterYourName.setVisibility(View.VISIBLE);
                    lineOfTheNed.setVisibility(View.VISIBLE);
                    etNewName.setVisibility(View.VISIBLE);
                    etNewName.setText(userName);

                    buttonOK.setVisibility(View.VISIBLE);
                    buttonOK.setOnClickListener(v1 -> {
                        String userNewName = (etNewName.getText().toString().isEmpty())
                                ? session.getUserName() : etNewName.getText().toString().trim();
                        session.setUserName(userNewName);
                        updateUserName(userNewName); // call
                        tvUserName.setText(etNewName.getText().toString());
                        layoutEditText.setVisibility(View.GONE);
                        tvEnterYourName.setVisibility(View.GONE);
                        etNewName.setVisibility(View.GONE);
                        buttonOK.setVisibility(View.GONE);
                        lineOfTheNed.setVisibility(View.GONE);
                        getActivity().recreate();
                    });
                } else {
                    layoutEditText.setVisibility(View.GONE);
                    tvEnterYourName.setVisibility(View.GONE);
                    etNewName.setVisibility(View.GONE);
                    buttonOK.setVisibility(View.GONE);
                    lineOfTheNed.setVisibility(View.GONE);
                }
                break;
            case R.id.avatar:
                if (!checkWriteExternalPermission()) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE}, 1);
                    return;
                }

                LinearLayout layoutButtons = (LinearLayout) getActivity().findViewById(R.id.layoutButtons);
                TextView tvTakeAPicture = (TextView) getActivity().findViewById(R.id.textViewTakeAPicture);
                TextView tvChooseFrom = (TextView) getActivity().findViewById(R.id.textViewChooseFromGallery);

                if (layoutButtons.getVisibility() == View.GONE) {
                    layoutButtons.setVisibility(View.VISIBLE);
                    tvTakeAPicture.setVisibility(View.VISIBLE);
                    tvChooseFrom.setVisibility(View.VISIBLE);
//                    tvChooseFrom.setOnClickListener(view -> startActivity(new Intent(context, PhotoActivity.class)));

                    // context, this
                    tvChooseFrom.setOnClickListener(view -> Crop.pickImage(context, this));

                    tvTakeAPicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(path, "profile.jpg");
                            picUri = Uri.fromFile(f);
                            startActivityForResult(intent, CAMERA_REQUEST);
                        }
                    });

                } else {
                    layoutButtons.setVisibility(View.GONE);
                    tvTakeAPicture.setVisibility(View.INVISIBLE);
                    tvChooseFrom.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.delete_acc:
                if (!session.getChampyOptions().get("challenges").equals("0")) {
                    surrenderAllChallengesDialog();
                } else {
                    deleteAccountDialog();
                }
                break;
            case R.id.about:
                startActivity(new Intent(context, AboutActivity.class));
                break;
            case R.id.contact_us:
                startActivity(new Intent(context, ContactUsActivity.class));
                break;

        }
        updateProfile(map);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data"); // get the cropped bitmap
                savePhoto(thePic);
                uploadPhoto.uploadPhotoForAPI(saveToStorageFromCamera(thePic));
                getActivity().recreate(); // or startActivity(new Intent(getActivity(), 1.class));
            } else if (requestCode == Crop.REQUEST_PICK) {
                beginCrop(data.getData());
            } else if (requestCode == Crop.REQUEST_CROP) {
                try {
                    handleCrop(resultCode, data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CROP_PIC) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data"); // get the cropped bitmap
                savePhoto(thePic);
                startActivity(new Intent(getActivity(), MainActivity.class));
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                performCrop(selectedImageUri);
            }

        }
    }

    // Initialization switches
    private void initSwitches(View view) {
        String pushNotify = userDetails.get("pushN");
        String acceptedYour = userDetails.get("acceptedYour");
        String challengeEnd = userDetails.get("challengeEnd");
        String newChallenge = userDetails.get("newChallReq");
        String challForToday = userDetails.get("challengesForToday");

        map.put("joinedChampy", "true");
        map.put("friendRequests", "true");
        map.put("challengeConfirmation", "true");
        map.put("reminderTime", "12"); // was 17
        map.put("challengeEnd", challengeEnd);
        map.put("challengesForToday", challForToday);
        map.put("acceptedYourChallenge", acceptedYour);
        map.put("newChallengeRequests", newChallenge);
        map.put("pushNotifications", pushNotify);

        Switch switchForPushNotif = (Switch) view.findViewById(R.id.switchPushNotifications);
        switchForPushNotif.setChecked(Boolean.parseBoolean(pushNotify));
        switchForPushNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("pushNotifications", "true");
                updateProfile(map);
            } else {
                map.put("pushNotifications", "false");
                updateProfile(map);
            }
        });

        Switch switchForNewChallRequests = (Switch) view.findViewById(R.id.switchNewChallengeRequest);
        switchForNewChallRequests.setChecked(Boolean.parseBoolean(newChallenge));
        switchForNewChallRequests.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("newChallengeRequests", "true");
                updateProfile(map);
            } else {
                map.put("newChallengeRequests", "false");
                updateProfile(map);
            }
        });

        Switch switchForAcceptedYourChall = (Switch) view.findViewById(R.id.switchAcceptedYourChallenge);
        switchForAcceptedYourChall.setChecked(Boolean.parseBoolean(acceptedYour));
        switchForAcceptedYourChall.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("acceptedYourChallenge", "true");
                updateProfile(map);
            } else {
                map.put("acceptedYourChallenge", "false");
                updateProfile(map);
            }
        });

        Switch switchForChallengesEnd = (Switch) view.findViewById(R.id.switchChallengeEnd);
        switchForChallengesEnd.setChecked(Boolean.parseBoolean(challengeEnd));
        switchForChallengesEnd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("challengeEnd", "true");
                updateProfile(map);
            } else {
                map.put("challengeEnd", "false");
                updateProfile(map);
            }
        });

        Switch switchChallengesForToday = (Switch) view.findViewById(R.id.switchChallengesForToday);
        switchChallengesForToday.setChecked(Boolean.parseBoolean(challForToday));
        switchChallengesForToday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("challengesForToday", "true");
                reminder.enableDailyNotificationReminder();
                updateProfile(map);
            } else {
                map.put("challengesForToday", "false");
                reminder.disableDailyNotificationReminder();
                updateProfile(map);
            }
        });

    }

    // @Call to API
    private void updateProfile(HashMap<String, String> map) {
        session.toggleChallengeEnd(map.get("challengeEnd"));
        session.togglePushNotification(map.get("pushNotifications"));
        session.toggleChallengesForToday(map.get("challengesForToday"));
        session.toggleNewChallengeRequest(map.get("newChallengeRequests"));
        session.toggleAcceptYourChallenge(map.get("acceptedYourChallenge"));

        Update_user update_user = retrofit.create(Update_user.class);
        Profile_data profile_data = new Profile_data(map);
        Call<User> call = update_user.update_profile_options(userID, userToken, profile_data);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    // @Call to API
    private void updateUserName(String newName) {
        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_user_name(userID, userToken, newName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    //взяти имя с базы и поставить как новое в текст-вью.
                } else {
                    Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }

    // @Костыль
    private void surrenderAllChallengesDialog() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            boolean canDeleteAcc = false;
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (offline.isConnectedToRemoteAPI(getActivity())) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ChallengeController cc = new ChallengeController(
                                context,
                                getActivity(),
                                userToken,
                                userID
                        );

                        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
                        if (c.moveToFirst()) {
                            int colchallenge_id = c.getColumnIndex("challenge_id");
                            do {
                                String challenge_id = c.getString(colchallenge_id);
                                try {
                                    cc.give_up(challenge_id, 0, null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } while (c.moveToNext());
                        }
                        canDeleteAcc = true;
                        c.close();
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }

            if (canDeleteAcc) {
                dialog.cancel();
                deleteAccountDialog();
            }

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.areYouSure)
                .setMessage("If you continue you will lose all your challenges")
                .setCancelable(false)
                .setPositiveButton("Continue", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();

    }

    // @Костыль
    private void deleteAccountDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent role = new Intent(context, RoleControllerActivity.class);
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        final Update_user update_user = retrofit.create(Update_user.class);

                        Call<Delete> callForDeleteUser = update_user.delete_user(userID, userToken);
                        callForDeleteUser.enqueue(new Callback<Delete>() {
                            @Override
                            public void onResponse(Response<Delete> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    File profile = new File(path, "profile.jpg");
                                    profile.delete();
                                    File blurred = new File(path, "blurred.png");
                                    blurred.delete();

                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    db.delete("pending", null, null);
                                    db.delete("pending_duel", null, null);
                                    db.delete("duel", null, null);
                                    db.delete("friends", null, null);
                                    db.delete("updated", null, null);
                                    db.delete("myChallenges", null, null);

                                    session.logout(getActivity());
                                    LoginManager.getInstance().logOut();
                                    startActivity(role);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        startActivity(role);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.areYouSure)
                .setMessage(R.string.youWantToDeleteYourAcc)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    // Check Permission
    private boolean checkWriteExternalPermission() {
        int res = getActivity().checkCallingOrSelfPermission(READ_EXTERNAL_STORAGE);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    /**
     * Methods for photo.
     */
    @Nullable
    private String getPath(Uri uri) throws URISyntaxException {
        System.out.println("я веселый метод: getPath(Uri uri)");
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor;

            try {
                cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
                cursor.close(); // added this line, if something went wrong than just delete this line;

            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getContext().getCacheDir(), "cropped"));
        // this thing starts activity for result 'REQUEST_CROP'
        Crop.of(source, destination).asSquare().withMaxSize(300, 300).start(context, this);
    }


    private void handleCrop(int resultCode, Intent result) throws IOException {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            String path = null;
            try {
                path = getPath(uri); // my method which returns path to picture...
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            uploadPhoto.uploadPhotoForAPI(path);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            savePhoto(bitmap); // my method which saves picture to storage
            getActivity().recreate(); // or startActivity(new Intent(getContext(), 1.class));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(context, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private String saveToStorageFromCamera(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/android/data/com.azinecllc.champy/images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 100000000;
        n = generator.nextInt(n);
        String fileName = "Image-" + n + ".jpg";
        File file = new File(myDir, fileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (Uri.fromFile(file).getPath());
    }


    public void savePhoto(Bitmap photo) {
        File profileImage = new File(path, "profile.jpg");
        File profileBlurred = new File(path, "blurred.png");
        Uri uri = Uri.fromFile(profileImage);

        Bitmap blurred = Blur.blurRenderScript(getActivity(), photo, 15);

        session.setUserPicture(uri.toString());

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(profileBlurred);
            blurred.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a loss less format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        out = null;
        try {
            out = new FileOutputStream(profileImage);
            photo.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a loss less format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void performCrop(Uri picUri) {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 4);
            cropIntent.putExtra("aspectY", 2);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 500);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException e) {
            Toast toast = Toast.makeText(getContext(), "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
