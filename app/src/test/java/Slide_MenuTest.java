import android.content.Intent;
import android.os.Build;

import com.example.ivan.champy_v2.BuildConfig;
import com.example.ivan.champy_v2.LoginActivity;
import com.example.ivan.champy_v2.MainActivity;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by ivan on 02.02.16.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class Slide_MenuTest {
    private MainActivity activity;


    @Before
    public void setup() {
        activity = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void Logout_test(){

       activity.findViewById(R.id.nav_logout).performClick();

        Intent expectedIntent = new Intent(activity, LoginActivity.class);

        OfflineMode offlineMode = new OfflineMode();

        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        if (offlineMode.isInternetAvailable(activity)) {
            assertTrue("Logout is OK!!!",actualIntent.filterEquals(expectedIntent));
        }
        else assertThat(ShadowToast.getTextOfLatestToast(), equalTo("Lost internet connection!"));
    }



}
