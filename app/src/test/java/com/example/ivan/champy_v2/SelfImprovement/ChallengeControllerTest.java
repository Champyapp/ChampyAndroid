package com.example.ivan.champy_v2.SelfImprovement;

import com.example.ivan.champy_v2.interfaces.SingleInProgress;
import com.example.ivan.champy_v2.model.create_challenge.CreateChallenge;

import org.junit.Test;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.example.ivan.champy_v2.utils.Constants.API_URL;
import static com.example.ivan.champy_v2.utils.Constants.typeSelf;
import static junit.framework.Assert.assertEquals;

/**
 * Created by SashaKhyzhun on 12/7/16.
 */
public class ChallengeControllerTest {

    private final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();


    @Test
    public void testCreateNewSelfImprovementChallenge() {
        com.example.ivan.champy_v2.interfaces.CreateChallenge createChallenge = retrofit.create(com.example.ivan.champy_v2.interfaces.CreateChallenge.class);
        Call<CreateChallenge> call = createChallenge.createChallenge("n", typeSelf, "desc", "det", "dur", "t");
        call.enqueue(new Callback<CreateChallenge>() {
            @Override
            public void onResponse(Response<CreateChallenge> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    assertEquals("OK", true, true);
                } else {
                    assertEquals("Wrong", true, false);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                assertEquals("Wrong", true, false);
            }
        });
    }

    @Test
    public void testSendSelfImprovementInProgress() {
        SingleInProgress singleInProgress = retrofit.create(SingleInProgress.class);
        Call<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> call = singleInProgress.startSingleInProgress("pID", "t");

        call.enqueue(new Callback<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    assertEquals("OK", true, true);
                } else {
                    assertEquals("Wrong", true, false);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                assertEquals("Wrong", true, false);
            }
        });
    }

}