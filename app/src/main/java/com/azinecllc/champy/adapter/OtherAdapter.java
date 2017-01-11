package com.azinecllc.champy.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.data.DBHelper;
import com.azinecllc.champy.interfaces.Friends;
import com.azinecllc.champy.model.FriendModel;
import com.azinecllc.champy.utils.OfflineMode;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class OtherAdapter extends RecyclerView.Adapter<OtherAdapter.ViewHolder> {

    private List<FriendModel> mContacts;
    private Retrofit retrofit;
    private SQLiteDatabase db;
    private ContentValues cv;
    private Activity activity;
    private Typeface typeFace;
    private Context context;
    private ArrayList<Integer> selected = new ArrayList<>();


    // Pass in the contact array into the constructor
    public OtherAdapter(List<FriendModel> contacts, Context c, Activity a, Retrofit r) {
        mContacts = contacts;
        this.context = c;
        this.activity = a;
        this.retrofit = r;
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
        viewHolder.closeView.setVisibility(View.VISIBLE);
        viewHolder.openView.setVisibility(View.GONE);

        // click on same contact
        contactView.setOnClickListener(v -> {
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
        });

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
        ImageView ivFriendPicture = viewHolder.friendImage;
        ImageView imageViewInProg = viewHolder.challenges;
        ImageView imageViewTotal  = viewHolder.total;
        ImageView imageViewWins   = viewHolder.wins;

        // response for counters in close view
        TextView counterInProgClose = (TextView)viewHolder.itemView.findViewById(R.id.counterInProgress);
        TextView counterTotalClose  = (TextView)viewHolder.itemView.findViewById(R.id.counterTotal);
        TextView counterWinsClose   = (TextView)viewHolder.itemView.findViewById(R.id.counterWins);

        // response for openView by counters in close view
        counterInProgClose.setText(contact.getmChallenges());
        counterTotalClose.setText(contact.getmTotal());
        counterWinsClose.setText(contact.getmWins());

        // response for typeface for counters in close view
        counterInProgClose.setTypeface(typeFace);
        counterTotalClose.setTypeface(typeFace);
        counterWinsClose.setTypeface(typeFace);

        /**
         * below open view
         */

        // Initialisation view elements
        ImageView imageViewUserAvatar = (ImageView) viewHolder.itemView.findViewById(R.id.imageViewUserAvatar);
        ImageView imageViewInProgOpen = viewHolder.mchallenges;
        ImageView imageViewTotalOpen  = viewHolder.mtotal;
        ImageView imageViewWinsOpen   = viewHolder.mwins;

        // Initialisation counters in open view
        TextView counterInProgOpen = (TextView) viewHolder.itemView.findViewById(R.id.info_inProgress);
        TextView counterTotalOpen  = (TextView) viewHolder.itemView.findViewById(R.id.info_total);
        TextView counterWinsOpen   = (TextView) viewHolder.itemView.findViewById(R.id.info_wins);

        // Setting value for counters in open view
        counterInProgOpen.setText(contact.getmChallenges());
        counterTotalOpen.setText(contact.getmTotal());
        counterWinsOpen.setText(contact.getmWins());

        // Initialisation text above counters
        TextView tvInProg = (TextView) viewHolder.itemView.findViewById(R.id.textViewChallenges);
        TextView tvTotal  = (TextView) viewHolder.itemView.findViewById(R.id.textViewTotal);
        TextView tvWins   = (TextView) viewHolder.itemView.findViewById(R.id.textViewWins);

        // Initialisation User name and setting it
        TextView tvUserName2 = (TextView) viewHolder.itemView.findViewById(R.id.textViewChallengesCounter);
        tvUserName2.setText(contact.getName());

        // typeface for our view
        tvUserName2.setTypeface(typeFace);
        tvInProg.setTypeface(typeFace);
        tvTotal.setTypeface(typeFace);
        tvWins.setTypeface(typeFace);

        // typeface for our counters
        counterInProgOpen.setTypeface(typeFace);
        counterTotalOpen.setTypeface(typeFace);
        counterWinsOpen.setTypeface(typeFace);


        // при нажатии нужно переобъявлять view, поэтому делаем это.
        // отвечает за вид в развернутом состоянии
        if (selected.contains(position)) {

            Glide.with(context).load(R.drawable.ic_score_prog) .override(40, 40).into(imageViewInProgOpen);
            Glide.with(context).load(R.drawable.ic_score_total).override(40, 40).into(imageViewTotalOpen);
            Glide.with(context).load(R.drawable.ic_score_wins) .override(40, 40).into(imageViewWinsOpen);

            Glide.with(context)
                    .load(R.drawable.stat_circle_00027)
                    .placeholder(R.drawable.ic_champy_circle)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleChall));

            Glide.with(context)
                    .load(R.drawable.stat_circle_00027)
                    .placeholder(R.drawable.ic_champy_circle)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleWins));

            Glide.with(context)
                    .load(R.drawable.stat_circle_00027)
                    .placeholder(R.drawable.ic_champy_circle)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleTotal));

            Glide.with(context)
                    .load(contact.getPicture())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(false)
                    .transform(new CropCircleTransformation(context))
                    .placeholder(R.drawable.ic_champy_circle)
                    .override(80, 80)
                    .dontAnimate()
                    .into(imageViewUserAvatar);

            // made our "open-view" is visible and 'close-view' invisible
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.GONE);
        }
        else {
            Glide.with(context).load(R.drawable.ic_score_prog).override(40, 40).into(imageViewInProg);
            Glide.with(context).load(R.drawable.ic_score_total).override(40, 40).into(imageViewTotal);
            Glide.with(context).load(R.drawable.ic_score_wins).override(40, 40).into(imageViewWins);

            Glide.with(context)
                    .load(contact.getPicture())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(false)
                    .transform(new CropCircleTransformation(context))
                    .placeholder(R.drawable.ic_champy_circle)
                    .override(80, 80)
                    .dontAnimate()
                    .into(ivFriendPicture);

            // made our "close-view" is visible and 'open-view' invisible
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.GONE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.VISIBLE);
        }


        // button add user
        viewHolder.add.setOnClickListener(v -> {
            SessionManager session = SessionManager.getInstance(context);
            OfflineMode offlineMode = OfflineMode.getInstance();
            final String token = session.getToken();
            final String id = session.getUserId();

            DBHelper dbHelper = DBHelper.getInstance(context);
            db = dbHelper.getWritableDatabase();
            cv = new ContentValues();
            if (offlineMode.isConnectedToRemoteAPI(activity)) {
                String friend = mContacts.get(position).getID();
                if (friend != null) {
                    //щоб воно не обновляло (і дублювало) лист друзів після додавання когось, то має бути false
                    session.setRefreshPending("false");
                    Friends friends = retrofit.create(Friends.class);
                    Call<com.azinecllc.champy.model.friend.Friend> call = friends.sendFriendRequest(id, friend, token);
                    call.enqueue(new Callback<com.azinecllc.champy.model.friend.Friend>() {
                        @Override
                        public void onResponse(Response<com.azinecllc.champy.model.friend.Friend> response, Retrofit retrofit1) {
                            if (response.isSuccess()) {
                                cv.put("name",    mContacts.get(position).getName());
                                cv.put("photo",   mContacts.get(position).getPicture());
                                cv.put("user_id", mContacts.get(position).getID());
                                cv.put("level",   mContacts.get(position).getmLevel());
                                cv.put("inProgressChallengesCount", mContacts.get(position).getmChallenges());
                                cv.put("successChallenges",  mContacts.get(position).getmWins());
                                cv.put("allChallengesCount", mContacts.get(position).getmTotal());
                                db.insert("pending", null, cv);
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

        ImageView mchallenges;
        ImageView mwins;
        ImageView mtotal;

        RelativeLayout closeView;
        RelativeLayout openView;

        ImageView dop;


        ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView)  itemView.findViewById(R.id.userName);
            friendImage  = (ImageView) itemView.findViewById(R.id.picture);
            challenges   = (ImageView) itemView.findViewById(R.id.imageView_challenges_logo);
            wins         = (ImageView) itemView.findViewById(R.id.imageView_wins_logo);
            total        = (ImageView) itemView.findViewById(R.id.imageView_total_logo);
            dop          = (ImageView) itemView.findViewById(R.id.imageViewUserAvatar);

            mchallenges  = (ImageView) itemView.findViewById(R.id.imageViewBgChallenges);
            mwins        = (ImageView) itemView.findViewById(R.id.imageViewBgWins);
            mtotal       = (ImageView) itemView.findViewById(R.id.imageViewBgTotal);

            closeView    = (RelativeLayout) itemView.findViewById(R.id.row_friends_list_close);
            openView     = (RelativeLayout) itemView.findViewById(R.id.row_friends_list_open);

            add          = (ImageButton) itemView.findViewById(R.id.imageButtonAddUser);
            block        = (ImageButton) itemView.findViewById(R.id.imageButtonBlockUser);
            block.setVisibility(View.INVISIBLE);


        }

    }


}
