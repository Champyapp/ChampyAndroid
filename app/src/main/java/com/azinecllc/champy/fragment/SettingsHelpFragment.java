package com.azinecllc.champy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.azinecllc.champy.R;
import com.azinecllc.champy.utils.ExpandableHeightListView;

import java.util.ArrayList;

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

        ArrayList<Integer> numbersArray = new ArrayList<Integer>();
        ExpandableHeightListView expandableListView = (ExpandableHeightListView) view.findViewById(R.id.expandable_listview);

        int i;
        for (i = 0; i <= 20; i++) {
            numbersArray.add(i);
        }

        ArrayAdapter<Integer> itemsAdapter =
                new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_list_item_1, numbersArray);

        expandableListView.setAdapter(itemsAdapter);

        // This actually do the magic
        expandableListView.setExpanded(true);


        return view;
    }



}
