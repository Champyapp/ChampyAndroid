package com.azinecllc.champy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.utils.ExpandableHeightListView;

import java.util.ArrayList;

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
        View view = inflater.inflate(R.layout.fragment_settings_profile, container, false);

//        ExpandableHeightListView listView = (ExpandableHeightListView) view.findViewById(R.id.list_view_colors);
//
////        Integer[] intColors = new Integer[]{
////                Color.RED,
////                Color.parseColor("#FFCF670C"),
////                Color.YELLOW,
////                Color.GREEN,
////                Color.BLUE,
////                Color.parseColor("#FF7209DA"),
////                Color.parseColor("#FFEC03DC")
////        };
//        String[] stringColors = new String[]{
//                "Red",
//                "Orange",
//                "Yellow",
//                "Green",
//                "Blue",
//                "Purple",
//                "Pink"
//        };
////
//        ArrayAdapter<String> adapterColorText = new ArrayAdapter<String>(
//                getContext(),
//                android.R.layout.simple_list_item_1,
////                R.id.text_view_color,
//                stringColors
//        );
//
////        ArrayAdapter<Integer> adapterColor = new ArrayAdapter<Integer>(
////                getContext(),
////                R.layout.item_list_view_colors,
////                R.id.image_view_color,
////                intColors
////        );
//
//
//
//        //listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        listView.setAdapter(adapterColorText);
//        listView.setExpanded(true);
////        listView.setAdapter(adapterColor);

        ExpandableHeightListView expandableListView = (ExpandableHeightListView) view.findViewById(R.id.expandable_listview);

        String[] stringColors = new String[]{
                "Red",
                "Orange",
                "Yellow",
                "Green",
                "Blue",
                "Purple",
                "Pink"
        };

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, stringColors);

        expandableListView.setAdapter(itemsAdapter);

        // This actually do the magic
        expandableListView.setExpanded(true);

        return view;
    }


}
