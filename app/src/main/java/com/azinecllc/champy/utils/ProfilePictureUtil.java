package com.azinecllc.champy.utils;

import android.app.Activity;
import android.widget.ImageView;

import com.azinecllc.champy.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * @autor SashaKhyzhun
 * Created on 4/26/17.
 */

public class ProfilePictureUtil {

    public static void setProfilePicture(Activity activity, String picture, String name, String email) {
        Glide.with(activity)
                .load(picture)
                .bitmapTransform(new CropCircleTransformation(activity))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into((ImageView) activity.findViewById(R.id.drawer_user_photo));

        Glide.with(activity)
                .load(picture)
                .bitmapTransform(new BlurTransformation(activity, 25))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into((ImageView) activity.findViewById(R.id.drawer_background));


    }

//    public static void setBackgroundPicture(Activity activity, String picture) {
//        Glide.with(activity)
//                .load(picture)
//                .bitmapTransform(new BlurTransformation(activity, 25))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into((ImageView) activity.findViewById(R.id.drawer_background));
//    }

}
