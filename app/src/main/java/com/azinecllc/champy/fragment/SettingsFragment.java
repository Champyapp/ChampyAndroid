package com.azinecllc.champy.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.AboutActivity;
import com.azinecllc.champy.activity.ContactUsActivity;
import com.azinecllc.champy.activity.PhotoActivity;
import com.azinecllc.champy.activity.RoleControllerActivity;
import com.azinecllc.champy.controller.ChallengeController;
import com.azinecllc.champy.controller.DailyRemindController;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.interfaces.Update_user;
import com.azinecllc.champy.model.user.Delete;
import com.azinecllc.champy.model.user.Profile_data;
import com.azinecllc.champy.model.user.User;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;

import java.io.File;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.azinecllc.champy.utils.Constants.API_URL;
import static com.azinecllc.champy.utils.Constants.path;

/**
 * Created by SashaKhyzhun on 2/7/17.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private DBHelper dbHelper;
    private Typeface typeface;
    private OfflineMode offline;
    private SessionManager session;
    private String name, userID, token;
    private DailyRemindController reminder;
    private TextView tvChangeName, tvUserName;

    private HashMap<String, String> map = new HashMap<>();
    private HashMap<String, String> user = new HashMap<>();
    private Retrofit retrofit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        reminder = new DailyRemindController(getContext()); // here should be getContext()
        session = SessionManager.getInstance(context);
        dbHelper = DBHelper.getInstance(context);
        offline = OfflineMode.getInstance();
        userID = session.getUserId();
        token = session.getToken();
        user = session.getUserDetails();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewSettings = inflater.inflate(R.layout.content_settings, container, false);

        ImageView userImageProfile = (ImageView) viewSettings.findViewById(R.id.img_profile);

        File fileProfile = new File(path, "profile.jpg");
        Uri uriProfile = Uri.fromFile(fileProfile);

        Glide.with(this)
                .load(uriProfile)
                .bitmapTransform(new CropCircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(130, 130)
                .into(userImageProfile);

        initSwitches(viewSettings);

        TextView terms = (TextView) viewSettings.findViewById(R.id.terms);
        TextView about = (TextView) viewSettings.findViewById(R.id.about);
        TextView avatar = (TextView) viewSettings.findViewById(R.id.avatar);
        TextView privacy = (TextView) viewSettings.findViewById(R.id.privacy);
        TextView tvLegal = (TextView) viewSettings.findViewById(R.id.tvLegal);
        TextView delete = (TextView) viewSettings.findViewById(R.id.delete_acc);
        TextView tvGeneral = (TextView) viewSettings.findViewById(R.id.tvGeneral);
        TextView contactUs = (TextView) viewSettings.findViewById(R.id.contact_us);
        TextView tvNotif = (TextView) viewSettings.findViewById(R.id.tvNotifications);
        tvUserName = (TextView) viewSettings.findViewById(R.id.tvUserName);
        tvChangeName = (TextView) viewSettings.findViewById(R.id.tvName);

        tvUserName.setText(name);
        tvUserName.setTypeface(typeface);
        tvLegal.setTypeface(typeface);
        tvNotif.setTypeface(typeface);
        tvGeneral.setTypeface(typeface);

        about.setOnClickListener(this);
        terms.setOnClickListener(this);
        delete.setOnClickListener(this);
        avatar.setOnClickListener(this);
        privacy.setOnClickListener(this);
        contactUs.setOnClickListener(this);
        tvChangeName.setOnClickListener(this);


        return viewSettings;
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tvName:
                tvChangeName.setVisibility(View.INVISIBLE);
                TextView tvEnterYourName = (TextView) v.findViewById(R.id.tvEntedYourName);
                tvEnterYourName.setVisibility(View.VISIBLE);

                EditText etNewName = (EditText) v.findViewById(R.id.new_name);
                etNewName.setVisibility(View.VISIBLE);
                etNewName.setText(name);

                Button imageButtonAcceptName = (Button) v.findViewById(R.id.imageButtonAcceptMaybe);
                imageButtonAcceptName.setVisibility(View.VISIBLE);

                View lineOfTheNed = v.findViewById(R.id.view11);
                lineOfTheNed.setVisibility(View.VISIBLE);

                imageButtonAcceptName.setOnClickListener(v1 -> {
                    String checkName = etNewName.getText().toString();
                    if (offline.isConnectedToRemoteAPI(getActivity()) && !checkName.isEmpty()) {
                        String newName = etNewName.getText().toString().trim();
                        session.change_name(newName);
                        setNewName(newName);

                        tvUserName.setText(etNewName.getText().toString());
                        imageButtonAcceptName.setVisibility(View.GONE);
                        tvChangeName.setVisibility(View.VISIBLE);
                        tvEnterYourName.setVisibility(View.GONE);
                        lineOfTheNed.setVisibility(View.GONE);
                        etNewName.setVisibility(View.GONE);
                    }
                });
                break;
            case R.id.avatar:
                //updateProfile(map);
                startActivity(new Intent(context, PhotoActivity.class));
                break;
            case R.id.delete_acc:
                if (!session.getChampyOptions().get("challenges").equals("0")) {
                    surrenderAllChallengesDialog();
                } else {
                    deleteAccountDialog();
                }
                break;
            case R.id.about:
                //updateProfile(map);
                startActivity(new Intent(context, AboutActivity.class));
                break;
            case R.id.privacy:
//                updateProfile(map);
//                intent = new Intent(context, PrivacyActivity.class);
//                startActivity(intent);
                break;
            case R.id.terms:
//                updateProfile(map);
//                intent = new Intent(context, TermsActivity.class);
//                startActivity(intent);
                break;
            case R.id.contact_us:
                startActivity(new Intent(context, ContactUsActivity.class));
                break;

        }
        updateProfile(map);
    }


    private void initSwitches(View view) {
        String pushNotify = user.get("pushN");
        String acceptedYour = user.get("acceptedYour");
        String challengeEnd = user.get("challengeEnd");
        String newChallengeReq = user.get("newChallReq");
        String challForToday = user.get("challengesForToday");

        map.put("joinedChampy", "true");
        map.put("friendRequests", "true");
        map.put("challengeConfirmation", "true");
        map.put("reminderTime", "12"); // was 17
        map.put("challengeEnd", challengeEnd);
        map.put("challengesForToday", challForToday);
        map.put("acceptedYourChallenge", acceptedYour);
        map.put("newChallengeRequests", newChallengeReq);
        map.put("pushNotifications", pushNotify);

        Switch switchForPushNotif = (Switch) view.findViewById(R.id.switchPushNotifications);
        switchForPushNotif.setChecked(Boolean.parseBoolean(pushNotify));
        switchForPushNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("pushNotifications", "true");
                updateProfile(map);
            } else {
                map.put("pushNotifications", "false");
                updateProfile(map);
            }
        });

        Switch switchForNewChallRequests = (Switch) view.findViewById(R.id.switchNewChallengeRequest);
        switchForNewChallRequests.setChecked(Boolean.parseBoolean(newChallengeReq));
        switchForNewChallRequests.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("newChallengeRequests", "true");
                updateProfile(map);
            } else {
                map.put("newChallengeRequests", "false");
                updateProfile(map);
            }
        });

        Switch switchForAcceptedYourChall = (Switch) view.findViewById(R.id.switchAcceptedYourChallenge);
        switchForAcceptedYourChall.setChecked(Boolean.parseBoolean(acceptedYour));
        switchForAcceptedYourChall.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("acceptedYourChallenge", "true");
                updateProfile(map);
            } else {
                map.put("acceptedYourChallenge", "false");
                updateProfile(map);
            }
        });

        Switch switchForChallengesEnd = (Switch) view.findViewById(R.id.switchChallengeEnd);
        switchForChallengesEnd.setChecked(Boolean.parseBoolean(challengeEnd));
        switchForChallengesEnd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("challengeEnd", "true");
                updateProfile(map);
            } else {
                map.put("challengeEnd", "false");
                updateProfile(map);
            }
        });

        Switch switchChallengesForToday = (Switch) view.findViewById(R.id.switchChallengesForToday);
        switchChallengesForToday.setChecked(Boolean.parseBoolean(challForToday));
        switchChallengesForToday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                map.put("challengesForToday", "true");
                reminder.enableDailyNotificationReminder();
                updateProfile(map);
            } else {
                map.put("challengesForToday", "false");
                reminder.disableDailyNotificationReminder();
                updateProfile(map);
            }
        });

    }


    private void updateProfile(HashMap<String, String> map) {
        session.toggleChallengeEnd(map.get("challengeEnd"));
        session.togglePushNotification(map.get("pushNotifications"));
        session.toggleChallengesForToday(map.get("challengesForToday"));
        session.toggleNewChallengeRequest(map.get("newChallengeRequests"));
        session.toggleAcceptYourChallenge(map.get("acceptedYourChallenge"));

        Update_user update_user = retrofit.create(Update_user.class);
        Profile_data profile_data = new Profile_data(map);
        Call<User> call = update_user.update_profile_options(userID, token, profile_data);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


    // @Костыль
    private void surrenderAllChallengesDialog() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            boolean canDeleteAcc = false;
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (offline.isConnectedToRemoteAPI(getActivity())) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ChallengeController cc = new ChallengeController(
                                context,
                                getActivity(),
                                token,
                                userID
                        );

                        Cursor c = db.query("myChallenges", null, null, null, null, null, null);
                        if (c.moveToFirst()) {
                            int colchallenge_id = c.getColumnIndex("challenge_id");
                            do {
                                String challenge_id = c.getString(colchallenge_id);
                                try {
                                    cc.give_up(challenge_id, 0, null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } while (c.moveToNext());
                        }
                        canDeleteAcc = true;
                        c.close();
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }

            if (canDeleteAcc) {
                dialog.cancel();
                deleteAccountDialog();
            }

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.areYouSure)
                .setMessage("If you continue you will lose all your challenges")
                .setCancelable(false)
                .setPositiveButton("Continue", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();

    }

    // @Костыль
    private void deleteAccountDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent role = new Intent(context, RoleControllerActivity.class);
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        final Update_user update_user = retrofit.create(Update_user.class);

                        Call<Delete> callForDeleteUser = update_user.delete_user(userID, token);
                        callForDeleteUser.enqueue(new Callback<Delete>() {
                            @Override
                            public void onResponse(Response<Delete> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    File profile = new File(path, "profile.jpg");
                                    profile.delete();
                                    File blurred = new File(path, "blurred.png");
                                    blurred.delete();

                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    db.delete("pending", null, null);
                                    db.delete("pending_duel", null, null);
                                    db.delete("duel", null, null);
                                    db.delete("friends", null, null);
                                    db.delete("updated", null, null);
                                    db.delete("myChallenges", null, null);

                                    session.logout(getActivity());
                                    LoginManager.getInstance().logOut();
                                    startActivity(role);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        startActivity(role);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.areYouSure)
                .setMessage(R.string.youWantToDeleteYourAcc)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    // @Call to API
    private void setNewName(String newName) {
        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_user_name(userID, token, newName);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    //взяти имя с базы и поставить как новое в текст-вью.
                } else {
                    Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_LONG).show();
            }
        });
    }

}
