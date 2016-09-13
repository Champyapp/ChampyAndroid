package com.example.ivan.champy_v2.helper;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.Blur;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.activity.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CHDownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    Context context;
    MainActivity activity;

    public CHDownloadImageTask(Context context, MainActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Log.d("DownLoadImageTask", "doInBackground: " + urldisplay);

        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("DownLoadImageTask", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
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

        CHUploadPhoto uploadPhoto = new CHUploadPhoto(context);
        uploadPhoto.uploadPhotoForAPI(Uri.fromFile(file).getPath());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        // Create imageDir
        File myPath = new File(directory,"profile.jpg");
        Log.i("DownloadImageTask", "saveToInternalStorage | PhotoPath: " + myPath.toString());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    public void loadImageFromStorage(String path) {
        try {
            File f = new File(path, "profile.jpg");
            Uri uri = Uri.fromFile(f);
            Log.i("DownloadImageTask", "LoadImageFromStorage: URI = " + uri);
            Glide.with(context).load(uri).bitmapTransform(new CropCircleTransformation(context))
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into((ImageView)activity.findViewById(R.id.profile_image));
            File file = new File(path, "blured2.jpg");
            if (file.exists()) {
                return;
            } else {
                file.createNewFile();
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

                Blur blur = new Blur();

                Bitmap blured = blur.blurRenderScript(context, b, 10);

                Bitmap bitmap = blured;

                Drawable dr = new BitmapDrawable(context.getResources(), bitmap);
                dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);
                ImageView background = (ImageView)activity.findViewById(R.id.slide_background);
                background.setScaleType(ImageView.ScaleType.CENTER_CROP);
                background.setImageDrawable(dr);
                background = (ImageView)activity.findViewById(R.id.main_background);
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

                RelativeLayout relativeLayout = (RelativeLayout)activity.findViewById(R.id.content_main);
                relativeLayout.setDrawingCacheEnabled(true);
                relativeLayout.buildDrawingCache();
                Bitmap bm = relativeLayout.getDrawingCache();

                blur = new Blur();
                blured = blur.blurRenderScript(context, bm, 25);

                ImageView screen = (ImageView)activity.findViewById(R.id.blurScreen);

                Drawable ob = new BitmapDrawable(context.getResources(), blured);
                screen.setImageDrawable(ob);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void onPostExecute(Bitmap result) {
        // Do your staff here to save image
        saveToInternalStorage(result);
        loadImageFromStorage("/data/data/com.example.ivan.champy_v2/app_imageDir/");
    }

}
