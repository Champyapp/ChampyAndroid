package com.example.ivan.champy_v2.model.User;

/**
 * Created by ivan on 01.03.16.
 */
public class LoginData {
    String facebookId;
    String name;
    String email;

    public LoginData(String mfacebookId, String mname, String memail) {
        this.facebookId = mfacebookId;
        this.name = mname;
        this.email = memail;
    }
}
