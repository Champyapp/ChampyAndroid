import com.example.ivan.champy_v2.interfaces.ActiveInProgress;
import com.example.ivan.champy_v2.interfaces.Friends;
import com.example.ivan.champy_v2.interfaces.NewUser;
import com.example.ivan.champy_v2.interfaces.SelfImprovement;
import com.example.ivan.champy_v2.model.Friend.Friend;
import com.example.ivan.champy_v2.model.User.User;

import org.junit.Test;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static junit.framework.Assert.assertEquals;

/**
 * Created by ivan on 21.03.16.
 */
public class API_test {

    final String API_URL = "http://46.101.21.24:3007";
    final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmYWNlYm9va0lkIjoidmFzeWEifQ.soq0_3F72wJnehA5E0PZbZ2_M71U5HsPx_U59wf-i7Q";
    final String id = "56d85b4eb1908db90f787e8e";

    @Test
    public void Test_Get_user() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NewUser newUser = retrofit.create(NewUser.class);
        Call<User> call = newUser.getUserInfo(token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) assertEquals("OK", true, true);
                else assertEquals("Wrong", true, false);
            }

            @Override
            public void onFailure(Throwable t) {
                assertEquals("Wrong", true, false);
            }
        });
    }

    @Test
    public void Test_get_Friends() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Friends friends = retrofit.create(Friends.class);
        Call<Friend> call = friends.getUserFriends(id, token);
        call.enqueue(new Callback<Friend>() {
            @Override
            public void onResponse(Response<Friend> response, Retrofit retrofit) {
                if (response.isSuccess()) assertEquals("OK", true, true);
                else assertEquals("Wrong", true, false);
            }

            @Override
            public void onFailure(Throwable t) {
                assertEquals("Wrong", true, false);
            }
        });
    }

    @Test
    public void Test_get_Challanges() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SelfImprovement selfImprovement = retrofit.create(SelfImprovement.class);
        Call<com.example.ivan.champy_v2.model.Self.SelfImprovement> call = selfImprovement.getChallenges(token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.Self.SelfImprovement>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.Self.SelfImprovement> response, Retrofit retrofit) {
                if (response.isSuccess()) assertEquals("OK", true, true);
                else assertEquals("Wrong", true, false);
            }

            @Override
            public void onFailure(Throwable t) {
                assertEquals("Wrong", true, false);
            }
        });
    }

    @Test
    public void Test_get_In_Progress() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        Call<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> call = activeInProgress.getActiveInProgress(id, "1457019726", token);
        call.enqueue(new Callback<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.example.ivan.champy_v2.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
                if (response.isSuccess()) assertEquals("OK", true, true);
                else assertEquals("Wrong", true, false);
            }

            @Override
            public void onFailure(Throwable t) {
                assertEquals("Wrong", true, false);
            }
        });
    }



}
