package com.azinecllc.champy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.azinecllc.champy.R;

/**
 * Created by SashaKhyzhun on 2/13/17.
 */

public class ContactUsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_contact_us, container, false);


        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().isFinishing();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }


}
