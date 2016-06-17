package com.example.ivan.champy_v2.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.interfaces.CustomItemClickListener;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.Friend;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.Other;
import com.example.ivan.champy_v2.Pending_friend;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;
import com.example.ivan.champy_v2.interfaces.Friends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by ivan on 05.02.16.
 */
public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.ViewHolder> {

    final private String API_URL = "http://46.101.213.24:3007";
    final private String TAG = "myLogs";
    private List<Pending_friend> mContacts;
    private Context _context;
    private Activity activity;
    int selectedPos = 0;
    CustomItemClickListener listener;
    ArrayList<Integer> selected = new ArrayList<>();

    private Other other = new Other(new ArrayList<Friend>());

    // Pass in the contact array into the constructor
    public PendingAdapter(List<Pending_friend> contacts, Context context, Activity activity, CustomItemClickListener customItemClickListener) {
        mContacts = contacts;
        _context = context;
        this.listener = customItemClickListener;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_friends, parent, false);

        TextView textView = (TextView)contactView.findViewById(R.id.name);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/bebasneue.ttf");
        textView.setTypeface(typeFace);

        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(contactView);
        viewHolder.simple.setVisibility(View.VISIBLE);
        viewHolder.info.setVisibility(View.GONE);

        // отвечает за клик по другу в списке
        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected.isEmpty()) {
                    selected.add(viewHolder.getAdapterPosition());
                    notifyItemChanged(viewHolder.getAdapterPosition());

                } else {
                    int oldSelected = selected.get(0);
                    selected.clear();
                    if (viewHolder.getAdapterPosition() == oldSelected) {
                        selected.add(-1);
                    }
                    notifyItemChanged(oldSelected);
                    notifyItemChanged(viewHolder.getAdapterPosition());
                    selected.clear();
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PendingAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final Pending_friend contact = mContacts.get(position);
        Log.i("Selected", " " + selected.contains(position));
        if (selected.contains(position)) {
            Log.i("Selected: ", position + " open");

            ImageView img = (ImageView)viewHolder.itemView.findViewById(R.id.imageViewUserAvatar);
            Glide.with(_context)
                    .load(contact.getPicture())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(new CropCircleTransformation(_context))
                    .placeholder(R.drawable.champy_icon2)
                    .override(80, 80)
                    .dontAnimate()
                    .into(img);

            Glide.with(_context)
                    .load(R.drawable.start_circle_00026)
                    .placeholder(R.drawable.champy_icon2)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleChall));

            Glide.with(_context)
                    .load(R.drawable.start_circle_00026)
                    .placeholder(R.drawable.champy_icon2)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleWins));

            Glide.with(_context)
                    .load(R.drawable.start_circle_00026)
                    .placeholder(R.drawable.champy_icon2)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleTotal));

            TextView textView = (TextView)viewHolder.itemView.findViewById(R.id.textViewScoreChallenges);
            textView.setText(contact.getName());
            Typeface typeFace = Typeface.createFromAsset(_context.getAssets(), "fonts/bebasneue.ttf");
            textView.setTypeface(typeFace);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textViewChallenges);
            textView.setTypeface(typeFace);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textViewWins);
            textView.setTypeface(typeFace);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textViewTotal);
            textView.setTypeface(typeFace);

            SessionManager sessionManager = new SessionManager(_context);
            HashMap<String, String> champy = sessionManager.getChampyOptions();



            textView = (TextView)viewHolder.itemView.findViewById(R.id.info_chall);
            textView.setText(champy.get("challenges"));
            //textView.setText(contact.getmChallenges());
            //textViewChallengesInfo.setText(contact.getmChallenges());

            textView = (TextView)viewHolder.itemView.findViewById(R.id.info_wins);
            textView.setText(champy.get("wins"));

            textView = (TextView)viewHolder.itemView.findViewById(R.id.info_total);
            textView.setText(champy.get("total"));

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textViewUserLevel);
            textView.setText("Level "+champy.get("level")+" Champy");

            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.VISIBLE);
            if (contact.getOwner().equals("true")) {
                Log.d(TAG, "Owner: true");
                viewHolder.add.setVisibility(View.GONE);
                viewHolder.block.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "Owner: false");
                viewHolder.add.setVisibility(View.VISIBLE);
                viewHolder.block.setVisibility(View.VISIBLE);
            }
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.GONE);

        }
        else {
            Log.i("Selected: ", position + " close");
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.GONE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.VISIBLE);

        }
        // Set item views based on the data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(contact.getName());

        viewHolder.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SessionManager sessionManager = new SessionManager(_context);
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isInternetAvailable(activity)) {

                    HashMap<String, String> user = new HashMap<>();
                    user = sessionManager.getUserDetails();

                    final String token = user.get("token");
                    final String id = user.get("id");

                    String friend = mContacts.get(position).getID();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(API_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);

                    Log.d(TAG, "Status: " + id + " " + friend);

                    Call<com.example.ivan.champy_v2.model.Friend.Friend> call = friends.removeFriend(id, friend, token);
                    call.enqueue(new Callback<com.example.ivan.champy_v2.model.Friend.Friend>() {
                        @Override
                        public void onResponse(Response<com.example.ivan.champy_v2.model.Friend.Friend> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                Log.d(TAG, "Status: Removed ");
                            } else Log.d(TAG, "Status: " + response.toString());
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
                    sessionManager.setRefreshPending("true");
                    mContacts.remove(position);
                    notifyItemRemoved(position);
                    selected.clear();
                } else {
                    Toast.makeText(activity, "No Internet Connection!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isInternetAvailable(activity)) {

                    final SessionManager sessionManager = new SessionManager(_context);
                    HashMap<String, String> user = new HashMap<>();
                    user = sessionManager.getUserDetails();
                    final String token = user.get("token");
                    final String id = user.get("id");
                    String friend = mContacts.get(position).getID();
                    Log.d(TAG, "User: " + friend);
                    if (friend == null && friend == id) {
                        Toast.makeText(_context, "This user has not installed Champy", Toast.LENGTH_SHORT).show();
                    } else {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        String friend = mContacts.get(position).getID();
                                        Retrofit retrofit = new Retrofit.Builder()
                                                .baseUrl(API_URL)
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build();
                                        sessionManager.setRefreshFriends("true");
                                        sessionManager.setRefreshPending("true");
                                        DBHelper dbHelper = new DBHelper(_context);
                                        final SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        final ContentValues cv = new ContentValues();
                                        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(Friends.class);
                                        Log.d(TAG, "Status: " + id + " " + friend + " " + token);

                                        Call<com.example.ivan.champy_v2.model.Friend.Friend> call = friends.acceptFriendRequest(id, friend, token);
                                        call.enqueue(new Callback<com.example.ivan.champy_v2.model.Friend.Friend>() {
                                            @Override
                                            public void onResponse(Response<com.example.ivan.champy_v2.model.Friend.Friend> response, Retrofit retrofit) {
                                                if (response.isSuccess()) {
                                                    Log.d(TAG, "Status: Accepted");
                                                    cv.put("name", mContacts.get(position).getName());
                                                    cv.put("photo", mContacts.get(position).getPicture());
                                                    cv.put("user_id", mContacts.get(position).getID());
                                                    db.insert("friends", null, cv);
                                                } else Log.d(TAG, "Status: " + response.code());
                                            }

                                            @Override
                                            public void onFailure(Throwable t) {

                                            }
                                        });
                                        mContacts.remove(position);
                                        notifyItemRemoved(position);
                                        selected.clear();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.cancel();
                                        break;

                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                        builder.setMessage("Do you want add this user to your friends list?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                } else {
                    Toast.makeText(activity, "No Internet Connection!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView imageView = viewHolder.friendImage;
        Glide.with(_context)
                .load(contact.getPicture())
                .asBitmap()
                .transform(new CropCircleTransformation(_context))
                .placeholder(R.drawable.champy_icon2)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(80, 80)
                .dontAnimate()
                .into(imageView);
        imageView = viewHolder.challenges;
        Glide.with(_context)
                .load(R.drawable.challenges)
                .override(40, 40)
                .into(imageView);
        imageView = viewHolder.wins;
        Glide.with(_context)
                .load(R.drawable.wins)
                .override(40, 40)
                .into(imageView);
        imageView = viewHolder.total;
        Glide.with(_context)
                .load(R.drawable.total)
                .override(40, 40)
                .into(imageView);

        imageView = viewHolder.mChallenges;
        Glide.with(_context)
                .load(R.drawable.challenges)
                .override(40, 40)
                .into(imageView);
        imageView = viewHolder.mWins;
        Glide.with(_context)
                .load(R.drawable.wins)
                .override(40, 40)
                .into(imageView);
        imageView = viewHolder.mTotal;
        Glide.with(_context)
                .load(R.drawable.total)
                .override(40, 40)
                .into(imageView);
        SessionManager sessionManager = new SessionManager(_context);
        HashMap<String, String> champy = sessionManager.getChampyOptions();

        textView = (TextView)viewHolder.itemView.findViewById(R.id.chall);
        textView.setText(champy.get("challenges"));
        textView = (TextView)viewHolder.itemView.findViewById(R.id.in_progress);
        textView.setText(champy.get("wins"));
        textView = (TextView)viewHolder.itemView.findViewById(R.id.total);
        textView.setText(champy.get("total"));
        textView = (TextView)viewHolder.itemView.findViewById(R.id.level);
        textView.setText("Level " + champy.get("level") + " Champy");

    }


    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public ImageView friendImage;
        public ImageView challenges;
        public ImageView wins;
        public ImageView total;
        public ImageButton block;
        public ImageButton add;

        public ImageView mChallenges;
        public ImageView mWins;
        public ImageView mTotal;

        public RelativeLayout simple;
        public RelativeLayout info;

        public ImageView dop;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.name);
            friendImage = (ImageView) itemView.findViewById(R.id.picture);
            challenges = (ImageView) itemView.findViewById(R.id.imageView_challenges_logo);
            wins = (ImageView) itemView.findViewById(R.id.imageView_wins_logo);
            total = (ImageView) itemView.findViewById(R.id.imageView_total_logo);
            dop = (ImageView) itemView.findViewById(R.id.imageViewUserAvatar);

            mChallenges = (ImageView) itemView.findViewById(R.id.imageViewBgChallenges);
            mWins = (ImageView) itemView.findViewById(R.id.imageViewBgWins);
            mTotal = (ImageView) itemView.findViewById(R.id.imageViewBgTotal);

            simple = (RelativeLayout)itemView.findViewById(R.id.row_friends_list_close);
            info = (RelativeLayout)itemView.findViewById(R.id.row_friends_list_open);

            block = (ImageButton)itemView.findViewById(R.id.imageButtonBlockUser);
            add = (ImageButton)itemView.findViewById(R.id.imageButtonAddUser);


        }

    }

}
