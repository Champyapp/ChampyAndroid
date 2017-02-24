package com.azinecllc.champy.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
    private String[] recipient = {"sasha.khyzhun@gmail.com"};
    private EditText etSubject, etMessage;
    private Activity activity;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.setupActivity(ContactUsActivity.class);
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
    public void testForButtonSend() throws Exception {
        Button button = (Button) activity.findViewById(R.id.buttonSend);
        assertNotNull(button);
        assertTrue(button.isClickable());
        assertTrue(R.id.buttonSend == button.getId());

        assertEquals("Send", button.getText());

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) button.getLayoutParams();
        assertEquals(8, lp.leftMargin);
        assertEquals(8, lp.rightMargin);
        assertEquals(24, lp.bottomMargin);

    }

    @Test
    public void sendSucceedFeedback() throws Exception {
        inputLayoutSubject = (TextInputLayout) activity.findViewById(R.id.input_layout_subject);
        inputLayoutMessage = (TextInputLayout) activity.findViewById(R.id.input_layout_message);

        etSubject = (EditText) activity.findViewById(R.id.input_subject);
        etMessage = (EditText) activity.findViewById(R.id.input_message);
        etSubject.setText("My Subject");
        etMessage.setText("My Message");

        if (!validateSubject(etSubject)) {
            assertNotEquals("", etSubject.getText().toString().trim());
            System.out.println("subject is empty");
        } else {
            assertEquals("My Subject", etSubject.getText().toString().trim());
            System.out.println("subject is not empty");
        }
        if (!validateMessage(etMessage)) {
            assertEquals("My Message", etMessage.getText().toString().trim());
            System.out.println("message is empty");
        } else {
            assertNotEquals("", etMessage.getText().toString().trim());
            System.out.println("message is not empty");
        }

        Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE, Uri.parse("mailto:"));
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, recipient);
        email.putExtra(Intent.EXTRA_SUBJECT, etSubject.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, etMessage.getText().toString());

        assertTrue(!etSubject.getText().toString().trim().isEmpty());
        assertTrue(!etMessage.getText().toString().trim().isEmpty());

        assertNotNull(email.getExtras().get(Intent.EXTRA_EMAIL));
        assertNotNull(email.getExtras().get(Intent.EXTRA_SUBJECT));
        assertNotNull(email.getExtras().get(Intent.EXTRA_TEXT));

        assertEquals(recipient, email.getExtras().get(Intent.EXTRA_EMAIL));
        assertEquals("My Subject", email.getExtras().get(Intent.EXTRA_SUBJECT));
        assertEquals("My Message", email.getExtras().get(Intent.EXTRA_TEXT));

        System.out.println("");
        if (etSubject.getText().toString().isEmpty()) {
            System.out.println("subject is empty, can't send a feedback");
            return;
        }
        if (etMessage.getText().toString().isEmpty()) {
            System.out.println("message is empty, can't send a feedback");
            return;
        }
        System.out.println("subject & message is not empty, we CAN send feedback");
    }

    @Test
    public void sendFeedbackWithoutSubject() throws Exception {
        inputLayoutSubject = (TextInputLayout) activity.findViewById(R.id.input_layout_subject);
        inputLayoutMessage = (TextInputLayout) activity.findViewById(R.id.input_layout_message);

        etSubject = (EditText) activity.findViewById(R.id.input_subject);
        etMessage = (EditText) activity.findViewById(R.id.input_message);
        etSubject.setText("");
        etMessage.setText("My Message");

        if (!validateSubject(etSubject)) {
            assertEquals("", etSubject.getText().toString().trim());
            System.out.println("subject is empty (vse ok)");
        } else {
            assertNotEquals("My Subject", etSubject.getText().toString().trim());
            System.out.println("subject is not empty");
        }

        if (!validateMessage(etMessage)) {
            assertEquals("My Message", etMessage.getText().toString().trim());
            System.out.println("message is empty (vse ok)");
        } else {
            assertNotEquals("", etMessage.getText().toString().trim());
            System.out.println("message is not empty");
        }

        Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE, Uri.parse("mailto:"));
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, recipient);
        email.putExtra(Intent.EXTRA_SUBJECT, etSubject.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, etMessage.getText().toString());

        assertTrue(etSubject.getText().toString().trim().isEmpty());
        assertTrue(!etMessage.getText().toString().trim().isEmpty());

        assertNotNull(email.getExtras().get(Intent.EXTRA_EMAIL));
        assertNotNull(email.getExtras().get(Intent.EXTRA_SUBJECT));
        assertNotNull(email.getExtras().get(Intent.EXTRA_TEXT));

        assertEquals(recipient, email.getExtras().get(Intent.EXTRA_EMAIL));
        assertEquals("", email.getExtras().get(Intent.EXTRA_SUBJECT));
        assertEquals("My Message", email.getExtras().get(Intent.EXTRA_TEXT));

        System.out.println("");
        if (etSubject.getText().toString().isEmpty()) {
            System.out.println("subject is empty, can't send a feedback");
            return;
        }
        if (etMessage.getText().toString().isEmpty()) {
            System.out.println("message is empty, can't send a feedback");
            return;
        }
        System.out.println("subject & message is not empty, we CAN send feedback");
    }

    @Test
    public void sendFeedbackWithoutMessage() throws Exception {
        inputLayoutSubject = (TextInputLayout) activity.findViewById(R.id.input_layout_subject);
        inputLayoutMessage = (TextInputLayout) activity.findViewById(R.id.input_layout_message);

        etSubject = (EditText) activity.findViewById(R.id.input_subject);
        etMessage = (EditText) activity.findViewById(R.id.input_message);
        etSubject.setText("My Subject");
        etMessage.setText("");

        if (!validateSubject(etSubject)) {
            assertNotEquals("", etSubject.getText().toString().trim());
            System.out.println("subject is empty");
        } else {
            assertEquals("My Subject", etSubject.getText().toString().trim());
            System.out.println("subject is not empty");
        }
        if (!validateMessage(etMessage)) {
            assertEquals("", etMessage.getText().toString().trim());
            System.out.println("message is empty (vse ok)");
        } else {
            assertNotEquals("My Message", etMessage.getText().toString().trim());
            System.out.println("message is not empty");
        }

        Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE, Uri.parse("mailto:"));
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, recipient);
        email.putExtra(Intent.EXTRA_SUBJECT, etSubject.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, etMessage.getText().toString());

        assertTrue(!etSubject.getText().toString().trim().isEmpty());
        assertTrue(etMessage.getText().toString().trim().isEmpty());

        assertNotNull(email.getExtras().get(Intent.EXTRA_EMAIL));
        assertNotNull(email.getExtras().get(Intent.EXTRA_SUBJECT));
        assertNotNull(email.getExtras().get(Intent.EXTRA_TEXT));

        assertEquals(recipient, email.getExtras().get(Intent.EXTRA_EMAIL));
        assertEquals("My Subject", email.getExtras().get(Intent.EXTRA_SUBJECT));
        assertEquals("", email.getExtras().get(Intent.EXTRA_TEXT));
        System.out.println("");
        if (etSubject.getText().toString().isEmpty()) {
            System.out.println("subject is empty, can't send a feedback");
            return;
        }
        if (etMessage.getText().toString().isEmpty()) {
            System.out.println("message is empty, can't send a feedback");
            return;
        }
        System.out.println("subject & message is not empty, we CAN send feedback");
    }


    private boolean validateSubject(EditText subject) {
        if (subject.getText().toString().trim().isEmpty()) {
            inputLayoutSubject.setError("Complete your subject");
            return false;
        } else {
            inputLayoutSubject.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validateMessage(EditText message) {
        String email = message.getText().toString().trim();
        if (email.isEmpty()) {
            inputLayoutMessage.setError("Complete your message");
            return false;
        } else {
            inputLayoutMessage.setErrorEnabled(false);
        }
        return true;
    }


}