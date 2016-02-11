import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.example.ivan.champy_v2.BuildConfig;
import com.example.ivan.champy_v2.LoginActivity;
import com.example.ivan.champy_v2.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

/**
 * Created by ivan on 04.01.16.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class FacebookLoginTest {
    private LoginActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(LoginActivity.class);
    }

    @Test
    public void testLoginButton()
    {

        Intent expectedIntent = new Intent(activity, MainActivity.class);

        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
      //  if (netInfo == null && netInfo.isConnectedOrConnecting()) assertTrue("Activity can't start",actualIntent.filterEquals(expectedIntent));
       //         else assertFalse("No Internet Connection",actualIntent.filterEquals(expectedIntent));
    }

}
