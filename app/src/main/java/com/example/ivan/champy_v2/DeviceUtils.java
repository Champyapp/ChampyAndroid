package com.example.ivan.champy_v2;

/**
 * Created by ivan on 19.02.16.
 */
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class DeviceUtils {

    /**
     * @param context
     * @return the screen height in pixels
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /**
     * @param context
     * @return the screen width in pixels
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}