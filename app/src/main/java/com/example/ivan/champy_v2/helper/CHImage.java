package com.example.ivan.champy_v2.helper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.Blur;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.interfaces.Update_user;
import com.example.ivan.champy_v2.model.User.User;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import javax.annotation.Resource;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by azinecdevelopment on 8/30/16.
 */
public class CHImage {



    public void saveImage(String imageUrl, String destinationFile, boolean isFacebook, Context context) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] bb = new byte[2048];
        int length;

        while ((length = is.read(bb)) != -1) {
            os.write(bb, 0, length);
        }

        is.close();
        os.close();
        File inputFile = new File("/data/data/com.example.ivan.champy_v2/app_imageDir/", "profile.jpg");
        if (isFacebook) {
            uploadPhotoToServer(inputFile, context);
        }

//        File file = new File("/data/data/com.example.ivan.champy_v2/app_imageDir/", "blured2.jpg");
//
//        file.createNewFile();
//        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(inputFile));
//
//        Blur blur = new Blur();
//
//        Bitmap blured = blur.blurRenderScript(context, b, 10);
//
//        Bitmap bitmap = blured;
//
//        Drawable dr = new BitmapDrawable(context.getResources(), bitmap);
//        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);
//
//        bitmap = ((BitmapDrawable)dr).getBitmap();
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
//        byte[] bitmapdata = bos.toByteArray();
//
//        FileOutputStream fos = new FileOutputStream(file);
//        fos.write(bitmapdata);
//        fos.flush();
//        fos.close();

    }



    public void uploadPhotoToServer(File file, Context context) {
        final String API_URL = "http://46.101.213.24:3007";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        String token = user.get("token");

        String id = user.get("id");

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);

        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_photo(id, token, requestBody);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) Log.d("UploadPhoto", "Status: VSE OK");
                else Log.d("UploadPhoto", "Status :" + response.code());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("asd", "Status: " + t);
            }
        });
    }

}
