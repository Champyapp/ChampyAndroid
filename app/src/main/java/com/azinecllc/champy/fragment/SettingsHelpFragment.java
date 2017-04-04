package com.azinecllc.champy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.activity.AboutActivity;
import com.azinecllc.champy.activity.ChallengeRulesActivity;
import com.azinecllc.champy.activity.SendFeedbackActivity;

/**
 * @autor SashaKhyzhun
 * Created on 4/3/17.
 */

public class SettingsHelpFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_help, container, false);

        TextView tvAbout = (TextView) view.findViewById(R.id.text_view_about);
        TextView tvRules = (TextView) view.findViewById(R.id.text_view_rules_and_faq);
        TextView tvDevInfo = (TextView) view.findViewById(R.id.text_view_developer_info);
        TextView tvPrivacy = (TextView) view.findViewById(R.id.text_view_privacy_policy);
        TextView tvEndUser = (TextView) view.findViewById(R.id.text_view_end_user_agreement);

        Button btnSendFeedBack = (Button) view.findViewById(R.id.button_send_feedback);


        tvAbout.setOnClickListener(v -> startActivity(new Intent(getContext(), AboutActivity.class)));
        tvRules.setOnClickListener(v -> startActivity(new Intent(getContext(), ChallengeRulesActivity.class)));
        tvDevInfo.setOnClickListener(v -> Toast.makeText(getContext(), "Dev Info", Toast.LENGTH_SHORT).show());
        tvPrivacy.setOnClickListener(v -> Toast.makeText(getContext(), "Privacy", Toast.LENGTH_SHORT).show());
        tvEndUser.setOnClickListener(v -> Toast.makeText(getContext(), "End User", Toast.LENGTH_SHORT).show());

        btnSendFeedBack.setOnClickListener(v -> startActivity(new Intent(getActivity(), SendFeedbackActivity.class)));

        return view;
    }



}
