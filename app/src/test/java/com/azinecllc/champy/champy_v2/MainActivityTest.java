package com.azinecllc.champy.champy_v2;

import android.os.Build;
import android.widget.TextView;

import com.azinecllc.champy.activity.MainActivity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class MainActivityTest {

    private MainActivity activity;

    @Before
    public void setup() {
        // Convenience method to run MainActivity through the Activity Lifecycle methods:
        // onCreate(...) => onStart() => onPostCreate(...) => onResume()
        activity = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void validateTextViewContent() {
        TextView textViewChallenges = (TextView)activity.findViewById(R.id.textViewChallenges);
        assertNotNull("TextView could not be found", textViewChallenges);
        assertTrue("Job is done", "Challenges".equals(textViewChallenges.getText().toString()));
    }


    @Test
    public void qwerty() {
        assertTrue(true);
    }

}