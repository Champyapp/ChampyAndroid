package com.example.ivan.champy_v2.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ivan.champy_v2.R;

import static java.lang.Math.round;

/**
 * this class help us to create view in login activity. We create right size of text on the screen,
 * right size of button "login" because we 'love' facebook's buttons.
 */
public class CHInitializeLogin {

    private final  Context context;
    private final CHImageModule CHImageModule;
    public Activity activity;

    public CHInitializeLogin(Activity _activity, Context _context, CHImageModule imagemodule){
        this.activity = _activity;
        this.context = _context;
        this.CHImageModule = imagemodule;
    }

    public void Init() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int x = round(width / 100);
        int y = round(height / 100);
        //Log.i("XX", "XX: " + x);

//        ImageButton button = (ImageButton)activity.findViewById(R.id.login_button);
//        Bitmap icon  = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_champy);
//        Bitmap login = BitmapFactory.decodeResource(context.getResources(), R.drawable.facebook);
//
//        login = CHImageModule.getResizedBitmap(login, x*90, x*20);
//        button.setImageBitmap(login);


//        loginText.setTextSize((float) ((float) y/x * 15));
//
//        Bitmap bitmap = CHImageModule.getResizedBitmap(icon, x*35, x*35);
//        icon = CHImageModule.getRoundedCornerBitmap(bitmap, 20);
//        ImageView Champy_icon= (ImageView)this.activity.findViewById(R.id.Champy_image);
//        Champy_icon.setImageBitmap(icon);
//
//        RelativeLayout relativeLayout = (RelativeLayout)activity.findViewById(R.id.login);
//        relativeLayout.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.champy_background));


    }


}
