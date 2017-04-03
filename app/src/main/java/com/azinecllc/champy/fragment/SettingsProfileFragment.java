package com.azinecllc.champy.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.azinecllc.champy.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @autor SashaKhyzhun
 * Created on 4/3/17.
 */

public class SettingsProfileFragment extends Fragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        Integer[] intColors = new Integer[]{
                Color.RED,
                Color.parseColor("#FFCF670C"),
                Color.YELLOW,
                Color.GREEN,
                Color.BLUE,
                Color.parseColor("#FF7209DA"),
                Color.parseColor("#FFEC03DC")
        };
        String[] stringColors = new String[]{
                "Red",
                "Orange",
                "Yellow",
                "Green",
                "Blue",
                "Purple",
                "Pink"
        };

        ArrayAdapter<String> adapterColorText = new ArrayAdapter<String>(
                getContext(),
                R.layout.item_list_view_colors,
                R.id.text_view_color,
                stringColors
        );

//        ArrayAdapter<Integer> adapterColor = new ArrayAdapter<Integer>(
//                getContext(),
//                R.layout.item_list_view_colors,
//                R.id.image_view_color,
//                intColors
//        );


        ListView listView = (ListView) view.findViewById(R.id.list_view_colors);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapterColorText);
//        listView.setAdapter(adapterColor);


        return view;
    }


}
