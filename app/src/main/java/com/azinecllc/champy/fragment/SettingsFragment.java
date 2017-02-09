package com.azinecllc.champy.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.AboutActivity;
import com.azinecllc.champy.activity.ContactUsActivity;
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
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
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
    private String userName, userID, userToken;
    private DailyRemindController reminder;
    private TextView tvChangeName, tvUserName;

    private HashMap<String, String> map = new HashMap<>();
    private HashMap<String, String> userDetails = new HashMap<>();
    private Retrofit retrofit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebasneue.ttf");
        reminder = new DailyRemindController(getContext()); // here should be getContext()
        session = SessionManager.getInstance(context);
        dbHelper = DBHelper.getInstance(context);
        offline = OfflineMode.getInstance();
        userID = session.getUserId();
        userName = session.getUserName();
        userToken = session.getToken();
        userDetails = session.getUserDetails();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewSettings = inflater.inflate(R.layout.content_settings, container, false);

        ImageView userImageProfile = (ImageView) viewSettings.findViewById(R.id.img_profile);

        File fileProfile = new File(path, "profile.jpg");
        Uri userPicturePath = Uri.fromFile(fileProfile);
        //String userPicturePath = session.getPathToPic();
        Glide.with(this)
                .load(userPicturePath)
                .bitmapTransform(new CropCircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(130, 130)
                .into(userImageProfile);

        initSwitches(viewSettings);

        TextView about = (TextView) viewSettings.findViewById(R.id.about);
        TextView avatar = (TextView) viewSettings.findViewById(R.id.avatar);
        TextView tvLegal = (TextView) viewSettings.findViewById(R.id.tvLegal);
        TextView delete = (TextView) viewSettings.findViewById(R.id.delete_acc);
        TextView tvGeneral = (TextView) viewSettings.findViewById(R.id.tvGeneral);
        TextView contactUs = (TextView) viewSettings.findViewById(R.id.contact_us);
        TextView tvNotif = (TextView) viewSettings.findViewById(R.id.tvNotifications);
        tvUserName = (TextView) viewSettings.findViewById(R.id.tvUserName);
        tvChangeName = (TextView) viewSettings.findViewById(R.id.tvName);

        tvUserName.setText(userName);
        tvUserName.setTypeface(typeface);
        tvLegal.setTypeface(typeface);
        tvNotif.setTypeface(typeface);
        tvGeneral.setTypeface(typeface);

        about.setOnClickListener(this);
        delete.setOnClickListener(this);
        avatar.setOnClickListener(this);
        contactUs.setOnClickListener(this);
        tvChangeName.setOnClickListener(this);


        return viewSettings;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvName:

                LinearLayout layoutEditText = (LinearLayout) getActivity().findViewById(R.id.layoutEditText);
                TextView tvEnterYourName = (TextView) getActivity().findViewById(R.id.tvEnterNewName);
                EditText etNewName = (EditText) getActivity().findViewById(R.id.editTextNewName);
                Button buttonOK = (Button) getActivity().findViewById(R.id.buttonOk);
                View lineOfTheNed = getActivity().findViewById(R.id.view11);

                if (layoutEditText.getVisibility() == View.GONE) {
                    Toast.makeText(context, "Було Gone, Стало не Gone", Toast.LENGTH_SHORT).show();
                    layoutEditText.setVisibility(View.VISIBLE);
                    tvEnterYourName.setVisibility(View.VISIBLE);
                    lineOfTheNed.setVisibility(View.VISIBLE);
                    etNewName.setVisibility(View.VISIBLE);
                    buttonOK.setVisibility(View.VISIBLE);

                    etNewName.setText(userName);

                    buttonOK.setOnClickListener(v1 -> {
                        String checkName = etNewName.getText().toString();
                        if (offline.isConnectedToRemoteAPI(getActivity()) && !checkName.isEmpty()) {
                            String newName = etNewName.getText().toString().trim();
                            session.setUserName(newName);

                            updateUserName(newName); // call

                            tvUserName.setText(etNewName.getText().toString());

                            layoutEditText.setVisibility(View.GONE);
                            tvEnterYourName.setVisibility(View.GONE);
                            etNewName.setVisibility(View.GONE);
                            buttonOK.setVisibility(View.GONE);
                            lineOfTheNed.setVisibility(View.GONE);

                        }
                    });
                } else {
                    layoutEditText.setVisibility(View.GONE);
                    tvEnterYourName.setVisibility(View.GONE);
                    etNewName.setVisibility(View.GONE);
                    buttonOK.setVisibility(View.GONE);
                    lineOfTheNed.setVisibility(View.GONE);
                }
                break;
            case R.id.avatar:
                //startActivity(new Intent(context, PhotoActivity.class));

                LinearLayout layoutButtons = (LinearLayout) getActivity().findViewById(R.id.layoutButtons);
                TextView tvTakeAPicture = (TextView) getActivity().findViewById(R.id.textViewTakeAPicture);
                TextView tvChooseFrom = (TextView) getActivity().findViewById(R.id.textViewChooseFromGallery);

                if (checkWriteExternalPermission()) {
                    if (layoutButtons.getVisibility() == View.GONE) {
                        layoutButtons.setVisibility(View.VISIBLE);
                        tvTakeAPicture.setVisibility(View.VISIBLE);
                        tvChooseFrom.setVisibility(View.VISIBLE);

                        tvTakeAPicture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(context, "Take A Pic", Toast.LENGTH_SHORT).show();
                            }
                        });

                        tvChooseFrom.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(context, "From Gallery", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        layoutButtons.setVisibility(View.GONE);
                        tvTakeAPicture.setVisibility(View.INVISIBLE);
                        tvChooseFrom.setVisibility(View.INVISIBLE);
                        tvTakeAPicture.setOnClickListener(null);
                        tvChooseFrom.setOnClickListener(null);
                    }
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE}, 1);
                    return;
                }

                break;
            case R.id.delete_acc:
                if (!session.getChampyOptions().get("challenges").equals("0")) {
                    surrenderAllChallengesDialog();
                } else {
                    deleteAccountDialog();
                }
                break;
            case R.id.about:
                startActivity(new Intent(context, AboutActivity.class));
                break;
            case R.id.contact_us:
                startActivity(new Intent(context, ContactUsActivity.class));
                break;

        }
        updateProfile(map);
    }


    private void initSwitches(View view) {
        String pushNotify = userDetails.get("pushN");
        String acceptedYour = userDetails.get("acceptedYour");
        String challengeEnd = userDetails.get("challengeEnd");
        String newChallenge = userDetails.get("newChallReq");
        String challForToday = userDetails.get("challengesForToday");

        map.put("joinedChampy", "true");
        map.put("friendRequests", "true");
        map.put("challengeConfirmation", "true");
        map.put("reminderTime", "12"); // was 17
        map.put("challengeEnd", challengeEnd);
        map.put("challengesForToday", challForToday);
        map.put("acceptedYourChallenge", acceptedYour);
        map.put("newChallengeRequests", newChallenge);
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
        switchForNewChallRequests.setChecked(Boolean.parseBoolean(newChallenge));
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
        Call<User> call = update_user.update_profile_options(userID, userToken, profile_data);

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
                                userToken,
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

                        Call<Delete> callForDeleteUser = update_user.delete_user(userID, userToken);
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
    private void updateUserName(String newName) {
        Update_user update_user = retrofit.create(Update_user.class);
        Call<User> call = update_user.update_user_name(userID, userToken, newName);
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

    // Check Permission
    private boolean checkWriteExternalPermission() {
        int res = getActivity().checkCallingOrSelfPermission(READ_EXTERNAL_STORAGE);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

}
