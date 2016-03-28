package com.example.ivan.champy_v2;

import android.content.Context;
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
public class FriendsAdapter extends
        RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    int selectedPos = 0;
    final private String API_URL = "http://46.101.213.24:3007";
    final private String TAG = "myLogs";
    private List<Friend> mContacts;
    private Context _context;
    CustomItemClickListener listener;
    ArrayList<Integer> selected = new ArrayList<>();

    private Other other = new Other(new ArrayList<Friend>());

    // Pass in the contact array into the constructor
    public FriendsAdapter(List<Friend> contacts, Context context, CustomItemClickListener customItemClickListener) {
        mContacts = contacts;
        _context = context;
        this.listener = customItemClickListener;
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
                    // notifyItemChanged(viewHolder.getAdapterPosition());

                }
            }
        });

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final FriendsAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final Friend contact = mContacts.get(position);
        Log.i("Selected", "" + selected.contains(position));
        if (selected.contains(position)) {
            Log.i("Selected: ", position + " open");

            ImageView img = (ImageView)viewHolder.itemView.findViewById(R.id.imageView5);
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
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageView6));

            Glide.with(_context)
                    .load(R.drawable.start_circle_00026)
                    .placeholder(R.mipmap.ic_launcher)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageView7));

            Glide.with(_context)
                    .load(R.drawable.start_circle_00026)
                    .placeholder(R.mipmap.ic_launcher)
                    .into((ImageView)viewHolder.itemView.findViewById(R.id.imageView8));

            TextView textView = (TextView)viewHolder.itemView.findViewById(R.id.textView2);
            textView.setText(contact.getName());
            Typeface typeFace = Typeface.createFromAsset(_context.getAssets(), "fonts/bebasneue.ttf");
            textView.setTypeface(typeFace);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textView5);
            textView.setTypeface(typeFace);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textView6);
            textView.setTypeface(typeFace);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textView7);
            textView.setTypeface(typeFace);

            SessionManager sessionManager = new SessionManager(_context);
            HashMap<String, String> champy = sessionManager.getChampyOptions();




            textView = (TextView)viewHolder.itemView.findViewById(R.id.info_chall);
            textView.setText(champy.get("challenges"));
            textView = (TextView)viewHolder.itemView.findViewById(R.id.info_wins);
            textView.setText(champy.get("wins"));
            textView = (TextView)viewHolder.itemView.findViewById(R.id.info_total);
            textView.setText(champy.get("total"));
            textView = (TextView)viewHolder.itemView.findViewById(R.id.info_level);
            textView.setText("Level "+champy.get("level")+" Champy");

            viewHolder.itemView.findViewById(R.id.info).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.simple).setVisibility(View.GONE);

        }
        else {
            Log.i("Selected: ", position + " close");
            viewHolder.itemView.findViewById(R.id.info).setVisibility(View.GONE);
            viewHolder.itemView.findViewById(R.id.simple).setVisibility(View.VISIBLE);

        }

        // Set item views based on the data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(contact.getName());

        viewHolder.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        if (response.isSuccess()){
                            Log.d(TAG, "Status: Removed ");
                        } else Log.d(TAG, "Status: "+response.toString());
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
                sessionManager.setRefreshFriends("true");
                mContacts.remove(position);
                notifyItemRemoved(position);
                selected.clear();
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

        imageView = viewHolder.mchallenges;
        Glide.with(_context)
                .load(R.drawable.challenges)
                .override(25, 25)
                .into(imageView);
        imageView = viewHolder.mwins;
        Glide.with(_context)
                .load(R.drawable.wins)
                .override(25, 25)
                .into(imageView);
        imageView = viewHolder.mtotal;
        Glide.with(_context)
                .load(R.drawable.total)
                .override(25,25)
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
            challenges = (ImageView) itemView.findViewById(R.id.imageView2);
            wins = (ImageView) itemView.findViewById(R.id.imageView3);
            total = (ImageView) itemView.findViewById(R.id.imageView4);
            dop = (ImageView) itemView.findViewById(R.id.imageView5);

            mchallenges = (ImageView) itemView.findViewById(R.id.imageView9);
            mwins = (ImageView) itemView.findViewById(R.id.imageView10);
            mtotal = (ImageView) itemView.findViewById(R.id.imageView11);

            simple = (RelativeLayout)itemView.findViewById(R.id.simple);
            info = (RelativeLayout)itemView.findViewById(R.id.info);

            block = (ImageButton)itemView.findViewById(R.id.imageButton2);
            add = (ImageButton)itemView.findViewById(R.id.imageButton3);


        }

    }

}
