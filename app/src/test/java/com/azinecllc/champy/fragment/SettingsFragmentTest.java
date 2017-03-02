package com.azinecllc.champy.fragment;

import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 3/2/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class SettingsFragmentTest {

    private View view;
    private SettingsFragment fragment;
    private MainActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
        fragment = new SettingsFragment();
        view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.fragment_privacy, null);
    }

    @Test
    public void testForActivity() throws Exception {
        assertNotNull(activity);
        assertEquals("MainActivity", activity.getClass().getSimpleName());
        System.out.println("MainActivity is not null");
    }

    @Test
    public void testForFragment() throws Exception {
        assertNotNull(fragment);
        assertEquals("SettingsFragment", fragment.getClass().getSimpleName());
        System.out.println("SettingsFragment is not null");
    }

    @Test
    public void testForScrollView() throws Exception {
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        assertNotNull(scrollView);
        System.out.println("ScrollView is not null");
    }

    @Test
    public void testForSettingsLayout() throws Exception {
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.settings_layout);
        assertNotNull(relativeLayout);
        System.out.println("RelativeLayout is not null");

    }

    @Test
    public void testForImageProfile() throws Exception {
        ImageView imageProfile = (ImageView) view.findViewById(R.id.img_profile);
        assertNotNull(imageProfile);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageProfile.getLayoutParams();
        assertEquals(100, lp.height);
        assertEquals(100, lp.width);
        assertFalse(imageProfile.isClickable());
    }

    @Test
    public void testForTextViewUserName() throws Exception {
        TextView tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
        assertNotNull(tvUserName);
        assertEquals(Color.WHITE, tvUserName.getCurrentTextColor());
    }

    @Test
    public void testForTextViewGeneral() throws Exception {
        TextView tvGeneral = (TextView) view.findViewById(R.id.tvGeneral);
        assertNotNull(tvGeneral);
        assertEquals(Color.WHITE, tvGeneral.getCurrentTextColor());
        assertEquals("General:", tvGeneral.getText());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvGeneral.getLayoutParams();
        assertEquals(8, lp.topMargin);
        assertEquals(8, lp.bottomMargin);
        assertEquals(8, lp.leftMargin);
        assertEquals(8, lp.rightMargin);
    }

    @Test
    public void testForLayoutChangeName() throws Exception {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layoutChangeName);
        assertNotNull(linearLayout);
        assertTrue(View.VISIBLE == linearLayout.getVisibility());
        assertEquals(LinearLayout.VERTICAL, linearLayout.getOrientation());
    }

    @Test
    public void testForLine11() throws Exception {
        View line = view.findViewById(R.id.view11);
        assertNotNull(line);
        assertTrue(View.GONE == line.getVisibility());
    }

    @Test
    public void testForTextViewName() throws Exception {
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        assertNotNull(tvName);
        assertEquals(Color.WHITE, tvName.getCurrentTextColor());
        assertEquals("Name", tvName.getText());
        assertEquals(View.VISIBLE, tvName.getVisibility());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvName.getLayoutParams();
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
        assertTrue(tvName.isClickable());
    }

    @Test
    public void testForLayoutEditText() throws Exception {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layoutEditText);
        assertNotNull(linearLayout);
        assertTrue(View.GONE == linearLayout.getVisibility());
        assertEquals(LinearLayout.VERTICAL, linearLayout.getOrientation());
    }

    @Test
    public void testForTextViewEnterNewName() throws Exception {
        TextView tvEnterNewName = (TextView) view.findViewById(R.id.tvEnterNewName);
        assertNotNull(tvEnterNewName);
        assertEquals(Color.WHITE, tvEnterNewName.getCurrentTextColor());
        assertEquals("Enter new name", tvEnterNewName.getText());
        assertEquals(View.GONE, tvEnterNewName.getVisibility());
        assertTrue(tvEnterNewName.isClickable());
    }

    @Test
    public void testForEditText() throws Exception {
        EditText editText = (EditText) view.findViewById(R.id.editTextNewName);
        assertNotNull(editText);

        assertEquals(Color.WHITE, editText.getCurrentTextColor());
        assertEquals(View.GONE, editText.getVisibility());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) editText.getLayoutParams();
        assertEquals(16, lp.rightMargin);
        assertEquals(16, lp.leftMargin);
    }

    @Test
    public void testForButtonOK() throws Exception {
        Button button = (Button) view.findViewById(R.id.buttonOk);
        assertNotNull(button);

        assertEquals("OK", button.getText());
        assertTrue(R.id.buttonOk == button.getId());
        assertEquals(Color.WHITE, button.getCurrentTextColor());
        assertEquals(View.GONE, button.getVisibility());
        assertTrue(Gravity.CENTER == button.getGravity());
    }

    @Test
    public void testForLine() throws Exception {
        View line = view.findViewById(R.id.view11);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) line.getLayoutParams();
        assertEquals(1, lp.height);
        assertEquals(8, lp.topMargin);
    }

    @Test
    public void testForLayoutProfilePicture() throws Exception {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layoutProfilePicture);
        assertNotNull(linearLayout);
        assertTrue(View.VISIBLE == linearLayout.getVisibility());
        assertEquals(LinearLayout.VERTICAL, linearLayout.getOrientation());
    }

    @Test
    public void testForTextViewProfilePictures() throws Exception {
        TextView avatar = (TextView) view.findViewById(R.id.avatar);
        assertNotNull(avatar);
        assertEquals(Color.WHITE, avatar.getCurrentTextColor());
        assertEquals("Profile Picture", avatar.getText());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) avatar.getLayoutParams();
        assertEquals(8, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
    }

    @Test
    public void testForButtonTakeAPicture() throws Exception {
        Button button = (Button) view.findViewById(R.id.buttonTakeAPicture);
        assertNotNull(button);

        assertEquals("Take a picture", button.getText());
        assertTrue(R.id.buttonTakeAPicture == button.getId());
        assertEquals(Color.WHITE, button.getCurrentTextColor());
        assertEquals(View.GONE, button.getVisibility());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) button.getLayoutParams();
        assertEquals(16, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
    }

    @Test
    public void testForButtonChooseFromGallery() throws Exception {
        Button button = (Button) view.findViewById(R.id.buttonChooseFromGallery);
        assertNotNull(button);

        assertEquals("Take a picture", button.getText());
        assertTrue(R.id.buttonChooseFromGallery == button.getId());
        assertEquals(Color.WHITE, button.getCurrentTextColor());
        assertEquals(View.GONE, button.getVisibility());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) button.getLayoutParams();
        assertEquals(16, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
    }

    @Test
    public void testForLine1() throws Exception {
        View line = view.findViewById(R.id.view1);
        assertNotNull(line);
        assertTrue(View.GONE == line.getVisibility());
    }





//    @Test public void test() throws Exception {}


}