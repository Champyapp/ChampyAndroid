package com.example.ivan.champy_v2;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://uifaces.com/")
                .build();

        FriendsAPI api = restAdapter.create(FriendsAPI.class);
        final List<Friend> friends = Friend.createFriendsList();
        Log.i("SYKY", "BLYAD1");
        api.getBooks(new Callback<List<MainPojo>>() {
            @Override
            public void success(List<MainPojo> mainPojos, Response response) {
                String[] names = new String[mainPojos.size()];
                Log.i("SYKY", "BLYAD");
                String[] photos = new String[mainPojos.size()];

                for (int i = 0; i < mainPojos.size(); i++) {
                    //Storing names to string array
                    names[i] = mainPojos.get(i).getUsername();
                    Image_urls image_urls = mainPojos.get(i).getImage_urls();
                    photos[i] = image_urls.getEpic();
                    friends.add(new Friend(names[i], photos[i]));

                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("SYKY", "BLYAD2");
            }
        });


        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        ContactsAdapter adapter = new ContactsAdapter(Friend.createFriendsList(), getContext());


        rvContacts.setAdapter(adapter);
        return view;
    }




}