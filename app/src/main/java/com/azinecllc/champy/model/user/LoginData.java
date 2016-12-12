package com.azinecllc.champy.model.user;

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
