package com.azinecllc.champy.activity;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.facebook.FacebookSdk;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.pm.PackageManager.GET_SIGNATURES;
import static com.facebook.FacebookSdk.getApplicationContext;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.robolectric.RuntimeEnvironment.getPackageManager;

/**
 * Created by SashaKhyzhun on 1/12/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class LoginActivityTest {

    private LoginActivity activity;
    private ImageView champyLogo;
    private TextView tvLoginText;
    private ImageButton buttonLogin;


    @Before
    public void setup() throws Exception {
        activity = Robolectric.buildActivity(LoginActivity.class).create().get();
        champyLogo  = (ImageView)   activity.findViewById(R.id.imageViewChampy);
        tvLoginText = (TextView)    activity.findViewById(R.id.login_text);
        buttonLogin = (ImageButton) activity.findViewById(R.id.login_button);
    }

    @Test
    public void testActivityForNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void testChampyLogo() throws Exception {
        assertNotNull(champyLogo);

        assertEquals(100, champyLogo.getLayoutParams().width);
        assertEquals(100, champyLogo.getLayoutParams().height);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)champyLogo.getLayoutParams();
        assertEquals(60, lp.bottomMargin);

    }

    @Test
    public void testLoginText() throws Exception {
        String t = "\'\'it\'s your thinking that \\n decides whether you\'re \\n going to succeed or fail\'\' - \\n henry ford";
        assertNotNull(tvLoginText);
        assertEquals(t, t, tvLoginText.getText());
        assertEquals(24, 24, tvLoginText.getTextSize());
        assertEquals(Gravity.CENTER, tvLoginText.getGravity());
    }

    @Test
    public void testButtonLogin() throws Exception {
        assertNotNull(buttonLogin);

        assertEquals(R.id.login_button, R.id.login_button, buttonLogin.getId());

        assertEquals(0.8, 0.8, buttonLogin.getScaleX());
        assertEquals(0.5, 0.5, buttonLogin.getScaleY());

        Drawable background = buttonLogin.getBackground();
        assertNotNull(background);
        //assertEquals(R.drawable.button_facebook, background);
    }

    @Test
    public void testGetHashKey() throws Exception {
        try {
            @SuppressLint("PackageManagerGetSignatures") // check package name..
                    PackageInfo info = getPackageManager().getPackageInfo("com.azinecllc.champy", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void onCreate() throws Exception {
//
//    }
//
//    @Test
//    public void onClick() throws Exception {
//
//    }
//
//    @Test
//    public void onResume() throws Exception {
//
//    }
//
//    @Test
//    public void onStop() throws Exception {
//
//    }
//
//    @Test
//    public void onBackPressed() throws Exception {
//
//    }

}