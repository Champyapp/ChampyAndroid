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

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by ivan on 05.02.16.
 */
public class ContactsAdapter extends
        RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    int selectedPos = 0;
    private List<Friend> mContacts;
    private Context _context;
    CustomItemClickListener listener;
    ArrayList<Integer> selected = new ArrayList<>();

    private Other other = new Other(new ArrayList<Friend>());

    // Pass in the contact array into the constructor
    public ContactsAdapter(List<Friend> contacts, Context context, CustomItemClickListener customItemClickListener) {
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

        TextView textView = (TextView)contactView.findViewById(R.id.friend_name);
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
    public void onBindViewHolder(ContactsAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        Friend contact = mContacts.get(position);
        Log.i("Selected", ""+selected.contains(position));
        if (selected.contains(position)) {
            Log.i("Selected: ", position + " open");

            ImageView img = (ImageView)viewHolder.itemView.findViewById(R.id.imageView5);
            Glide.with(_context)
                    .load(contact.getPicture())
                    .asBitmap()
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

            nameTextView = (TextView) itemView.findViewById(R.id.friend_name);
            friendImage = (ImageView) itemView.findViewById(R.id.friend_pic);
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


        }

    }

}
