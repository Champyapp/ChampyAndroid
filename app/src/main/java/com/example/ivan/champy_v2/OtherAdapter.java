package com.example.ivan.champy_v2;

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
 * Отвечает за каждый раздел в friends (friends, pending, others)
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

        TextView tvName = (TextView)contactView.findViewById(R.id.name);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/bebasneue.ttf");
        tvName.setTypeface(typeFace);


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
    public void onBindViewHolder(OtherAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final Friend contact = mContacts.get(position);
        Log.i("Selected", "" + selected.contains(position));
        if (selected.contains(position)) {
            Log.i("Selected: ", position + " open");

            ImageView img = (ImageView)viewHolder.itemView.findViewById(R.id.imageViewUserAvatar);
            Glide.with(_context)
                    .load(contact.getPicture())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transform(new CropCircleTransformation(_context))
                    .placeholder(R.mipmap.ic_launcher)
                    .override(80, 80)
                    .dontAnimate()
                    .into(img);

            Glide.with(_context)
                    .load(R.drawable.start_circle_00026)
                    .placeholder(R.mipmap.ic_launcher)
                    .into((ImageView) viewHolder.itemView.findViewById(R.id.imageViewBgForCircleChall));

            Glide.with(_context)
                    .load(R.drawable.start_circle_00026)
                    .placeholder(R.mipmap.ic_launcher)
                    .into((ImageView) viewHolder.itemView.findViewById(R.id.imageViewBgForCircleWins));

            Glide.with(_context)
                    .load(R.drawable.start_circle_00026)
                    .placeholder(R.mipmap.ic_launcher)
                    .into((ImageView) viewHolder.itemView.findViewById(R.id.imageViewBgForCircleTotal));


            //-------------------------------- Take username ---------------------------------//
            TextView textViewScoreChallenges = (TextView)viewHolder.itemView.findViewById(R.id.textViewScoreChallenges);
            textViewScoreChallenges.setText(contact.getName());

            //----------------------------------- Typeface -----------------------------------//
            Typeface typeFace = Typeface.createFromAsset(_context.getAssets(), "fonts/bebasneue.ttf");
            textViewScoreChallenges.setTypeface(typeFace);

            //--------------------------------- Simple text ----------------------------------//
            TextView tvChallenges = (TextView)viewHolder.itemView.findViewById(R.id.textViewChallenges);
            tvChallenges.setTypeface(typeFace);

            TextView tvWins = (TextView)viewHolder.itemView.findViewById(R.id.textViewWins);
            tvWins.setTypeface(typeFace);

            TextView tvTotal = (TextView)viewHolder.itemView.findViewById(R.id.textViewTotal);
            tvTotal.setTypeface(typeFace);

            //----------------------------------- Session ------------------------------------//
            SessionManager sessionManager = new SessionManager(_context);
            HashMap<String, String> champy = sessionManager.getChampyOptions();

            //--------------------------------- Counters view --------------------------------//
            TextView tvChallengesInfo = (TextView)viewHolder.itemView.findViewById(R.id.info_chall);
            tvChallengesInfo.setText(contact.getmChallenges());

            TextView tvWinsInfo = (TextView)viewHolder.itemView.findViewById(R.id.info_wins);
            tvWinsInfo.setText(contact.getmWins());

            TextView tvTotalInfo = (TextView)viewHolder.itemView.findViewById(R.id.info_total);
            tvTotalInfo.setText(contact.getmTotal());

            TextView tvUserLevel = (TextView)viewHolder.itemView.findViewById(R.id.textViewUserLevel);
            tvUserLevel.setText("Level "+contact.getmLevel()+" Champy");

            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.row_friends_list).setVisibility(View.GONE);

        }
        else {
            Log.i("Selected: ", position + " close");
            viewHolder.itemView.findViewById(R.id.row_friends_list_open).setVisibility(View.GONE);
            viewHolder.itemView.findViewById(R.id.row_friends_list).setVisibility(View.VISIBLE);

        }
        // Set item views based on the data model
        TextView tvUserName = viewHolder.nameTextView;
        tvUserName.setText(contact.getName());

        // button block user in All pages
        viewHolder.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isInternetAvailable(activity)) {
                    mContacts.remove(position);
                    notifyItemRemoved(position);
                    selected.clear();
                } else {
                    Toast.makeText(activity, "No Internet Connection!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // button add user in All pages
        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isInternetAvailable(activity)) {

                    final SessionManager sessionManager = new SessionManager(_context);
                    HashMap<String, String> user = new HashMap<>();
                    user = sessionManager.getUserDetails();
                    final String token = user.get("token");
                    Log.i("stat", "token: " + token);
                    final String id = user.get("id");
                    String friend = mContacts.get(position).getID();
                    Log.d(TAG, "RefreshPending: " + sessionManager.getRefreshPending());
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
                    } else if (friend == id) {
                        Toast.makeText(_context, "This user has not installed Champy", Toast.LENGTH_SHORT).show();
                    } else {
                        // dialog "Do you want add this user to your friends list?"
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
                                        DBHelper dbHelper = new DBHelper(_context);
                                        final SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        final ContentValues cv = new ContentValues();

                                        com.example.ivan.champy_v2.interfaces.Friends friends = retrofit.create(Friends.class);
                                        Log.d(TAG, "Status: " + id + " " + friend);
                                        sessionManager.setRefreshPending("true");

                                        Log.d(TAG, "RefreshPending: " + sessionManager.getRefreshPending());

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
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(80, 80)
                .dontAnimate()
                .into(imageView);
        // ----------------- Close friend row ----------------- //
        // CHALLENGES
        imageView = viewHolder.challenges;
        Glide.with(_context)
                .load(R.drawable.challenges)
                .override(40, 40)
                .into(imageView);
        // WINS
        imageView = viewHolder.wins;
        Glide.with(_context)
                .load(R.drawable.wins)
                .override(40, 40)
                .into(imageView);
        // TOTAL
        imageView = viewHolder.total;
        Glide.with(_context)
                .load(R.drawable.total)
                .override(40, 40)
                .into(imageView);

        // ----------------- Open friend row ------------------ //
        // CHALLENGES
        imageView = viewHolder.mchallenges;
        Glide.with(_context)
                .load(R.drawable.challenges)
                .override(40, 40)
                .into(imageView);
        // WINS
        imageView = viewHolder.mwins;
        Glide.with(_context)
                .load(R.drawable.wins)
                .override(40, 40)
                .into(imageView);
        // TOTAL
        imageView = viewHolder.mtotal;
        Glide.with(_context)
                .load(R.drawable.total)
                .override(40, 40)
                .into(imageView);

        SessionManager sessionManager = new SessionManager(_context);
        HashMap<String, String> champy = sessionManager.getChampyOptions();

        tvUserName = (TextView)viewHolder.itemView.findViewById(R.id.chall);
        tvUserName.setText(contact.getmChallenges());
        tvUserName = (TextView)viewHolder.itemView.findViewById(R.id.in_progress);
        tvUserName.setText(contact.getmWins());
        tvUserName = (TextView)viewHolder.itemView.findViewById(R.id.total);
        tvUserName.setText(contact.getmTotal());
        tvUserName = (TextView)viewHolder.itemView.findViewById(R.id.level);
        tvUserName.setText("Level " + contact.getmLevel()+ " Champy");

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

            mchallenges = (ImageView) itemView.findViewById(R.id.imageView9);
            mwins = (ImageView) itemView.findViewById(R.id.imageView10);
            mtotal = (ImageView) itemView.findViewById(R.id.imageView11);

            simple = (RelativeLayout)itemView.findViewById(R.id.row_friends_list);
            info = (RelativeLayout)itemView.findViewById(R.id.row_friends_list_open);

            block = (ImageButton)itemView.findViewById(R.id.imageButtonBlockUser);
            add = (ImageButton)itemView.findViewById(R.id.imageButtonAddUser);


        }

    }

}
