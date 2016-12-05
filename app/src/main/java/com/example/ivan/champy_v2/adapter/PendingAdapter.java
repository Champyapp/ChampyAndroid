package com.example.ivan.champy_v2.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.interfaces.CustomItemClickListener;
import com.example.ivan.champy_v2.interfaces.Friends;
import com.example.ivan.champy_v2.model.Pending_friend;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.example.ivan.champy_v2.utils.Constants.API_URL;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.ViewHolder> {

    private final String TAG = PendingAdapter.class.getSimpleName();
    private List<Pending_friend> mContacts;
    private String token, id;
    private Context context;
    private Activity activity;
    private CustomItemClickListener listener;
    private SessionManager sessionManager;
    private Retrofit retrofit;
    private OfflineMode offlineMode;
    private ArrayList<Integer> selected = new ArrayList<>();


    public PendingAdapter(List<Pending_friend> contacts, Context context, Activity activity, CustomItemClickListener customItemClickListener) {
        mContacts = contacts;
        this.context = context;
        this.activity = activity;
        this.listener = customItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_friends, parent, false);

        TextView tvUserName = (TextView)contactView.findViewById(R.id.userName);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/bebasneue.ttf");
        tvUserName.setTypeface(typeFace);

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

        sessionManager = new SessionManager(context);
        offlineMode = new OfflineMode();
        token = sessionManager.getToken();
        id = sessionManager.getUserId();

        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PendingAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final Pending_friend contact = mContacts.get(position);
        // Set item views based on the data model
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/bebasneue.ttf");
        TextView tvUserName = viewHolder.nameTextView;
        tvUserName.setText(contact.getName());

        /**
         * below close view
         */
        // response for icons in close view
        ImageView imageViewFriendPicture = viewHolder.friendImage;
        ImageView imageViewChallenges = viewHolder.challenges;
        ImageView imageViewTotal = viewHolder.total;
        ImageView imageViewWins = viewHolder.wins;
        // response for counters in close view
        TextView counterInProgressClose = (TextView)viewHolder.itemView.findViewById(R.id.counterInProgress);
        TextView counterTotalClose = (TextView)viewHolder.itemView.findViewById(R.id.counterTotal);
        TextView counterWinsClose = (TextView)viewHolder.itemView.findViewById(R.id.counterWins);
        // response for info by counters in close view
        counterInProgressClose.setText(contact.getmChallenges());
        counterWinsClose.setText(contact.getmWins());
        counterTotalClose.setText(contact.getmTotal());
        // response for typeface for counters in close view
        counterInProgressClose.setTypeface(typeFace);
        counterWinsClose.setTypeface(typeFace);
        counterTotalClose.setTypeface(typeFace);

        /**
         * below open view
         */

        // response for user info and icons in open view
        ImageView imageViewUserAvatar = (ImageView) viewHolder.itemView.findViewById(R.id.imageViewUserAvatar);
        ImageView imageViewChallengesOpen = viewHolder.mChallenges;
        ImageView imageViewTotalOpen = viewHolder.mTotal;
        ImageView imageViewWinsOpen = viewHolder.mWins;
        TextView tvUserName2 = (TextView) viewHolder.itemView.findViewById(R.id.textViewChallengesCounter);
        tvUserName2.setText(contact.getName());
        tvUserName2.setTypeface(typeFace);
        // response for icons in open view
        TextView tvChallenges = (TextView) viewHolder.itemView.findViewById(R.id.textViewChallenges);
        TextView tvTotal = (TextView) viewHolder.itemView.findViewById(R.id.textViewTotal);
        TextView tvWins = (TextView) viewHolder.itemView.findViewById(R.id.textViewWins);
        tvChallenges.setTypeface(typeFace);
        tvTotal.setTypeface(typeFace);
        tvWins.setTypeface(typeFace);
        // response for counters in open view
        TextView counterInProgressOpen = (TextView) viewHolder.itemView.findViewById(R.id.info_inProgress);
        TextView counterTotalOpen = (TextView) viewHolder.itemView.findViewById(R.id.info_total);
        TextView counterWinsOpen = (TextView) viewHolder.itemView.findViewById(R.id.info_wins);
        // response for info by counters in close view
        counterInProgressOpen.setText(contact.getmChallenges());
        counterTotalOpen.setText(contact.getmTotal());
        counterWinsOpen.setText(contact.getmWins());
        // response for typeface for counters in close view
        counterInProgressOpen.setTypeface(typeFace);
        counterTotalOpen.setTypeface(typeFace);
        counterWinsOpen.setTypeface(typeFace);


        // при нажатии нужно переобъявлять view, поэтому делаем это.
        // отвечает за вид в развернутом состоянии
        if (selected.contains(position)) {
            Glide.with(context).load(R.drawable.wins).override(40, 40).into(imageViewWinsOpen);
            Glide.with(context).load(R.drawable.challenges).override(40, 40).into(imageViewChallengesOpen);
            Glide.with(context).load(R.drawable.total).override(40, 40).into(imageViewTotalOpen);
            Glide.with(context).load(R.drawable.start_circle_00026).placeholder(R.drawable.icon_champy)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleChall));
            Glide.with(context).load(R.drawable.start_circle_00026).placeholder(R.drawable.icon_champy)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleWins));
            Glide.with(context).load(R.drawable.start_circle_00026).placeholder(R.drawable.icon_champy)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleTotal));


            Glide.with(context)
                    .load(contact.getPicture())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(new CropCircleTransformation(context))
                    .placeholder(R.drawable.icon_champy)
                    .override(80, 80)
                    .dontAnimate()
                    .into(imageViewUserAvatar);
//            Glide.with(context)
//                    .load(contact.getPicture())
//                    .asBitmap()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .transform(new CropCircleTransformation(context))
//                    .placeholder(R.drawable.icon_champy)
//                    .override(80, 80)
//                    .into(imageViewUserAvatar);

            // response for visible of button 'add' (owner = myself);
            if (contact.getOwner().equals("true")) {
                // если я приглашаю в друзья, то кнопка "add" толжна отсутствовать!
                Log.d(TAG, "Owner: true");
                viewHolder.add.setVisibility(View.GONE);
                viewHolder.block.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "Owner: false");
                viewHolder.add.setVisibility(View.VISIBLE);
                viewHolder.block.setVisibility(View.VISIBLE);
            }
            // made our "open-view" is visible and 'close-view' invisible
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.GONE);
        }
        else {
            Glide.with(context).load(R.drawable.wins).override(40, 40).into(imageViewWins);
            Glide.with(context).load(R.drawable.challenges).override(40, 40).into(imageViewChallenges);
            Glide.with(context).load(R.drawable.total).override(40, 40).into(imageViewTotal);

            Glide.with(context)
                    .load(contact.getPicture())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(new CropCircleTransformation(context))
                    .placeholder(R.drawable.icon_champy)
                    .override(80, 80)
                    .dontAnimate()
                    .into(imageViewFriendPicture);
//            Glide.with(context)
//                    .load(contact.getPicture())
//                    .asBitmap()
//                    .transform(new CropCircleTransformation(context))
//                    .placeholder(R.drawable.icon_champy)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .override(80, 80)
//                    .into(imageViewFriendPicture);
            // made our "close-view" is visible and 'open-view' invisible
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.GONE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.VISIBLE);
        }

        viewHolder.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (offlineMode.isConnectedToRemoteAPI(activity)) {
                    String friend = mContacts.get(position).getID();
                    com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);
                    Call<com.example.ivan.champy_v2.model.friend.Friend> call = friends.removeFriend(id, friend, token);
                    call.enqueue(new Callback<com.example.ivan.champy_v2.model.friend.Friend>() {
                        @Override
                        public void onResponse(Response<com.example.ivan.champy_v2.model.friend.Friend> response, Retrofit retrofit) {
                            String myLog = (response.isSuccess()) ? "Status: Removed" : "Status: " + response.toString();
                            Log.d(TAG, "onResponse: " + myLog);
                        }

                        @Override
                        public void onFailure(Throwable t) {}
                    });

                    //щоб воно не обновляло (і дублювало) лист друзів після додавання когось, то має юути false
                    sessionManager.setRefreshPending("false");
                    mContacts.remove(position);
                    notifyItemRemoved(position);
                    selected.clear();
                }
            }
        });


        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (offlineMode.isConnectedToRemoteAPI(activity)) {
                    String friend = mContacts.get(position).getID();
                    if (friend != null) {
                        sessionManager.setRefreshFriends("false");
                        sessionManager.setRefreshPending("false");
                        DBHelper dbHelper = DBHelper.getInstance(context);
                        final SQLiteDatabase db = dbHelper.getWritableDatabase();
                        final ContentValues cv = new ContentValues();

                        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(Friends.class);
                        Call<com.example.ivan.champy_v2.model.friend.Friend> call = friends.acceptFriendRequest(id, friend, token);
                        call.enqueue(new Callback<com.example.ivan.champy_v2.model.friend.Friend>() {
                            @Override
                            public void onResponse(Response<com.example.ivan.champy_v2.model.friend.Friend> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    cv.put("name", mContacts.get(position).getName());
                                    cv.put("photo", mContacts.get(position).getPicture());
                                    cv.put("user_id", mContacts.get(position).getID());
                                    db.insert("friends", null, cv);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) { }
                        });
                        mContacts.remove(position);
                        notifyItemRemoved(position);
                        selected.clear();
                    }
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView friendImage;
        public ImageView challenges;
        public ImageView wins;
        public ImageView total;
        ImageButton block;
        public ImageButton add;
        ImageView mChallenges;
        ImageView mWins;
        ImageView mTotal;
        RelativeLayout simple;
        public RelativeLayout info;
        ImageView dop;

        ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.userName);
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
