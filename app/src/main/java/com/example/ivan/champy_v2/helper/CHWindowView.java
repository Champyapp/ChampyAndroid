package com.example.ivan.champy_v2.helper;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Class helper for CustomPagerBase. With this class we can take WindowWidth and CurrentCardPositionX
 */

public class CHWindowView {

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

    // TODO: 06.10.2016 Delete trash
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