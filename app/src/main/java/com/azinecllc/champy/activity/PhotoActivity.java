package com.azinecllc.champy.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.azinecllc.champy.R;
import com.azinecllc.champy.helper.CHUploadPhoto;
import com.azinecllc.champy.interfaces.Update_user;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.Blur;
import com.azinecllc.champy.utils.SessionManager;
import com.soundcloud.android.crop.Crop;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;
import static com.azinecllc.champy.utils.Constants.path;

public class PhotoActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 1999;
    private static final int CROP_PIC = 1777;
    private final String TAG = "PhotoActivity";
    private SessionManager sessionManager;
    private CHUploadPhoto uploadPhoto;
    public Uri picUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        sessionManager = SessionManager.getInstance(getApplicationContext());

        Typeface typeface = android.graphics.Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
        TextView tvChooseFromGallery = (TextView)findViewById(R.id.tv_choose_from_gallery);
        TextView tvTakePicture = (TextView)findViewById(R.id.tv_take_a_picture);
        ImageView imageView = (ImageView)findViewById(R.id.photo);
        tvChooseFromGallery.setTypeface(typeface);
        tvTakePicture.setTypeface(typeface);

        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);
        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).centerCrop().into((ImageView) findViewById(R.id.photo));

        Glide.with(this)
                .load(url)
                .asBitmap()
                .transform(new CropCircleTransformation(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(100, 100)
                .dontAnimate()
                .into(imageView);

        uploadPhoto = new CHUploadPhoto(getApplicationContext());

        tvChooseFromGallery.setOnClickListener(v -> {
            Crop.pickImage(PhotoActivity.this);
        });

        tvTakePicture.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File f = new File(path, "profile.jpg");
            picUri = Uri.fromFile(f);
            startActivityForResult(intent, CAMERA_REQUEST);
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_REQUEST ) {
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                savePhoto(thePic);
//                Upload_photo(saveFromCamera(thePic));
                uploadPhoto.uploadPhotoForAPI(saveFromCamera(thePic));

                Intent intent = new Intent(PhotoActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else if (requestCode == CROP_PIC) {
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                savePhoto(thePic);
                Intent intent = new Intent(PhotoActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else if (requestCode == Crop.REQUEST_PICK) {
                beginCrop(data.getData());
            } else if (requestCode == Crop.REQUEST_CROP) {
                try {
                    handleCrop(resultCode, data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE ){
                Uri selectedImageUri = data.getData();
                performCrop(selectedImageUri);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


    @Nullable
    private String getPath(Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor;

            try {
                cursor = getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
                cursor.close(); // added this line, if something went wrong than just delete this line;

            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().withMaxSize(300, 300).start(this);
    }


    private void handleCrop(int resultCode, Intent result) throws IOException {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            String path = null;
            try {
                path = getPath(uri);
            } catch (URISyntaxException e) {e.printStackTrace();}

            //Upload_photo(path);
            uploadPhoto.uploadPhotoForAPI(path);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            savePhoto(bitmap);
            Intent intent = new Intent(PhotoActivity.this, SettingsActivity.class);
            startActivity(intent);

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private String saveFromCamera(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/android/data/com.azinecllc.champy/images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 100000000;
        n = generator.nextInt(n);
        String fileName = "Image-"+ n +".jpg";
        File file = new File (myDir, fileName);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {e.printStackTrace();}

        return (Uri.fromFile(file).getPath());
    }


    public void savePhoto (Bitmap photo) {
        File profileImage = new File(path, "profile.jpg");
        File profileBlured = new File(path, "blurred.png");
        Uri uri = Uri.fromFile(profileImage);

        Bitmap blured = Blur.blurRenderScript(getApplicationContext(), photo, 10);

        sessionManager.change_avatar(uri.toString());

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(profileBlured);
            blured.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
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
            // PNG is a lossless format, the compression factor (100) is ignored
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
            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


//    public void Upload_photo(String path) {
//        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
//        String token = sessionManager.getToken();
//        String id = sessionManager.getUserId();
//
//        File photoFile = new File(path);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), photoFile);
//
//        Update_user update_user = retrofit.create(Update_user.class);
//        Call<User> call = update_user.update_photo(id, token, requestBody);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Response<User> response, Retrofit retrofit) {
//                String myLog = (response.isSuccess()) ? "Status: photo uploaded!" : "Status: " + response.code();
//                Log.i(TAG, "onResponse: " + myLog);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Log.d(TAG, "Status: "+t);
//            }
//        });
//    }


}

