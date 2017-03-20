package com.azinecllc.champy.fragment;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
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
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.controller.DailyRemindController;
import com.azinecllc.champy.controller.UserController;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static com.azinecllc.champy.utils.Constants.API_URL;
import static com.azinecllc.champy.utils.Constants.CAMERA_REQUEST;
import static com.azinecllc.champy.utils.Constants.CROP_PIC;
import static com.azinecllc.champy.utils.Constants.SELECT_FILE;
import static com.azinecllc.champy.utils.Constants.path;

/**
 * @autor SashaKhyzhun
 * Created on 2/7/17.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private DBHelper dbHelper;
    private Typeface typeface;
    private OfflineMode offline;
    private SessionManager session;
    private ImageView userImageProfile;
    private UserController userController;
    private DailyRemindController reminder;
    private TextView tvChangeName, tvUserName;
    private HashMap<String, String> map = new HashMap<>();
    private String userName, userPicture;
    private HashMap<String, String> userDetails = new HashMap<>();
    public Uri picUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        session = SessionManager.getInstance(context);
        dbHelper = DBHelper.getInstance(context);
        offline = OfflineMode.getInstance();
        userName = session.getUserName();
        userPicture = session.getUserPicture();
        userDetails = session.getUserDetails();
        userController = new UserController(session, retrofit);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewSettings = inflater.inflate(R.layout.fragment_settings, container, false);

        userImageProfile = (ImageView) viewSettings.findViewById(R.id.img_profile);

        Glide.with(this)
                .load(userPicture)
                .bitmapTransform(new CropCircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(130, 130)
                .into(userImageProfile);

        //userImageProfile.setOnClickListener(v -> reminder.enableDailyNotificationReminder());

        initSwitches(viewSettings);

        TextView about = (TextView) viewSettings.findViewById(R.id.about);
        TextView avatar = (TextView) viewSettings.findViewById(R.id.avatar);
        TextView delete = (TextView) viewSettings.findViewById(R.id.delete_acc);
        TextView tvNotif = (TextView) viewSettings.findViewById(R.id.tvNotifications);
        TextView tvLegal = (TextView) viewSettings.findViewById(R.id.tvLegal);
        TextView tvLogout = (TextView) viewSettings.findViewById(R.id.textViewLogout);
        TextView tvGeneral = (TextView) viewSettings.findViewById(R.id.tvGeneral);
        TextView contactUs = (TextView) viewSettings.findViewById(R.id.contact_us);

        tvUserName = (TextView) viewSettings.findViewById(R.id.tv_user_name);
        tvChangeName = (TextView) viewSettings.findViewById(R.id.tvName);

        tvUserName.setText(userName);
        tvUserName.setTypeface(typeface);
        tvLegal.setTypeface(typeface);
        tvNotif.setTypeface(typeface);
        tvGeneral.setTypeface(typeface);

        about.setOnClickListener(this);
        delete.setOnClickListener(this);
        avatar.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
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
                    etNewName.setText(session.getUserName());

                    buttonOK.setVisibility(View.VISIBLE);
                    buttonOK.setOnClickListener(v1 -> {
                        String newName = etNewName.getText().toString().trim();
                        if (!newName.equals(session.getUserName()) && !newName.isEmpty()) {
                            session.setUserName(newName);
                            userController.updateUserName(newName); // call
                            tvUserName.setText(newName);
                            etNewName.setText(newName);
                            TextView drawerUserName = (TextView) getActivity().findViewById(R.id.drawer_tv_user_name);
                            drawerUserName.setText(newName);
                            userController.updateProfile(map);
                        }
                        hideKeyboard();
                        layoutEditText.setVisibility(View.GONE);
                        tvEnterYourName.setVisibility(View.GONE);
                        etNewName.setVisibility(View.GONE);
                        buttonOK.setVisibility(View.GONE);
                        lineOfTheNed.setVisibility(View.GONE);
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
                TextView tvTakeAPicture = (TextView) getActivity().findViewById(R.id.buttonTakeAPicture);
                TextView tvChooseFrom = (TextView) getActivity().findViewById(R.id.buttonChooseFromGallery);

                if (layoutButtons.getVisibility() == View.GONE) {
                    layoutButtons.setVisibility(View.VISIBLE);
                    tvTakeAPicture.setVisibility(View.VISIBLE);
                    tvChooseFrom.setVisibility(View.VISIBLE);
                    tvChooseFrom.setOnClickListener(view -> {
                        Crop.pickImage(context, this);
                        layoutButtons.setVisibility(View.GONE);
                    });
                    tvTakeAPicture.setOnClickListener(view -> {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String root = Environment.getExternalStorageDirectory().toString(); // path
                        File f = new File(root + path, "profile.jpg");
                        picUri = Uri.fromFile(f);
                        startActivityForResult(intent, CAMERA_REQUEST);
                        layoutButtons.setVisibility(View.GONE);
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
            case R.id.textViewLogout:
                userController.updateProfile(map);
                session.logout(getActivity());
                break;
            case R.id.about:
                userController.updateProfile(map);
                startActivity(new Intent(context, AboutActivity.class));
                break;
            case R.id.contact_us:
                userController.updateProfile(map);
                startActivity(new Intent(context, ContactUsActivity.class));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data"); // get the cropped bitmap
                savePhotoToStorageAndMakeBG(thePic);
                userController.uploadPhotoForAPI(getPicturePathFromStorage(thePic));
                //getActivity().recreate();
            } else if (requestCode == Crop.REQUEST_PICK) {
                // this thing starts activity for result 'REQUEST_CROP'
                Uri destination = Uri.fromFile(new File(getContext().getCacheDir(), "cropped"));
                Crop.of(data.getData(), destination).asSquare().withMaxSize(300, 300).start(context, this);
            } else if (requestCode == Crop.REQUEST_CROP) {
                try {
                    handleCrop(resultCode, data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //getActivity().recreate();
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                performCrop(selectedImageUri); // this thing starts activity for result 'CROP_PIC'
            } else if (requestCode == CROP_PIC) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data"); // get the cropped bitmap
                savePhotoToStorageAndMakeBG(thePic);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().isFinishing();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    /**
     * Simple initialization switches. Here I had set for them onClickListeners, load current state
     * from sessionManager and inputted call for api after each changes.
     */
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
            } else {
                map.put("pushNotifications", "false");
            }
            userController.updateProfile(map);
        });

        Switch switchForNewChallRequests = (Switch) view.findViewById(R.id.switchNewChallengeRequest);
        switchForNewChallRequests.setChecked(Boolean.parseBoolean(newChallenge));
        switchForNewChallRequests.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("newChallengeRequests", "true");
            } else {
                map.put("newChallengeRequests", "false");
            }
            userController.updateProfile(map);
        });

        Switch switchForAcceptedYourChall = (Switch) view.findViewById(R.id.switchAcceptedYourChallenge);
        switchForAcceptedYourChall.setChecked(Boolean.parseBoolean(acceptedYour));
        switchForAcceptedYourChall.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("acceptedYourChallenge", "true");
            } else {
                map.put("acceptedYourChallenge", "false");
            }
            userController.updateProfile(map);
        });

        Switch switchForChallengesEnd = (Switch) view.findViewById(R.id.switchChallengeEnd);
        switchForChallengesEnd.setChecked(Boolean.parseBoolean(challengeEnd));
        switchForChallengesEnd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("challengeEnd", "true");
            } else {
                map.put("challengeEnd", "false");
            }
            userController.updateProfile(map);
        });

        Switch switchChallengesForToday = (Switch) view.findViewById(R.id.switchChallengesForToday);
        switchChallengesForToday.setChecked(Boolean.parseBoolean(challForToday));
        switchChallengesForToday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminder = new DailyRemindController(getContext()); // here should be getContext()
            if (isChecked) {
                map.put("challengesForToday", "true");
                reminder.enableDailyNotificationReminder();
            } else {
                map.put("challengesForToday", "false");
                reminder.disableDailyNotificationReminder();
            }
            userController.updateProfile(map);
        });

    }

    /**
     * Method to avoid bug on APi. Before deleting an account we need to surrender all challenge.
     * In case if we doesn't do it another opponent always will have one challenge in progress.
     * @Костыль
     */
    private void surrenderAllChallengesDialog() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            boolean canDeleteAcc = false;
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (offline.isConnectedToRemoteAPI(getActivity())) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ChallengeController cc = new ChallengeController(context, getActivity());

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
        builder.setTitle(R.string.are_you_sure)
                .setMessage("If you continue you will lose all your challenges")
                .setCancelable(false)
                .setPositiveButton("Continue", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();

    }

    /**
     * Method to delete user account. We make call to api for that and locally clear out database,
     * delete user profile and clear other data.
     * @Костыль
     */
    private void deleteAccountDialog() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    userController.deleteUserProfile(dbHelper, getActivity());
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.are_you_sure)
                .setMessage(R.string.youWantToDeleteYourAcc)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    /**
     * Method which returns boolean value of granted permission. To work with storage we need to
     * have this permission and we had to check it in runtime
     * @return value of granted permission
     */
    private boolean checkWriteExternalPermission() {
        int res = getActivity().checkCallingOrSelfPermission(READ_EXTERNAL_STORAGE);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Method witch handling crop the picture. Here we get photo path, cropped it and upload on API
     * @param resultCode - code from intent 'start activity for result' must be 'OK'
     * @param result - code from intent which shows for us specify request, must be 'REQUEST_CROP'
     * @throws IOException if something went wrong then we  can expect empty fields.
     */
    private void handleCrop(int resultCode, Intent result) throws IOException {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            String path = null;
            try {
                path = getPath(uri); // my method which returns path to picture...
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            userController.uploadPhotoForAPI(path);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            savePhotoToStorageAndMakeBG(bitmap); // my method which saves picture to storage

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(context, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to save photo in storage from camera. Actually this method creates folder and file
     * like as Object on device. We uses this method after 'take picture' and 'choose from storage'
     * @param photo - bitmap which we get from extras.getParcelable
     */
    private void savePhotoToStorageAndMakeBG(Bitmap photo) {
        String root = Environment.getExternalStorageDirectory().toString(); // path
        File myDir = new File(root + "/android/data/com.azinecllc.champy/images"); // folder
        myDir.mkdirs(); // create folder

        File file = new File(myDir, "profile.jpg"); // file
        if (!file.exists()) {
            try { // if not exist
                file.createNewFile(); // create
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.delete(); // if exist - delete
            try {
                file.createNewFile(); // and again create, ibo nexyu
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Uri uri = Uri.fromFile(file);
        //userController.uploadPhotoForAPI(uri.toString());
        session.setUserPicture(uri.toString());

        Glide.with(getActivity())
                .load(uri)
                .bitmapTransform(new CropCircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(130, 130)
                .into(userImageProfile);
        Glide.with(getActivity())
                .load(uri)
                .bitmapTransform(new CropCircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into((ImageView) getActivity().findViewById(R.id.drawer_user_photo));
        Glide.with(getActivity())
                .load(uri)
                .bitmapTransform(new BlurTransformation(context, 25))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into((ImageView) getActivity().findViewById(R.id.drawer_background));
//        Glide.with(getActivity())
//                .load(uri)
//                .bitmapTransform(new BlurTransformation(context, 25))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into((ImageView) getActivity().findViewById(R.id.main_background));

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file); // create folder in system like a real FILE, not virtual.
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close(); // closing stream
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        out = null;
        try {
            out = new FileOutputStream(file); // create picture in system like a real FILE, not virtual.
            photo.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close(); // closing stream
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method to get photo from cache-folder in an Internal storage on the device. We use this method
     * only after 'take a picture from camera' because we need to create this picture into storage.
     *
     * @param finalBitmap - this is parcelable data from intent (extras.getParcelable("data"))
     * @return the actually path to picture with witch we can upload this photo to API.
     */
    private String getPicturePathFromStorage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/android/data/com.azinecllc.champy/images");
        myDir.mkdirs();
        String fileName = "profile.jpg";
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

    /**
     * Method to start crop the photo. This method starts activity for result 'CROP_PIC'
     * @param picUri - this is data from intent: data.getData();
     */
    private void performCrop(Uri picUri) {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*"); // indicate image type and Uri
            cropIntent.putExtra("crop", "true");          // set crop properties
            cropIntent.putExtra("aspectX", 4);            // indicate aspect of desired crop
            cropIntent.putExtra("aspectY", 2);            // indicate output X and Y
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 500);
            cropIntent.putExtra("return-data", true);     // retrieve data on return
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException e) {
            Toast toast = Toast.makeText(getContext(), "This device doesn't support the crop action!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * Methods to get absolute path to picture from internal storage.
     *
     * @param uri - this is data from intent: data.getData();
     * @return absolute path to picture from storage
     */
    @Nullable
    private String getPath(Uri uri) throws URISyntaxException {
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

    /**
     * Method to hide keyboard automatically after click on the button 'OK' with or without
     * some changes.
     */
    private void hideKeyboard() {
        InputMethodManager input = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getActivity().getWindow().getCurrentFocus() != null) {
            input.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
        }
    }


}
