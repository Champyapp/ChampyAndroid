package com.azinecllc.champy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.model.FriendModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>/* implements Filterable */ {

    public static final String TAG = "FriendsAdapter";
    private List<FriendModel> mFriends;
    private List<FriendModel> filteredFriends;
    private Context context;
    private Activity activity;
//    private ModelFilter filter;
//    private SQLiteDatabase db;
//    private ContentValues cv;
//    private ArrayList<Integer> selected = new ArrayList<>();
//    private RecyclerFriendsClickListener onCardClickListener;


    // Pass in the contact array into the constructor
    public FriendsAdapter(List<FriendModel> contacts, Context ctx, Activity activity) {
        mFriends = contacts;
        this.context = ctx;
        this.activity = activity;
        this.filteredFriends = new ArrayList<FriendModel>();
        filteredFriends.addAll(mFriends);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        FacebookSdk.sdkInitialize(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_friends, parent, false);

        Log.i(TAG, "onCreateViewHolder: ");
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        //viewHolder.closeView.setVisibility(View.VISIBLE);
        //viewHolder.openView.setVisibility(View.GONE);


        // click on same contact
        contactView.setOnClickListener(v -> {
            if (activity.getCallingActivity() != null) {
                System.out.println("activity for result");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("name", viewHolder.tvUserName.getText());
                System.out.println("result: " + viewHolder.tvUserName.getText());
                activity.setResult(Activity.RESULT_OK, returnIntent);
                activity.finish();
            } else {
                System.out.println("simple starts activity");
                if (viewHolder.layoutInfo.getVisibility() == View.INVISIBLE) {
                    viewHolder.layoutInfo.setVisibility(View.VISIBLE);
                    viewHolder.tvUserName.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.layoutInfo.setVisibility(View.INVISIBLE);
                    viewHolder.tvUserName.setVisibility(View.VISIBLE);
                }
            }


        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        FriendModel contact = mFriends.get(position);


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
//                String friend = mFriends.get(position).getID();
//                if (friend != null) {
//                    //щоб воно не обновляло (і дублювало) лист друзів після додавання когось, то має бути false
//                    session.setRefreshPending("false");
//                    Friends friends = retrofit.create(Friends.class);
//                    Call<com.azinecllc.champy.model.friend.Friend> call = friends.sendFriendRequest(id, friend, token);
//                    call.enqueue(new Callback<com.azinecllc.champy.model.friend.Friend>() {
//                        @Override
//                        public void onResponse(Response<com.azinecllc.champy.model.friend.Friend> response, Retrofit retrofit1) {
//                            if (response.isSuccess()) {
//                                cv.put("name",    mFriends.get(position).getName());
//                                cv.put("photo",   mFriends.get(position).getPicture());
//                                cv.put("user_id", mFriends.get(position).getID());
//                                cv.put("level",   mFriends.get(position).getmLevel());
//                                cv.put("inProgressChallengesCount", mFriends.get(position).getmChallenges());
//                                cv.put("successChallenges", mFriends.get(position).getmWins());
//                                cv.put("allChallengesCount", mFriends.get(position).getmTotal());
//                                db.insert("pending", null, cv);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Throwable t) { }
//                    });
//                    mFriends.remove(position);
//                    notifyItemRemoved(position);
//                    selected.clear();
//                }
//            }
//        });

    }

//    @Override
//    public Filter getFilter() {
//        if (filter == null){
//            filter  = new ModelFilter();
//        }
//        return filter;
//    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
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


//    private class ModelFilter extends Filter {
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            constraint = constraint.toString().toLowerCase();
//            FilterResults result = new FilterResults();
//            if(constraint.toString().length() > 0) {
//                ArrayList<String> filteredItems = new ArrayList<String>();
//
//                for(int i = 0, l = mFriends.size(); i < l; i++) {
//                    String m = String.valueOf(mFriends.get(i));
//                    if(m.toLowerCase().contains(constraint))
//                        filteredItems.add(m);
//                }
//                result.count = filteredItems.size();
//                result.values = filteredItems;
//            }
//            else {
//                synchronized(this) {
//                    result.values = mFriends;
//                    result.count = mFriends.size();
//                }
//            }
//            return result;
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            filteredFriends = (List<FriendModel>) results.values;
//            notifyDataSetChanged();
//            //clear();
////            for(int i = 0, l = filteredFriends.size(); i < l; i++) {
////                add(filteredFriends.get(i));
////            }
////            notifyDataSetInvalidated();
//        }
//    }


    public void setFilter(List<FriendModel> countryModels) {
        mFriends = new ArrayList<>();
        mFriends.addAll(countryModels);
        notifyDataSetChanged();
    }

//    public void setOnRecyclerClickListener(RecyclerFriendsClickListener recyclerClickListener) {
//        this.onCardClickListener = recyclerClickListener;
//    }

}
