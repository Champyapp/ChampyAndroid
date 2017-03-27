package com.azinecllc.champy.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.interfaces.CustomRecyclerClickListener;
import com.azinecllc.champy.interfaces.Friends;
import com.azinecllc.champy.interfaces.OnCardClickListener;
import com.azinecllc.champy.model.FriendModel;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private List<FriendModel> mContacts;
    private SQLiteDatabase db;
    private ContentValues cv;
    private Context context;
    private ArrayList<Integer> selected = new ArrayList<>();
    private CustomRecyclerClickListener onCardClickListener;


    // Pass in the contact array into the constructor
    public FriendsAdapter(List<FriendModel> contacts, Context ctx) {
        mContacts = contacts;
        this.context = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        FacebookSdk.sdkInitialize(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_friends, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        //viewHolder.closeView.setVisibility(View.VISIBLE);
        //viewHolder.openView.setVisibility(View.GONE);


        // click on same contact
        contactView.setOnClickListener(v -> {
            if (viewHolder.layoutInfo.getVisibility() == View.INVISIBLE) {
                viewHolder.layoutInfo.setVisibility(View.VISIBLE);
                viewHolder.tvUserName.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.layoutInfo.setVisibility(View.INVISIBLE);
                viewHolder.tvUserName.setVisibility(View.VISIBLE);
            }

        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        FriendModel contact = mContacts.get(position);


        Random random = new Random();
        int low = 0;
        int high = 100;
        String mockStreak = String.valueOf(random.nextInt(high - low) + low);
        int r = random.nextInt(256), g = random.nextInt(256), b = random.nextInt(256);
        String mockColor = String.valueOf(Color.argb(255, r, g, b));

        Glide.with(context)
                .load(contact.getPicture())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(false)
                .override(80, 80)
                .transform(new CropCircleTransformation(context))
                .placeholder(R.drawable.ic_champy_circle)
                .dontAnimate()
                .into(viewHolder.ivUserPhoto);

        viewHolder.tvUserName.setText(contact.getName());
        viewHolder.tvChallengesN.setText(contact.getmTotal());
        viewHolder.tvStreakN.setText(mockStreak);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Integer.parseInt(mockColor)); // origin: #cdced2
        gd.setCornerRadius(25);
        viewHolder.layoutCard.setBackgroundDrawable(gd);

//        // button add user
//        viewHolder.add.setOnClickListener(v -> {
//            SessionManager session = SessionManager.getInstance(context);
//            OfflineMode offlineMode = OfflineMode.getInstance();
//            final String token = session.getToken();
//            final String id = session.getUserId();
//
//            DBHelper dbHelper = DBHelper.getInstance(context);
//            db = dbHelper.getWritableDatabase();
//            cv = new ContentValues();
//            if (offlineMode.isConnectedToRemoteAPI(activity)) {
//                String friend = mContacts.get(position).getID();
//                if (friend != null) {
//                    //щоб воно не обновляло (і дублювало) лист друзів після додавання когось, то має бути false
//                    session.setRefreshPending("false");
//                    Friends friends = retrofit.create(Friends.class);
//                    Call<com.azinecllc.champy.model.friend.Friend> call = friends.sendFriendRequest(id, friend, token);
//                    call.enqueue(new Callback<com.azinecllc.champy.model.friend.Friend>() {
//                        @Override
//                        public void onResponse(Response<com.azinecllc.champy.model.friend.Friend> response, Retrofit retrofit1) {
//                            if (response.isSuccess()) {
//                                cv.put("name",    mContacts.get(position).getName());
//                                cv.put("photo",   mContacts.get(position).getPicture());
//                                cv.put("user_id", mContacts.get(position).getID());
//                                cv.put("level",   mContacts.get(position).getmLevel());
//                                cv.put("inProgressChallengesCount", mContacts.get(position).getmChallenges());
//                                cv.put("successChallenges", mContacts.get(position).getmWins());
//                                cv.put("allChallengesCount", mContacts.get(position).getmTotal());
//                                db.insert("pending", null, cv);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Throwable t) { }
//                    });
//                    mContacts.remove(position);
//                    notifyItemRemoved(position);
//                    selected.clear();
//                }
//            }
//        });

    }


    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivUserPhoto;
        private TextView tvUserName;
        private TextView tvStreak;
        private TextView tvStreakN;
        private TextView tvChallenges;
        private TextView tvChallengesN;
        private LinearLayout layoutInfo;
        private RelativeLayout layoutCard;
        ViewHolder(View itemView) {
            super(itemView);
            ivUserPhoto = (ImageView) itemView.findViewById(R.id.image_view_user_photo);
            tvUserName = (TextView) itemView.findViewById(R.id.text_view_user_name);
            tvStreak = (TextView) itemView.findViewById(R.id.text_view_streak);
            tvStreakN = (TextView) itemView.findViewById(R.id.text_view_streak_n);
            tvChallenges = (TextView) itemView.findViewById(R.id.text_view_challenges);
            tvChallengesN = (TextView) itemView.findViewById(R.id.text_view_challenges_n);
            layoutInfo = (LinearLayout) itemView.findViewById(R.id.layout_info);
            layoutCard = (RelativeLayout) itemView.findViewById(R.id.card_layout);
        }
    }

    public void setOnRecyclerClickListener(CustomRecyclerClickListener recyclerClickListener) {
        this.onCardClickListener = recyclerClickListener;
    }

}
