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
import com.example.ivan.champy_v2.interfaces.Friends;
import com.example.ivan.champy_v2.model.FriendModel;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;
import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class OtherAdapter extends RecyclerView.Adapter<OtherAdapter.ViewHolder> {

    final private String API_URL = "http://46.101.213.24:3007";
    final private String TAG = "myLogs";
    private com.facebook.CallbackManager CallbackManager;
    private SessionManager sessionManager;
    private List<FriendModel> mContacts;
    private OfflineMode offlineMode;
    private String token, id;
    private Activity activity;
    private Typeface typeFace;
    private Context context;
    private ArrayList<Integer> selected = new ArrayList<>();


    // Pass in the contact array into the constructor
    public OtherAdapter(List<FriendModel> contacts, Context context, Activity activity) {
        mContacts = contacts;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        FacebookSdk.sdkInitialize(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_friends, parent, false);
        TextView tvUserName = (TextView)contactView.findViewById(R.id.userName);
        typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/bebasneue.ttf");
        tvUserName.setTypeface(typeFace);

        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(contactView);
        viewHolder.simple.setVisibility(View.VISIBLE);
        viewHolder.info.setVisibility(View.GONE);

        // клик по контакту
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

        sessionManager = new SessionManager(context);
        offlineMode = new OfflineMode();
        HashMap<String, String> user;
        user = sessionManager.getUserDetails();
        token = user.get("token");
        id = user.get("id");

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OtherAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final FriendModel contact = mContacts.get(position);

        // Set item views based on the data model
        TextView nameTextView = viewHolder.nameTextView;
        nameTextView.setText(contact.getName());

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
        counterInProgressClose.setText(contact.getmTotal());
        counterTotalClose.setText(contact.getmChallenges());
        counterWinsClose.setText(contact.getmWins());
        // response for typeface for counters in close view
        counterInProgressClose.setTypeface(typeFace);
        counterTotalClose.setTypeface(typeFace);
        counterWinsClose.setTypeface(typeFace);

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
        counterInProgressOpen.setText(contact.getmTotal());
        counterTotalOpen.setText(contact.getmChallenges());
        counterWinsOpen.setText(contact.getmWins());
        // response for typeface for counters in close view
        counterInProgressOpen.setTypeface(typeFace);
        counterTotalOpen.setTypeface(typeFace);
        counterWinsOpen.setTypeface(typeFace);


        // при нажатии нужно переобъявлять view, поэтому делаем это.
        // отвечает за вид в развернутом состоянии
        if (selected.contains(position)) {

            Glide.with(context).load(R.drawable.challenges).override(40, 40).into(imageViewChallengesOpen);
            Glide.with(context).load(R.drawable.total).override(40, 40).into(imageViewTotalOpen);
            Glide.with(context).load(R.drawable.wins).override(40, 40).into(imageViewWinsOpen);
            Glide.with(context).load(R.drawable.start_circle_00026).placeholder(R.drawable.icon_champy)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleChall));
            Glide.with(context).load(R.drawable.start_circle_00026).placeholder(R.drawable.icon_champy)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleWins));
            Glide.with(context).load(R.drawable.start_circle_00026).placeholder(R.drawable.icon_champy).
                    into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleTotal));

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
//                    .dontAnimate()
//                    .into(imageViewUserAvatar);

            // made our "open-view" is visible and 'close-view' invisible
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.GONE);
        }
        else {
            Glide.with(context).load(R.drawable.challenges).override(40, 40).into(imageViewChallenges);
            Glide.with(context).load(R.drawable.total).override(40, 40).into(imageViewTotal);
            Glide.with(context).load(R.drawable.wins).override(40, 40).into(imageViewWins);

            Glide.with(context)
                    .load(contact.getPicture())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
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
//                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .skipMemoryCache(true)
//                    .override(80, 80)
//                    .into(imageViewFriendPicture);

            // made our "close-view" is visible and 'open-view' invisible
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.GONE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.VISIBLE);
        }


        // button add user
        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (offlineMode.isConnectedToRemoteAPI(activity)) {
                    String friend = mContacts.get(position).getID();
                    if (friend != null) {
                        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
                        DBHelper dbHelper = new DBHelper(context);
                        final SQLiteDatabase db = dbHelper.getWritableDatabase();
                        final ContentValues cv = new ContentValues();

                        //щоб воно не обновляло (і дублювало) лист друзів після додавання когось, то має бути false
                        sessionManager.setRefreshPending("false");

                        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(Friends.class);
                        Call<com.example.ivan.champy_v2.model.friend.Friend> call = friends.sendFriendRequest(id, friend, token);
                        call.enqueue(new Callback<com.example.ivan.champy_v2.model.friend.Friend>() {
                            @Override
                            public void onResponse(Response<com.example.ivan.champy_v2.model.friend.Friend> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    Log.d(TAG, "Status: Sent FriendModel Request");
                                    cv.put("name", mContacts.get(position).getName());
                                    cv.put("photo", mContacts.get(position).getPicture());
                                    cv.put("user_id", mContacts.get(position).getID());
                                    cv.put("level", mContacts.get(position).getmLevel());
                                    cv.put("inProgressChallengesCount", mContacts.get(position).getmChallenges());
                                    cv.put("successChallenges", mContacts.get(position).getmWins());
                                    cv.put("allChallengesCount", mContacts.get(position).getmTotal());
                                    db.insert("pending", null, cv);
                                } else Log.d(TAG, "Status: " + response.toString());
                            }

                            @Override
                            public void onFailure(Throwable t) {
                            }
                        });
                        mContacts.remove(position);
                        notifyItemRemoved(position);
                        selected.clear();

                }
            }
        }
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setMessage("Do you want add this user to your friends list?")
//                                .setPositiveButton("Yes", dialogClickListener)
//                                .setNegativeButton("No", dialogClickListener)
//                                .show();
//                    }
//                }
//            }
});

    }

//    @Override
//    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
//        super.unregisterAdapterDataObserver(observer);
//        //new ProgressTask().execute();
//        Log.d(TAG, "unregisterAdapterDataObserver: ");
//    }

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

        ImageView mchallenges;
        ImageView mwins;
        ImageView mtotal;

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

            mchallenges = (ImageView) itemView.findViewById(R.id.imageViewBgChallenges);
            mwins = (ImageView) itemView.findViewById(R.id.imageViewBgWins);
            mtotal = (ImageView) itemView.findViewById(R.id.imageViewBgTotal);

            simple = (RelativeLayout)itemView.findViewById(R.id.row_friends_list_close);
            info = (RelativeLayout)itemView.findViewById(R.id.row_friends_list_open);

            block = (ImageButton)itemView.findViewById(R.id.imageButtonBlockUser);
            block.setVisibility(View.INVISIBLE);
            add = (ImageButton)itemView.findViewById(R.id.imageButtonAddUser);


        }

    }


    private class ProgressTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Glide.get(context).clearDiskCache();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

}
