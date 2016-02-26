package com.example.ivan.champy_v2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ivan on 17.02.16.
 */
public class PageFragment1 extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static PageFragment1 newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment1 fragment = new PageFragment1();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        final List<Friend> friends = Friend.createFriendsList();
        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        final ContactsAdapter adapter = new ContactsAdapter(friends, getContext(), new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Friend friend = friends.get(position);
                int id = friend.getID();
                Log.i("Click_on", " " + view.toString() + " " + id);
            }
        });

        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);
        return view;

    }






}