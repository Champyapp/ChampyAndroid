package com.example.ivan.champy_v2.model.User;

public class LoginData {

    private String facebookId;
    private String name;
    private String email;

    public LoginData(String mFacebookId, String mName, String mEmail) {
        this.facebookId = mFacebookId;
        this.name = mName;
        this.email = mEmail;
    }
}
