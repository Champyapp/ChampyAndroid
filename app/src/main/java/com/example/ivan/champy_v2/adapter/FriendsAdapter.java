package com.example.ivan.champy_v2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
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
import com.example.ivan.champy_v2.activity.DuelActivity;
import com.example.ivan.champy_v2.Friend;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.Other;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.SessionManager;

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
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    final private String API_URL = "http://46.101.213.24:3007";
    final private String TAG = "myLogs";
    private List<Friend> mContacts;
    private Context _context;
    private Activity activity;
    int selectedPos = 0;
    CustomItemClickListener listener;
    ArrayList<Integer> selected = new ArrayList<>();

    private Other other = new Other(new ArrayList<Friend>());

    // Pass in the contact array into the constructor
    public FriendsAdapter(List<Friend> contacts, Context context, Activity activity, CustomItemClickListener customItemClickListener) {
        mContacts = contacts;
        _context = context;
        this.listener = customItemClickListener;
        this.activity = activity;
    }

    public void Add_to_list(Friend friend){
        mContacts.add(friend);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_friends, parent, false);

        TextView tvUserName = (TextView)contactView.findViewById(R.id.name);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/bebasneue.ttf");
        tvUserName.setTypeface(typeFace);


        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(contactView);
        viewHolder.simple.setVisibility(View.VISIBLE);
        viewHolder.info.setVisibility(View.GONE);

        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected.isEmpty()) {
                    selected.add(viewHolder.getAdapterPosition());
                    notifyItemChanged(viewHolder.getAdapterPosition());

                } else {
                    int oldSelected = selected.get(0);
                    selected.clear();
                    if (viewHolder.getAdapterPosition() == oldSelected) selected.add(-1);
                    notifyItemChanged(oldSelected);
                    notifyItemChanged(viewHolder.getAdapterPosition());
                    selected.clear();
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FriendsAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final Friend contact = mContacts.get(position);

        //SessionManager sessionManager = new SessionManager(_context);
        //HashMap<String, String> champy = sessionManager.getChampyOptions();

        // Set item views based on the data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(contact.getName());

        Log.i("Selected", "" + selected.contains(position));

        // при нажатии нужно переобъявлять view, поэтому делаем это.
        // отвечает за вид в развернутом состоянии
        if (selected.contains(position)) {
            Log.i("Selected: ", position + " open");

            // отвечает за значки в развернутом виде
            ImageView imageViewUserAvatar = (ImageView)viewHolder.itemView.findViewById(R.id.imageViewUserAvatar);
            Glide.with(_context).load(contact.getPicture()).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).transform(new CropCircleTransformation(_context))
                    .placeholder(R.drawable.champy_icon2).override(80, 80).dontAnimate().into(imageViewUserAvatar);
            ImageView imageViewChallengesOpen = viewHolder.mchallenges;
            Glide.with(_context).load(R.drawable.challenges).override(40, 40).into(imageViewChallengesOpen);
            ImageView imageViewWinsOpen = viewHolder.mwins;
            Glide.with(_context).load(R.drawable.wins).override(40, 40).into(imageViewWinsOpen);
            ImageView imageViewTotalOpen = viewHolder.mtotal;
            Glide.with(_context).load(R.drawable.total).override(40, 40).into(imageViewTotalOpen);

            // отвечает за круги в развернутом виде
            Glide.with(_context).load(R.drawable.start_circle_00026).placeholder(R.drawable.champy_icon2)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleChall));
            Glide.with(_context).load(R.drawable.start_circle_00026).placeholder(R.drawable.champy_icon2)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleWins));
            Glide.with(_context).load(R.drawable.start_circle_00026).placeholder(R.drawable.champy_icon2)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleTotal));

            // отвечает за имя юзера в развернутом виде
            TextView tvUserName = (TextView)viewHolder.itemView.findViewById(R.id.textViewChallengesCounter);
            tvUserName.setText(contact.getName());
            Typeface typeFace = Typeface.createFromAsset(_context.getAssets(), "fonts/bebasneue.ttf");
            tvUserName.setTypeface(typeFace);

            // создаем вид счетчиком в развернутом виде
            TextView textViewChallenges = (TextView)viewHolder.itemView.findViewById(R.id.textViewChallenges);
            textViewChallenges.setTypeface(typeFace);
            TextView textViewWins = (TextView)viewHolder.itemView.findViewById(R.id.textViewWins);
            textViewWins.setTypeface(typeFace);
            TextView tvTotal = (TextView)viewHolder.itemView.findViewById(R.id.textViewTotal);
            tvTotal.setTypeface(typeFace);

            //SessionManager sessionManager = new SessionManager(_context);
            //HashMap<String, String> champy = sessionManager.getChampyOptions();

            // отвечает за счетчики в развернутом виде
            TextView counterInProgressOpen = (TextView)viewHolder.itemView.findViewById(R.id.info_total);
            counterInProgressOpen.setText(contact.getmChallenges());
            TextView counterWinsOpen = (TextView)viewHolder.itemView.findViewById(R.id.info_wins);
            counterWinsOpen.setText(contact.getmWins());
            TextView counterTotalOpen = (TextView)viewHolder.itemView.findViewById(R.id.info_inProgress);
            counterTotalOpen.setText(contact.getmTotal());

            // отвечает за лвл юзера в свернутом виде
            TextView tvUserLevelOpen = (TextView)viewHolder.itemView.findViewById(R.id.textViewWinsCounter);
            tvUserLevelOpen.setText("Level "+ contact.getmLevel() + " Champy");

            // делаем view open видимой, view close невидимой
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.GONE);

        }
        else {
            Log.i("Selected: ", position + " close");
            // отвечает за значки в свернутом виде
            ImageView imageViewFriendPicture = viewHolder.friendImage;
            Glide.with(_context).load(contact.getPicture()).asBitmap().transform(new CropCircleTransformation(_context)).
                    placeholder(R.drawable.champy_icon2).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).override(80, 80).dontAnimate().into(imageViewFriendPicture);
            ImageView imageViewChallenges = viewHolder.challenges;
            Glide.with(_context).load(R.drawable.challenges).override(40, 40).into(imageViewChallenges);
            ImageView imageViewWins = viewHolder.wins;
            Glide.with(_context).load(R.drawable.wins).override(40, 40).into(imageViewWins);
            ImageView imageViewTotal = viewHolder.total;
            Glide.with(_context).load(R.drawable.total).override(40, 40).into(imageViewTotal);

            // отвечает за счетчики в свернутом виде
            TextView counterInProgressClose = (TextView)viewHolder.itemView.findViewById(R.id.counterInProgress);
            counterInProgressClose.setText(contact.getmChallenges());
            TextView counterWinsClose = (TextView)viewHolder.itemView.findViewById(R.id.counterWins);
            counterWinsClose.setText(contact.getmWins());
            TextView counterTotalClose = (TextView)viewHolder.itemView.findViewById(R.id.counterTotal);
            counterTotalClose.setText(contact.getmTotal());

            // отвечает за лвл юзера в свернутом виде
            TextView tvUserLevelClose = (TextView)viewHolder.itemView.findViewById(R.id.level);
            tvUserLevelClose.setText("Level " + contact.getmLevel()+ " Champy");

            // делаем view open невидимой, view close видимой
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.GONE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.VISIBLE);
        }

        // button block user in All pages
        viewHolder.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfflineMode offlineMode = new OfflineMode();
                if (!offlineMode.isInternetAvailable(activity)) {
                    Toast.makeText(activity, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                } else {
                    // можно добавить диалог типа "ты уверен что хочешь удалить юзера?"
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    final SessionManager sessionManager = new SessionManager(_context);
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
                                            } else {
                                                Log.d(TAG, "Status: " + response.toString());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                        }
                                    });
                                    sessionManager.setRefreshFriends("true");
                                    mContacts.remove(position);
                                    notifyItemRemoved(position);
                                    selected.clear();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                    builder.setMessage("Do you want to delete this user from your friends list?")
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No",  dialogClickListener)
                            .show();
                }
            }
        });

        ImageButton imageButtonAdd = viewHolder.add;
        imageButtonAdd.setBackgroundDrawable(_context.getResources().getDrawable(R.drawable.duel));

        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfflineMode offlineMode = new OfflineMode();
                if (!offlineMode.isInternetAvailable(activity)) {
                    Toast.makeText(activity, "No Internet Connection!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(_context, DuelActivity.class);
                    intent.putExtra("friend", contact.getPicture());
                    intent.putExtra("name", contact.getName());
                    intent.putExtra("id", contact.getID());
                    _context.startActivity(intent);
                }
            }
        });



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

        public ImageView mchallenges;
        public ImageView mwins;
        public ImageView mtotal;

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

            mchallenges = (ImageView) itemView.findViewById(R.id.imageViewBgChallenges);
            mwins = (ImageView) itemView.findViewById(R.id.imageViewBgWins);
            mtotal = (ImageView) itemView.findViewById(R.id.imageViewBgTotal);

            simple = (RelativeLayout)itemView.findViewById(R.id.row_friends_list_close);
            info = (RelativeLayout)itemView.findViewById(R.id.row_friends_list_open);

            block = (ImageButton)itemView.findViewById(R.id.imageButtonBlockUser);
            add = (ImageButton)itemView.findViewById(R.id.imageButtonAddUser);


        }

    }

}
