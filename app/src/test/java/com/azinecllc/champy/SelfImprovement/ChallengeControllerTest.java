package com.azinecllc.champy.SelfImprovement;

import com.azinecllc.champy.interfaces.SingleInProgress;
import com.azinecllc.champy.model.create_challenge.CreateChallenge;

import org.junit.Test;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;
import static com.azinecllc.champy.utils.Constants.typeSelf;
import static junit.framework.Assert.assertEquals;

/**
 * Created by SashaKhyzhun on 12/7/16.
 */
public class ChallengeControllerTest {

    private final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();


    @Test
    public void testCreateNewSelfImprovementChallenge() {
        com.azinecllc.champy.interfaces.CreateChallenge createChallenge = retrofit.create(com.azinecllc.champy.interfaces.CreateChallenge.class);
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
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = singleInProgress.startSingleInProgress("pID", "t");

        call.enqueue(new Callback<com.azinecllc.champy.model.single_in_progress.SingleInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.single_in_progress.SingleInProgress> response, Retrofit retrofit) {
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