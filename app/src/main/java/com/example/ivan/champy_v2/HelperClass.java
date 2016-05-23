package com.example.ivan.champy_v2;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by ivan on 09.12.15.
 */
public class HelperClass {

    private static Point screenSize;

    public static int getWindowWidth(Context context){
        if(screenSize == null){
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            screenSize = new Point();
            display.getSize(screenSize);
        }
        if(screenSize != null)
            return screenSize.x;
        else
            return 0;
    }

    public static int getWindowHeight(Context context){
        if(screenSize == null){
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            screenSize = new Point();
            display.getSize(screenSize);
        }
        if(screenSize != null)
            return screenSize.y;
        else
            return 0;
    }

    public static int getCurrentCardPositionX(Context context){
        float cardWidth = getWindowWidth(context)/100*65;
        return (int) (getWindowWidth(context)/2 - cardWidth/2 );
    }

}