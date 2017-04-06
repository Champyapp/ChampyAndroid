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
import android.widget.Switch;
import android.widget.TextView;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.ChallengeActivity;

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
    private ChallengeActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(ChallengeActivity.class).create().get();
        fragment = new SettingsFragment();
        view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.fragment_settings, null);
    }

    @Test
    public void testForActivity() throws Exception {
        assertNotNull(activity);
        assertEquals("ChallengeActivity", activity.getClass().getSimpleName());
        System.out.println("ChallengeActivity is not null");
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
        ImageView imageProfile = (ImageView) view.findViewById(R.id.iv_profile_picture_bg);
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

//    @Test
//    public void testForTextViewGeneral() throws Exception {
//        TextView tvGeneral = (TextView) view.findViewById(R.id.text_view_name);
//        assertNotNull(tvGeneral);
//        assertEquals(Color.WHITE, tvGeneral.getCurrentTextColor());
//        assertEquals("General:", tvGeneral.getText());
//
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvGeneral.getLayoutParams();
//        assertEquals(8, lp.topMargin);
//        assertEquals(8, lp.bottomMargin);
//        assertEquals(8, lp.leftMargin);
//        assertEquals(8, lp.rightMargin);
//    }

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

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvName.getLayoutParams();
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
        assertFalse(tvEnterNewName.isClickable());
    }

    @Test
    public void testForEditText() throws Exception {
        EditText editText = (EditText) view.findViewById(R.id.editTextNewName);
        assertNotNull(editText);

        assertEquals(Color.WHITE, editText.getCurrentTextColor());
        assertEquals(View.GONE, editText.getVisibility());

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) editText.getLayoutParams();
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
        assertTrue(View.GONE == line.getVisibility());
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) line.getLayoutParams();
        assertEquals(1, lp.height);
        assertEquals(0, lp.topMargin);
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
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) avatar.getLayoutParams();
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

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) button.getLayoutParams();
        assertEquals(16, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
    }

    @Test
    public void testForButtonChooseFromGallery() throws Exception {
        Button button = (Button) view.findViewById(R.id.buttonChooseFromGallery);
        assertNotNull(button);

        assertEquals("Choose from Gallery", button.getText());
        assertTrue(R.id.buttonChooseFromGallery == button.getId());
        assertEquals(Color.WHITE, button.getCurrentTextColor());
        assertEquals(View.GONE, button.getVisibility());

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) button.getLayoutParams();
        assertEquals(16, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
    }

    @Test
    public void testForLine1() throws Exception {
        View line = view.findViewById(R.id.view1);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
    }

    @Test
    public void testForTextViewDeleteAccount() throws Exception {
        TextView tvDeleteAcc = (TextView) view.findViewById(R.id.delete_acc);
        assertNotNull(tvDeleteAcc);
        assertEquals(Color.WHITE, tvDeleteAcc.getCurrentTextColor());
        assertEquals("Delete Account", tvDeleteAcc.getText());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvDeleteAcc.getLayoutParams();
        assertEquals(8, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
        assertTrue(tvDeleteAcc.isClickable());
    }

    @Test
    public void testForLine2() throws Exception {
        View line = view.findViewById(R.id.view2);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
    }

    @Test
    public void testForTextViewLogout() throws Exception {
        TextView textView = (TextView) view.findViewById(R.id.textViewLogout);
        assertNotNull(textView);
        assertEquals(Color.WHITE, textView.getCurrentTextColor());
        assertEquals("Logout", textView.getText());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(8, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
        assertTrue(textView.isClickable());
    }

    @Test
    public void testForLine12() throws Exception {
        View line = view.findViewById(R.id.view12);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
    }

    @Test
    public void testForTextViewNotifications() throws Exception {
        TextView textView = (TextView) view.findViewById(R.id.tvNotifications);
        assertNotNull(textView);
        assertEquals(Color.WHITE, textView.getCurrentTextColor());
        assertEquals("Notifications:", textView.getText());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(16, lp.topMargin);
        assertEquals(8, lp.leftMargin);
        assertEquals(8, lp.rightMargin);
        assertFalse(textView.isClickable());
    }

    @Test
    public void testForSwitchNotifications() throws Exception {
        Switch switchPushNoti = (Switch) view.findViewById(R.id.switchPushNotifications);
        assertNotNull(switchPushNoti);
        assertEquals("Push Notifications", switchPushNoti.getText());
        assertTrue(switchPushNoti.isChecked());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) switchPushNoti.getLayoutParams();
        assertEquals(8, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
    }

    @Test
    public void testForLine3() throws Exception {
        View line = view.findViewById(R.id.view3);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
    }

    @Test
    public void testForSwitchNewChallengeRequests() throws Exception {
        Switch switchAcceptedYourChallenge = (Switch) view.findViewById(R.id.switchNewChallengeRequest);
        assertNotNull(switchAcceptedYourChallenge);
        assertEquals("New Challenge Requests", switchAcceptedYourChallenge.getText());
        assertTrue(switchAcceptedYourChallenge.isChecked());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) switchAcceptedYourChallenge.getLayoutParams();
        assertEquals(8, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
    }

    @Test
    public void testForLine4() throws Exception {
        View line = view.findViewById(R.id.view4);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
    }

    @Test
    public void testForSwitchAcceptedYourChallenge() throws Exception {
        Switch switchAcceptedYourChallenge = (Switch) view.findViewById(R.id.switchAcceptedYourChallenge);
        assertNotNull(switchAcceptedYourChallenge);
        assertEquals("Accepted Your Challenge", switchAcceptedYourChallenge.getText());
        assertTrue(switchAcceptedYourChallenge.isChecked());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) switchAcceptedYourChallenge.getLayoutParams();
        assertEquals(8, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
    }

    @Test
    public void testForLine5() throws Exception {
        View line = view.findViewById(R.id.view5);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
    }

    @Test
    public void testForSwitchChallengeEnd() throws Exception {
        Switch switchAcceptedYourChallenge = (Switch) view.findViewById(R.id.switch_reminder);
        assertNotNull(switchAcceptedYourChallenge);
        assertEquals("Challenge End", switchAcceptedYourChallenge.getText());
        assertTrue(switchAcceptedYourChallenge.isChecked());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) switchAcceptedYourChallenge.getLayoutParams();
        assertEquals(8, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
    }

    @Test
    public void testForLine6() throws Exception {
        View line = view.findViewById(R.id.view6);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
    }

    @Test
    public void testForSwitchDailyReminder() throws Exception {
        Switch switchAcceptedYourChallenge = (Switch) view.findViewById(R.id.switchChallengesForToday);
        assertNotNull(switchAcceptedYourChallenge);
        assertEquals("Daily Reminder", switchAcceptedYourChallenge.getText());
        assertTrue(switchAcceptedYourChallenge.isChecked());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) switchAcceptedYourChallenge.getLayoutParams();
        assertEquals(8, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);
    }

    @Test
    public void testForLineViewLine() throws Exception {
        View line = view.findViewById(R.id.viewLine);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
    }

    @Test
    public void testForTextViewLegal() throws Exception {
        TextView textView = (TextView) view.findViewById(R.id.tvLegal);
        assertNotNull(textView);

        assertEquals(Color.WHITE, textView.getCurrentTextColor());
        assertEquals("Legal:", textView.getText());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(16, lp.topMargin);
        assertEquals(8, lp.leftMargin);
        assertEquals(8, lp.rightMargin);

        assertFalse(textView.isClickable());
    }

    @Test
    public void testForTextViewAbout() throws Exception {
        TextView textView = (TextView) view.findViewById(R.id.about);
        assertNotNull(textView);

        assertEquals(Color.WHITE, textView.getCurrentTextColor());
        assertEquals("About", textView.getText());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(8, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);

        assertTrue(textView.isClickable());
    }

    @Test
    public void testForLine8() throws Exception {
        View line = view.findViewById(R.id.view8);
        assertNotNull(line);
        assertTrue(View.VISIBLE == line.getVisibility());
    }

    @Test
    public void testForTextViewContactUs() throws Exception {
        TextView textView = (TextView) view.findViewById(R.id.contact_us);
        assertNotNull(textView);

        assertEquals(Color.WHITE, textView.getCurrentTextColor());
        assertEquals("Contact Us", textView.getText());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        assertEquals(8, lp.topMargin);
        assertEquals(16, lp.leftMargin);
        assertEquals(16, lp.rightMargin);

        assertTrue(textView.isClickable());
    }

//    @Test
//    public void testForLine9() throws Exception {
//        View line = view.findViewById(R.id.view9);
//        assertNotNull(line);
//        assertTrue(View.VISIBLE == line.getVisibility());
//    }


}