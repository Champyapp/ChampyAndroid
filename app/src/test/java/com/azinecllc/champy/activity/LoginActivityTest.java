package com.azinecllc.champy.activity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.facebook.login.widget.LoginButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by SashaKhyzhun on 1/12/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class LoginActivityTest {

    private LoginActivity activity;
    private ImageView champyLogo;
    private TextView tvLoginText;
    private LoginButton buttonLogin;


    @Before
    public void setup() throws Exception {
        activity = Robolectric.setupActivity(LoginActivity.class);
        champyLogo  = (ImageView)   activity.findViewById(R.id.imageViewChampy);
        tvLoginText = (TextView)    activity.findViewById(R.id.login_text);
        buttonLogin = (LoginButton) activity.findViewById(R.id.login_button);
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