package com.example.ivan.champy_v2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ivan.champy_v2.model.Friend.Datum;
import com.example.ivan.champy_v2.model.Friend.Friend_;
import com.example.ivan.champy_v2.model.Friend.Owner;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by ivan on 05.02.16.
 */
public class Pending extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    final String API_URL = "http://46.101.213.24:3007";

    private int mPage;

    public static Pending newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        Pending fragment = new Pending();
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
        Log.i("stat", "Created Pending");
        final View view = inflater.inflate(R.layout.fragment_first, container, false);
        final List<Pending_friend> friends = new ArrayList<Pending_friend>();

        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("pending", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int photoColIndex = c.getColumnIndex("photo");
            int index = c.getColumnIndex("user_id");
            int owner = c.getColumnIndex("owner");

            do {
                Log.i("stat", "Status: "+c.getString(photoColIndex));
                friends.add(new Pending_friend(c.getString(nameColIndex), API_URL+c.getString(photoColIndex), c.getString(index), c.getString(owner)));
            } while (c.moveToNext());
        } else
            Log.i("stat", "0 rows");
        c.close();

        /*for (int i=0; i<20; i++)
        {
            friends.add(new Friend("My friend number "+i, "http://loremflickr.com/320/240?random="+(i+1), 0));
        }*/
        Log.i("stat", "Friends :"+friends);


        final RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        final PendingAdapter adapter = new PendingAdapter(friends, getContext(), new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Pending_friend friend = friends.get(position);
            }
        });
        FloatingActionButton floatingActionButton = (FloatingActionButton)getActivity().findViewById(R.id.imageButton);
        floatingActionButton.attachToRecyclerView(rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(adapter);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                final String API_URL = "http://46.101.213.24:3007";
                SessionManager sessionManager = new SessionManager(getContext());
                HashMap<String, String> user = new HashMap<>();
                user = sessionManager.getUserDetails();
                final String id = user.get("id");
                String token = user.get("token");
                final Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                DBHelper dbHelper = new DBHelper(getContext());
                final SQLiteDatabase db = dbHelper.getWritableDatabase();
                int clearCount = db.delete("pending", null, null);
                final ContentValues cv = new ContentValues();

                com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);
                Call<com.example.ivan.champy_v2.model.Friend.Friend> call = friends.getUserFriends(id, token);
                call.enqueue(new Callback<com.example.ivan.champy_v2.model.Friend.Friend>() {
                    @Override
                    public void onResponse(Response<com.example.ivan.champy_v2.model.Friend.Friend> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            List<Datum> data = response.body().getData();
                            for (int i = 0; i < data.size(); i++) {
                                Datum datum = data.get(i);
                                if (datum.getFriend() != null) {
                                    if (datum.getStatus().toString().equals("false")) {
                                        if (datum.getOwner().get_id().equals(id)) {
                                            Friend_ friend = datum.getFriend();
                                            cv.put("name", friend.getName());
                                            if (friend.getPhoto() != null)
                                                cv.put("photo", friend.getPhoto().getMedium());
                                            else cv.put("photo", "");
                                            cv.put("user_id", friend.getId());
                                            cv.put("owner", "false");
                                            db.insert("pending", null, cv);
                                        } else {
                                            Owner friend = datum.getOwner();
                                            cv.put("name", friend.getName());
                                            if (friend.getPhoto() != null)
                                                cv.put("photo", friend.getPhoto().getMedium());
                                            else cv.put("photo", "");
                                            cv.put("user_id", friend.get_id());
                                            cv.put("owner", "true");
                                            db.insert("pending", null, cv);
                                        }
                                    }
                                }
                            }
                            final List<Pending_friend> newfriends = new ArrayList<Pending_friend>();
                            Cursor c = db.query("pending", null, null, null, null, null, null);
                            if (c.moveToFirst()) {
                                int idColIndex = c.getColumnIndex("id");
                                int nameColIndex = c.getColumnIndex("name");
                                int photoColIndex = c.getColumnIndex("photo");
                                int index = c.getColumnIndex("user_id");
                                int owner = c.getColumnIndex("owner");
                                do {
                                    Log.i("newusers", "NewUser: " + c.getString(nameColIndex) + " Photo: " + c.getString(photoColIndex));
                                    newfriends.add(new Pending_friend(c.getString(nameColIndex), API_URL+c.getString(photoColIndex), c.getString(index), c.getString(owner)));
                                } while (c.moveToNext());
                            } else
                                Log.i("stat", "0 rows");
                            c.close();

                            Log.i("stat", "Friends :" + newfriends.toString());


                            //  RecclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
                            final PendingAdapter adapter = new PendingAdapter(newfriends, getContext(), new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Pending_friend friend = newfriends.get(position);
                                }
                            });
                            rvContacts.setAdapter(adapter);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });



            }
        });
        return view;

    }







}