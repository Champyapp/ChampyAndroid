package com.azinecllc.champy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.controller.DailyRemindController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @autor SashaKhyzhun
 * Created on 4/3/17.
 */

public class SettingsNotificationsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {


    private DailyRemindController drc;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_notifications, container, false);

        Switch switchMorningReminder = (Switch) view.findViewById(R.id.switch_morning_reminder);
        Switch switchEveningReminder = (Switch) view.findViewById(R.id.switch_evening_reminder);

        switchMorningReminder.setOnCheckedChangeListener(this);
        switchEveningReminder.setOnCheckedChangeListener(this);

        drc = new DailyRemindController(getContext());
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_morning_reminder:

                Toast.makeText(
                        getContext(),
                        "Clicked On Morning Layout, Status: " + ((isChecked) ? "ON" : "OFF"),
                        Toast.LENGTH_SHORT)
                        .show();

                break;
            case R.id.switch_evening_reminder:

                Toast.makeText(
                        getContext(),
                        "Clicked On Morning Layout, Status: " + ((isChecked) ? "ON" : "OFF"),
                        Toast.LENGTH_SHORT)
                        .show();

                break;
        }
    }


}
