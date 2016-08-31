package com.example.ivan.champy_v2.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.Blur;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.interfaces.Update_user;
import com.example.ivan.champy_v2.model.User.User;
import com.soundcloud.android.crop.Crop;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.framed.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Random;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class PhotoActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 1999;
    private static final int CROP_PIC = 1777;
    private ImageView imageView;
    private Uri picUri;
    public final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.change_photo);
        relativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.champy_background));
        final String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);

        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).centerCrop().into((ImageView) findViewById(R.id.photo));

        this.imageView = (ImageView)this.findViewById(R.id.photo);
        TextView camera = (TextView)findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(path, "test.jpg");
                picUri = Uri.fromFile(f);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });
        TextView gallery = (TextView)findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(PhotoActivity.this);
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_REQUEST ) {
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                savePhoto(thePic);
                Upload_photo(SaveFromCamera(thePic));
                Intent intent = new Intent(PhotoActivity.this, SettingsActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Changed", Toast.LENGTH_SHORT).show();
            } else if (requestCode == CROP_PIC){
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                savePhoto(thePic);
                Intent intent = new Intent(PhotoActivity.this, SettingsActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Changed", Toast.LENGTH_SHORT).show();
            } if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
                    Uri uri = data.getData();

                    beginCrop(data.getData());
                } else if (requestCode == Crop.REQUEST_CROP) {
                    try {
                        handleCrop(resultCode, data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            if (requestCode == SELECT_FILE ){
                Uri selectedImageUri = data.getData();
                performCrop(selectedImageUri);

            }
        }
    }


    public String getPath(Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
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
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            Upload_photo(path);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            savePhoto(bitmap);
            Intent intent = new Intent(PhotoActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private String SaveFromCamera(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
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


    public void savePhoto (Bitmap photo) {
        String path = "/data/data/com.example.ivan.champy_v2/app_imageDir/";
        File file = new File(path, "profile.jpg");
        File file1 = new File(path, "blured2.jpg");
        Uri uri = Uri.fromFile(file);

        Blur blur = new Blur();

        Bitmap blured = blur.blurRenderScript(getApplicationContext(), photo, 10);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.change_avatar(uri.toString());

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file1);
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
            out = new FileOutputStream(file);
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
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public void Upload_photo(String path) {
        final String API_URL = "http://46.101.213.24:3007";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = new HashMap<>();
        user = sessionManager.getUserDetails();
        String token = user.get("token");
        String id = user.get("id");

        File f = new File(path);

        RequestBody requestBody =
                RequestBody.create(MediaType.parse("image/jpeg"), f);

        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_photo(id, token, requestBody);
        Log.d(TAG, "Status: RUN");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) { Log.d(TAG, "Status: photo_uploaded");}
                else Log.d(TAG, "Status :" + response.code()); }

            @Override
            public void onFailure(Throwable t) {Log.d(TAG, "Status: "+t); }
        });
    }


}



