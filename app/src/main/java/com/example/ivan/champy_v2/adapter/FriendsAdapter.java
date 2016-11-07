package com.example.ivan.champy_v2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.model.FriendModel;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.example.ivan.champy_v2.activity.DuelActivity;
import com.example.ivan.champy_v2.helper.CurrentUserHelper;
import com.example.ivan.champy_v2.interfaces.CustomItemClickListener;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    final private String API_URL = "http://46.101.213.24:3007";
    final private String TAG = "FriendsAdapter";
    private List<FriendModel> mContacts;
    private Context context;
    private Activity activity;
    private ArrayList<Integer> selected = new ArrayList<>();

    public FriendsAdapter(List<FriendModel> contacts, Context context, Activity activity, CustomItemClickListener itemOnClick) {
        mContacts = contacts;
        this.context = context;
        CustomItemClickListener listener = itemOnClick;
        this.activity = activity;
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
        final FriendModel contact = mContacts.get(position);
        // Set item views based on the data model
        TextView tvUserName = viewHolder.nameTextView;
        tvUserName.setText(contact.getName());
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/bebasneue.ttf");


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
        ImageView imageViewChallengesOpen = viewHolder.mchallenges;
        ImageView imageViewTotalOpen = viewHolder.mtotal;
        ImageView imageViewWinsOpen = viewHolder.mwins;
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
        // response for view in open state
        if (selected.contains(position)) {

            Glide.with(context).load(R.drawable.wins).override(40, 40).into(imageViewWinsOpen);
            Glide.with(context).load(contact.getPicture()).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).transform(new CropCircleTransformation(context)).placeholder(R.drawable.icon_champy).override(80, 80).dontAnimate().into(imageViewUserAvatar);
            Glide.with(context).load(R.drawable.challenges).override(40, 40).into(imageViewChallengesOpen);
            Glide.with(context).load(R.drawable.total).override(40, 40).into(imageViewTotalOpen);
            Glide.with(context).load(R.drawable.start_circle_00026).placeholder(R.drawable.icon_champy).into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleChall));
            Glide.with(context).load(R.drawable.start_circle_00026).placeholder(R.drawable.icon_champy).into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleWins));
            Glide.with(context).load(R.drawable.start_circle_00026).placeholder(R.drawable.icon_champy).into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleTotal));
            // made our "open-view" is visible and 'close-view' invisible
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.GONE);
        }
        else {
            Glide.with(context).load(R.drawable.wins).override(40, 40).into(imageViewWins);
            Glide.with(context).load(contact.getPicture()).asBitmap().transform(new CropCircleTransformation(context)).placeholder(R.drawable.icon_champy).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).override(80, 80).dontAnimate().into(imageViewFriendPicture);
            Glide.with(context).load(R.drawable.challenges).override(40, 40).into(imageViewChallenges);
            Glide.with(context).load(R.drawable.total).override(40, 40).into(imageViewTotal);
            // made our "close-view" is visible and 'open-view' invisible
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.GONE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.VISIBLE);
        }

        // button block user in All pages
        viewHolder.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isConnectedToRemoteAPI(activity)) {

                    final SessionManager sessionManager = new SessionManager(context);
                    CurrentUserHelper user = new CurrentUserHelper(context);
                    final String token = user.getToken();
                    final String userId = user.getUserObjectId();
                    String friend = mContacts.get(position).getID();
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();

                    com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(com.example.ivan.champy_v2.interfaces.Friends.class);
                    Call<com.example.ivan.champy_v2.model.Friend.Friend> call = friends.removeFriend(userId, friend, token);
                    call.enqueue(new Callback<com.example.ivan.champy_v2.model.Friend.Friend>() {
                        @Override
                        public void onResponse(Response<com.example.ivan.champy_v2.model.Friend.Friend> response, Retrofit retrofit) {
                            String myLog = (response.isSuccess()) ? "Status: Removed" : "Status: " + response.toString();
                            Log.d(TAG, "onResponse: " + myLog);
                        }

                        @Override
                        public void onFailure(Throwable t) { }
                    });
                    sessionManager.setRefreshFriends("false");
                    mContacts.remove(position);
                    notifyItemRemoved(position);
                    selected.clear();
                }
            }
        });

        ImageButton imageButtonAdd = viewHolder.add;
        imageButtonAdd.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.duel));
        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isConnectedToRemoteAPI(activity)) {
                    Intent intent = new Intent(context, DuelActivity.class);
                    intent.putExtra("photo", contact.getPicture());
                    intent.putExtra("name", contact.getName());
                    intent.putExtra("id", contact.getID());
                    context.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView nameTextView;

        ImageView friendImage;
        public ImageView challenges;
        public ImageView wins;
        public ImageView total;
        ImageButton block;
        public ImageButton add;

        ImageView mchallenges;
        ImageView mwins;
        ImageView mtotal;

        RelativeLayout simple;
        public RelativeLayout info;

        ImageView dop;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.userName);
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
