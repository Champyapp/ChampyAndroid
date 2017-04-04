package com.azinecllc.champy.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.azinecllc.champy.R;

public class SendFeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputSubject, inputMessage;
    private TextInputLayout inputLayoutSubject, inputLayoutMessage;
    private String[] recipients = {"azinecllc@gmail.com"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputLayoutSubject = (TextInputLayout) findViewById(R.id.input_layout_subject);
        inputLayoutMessage = (TextInputLayout) findViewById(R.id.input_layout_message);
        inputSubject = (EditText) findViewById(R.id.input_subject);
        inputMessage = (EditText) findViewById(R.id.input_message);

        Button buttonSend = (Button) findViewById(R.id.buttonSend);
        inputSubject.addTextChangedListener(new MyTextWatcher(inputSubject));
        inputMessage.addTextChangedListener(new MyTextWatcher(inputMessage));
        buttonSend.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        submitForm();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


    private void submitForm() {
        if (!validateSubject()) {
            return;
        }
        if (!validateMessage()) {
            return;
        }
        sendEmail();
    }


    private void sendEmail() {
        Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE, Uri.parse("mailto:"));
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, recipients);
        email.putExtra(Intent.EXTRA_SUBJECT, inputSubject.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, inputMessage.getText().toString());

        try {
            if (!inputSubject.getText().toString().isEmpty() && !inputMessage.getText().toString().isEmpty()) {
                startActivity(Intent.createChooser(email, "Choose an email client from..."));
            }
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SendFeedbackActivity.this, "No email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validateSubject() {
        if (inputSubject.getText().toString().trim().isEmpty()) {
            inputLayoutSubject.setError("Complete your subject");
            requestFocus(inputSubject);
            return false;
        } else {
            inputLayoutSubject.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validateMessage() {
        String email = inputMessage.getText().toString().trim();

        if (email.isEmpty()) {
            inputLayoutMessage.setError("Complete your message");
            requestFocus(inputMessage);
            return false;
        } else {
            inputLayoutMessage.setErrorEnabled(false);
        }
        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private class MyTextWatcher implements TextWatcher {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_subject:
                    validateSubject();
                    break;
                case R.id.input_message:
                    validateMessage();
                    break;
            }
        }
    }

}
