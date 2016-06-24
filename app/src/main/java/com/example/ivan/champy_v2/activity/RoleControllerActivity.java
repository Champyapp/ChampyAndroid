package com.example.ivan.champy_v2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.facebook.login.LoginManager;

public class RoleControllerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_controller);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        Intent goToActivity;
        if (!sessionManager.isUserLoggedIn()) {
            goToActivity = new Intent(this, LoginActivity.class);
        } else {
            goToActivity = new Intent(this, MainActivity.class);
        }
        startActivity(goToActivity);

    }


}
