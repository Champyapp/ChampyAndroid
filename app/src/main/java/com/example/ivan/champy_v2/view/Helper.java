package com.example.ivan.champy_v2.view;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Created by ivan on 02.03.16.
 */
public class Helper {

    public String get_jwt_token(String fb_id)
    {
        String jwtString = Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setPayload("{\n" +
                "  \"facebookId\": \""+fb_id+"\"\n" +
                "}").signWith(SignatureAlgorithm.HS256, "secret").compact();
        return jwtString;
    }
}
