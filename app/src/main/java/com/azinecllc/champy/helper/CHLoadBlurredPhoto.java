//package com.azinecllc.champy.helper;
//
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.graphics.PorterDuff;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//
//public class CHLoadBlurredPhoto {
//
//    public static Drawable Init(String path) throws FileNotFoundException {
//        File file = new File(path, "blured2.jpg");
//        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
//        Drawable dr = new BitmapDrawable(Resources.getSystem(), bitmap);
//        dr.setColorFilter(Color.argb(230, 52, 108, 117), PorterDuff.Mode.MULTIPLY);
//        return dr;
//    }
//
//}
