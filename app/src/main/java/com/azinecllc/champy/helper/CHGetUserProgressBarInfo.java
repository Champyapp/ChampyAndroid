package com.azinecllc.champy.helper;

import android.content.Context;

import com.azinecllc.champy.interfaces.NewUser;
import com.azinecllc.champy.model.user.Data;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.SessionManager;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;

/**
 * Class-helper with a call to get information on the status bar (in progress, wins and total).
 * We can move this method to "UserController", but actually we do not need that because we use this
 * class only after calling the method 'generateCardsForMainActivity'. Here we need a session
 * manager that is not there in class...
 */
public class CHGetUserProgressBarInfo {

    private Context context;

    public CHGetUserProgressBarInfo(Context context) {
        this.context = context;
    }

    public void getUserProgressBarInfo() {
        SessionManager sessionManager = SessionManager.getInstance(context);
        String token = sessionManager.getToken();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

        NewUser newUser = retrofit.create(NewUser.class);
        Call<User> userCall = newUser.getUserInfo(token);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    User decodedResponse = response.body();
                    Data data = decodedResponse.getData();

                    sessionManager.setChampyOptions(
                            data.getInProgressChallenges().toString(),
                            data.getSuccessChallenges().toString(),
                            data.getAllChallengesCount().toString(),
                            data.getLevel().getNumber().toString());
                }
            }

            @Override
            public void onFailure(Throwable t) { }
        });

    }

}
