package com.azinecllc.champy.helper;

import android.content.Context;
import android.util.Log;

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

public class CHLoadUserProgressBarInfo {

    private Context context;

    public CHLoadUserProgressBarInfo(Context context) {
        this.context = context;
    }

    public void loadUserProgressBarInfo() {
        SessionManager sessionManager = SessionManager.getInstance(context);
        final String token = sessionManager.getToken();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

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
                } else Log.d("LoadUserProgressBar", "onResponse: failed! " + response.message());
            }

            @Override
            public void onFailure(Throwable t) { }
        });

    }

}
