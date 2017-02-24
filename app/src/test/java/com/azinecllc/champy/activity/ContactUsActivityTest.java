package com.azinecllc.champy.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 2/24/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class ContactUsActivityTest {

    private TextInputLayout inputLayoutSubject, inputLayoutMessage;
    private EditText etSubject, etMessage;
    private Activity activity;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.setupActivity(ContactUsActivity.class);

        inputLayoutSubject = (TextInputLayout) activity.findViewById(R.id.input_layout_subject);
        inputLayoutMessage = (TextInputLayout) activity.findViewById(R.id.input_layout_message);

        etSubject = (EditText) activity.findViewById(R.id.input_subject);
        etMessage = (EditText) activity.findViewById(R.id.input_message);

    }

    @Test
    public void onCreate() throws Exception {
        assertNotNull(activity);
        assertEquals("ContactUsActivity", activity.getClass().getSimpleName());
    }

    @Test
    public void testForAppBarLayout() throws Exception {
        AppBarLayout appBarLayout = (AppBarLayout) activity.findViewById(R.id.appbar_contact_us);
        assertNotNull(appBarLayout);
        assertTrue(View.VISIBLE == appBarLayout.getVisibility());
        assertEquals(R.id.appbar_contact_us, appBarLayout.getId());
    }

    @Test
    public void testForToolbar() throws Exception {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        assertNotNull(toolbar);
        assertEquals("Contact Us", toolbar.getTitle());
        System.out.println("Expected: Contact Us | actual: " + toolbar.getTitle());
        assertTrue(R.id.toolbar == toolbar.getId());
        System.out.println("Expected: R.id.toolbar | actual: " + toolbar.getId());
    }

    @Test
    public void testForTextInputLayoutSubject() throws Exception {
        TextInputLayout inputLayout = (TextInputLayout) activity.findViewById(R.id.input_layout_subject);
        assertNotNull(inputLayout);
        assertTrue(R.id.input_layout_subject == inputLayout.getId());
        assertTrue(View.VISIBLE == inputLayout.getVisibility());
        assertEquals("Subject", inputLayout.getHint());
    }

    @Test
    public void testForTextInputLayoutMessage() throws Exception {
        TextInputLayout inputLayout = (TextInputLayout) activity.findViewById(R.id.input_layout_message);
        assertNotNull(inputLayout);
        assertTrue(R.id.input_layout_message == inputLayout.getId());
        assertTrue(View.VISIBLE == inputLayout.getVisibility());
        assertEquals("Your message", inputLayout.getHint());
    }

    @Test
    public void onClick() throws Exception {

    }

    @Test
    public void sendSucceedFeedback() throws Exception {
        etSubject.setText("My Subject");
        etMessage.setText("My Message");

        if (etSubject.getText().toString().trim().isEmpty()) {
            inputLayoutSubject.setError("Complete your subject");
            assertEquals("Should be not empty", "My Subject", etSubject.getText().toString().trim());
        } else {
            inputLayoutSubject.setErrorEnabled(false);
            assertEquals("Should be not empty", "My Subject", etSubject.getText().toString().trim());
        }


        if (etMessage.getText().toString().trim().isEmpty()) {
            inputLayoutMessage.setError("Complete your message");
            assertEquals("Should be not empty", "My Message", etMessage.getText().toString().trim());
        } else {
            inputLayoutMessage.setErrorEnabled(false);
            assertEquals("Should be not empty", "My Message", etMessage.getText().toString().trim());
        }

        System.out.println("sendSucceedFeedback: errorSbj = " + inputLayoutSubject.getError());
        System.out.println("sendSucceedFeedback: errorMsg = " + inputLayoutMessage.getError());


        Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE, Uri.parse("mailto:"));
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"sasha.khyzhun@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, etSubject.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, etMessage.getText().toString());

        try {
            if (!etSubject.getText().toString().isEmpty() && !etMessage.getText().toString().isEmpty()) {
                // vse ok
            }
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "No email client installed.", Toast.LENGTH_SHORT).show();
        }

        System.out.println("bI");
    }


//    private void sendEmail(EditText et_sub, EditText et_msg) {
//        Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE, Uri.parse("mailto:"));
//        email.setType("message/rfc822");
//        email.putExtra(Intent.EXTRA_EMAIL, new String[] {"sasha.khyzhun@gmail.com"});
//        email.putExtra(Intent.EXTRA_SUBJECT, et_sub.getText().toString());
//        email.putExtra(Intent.EXTRA_TEXT, et_msg.getText().toString());
//
//        try {
//            if (!et_sub.getText().toString().isEmpty() && !et_msg.getText().toString().isEmpty()) {
//                // vse ok
//            }
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(activity, "No email client installed.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private boolean validateSubject(EditText text) {
//        if (text.getText().toString().trim().isEmpty()) {
//            inputLayoutSubject.setError("Complete your subject");
//            return false;
//        } else {
//            inputLayoutSubject.setErrorEnabled(false);
//        }
//        return true;
//    }
//
//
//    private boolean validateMessage(EditText text) {
//        String email = text.getText().toString().trim();
//        if (email.isEmpty()) {
//            inputLayoutMessage.setError("Complete your message");
//            return false;
//        } else {
//            inputLayoutMessage.setErrorEnabled(false);
//        }
//        return true;
//    }


    @Test
    public void onBackPressed() throws Exception {

    }

    @Test
    public void onDestroy() throws Exception {

    }

}