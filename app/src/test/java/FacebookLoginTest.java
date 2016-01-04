import android.content.Intent;
import android.os.Build;

import com.example.ivan.champy_v2.BuildConfig;
import com.example.ivan.champy_v2.LoginActivity;
import com.example.ivan.champy_v2.MainActivity;
import com.example.ivan.champy_v2.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static junit.framework.Assert.assertTrue;

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
        activity.findViewById(R.id.LoginButton).performClick();

        Intent expectedIntent = new Intent(activity, MainActivity.class);

        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        assertTrue("Activity can't start",actualIntent.filterEquals(expectedIntent));
    }

}
