import com.azinecllc.champy.interfaces.ActiveInProgress;
import com.azinecllc.champy.interfaces.Friends;
import com.azinecllc.champy.interfaces.NewUser;
import com.azinecllc.champy.interfaces.SelfImprovement;
import com.azinecllc.champy.interfaces.SingleInProgress;
import com.azinecllc.champy.model.create_challenge.CreateChallenge;
import com.azinecllc.champy.model.duel.Duel;
import com.azinecllc.champy.model.friend.Friend;
import com.azinecllc.champy.model.user.User;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static junit.framework.Assert.assertEquals;


public class API_test {

    private static final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmYWNlYm9va0lkIjoidmFzeWEifQ.soq0_3F72wJnehA5E0PZbZ2_M71U5HsPx_U59wf-i7Q";
    private static final String typeSelf = "567d51c48322f85870fd931a";
    private static final String typeDuel = "567d51c48322f85870fd931b";
    private static final String typeWake = "567d51c48322f85870fd931c";
    private static final String API_URL = "http://46.101.21.24:3007";
    private static final String updated = "0";

    private final String inProgressID = "challenge_in_progress_id";
    private final String description = "challenge_description";
    private final String recipient = "challenge_recipient";
    private final String duration = "challenge_duration";
    private final String details = "challenge_details";
    private final String name = "challenge_name";
    private final String id = "challenge_id";

    private final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
    private final NetworkBehavior behavior = NetworkBehavior.create();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    @Test
    public void retrofitNullThrows() {
        try {
            new MockRetrofit.Builder(null);
            assertEquals("OK", true, true);
        } catch (NullPointerException e) {
            assertEquals("Wrong", true, false);
        }
    }

//    @Test
//    public void retrofitPropagated() {
//        MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit).build();
//        assertThat(mockRetrofit.retrofit()).isSameAs(retrofit);
//    }


    @Test
    public void testForStupidChallengeName() {
        if (name.equals("challenge_name")) {
            assertEquals("OK", true, true);
        } else {
            assertEquals("Wrong", true, false);
        }
    }

    @Test
    public void testGetUser() {
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
    public void testUserGetFriends() {
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
    public void testGetChallenges() {
        SelfImprovement selfImprovement = retrofit.create(SelfImprovement.class);
        Call<com.azinecllc.champy.model.self.SelfImprovement> call = selfImprovement.getChallenges(token);
        call.enqueue(new Callback<com.azinecllc.champy.model.self.SelfImprovement>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.self.SelfImprovement> response, Retrofit retrofit) {
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
    public void testGetInProgress() {
        ActiveInProgress activeInProgress = retrofit.create(ActiveInProgress.class);
        Call<com.azinecllc.champy.model.active_in_progress.ActiveInProgress> call =
                activeInProgress.getActiveInProgress(id, "0", token);

        call.enqueue(new Callback<com.azinecllc.champy.model.active_in_progress.ActiveInProgress>() {
            @Override
            public void onResponse(Response<com.azinecllc.champy.model.active_in_progress.ActiveInProgress> response, Retrofit retrofit) {
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
    public void testCreateNewSelfImprovementChallenge() {
        com.azinecllc.champy.interfaces.CreateChallenge createChallenge = retrofit.create(com.azinecllc.champy.interfaces.CreateChallenge.class);
        Call<CreateChallenge> call = createChallenge.createChallenge(name, typeSelf, description, details, duration, token);
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
    public void testCreateNewDuelChallenge() {
        com.azinecllc.champy.interfaces.CreateChallenge createChallenge = retrofit.create(com.azinecllc.champy.interfaces.CreateChallenge.class);
        Call<CreateChallenge> call = createChallenge.createChallenge(name, typeDuel, description, details, duration, token);
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
    public void testCreateNewWakeUpChallenge() {
        com.azinecllc.champy.interfaces.CreateChallenge createChallenge =
                retrofit.create(com.azinecllc.champy.interfaces.CreateChallenge.class);

        Call<com.azinecllc.champy.model.create_challenge.CreateChallenge> call =
                createChallenge.createChallenge(name, typeWake, description, details, duration, token);

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
    public void testSendSingleInProgressForDuel() {
        SingleInProgress singleInProgress = retrofit.create(SingleInProgress.class);
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = singleInProgress.startSingleInProgress(inProgressID, token);

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

    @Test
    public void testSendDuelChallengeInProgress() {
        SingleInProgress singleInProgress = retrofit.create(SingleInProgress.class);
        Call<Duel> call = singleInProgress.startDuel(recipient, inProgressID, token);
        call.enqueue(new Callback<Duel>() {
            @Override
            public void onResponse(Response<Duel> response, Retrofit retrofit) {
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
    public void testSendSingleInProgressForWakeUp() {
        SingleInProgress singleinprogress = retrofit.create(SingleInProgress.class);
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call
                = singleinprogress.startSingleInProgress(inProgressID, token);

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



    @Test
    public void testJoinToChallenge() {
        SingleInProgress singleInProgress = retrofit.create(SingleInProgress.class);
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = singleInProgress.join(inProgressID, token);
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

    @Test
    public void testRejectInviteForPendingDuel() throws IOException {
        SingleInProgress activeInProgress = retrofit.create(SingleInProgress.class);
        Call<com.azinecllc.champy.model.single_in_progress.SingleInProgress> call = activeInProgress.reject(inProgressID, token);
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

    @Test
    public void testDoneForToday() throws IOException {

    }


}
