package com.azinecllc.champy.helper;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.azinecllc.champy.controller.UserController;
import com.azinecllc.champy.utils.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit.Retrofit;

/**
 * Created by SashaKhyzhun on 2/17/17.
 * Class Helper to save photo from facebook to local storage and upload it on the API.
 * How I know only async task can do it, because we have only path to picture when we need bitmap.
 * Here we get path to picture, create folder, create photoFile, save it and put picture into file
 */

public class CHSaveAndUploadPhoto extends AsyncTask<String, Void, Bitmap> {

    private Context context;
    private Retrofit retrofit;

    public CHSaveAndUploadPhoto(Context context, Retrofit retrofit) {
        this.context = context;
        this.retrofit = retrofit;
    }


    protected Bitmap doInBackground(String... urls) {
        String path_to_pic = urls[0];
        Bitmap bitmapImage = null;
        try {
            InputStream in = new java.net.URL(path_to_pic).openStream();
            bitmapImage = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmapImage;
    }


    protected void onPostExecute(Bitmap result) {
        // Do your staff here to save image
        saveToInternalStorage(result);
    }


    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(context);

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/android/data/com.azinecllc.champy/images");
        myDir.mkdirs();
        String fileName = "profile.jpg";

        File file = new File(myDir, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SessionManager ss = SessionManager.getInstance(context);
        UserController userController = new UserController(ss, retrofit);
        userController.uploadPhotoForAPI(Uri.fromFile(file).getPath());
        userController.updatePushIdentifier();

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, "profile.jpg"); // Create imageDir
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return directory.getAbsolutePath();
    }


}
