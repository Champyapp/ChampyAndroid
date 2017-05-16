package com.azinecllc.champy.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.controller.UserController;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.azinecllc.champy.utils.UserProfileUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.soundcloud.android.crop.Crop;
import com.thebluealliance.spectrum.SpectrumDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
 * Created on 4/3/17.
 */

public class SettingsProfileFragment extends Fragment {

    @BindView(R.id.fabChangePhoto)
    FloatingActionButton fabChangePhoto;
    @BindView(R.id.tv_logout)
    TextView tvLogout;
    @BindView(R.id.tv_delete_account)
    TextView tvDeleteAcc;
    @BindView(R.id.tv_first_name)
    TextView tvFirstName;
    @BindView(R.id.tv_select_color)
    TextView tvSelectColor;
    private SessionManager sessionManager;
    private DBHelper dbHelper;
    private OfflineMode offlineMode;
    private Uri picUri;
    private Retrofit retrofit;
    private UserController userController;
    private ImageView ivUserPhotoBG;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = SessionManager.getInstance(getContext());
        dbHelper = DBHelper.getInstance(getContext());
        offlineMode = OfflineMode.getInstance();
        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userController = new UserController(sessionManager, retrofit);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_profile_two, container, false);
        ButterKnife.bind(this, view);

//        ExpandableHeightListView listView = (ExpandableHeightListView) view.findViewById(R.id.list_view_colors);
//
//        Integer[] intColors = new Integer[]{
//                Color.RED,
//                Color.parseColor("#FFCF670C"),
//                Color.YELLOW,
//                Color.GREEN,
//                Color.BLUE,
//                Color.parseColor("#FF7209DA"),
//                Color.parseColor("#FFEC03DC")
//        };
//
//        String[] stringColors = new String[]{
//                "Red",
//                "Orange",
//                "Yellow",
//                "Green",
//                "Blue",
//                "Purple",
//                "Pink"
//        };
//
//        ArrayAdapter<String> adapterColorText = new ArrayAdapter<String>(
//                getContext(),
//                R.layout.item_list_view_colors,
//                R.id.text_view_color,
//                stringColors
//        );
//        ArrayAdapter<Integer> adapterColorHex = new ArrayAdapter<Integer>(
//                getContext(),
//                R.layout.item_list_view_colors,
//                R.id.text_view_color,
//                intColors
//        );
//
//
//
//
//        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        listView.setAdapter(adapterColorText);
//        listView.setExpanded(true); // This actually do the magic
//        listView.setAdapter(adapterColor);

        ivUserPhotoBG = (ImageView) view.findViewById(R.id.iv_profile_picture_bg);

//        ImageView fabChangePhoto = (ImageView) view.findViewById(R.id.iv_change_photo);
//        TextView tvFirstName = (TextView) view.findViewById(R.id.tv_first_name);
//        TextView tvLastName = (TextView) view.findViewById(R.id.tv_last_name);
//        TextView tvLogout = (TextView) view.findViewById(R.id.tv_logout);
//        TextView tvDeleteAcc = (TextView) view.findViewById(R.id.tv_delete_account);

        Switch switchFB = (Switch) view.findViewById(R.id.switch_facebook);
        switchFB.setChecked(sessionManager.isUserLoggedIn());

        String userPicture = sessionManager.getUserPicture();
        String userEmail = sessionManager.getUserEmail();
        String userName = sessionManager.getUserName();
        Glide.with(this)
                .load(userPicture)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(ivUserPhotoBG);

        tvFirstName.setText(userName);


        return view;
    }

    @OnClick(R.id.tv_select_color)
    public void onClickSelectColor() {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.demo_colors)
                .setSelectedColorRes(android.R.color.holo_red_dark)
                .setDismissOnColorSelected(false)
                .setOutlineWidth(0)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color).toUpperCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Dialog cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build()
                .show(getFragmentManager(), "dialog_demo_1");
    }

    @OnClick(R.id.fabChangePhoto)
    public void onClickChangePhoto() {
        Toast.makeText(getContext(), "clicked on the camera", Toast.LENGTH_SHORT).show();
        if (!checkWriteExternalPermission()) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE}, 1);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Title");
        builder.setItems(new CharSequence[]{"From Camera", "From Gallery"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    Toast.makeText(getContext(), "clicked 'From Camera'", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String root = Environment.getExternalStorageDirectory().toString(); // path
                    File f = new File(root + path, "profile.jpg");
                    picUri = Uri.fromFile(f);
                    startActivityForResult(intent, CAMERA_REQUEST);
                    break;
                case 1:
                    Toast.makeText(getContext(), "clicked 'From Gallery'", Toast.LENGTH_SHORT).show();
                    Crop.pickImage(getContext(), this);
                    break;
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data"); // get the cropped bitmap
                savePhotoToStorageAndMakeBG(thePic);
                userController.uploadPhotoForAPI(getPicturePathFromStorage(thePic));
            } else if (requestCode == Crop.REQUEST_PICK) {
                // this thing starts activity for result 'REQUEST_CROP'
                Uri destination = Uri.fromFile(new File(getContext().getCacheDir(), "cropped"));
                Crop.of(data.getData(), destination).asSquare().withMaxSize(300, 300).start(getContext(), this);
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

    @OnClick(R.id.tv_logout)
    public void onClickLogout() {
        sessionManager.logout(getActivity());
        Toast.makeText(getContext(), "{Logout...}", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.tv_delete_account)
    public void onClickDeleteAccount() {
        if (!sessionManager.getChampyOptions().get("challenges").equals("0")) {
            surrenderAllChallengesDialog();
        } else {
            deleteAccountDialog();
        }
    }

    @OnClick(R.id.switch_facebook)
    public void onClickFacebookSwitch() {
        if (sessionManager.isUserLoggedIn()) {
            sessionManager.logout(getActivity());
            Toast.makeText(getContext(), "Logout...", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Login...", Toast.LENGTH_SHORT).show();

    }


    /**
     * Method to avoid bug on APi. Before deleting an account we need to surrender all challenge.
     * In case if we doesn't do it another opponent always will have one challenge in progress.
     *
     * @Костыль
     */
    private void surrenderAllChallengesDialog() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            boolean canDeleteAcc = false;
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (offlineMode.isConnectedToRemoteAPI(getActivity())) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ChallengeController cc = new ChallengeController(getContext(), getActivity());

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
     *
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.are_you_sure)
                .setMessage(R.string.youWantToDeleteYourAcc)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    /**
     * Method to save photo in storage from camera. Actually this method creates folder and file
     * like as Object on device. We uses this method after 'take picture' and 'choose from storage'
     *
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
        sessionManager.setUserPicture(uri.toString());


        Glide.with(this)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(ivUserPhotoBG);

        /////////////////////////////////////////////////////////////////////////////////

        UserProfileUtil.setProfilePicture(getActivity(), uri.toString());
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
     * Method witch handling crop the picture. Here we get photo path, cropped it and upload on API
     *
     * @param resultCode - code from intent 'start activity for result' must be 'OK'
     * @param result     - code from intent which shows for us specify request, must be 'REQUEST_CROP'
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
            Toast.makeText(getContext(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
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
     * Method to start crop the photo. This method starts activity for result 'CROP_PIC'
     *
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
     * Method to hide keyboard automatically after click on the button 'OK' with or without
     * some changes.
     */
    private void hideKeyboard() {
        InputMethodManager input = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getActivity().getWindow().getCurrentFocus() != null) {
            input.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Method which returns boolean value of granted permission. To work with storage we need to
     * have this permission and we had to check it in runtime
     *
     * @return value of granted permission
     */
    private boolean checkWriteExternalPermission() {
        int res = getActivity().checkCallingOrSelfPermission(READ_EXTERNAL_STORAGE);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

}
