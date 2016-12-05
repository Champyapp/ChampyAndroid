package com.example.ivan.champy_v2.controller;

import android.util.Log;

import com.example.ivan.champy_v2.interfaces.CreateChallenge;
import com.example.ivan.champy_v2.utils.SessionManager;

import org.junit.Test;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.example.ivan.champy_v2.utils.Constants.API_URL;
import static com.example.ivan.champy_v2.utils.Constants.typeSelf;
import static org.junit.Assert.*;

/**
 * Created by SashaKhyzhun on 12/5/16.
 */
public class ChallengeControllerTest {

    @Test
    public void createNewSelfImprovementChallenge() throws Exception {
        String description = "No Smoking";
        int days = 21;
        String duration = String.valueOf(21 * 86400);
        String details = description + " during this period: " + days + " days";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmYWNlYm9va0lkIjoiMTA3MTcxNDMzNjIzMzQyOCIsIkFuZHJvaWRPUyI6IntcInRva2VuXCI6XCJjLU90ZFg1aWlzazpBUEE5MWJFN1kwNFAtelU5SnV1YWdXS3JPajZsWUEwcUstWEJmNkJtZU4tQTlrTVJMdTdyNHBMM2w3R2FnbHdDbmNIdXF5N1E1QURWSWNNMlczNzBHY2M3SzctcjhweFlvbk1CWnh3RmhFUGljSWpHaUs5TDMxbVVaVmxiR1BLd0JJVzk3WFJlYjFuQVwiLFwidGltZVpvbmVcIjpcIi0yXCJ9In0.9Xusc5sx32fOErlSuoBdYKfp8qH7vKmvrSPDsIEiuW4";

        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        CreateChallenge createChallenge = retrofit.create(CreateChallenge.class);
        Call<com.example.ivan.champy_v2.model.create_challenge.CreateChallenge> call = createChallenge
                .createChallenge("User_Challenge", typeSelf, description, details, duration, token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.create_challenge.CreateChallenge>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.create_challenge.CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.d("TAG", "onResponse: vse ok");
//                    String challengeId = response.body().getData().get_id();
//                    sendSingleInProgressForSelf(challengeId);
//                            + "\n CHALL_ID    = " + challengeId
//                            + "\n TYPE_ID     = " + type_id
//                            + "\n DESCRIPTION = " + description
//                            + "\n DETAILS     = " + details
//                            + "\n DURATION    = " + duration);
                } else {
                    Log.d("TAG", "onResponse: vse hyunya");
                }
            }

            @Override
            public void onFailure(Throwable t) {}
        });
    }

    @Test
    public void sendSingleInProgressForSelf() throws Exception {

    }

}