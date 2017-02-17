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
import java.io.InputStream;
import java.util.Random;

import retrofit.Retrofit;

/**
 * Created by SashaKhyzhun on 2/17/17.
 */

public class CHDownloadPhotoAndSave extends AsyncTask<String, Void, Bitmap> {

    private Context context;
    private Retrofit retrofit;

    public CHDownloadPhotoAndSave(Context context, Retrofit retrofit) {
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
        String fileName = "ChampyAwesomePicture.jpg";

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
