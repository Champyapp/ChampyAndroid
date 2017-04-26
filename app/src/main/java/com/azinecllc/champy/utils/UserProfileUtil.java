package com.azinecllc.champy.utils;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * @autor SashaKhyzhun
 * Created on 4/26/17.
 */

public class UserProfileUtil {

    public static void setProfilePicture(Activity activity, String picture) {
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

    public static void setUserNameAndEmail(Activity activity, String name, String email) {
        TextView userName = (TextView) activity.findViewById(R.id.drawer_tv_user_name);
        userName.setText(name);

        TextView userEmail = (TextView) activity.findViewById(R.id.drawer_tv_user_email);
        userEmail.setText(email);
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
