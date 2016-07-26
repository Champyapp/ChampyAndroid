package com.example.ivan.champy_v2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static java.lang.Math.round;

/**
 * Created by ivan on 11.01.16.
 */
public class InitializeLogin {

    private final  Context context;
    private final com.example.ivan.champy_v2.ImageModule imageModule;
    public Activity activity;

    public InitializeLogin(Activity _activity, Context _context, com.example.ivan.champy_v2.ImageModule imagemodule){
        this.activity = _activity;
        this.context = _context;
        this.imageModule = imagemodule;
    }

    public void Init() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        //int height = size.y;
        int x = round(width / 100);
        //int y = round(height / 100);
        //Log.i("XX", "XX: " + x);


        ImageButton button = (ImageButton)activity.findViewById(R.id.login_button);
        //Bitmap icon  = BitmapFactory.decodeResource(context.getResources(), R.drawable.champy_icon);
        Bitmap login = BitmapFactory.decodeResource(context.getResources(), R.drawable.facebook);

        login = imageModule.getResizedBitmap(login, x*90, x*20);
        button.setImageBitmap(login);

        //TextView loginText = (TextView)activity.findViewById(R.id.login_text);
        //loginText.setTypeface(face);
        //loginText.setTextSize((float) ((float) y/x * 10));


        //Bitmap bitmap = imageModule.getResizedBitmap(icon, x*35, x*35);
        //icon = imageModule.getRoundedCornerBitmap(bitmap, 20);
        //ImageView Champy_icon= (ImageView)this.activity.findViewById(R.id.Champy_image);
        //Champy_icon.setImageBitmap(icon);

        //RelativeLayout relativeLayout = (RelativeLayout)activity.findViewById(R.id.login);
        //relativeLayout.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.champy_background));


    }


}
