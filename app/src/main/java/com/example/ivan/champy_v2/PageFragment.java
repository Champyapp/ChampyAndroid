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
 * Created by ivan on 05.02.16.
 */
public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);



        /*TextView textView = (TextView) view;
        textView.setText("Fragment #" + mPage);*/


        final List<Friend> friends = Friend.createFriendsList();

        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        ContactsAdapter adapter = new ContactsAdapter(friends, getContext(), new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Friend friend = friends.get(position);
                int id = friend.getID();
                Log.i("Click_on", " " + view.toString() + " " + id);
              /*  String name = friend.getName();
                    ImageView img = (ImageView) view.findViewById(R.id.imageView5);
                    ImageView imageView = (ImageView) view.findViewById(R.id.friend_pic);
                    if (imageView.getVisibility() == imageView.VISIBLE) {
                        imageView.setVisibility(View.INVISIBLE);
                        img.setImageDrawable(imageView.getDrawable());
                    } else {

                        img.setImageDrawable(null);
                        imageView.setVisibility(View.VISIBLE);
                    }*/
            }
        });


        rvContacts.setAdapter(adapter);
       /* rvContacts.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Friend friend = friends.get(position);
                        String name = friend.getName();
                        int id = friend.getID();
                        if (position != id) return;
                        ImageView img = (ImageView)view.findViewById(R.id.imageView5);
                        ImageView imageView = (ImageView) view.findViewById(R.id.friend_pic);
                        if (imageView.getVisibility() == imageView.VISIBLE) {
                            imageView.setVisibility(View.INVISIBLE);
                            img.setImageDrawable(imageView.getDrawable());
                        } else {

                            img.setImageDrawable(null);
                            imageView.setVisibility(View.VISIBLE);
                        }

                    }
                })
        );*/
        return view;
    }






}