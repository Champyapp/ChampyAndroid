package com.azinecllc.champy.helper;

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
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.azinecllc.champy.champy_v2.R;
import com.azinecllc.champy.activity.MainActivity;
import com.azinecllc.champy.utils.Blur;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/** Class helper for download and blur user's photo.
 *  Use the compress method on the BitMap object to write image to the OutputStream ! */
public class CHDownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private Context context;
    private MainActivity activity;

    public CHDownloadImageTask(Context context, MainActivity activity) {
        this.context = context;
        this.activity = activity;
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


    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(context);
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/android/data/com.azinecllc.champy/images");
        myDir.mkdirs();

        Random generator = new Random();
        int n = 100000000;
        n = generator.nextInt(n);
        String fileName = "Image-" + n + ".jpg";

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

        CHUploadPhoto uploadPhoto = new CHUploadPhoto(context);
        uploadPhoto.uploadPhotoForAPI(Uri.fromFile(file).getPath());

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


    protected void onPostExecute(Bitmap result) {
        // Do your staff here to save image
        saveToInternalStorage(result);
        loadImageFromStorage("/data/data/com.example.ivan.champy_v2/app_imageDir/");
    }


    private void loadImageFromStorage(String path) {
        try {
            File f = new File(path, "profile.jpg");
            Uri uri = Uri.fromFile(f);
            Glide.with(context)
                    .load(uri)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into((ImageView)activity.findViewById(R.id.profile_image));

            File file = new File(path, "blured2.jpg");
            if (file.exists()) {
                return;
            } else {
                file.createNewFile();

                /*********************************** Make Blur ************************************/
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                Bitmap bitmap = Blur.blurRenderScript(context, b, 10);
                Drawable dr = new BitmapDrawable(context.getResources(), bitmap);
                dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);

                /******************************* Set blurred view *******************************/
                ImageView background = (ImageView)activity.findViewById(R.id.main_background);
                background.setScaleType(ImageView.ScaleType.CENTER_CROP);
                background.setImageDrawable(dr);

                ImageView drawerBackground = (ImageView)activity.findViewById(R.id.slide_background);
                drawerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
                drawerBackground.setImageDrawable(dr);

                ImageView blurItem = (ImageView)activity.findViewById(R.id.item_blur);
                blurItem.setVisibility(View.INVISIBLE);

                /******************** Write image to OutputStream and close it ********************/
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap = ((BitmapDrawable)dr).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapData = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapData);
                fos.flush();
                fos.close();

//                RelativeLayout relativeLayout = (RelativeLayout)activity.findViewById(R.id.content_main);
//                relativeLayout.setDrawingCacheEnabled(true);
//                relativeLayout.buildDrawingCache();
                //Bitmap bm = relativeLayout.getDrawingCache();
                //blurred = Blur.blurRenderScript(context, bm, 25);

//                ImageView screen = (ImageView)activity.findViewById(R.id.blurScreen);

//                Drawable ob = new BitmapDrawable(context.getResources(), blurred);
//                screen.setImageDrawable(ob);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

}
