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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.data.DBHelper;
import com.example.ivan.champy_v2.Friend;
import com.example.ivan.champy_v2.OfflineMode;
import com.example.ivan.champy_v2.model.Other;
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
 * Отвечает за раздел Others в "friends"
 */
public class OtherAdapter extends RecyclerView.Adapter<OtherAdapter.ViewHolder> {

    final private String API_URL = "http://46.101.213.24:3007";
    final private String TAG = "myLogs";
    private List<Friend> mContacts;
    private Context _context;
    int selectedPos = 0;
    Activity activity;
    ArrayList<Integer> selected = new ArrayList<>();

    private Other other = new Other(new ArrayList<Friend>());

    // Pass in the contact array into the constructor
    public OtherAdapter(List<Friend> contacts, Context context, Activity activity) {
        mContacts = contacts;
        _context = context;
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

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OtherAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final Friend contact = mContacts.get(position);

        // Set item views based on the data model
        TextView nameTextView = viewHolder.nameTextView;
        nameTextView.setText(contact.getName());

        /**
         *   при нажатии нужно переобъявлять view, поэтому делаем это.
         */

        // отвечает за вид в развернутом состоянии
        if (selected.contains(position)) {
            // отвечает за значки в развернутом виде
            ImageView imageViewUserAvatar = (ImageView)viewHolder.itemView.findViewById(R.id.imageViewUserAvatar);
            ImageView imageViewChallengesOpen = viewHolder.mtotal;
            ImageView imageViewWinsOpen = viewHolder.mwins;
            ImageView imageViewTotalOpen = viewHolder.mchallenges;

            Glide.with(_context).load(R.drawable.challenges).override(40, 40).into(imageViewChallengesOpen);
            Glide.with(_context).load(R.drawable.wins).override(40, 40).into(imageViewWinsOpen);
            Glide.with(_context).load(R.drawable.total).override(40, 40).into(imageViewTotalOpen);
            Glide.with(_context).load(contact.getPicture()).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).transform(new CropCircleTransformation(_context)).placeholder(R.drawable.champy_icon2).override(80, 80).dontAnimate().into(imageViewUserAvatar);

            // отвечает за круги в развернутом виде
            Glide.with(_context).load(R.drawable.start_circle_00026).placeholder(R.drawable.champy_icon2).into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleChall));
            Glide.with(_context).load(R.drawable.start_circle_00026).placeholder(R.drawable.champy_icon2).into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleWins));
            Glide.with(_context).load(R.drawable.start_circle_00026).placeholder(R.drawable.champy_icon2).into((ImageView)viewHolder.itemView.findViewById(R.id.imageViewBgForCircleTotal));

            // отвечает за имя юзера в развернутом виде
            TextView tvUserName = (TextView)viewHolder.itemView.findViewById(R.id.textViewChallengesCounter);
            tvUserName.setText(contact.getName());
            Typeface typeFace = Typeface.createFromAsset(_context.getAssets(), "fonts/bebasneue.ttf");
            tvUserName.setTypeface(typeFace);


            // создаем вид счетчиком в развернутом виде
            TextView tvChallenges = (TextView)viewHolder.itemView.findViewById(R.id.textViewChallenges);
            TextView tvWins = (TextView)viewHolder.itemView.findViewById(R.id.textViewWins);
            TextView tvTotal = (TextView)viewHolder.itemView.findViewById(R.id.textViewTotal);
            tvChallenges.setTypeface(typeFace);
            tvWins.setTypeface(typeFace);
            tvTotal.setTypeface(typeFace);

            // отвечает за счетчики в развернутом виде
            TextView counterInProgressOpen = (TextView)viewHolder.itemView.findViewById(R.id.info_inProgress);
            TextView counterWinsOpen = (TextView)viewHolder.itemView.findViewById(R.id.info_wins);
            TextView counterTotalOpen = (TextView)viewHolder.itemView.findViewById(R.id.info_total);

            // TODO: 30.08.2016 CHANGE TOTAL FOR TOTAL
            counterInProgressOpen.setText(contact.getmTotal());
            counterWinsOpen.setText(contact.getmWins());
            counterTotalOpen.setText(contact.getmChallenges());

            // отвечает за лвл юзера в свернутом виде
            TextView tvUserLevelOpen = (TextView)viewHolder.itemView.findViewById(R.id.textViewWinsCounter);
            tvUserLevelOpen.setText(_context.getString(R.string.level) + contact.getmLevel() + _context.getString(R.string.champy));

            // делаем view open видимой, view close невидимой
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.GONE);

        }
        else {
            // отвечает за значки в свернутом виде
            ImageView imageViewChallenges = viewHolder.challenges;
            ImageView imageViewWins = viewHolder.wins;
            ImageView imageViewTotal = viewHolder.total;
            ImageView imageViewFriendPicture = viewHolder.friendImage;
            Glide.with(_context).load(R.drawable.challenges).override(40, 40).into(imageViewChallenges);
            Glide.with(_context).load(R.drawable.wins).override(40, 40).into(imageViewWins);
            Glide.with(_context).load(R.drawable.total).override(40, 40).into(imageViewTotal);
            Glide.with(_context).load(contact.getPicture()).asBitmap().transform(new CropCircleTransformation(_context)).
                    placeholder(R.drawable.champy_icon2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).override(80, 80).dontAnimate().into(imageViewFriendPicture);

            // отвечает за счетчики в свернутом виде
            TextView counterInProgressClose = (TextView)viewHolder.itemView.findViewById(R.id.counterInProgress);
            TextView counterWinsClose = (TextView)viewHolder.itemView.findViewById(R.id.counterWins);
            TextView counterTotalClose = (TextView)viewHolder.itemView.findViewById(R.id.counterTotal);

            // TODO: 30.08.2016 CHANGE TOTAL FOR TOTAL
            counterInProgressClose.setText(contact.getmTotal());
            counterWinsClose.setText(contact.getmWins());
            counterTotalClose.setText(contact.getmChallenges());

            // отвечает за лвл юзера в свернутом виде
            TextView tvUserLevelClose = (TextView)viewHolder.itemView.findViewById(R.id.level);
            tvUserLevelClose.setText(_context.getString(R.string.level) + contact.getmLevel() + _context.getString(R.string.champy));

            // делаем view open невидимой, view close видимой
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.GONE);
            viewHolder.itemView.findViewById(R.id.row_friends_list_close).setVisibility(View.VISIBLE);
        }

//        viewHolder.block.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OfflineMode offlineMode = new OfflineMode();
//                if (offlineMode.isConnectedToRemoteAPI(activity)) {
//                    mContacts.remove(position);
//                    notifyItemRemoved(position);
//                    selected.clear();
//                }
//            }
//        });

        // button add user

        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isConnectedToRemoteAPI(activity)) {
                    final SessionManager sessionManager = new SessionManager(_context);
                    HashMap<String, String> user;
                    user = sessionManager.getUserDetails();
                    final String token = user.get("token");
                    final String id = user.get("id");
                    
                    String friend = mContacts.get(position).getID();
                    if (friend == null) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                        builder.setMessage("This user has not installed Champy. Do you want to send invite?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener)
                                .show();
                    } else {
                        // dialog "Do you want add this user to your friends list?"
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        String friend = mContacts.get(position).getID();
                                        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
                                        DBHelper dbHelper = new DBHelper(_context);
                                        final SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        final ContentValues cv = new ContentValues();

                                        //щоб воно не обновляло (і дублювало) лист друзів після додавання когось, то має юути false
                                        sessionManager.setRefreshPending("false");

                                        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(Friends.class);
                                        Call<com.example.ivan.champy_v2.model.Friend.Friend> call = friends.sendFriendRequest(id, friend, token);
                                        call.enqueue(new Callback<com.example.ivan.champy_v2.model.Friend.Friend>() {
                                            @Override
                                            public void onResponse(Response<com.example.ivan.champy_v2.model.Friend.Friend> response, Retrofit retrofit) {
                                                if (response.isSuccess()) {
                                                    Log.d(TAG, "Status: Sended Friend Request");
                                                    cv.put("name", mContacts.get(position).getName());
                                                    cv.put("photo", mContacts.get(position).getPicture());
                                                    cv.put("user_id", mContacts.get(position).getID());
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
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                        builder.setMessage("Do you want add this user to your friends list?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener)
                                .show();
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

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


        public ViewHolder(View itemView) {
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

            //block = (ImageButton)itemView.findViewById(R.id.imageButtonBlockUser);
            add = (ImageButton)itemView.findViewById(R.id.imageButtonAddUser);


        }

    }

}
