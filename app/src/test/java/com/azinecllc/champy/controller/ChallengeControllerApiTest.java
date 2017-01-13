package com.azinecllc.champy.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import retrofit.GsonConverterFactory;
import retrofit2.Retrofit;

import static org.junit.Assert.assertEquals;

/**
 * Created by SashaKhyzhun on 1/13/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class ChallengeControllerApiTest {

    private static final String API_URL = "http://46.101.21.24:3006"; // test

    @Mock
    Retrofit mockRetrofit;

    @Before
    public void setup() throws Exception {
        mockRetrofit = Mockito.mock(Retrofit.class);
    }


//    @Test
//    public void Test_Get_user() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(API_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        mockRetrofit.baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
//        NewUser newUser = retrofit.create(NewUser.class);
//        Call<User> call = newUser.getUserInfo(token);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Response<User> response, Retrofit retrofit) {
//                if (response.isSuccess()) assertEquals("OK", true, true);
//                else assertEquals("Wrong", true, false);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                assertEquals("Wrong", true, false);
//            }
//        });
//    }

//    @Test
//    public void Test_get_Friends() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(API_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        Friends friends = retrofit.create(Friends.class);
//        Call<Friend> call = friends.getUserFriends(id, token);
//        call.enqueue(new Callback<Friend>() {
//            @Override
//            public void onResponse(Response<Friend> response, Retrofit retrofit) {
//                if (response.isSuccess()) assertEquals("OK", true, true);
//                else assertEquals("Wrong", true, false);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                assertEquals("Wrong", true, false);
//            }
//        });
//    }
//
//    @Test
//    public void Test_get_Challenges() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(API_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        SelfImprovement selfImprovement = retrofit.create(SelfImprovement.class);
//        Call<com.example.ivan.champy_v2.model.self.SelfImprovement> call = selfImprovement.getChallenges(token);
//        call.enqueue(new Callback<com.example.ivan.champy_v2.model.self.SelfImprovement>() {
//            @Override
//            public void onResponse(Response<com.example.ivan.champy_v2.model.self.SelfImprovement> response, Retrofit retrofit) {
//                if (response.isSuccess()) assertEquals("OK", true, true);
//                else assertEquals("Wrong", true, false);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                assertEquals("Wrong", true, false);
//            }
//        });
//    }
//
//    @Test
//    public void Test_get_In_Progress() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(API_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
//        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call = activeInProgress.getActiveInProgress(id, "1457019726", token);
//        call.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
//            @Override
//            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
//                if (response.isSuccess()) assertEquals("OK", true, true);
//                else assertEquals("Wrong", true, false);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                assertEquals("Wrong", true, false);
//            }
//        });
//    }

}
