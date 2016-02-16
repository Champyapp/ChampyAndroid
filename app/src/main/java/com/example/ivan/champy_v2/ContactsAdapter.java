package com.example.ivan.champy_v2;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private int selectedPos = 0;

    private List<Friend> mContacts;
    private Context _context;
    CustomItemClickListener listener;
    private final ArrayList<Integer> selected = new ArrayList<>();

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
        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (selected.isEmpty()){
                   selected.add(viewHolder.getAdapterPosition());
               }
               else {
                   int oldSelected = selected.get(0);
                   selected.clear();
                   selected.add(viewHolder.getAdapterPosition());
                   // we do not notify that an item has been selected
                   // because that work is done here.  we instead send
                   // notifications for items to be deselected
                   notifyItemChanged(oldSelected);
               }
               listener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Friend contact = mContacts.get(position);
        Log.i("Selected ", selected.toString());
        if (selected.contains(position)) {
            Log.i("Selected: ", position+" open");

            ImageView img = (ImageView)viewHolder.itemView.findViewById(R.id.imageView5);
            Glide.with(_context)
                    .load(contact.getPicture())
                    .asBitmap()
                    .transform(new CropCircleTransformation(_context))
                    .placeholder(R.mipmap.ic_launcher)
                    .override(80, 80)
                    .dontAnimate()
                    .into(img);

            viewHolder.itemView.findViewById(R.id.friend_pic).setVisibility(View.INVISIBLE);
            viewHolder.itemView.findViewById(R.id.friend_name).setVisibility(View.INVISIBLE);
            viewHolder.itemView.findViewById(R.id.level).setVisibility(View.INVISIBLE);
            viewHolder.itemView.findViewById(R.id.imageView2).setVisibility(View.INVISIBLE);
            viewHolder.itemView.findViewById(R.id.imageView3).setVisibility(View.INVISIBLE);
            viewHolder.itemView.findViewById(R.id.imageView4).setVisibility(View.INVISIBLE);
            viewHolder.itemView.findViewById(R.id.chall).setVisibility(View.INVISIBLE);
            viewHolder.itemView.findViewById(R.id.wins).setVisibility(View.INVISIBLE);
            viewHolder.itemView.findViewById(R.id.total).setVisibility(View.INVISIBLE);

            TextView textView = (TextView)viewHolder.itemView.findViewById(R.id.textView2);
            textView.setText(contact.getName());
            textView.setTextSize(20);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textView3);
            textView.setTextSize(15);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textView4);
            textView.setTextSize(10);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textView5);
            textView.setTextSize(10);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textView6);
            textView.setTextSize(10);


            img = (ImageView)viewHolder.itemView.findViewById(R.id.imageView6);
            img.setImageDrawable(_context.getResources().getDrawable(R.drawable.start_circle_00026));
            img.getLayoutParams().height = 100;
            img.getLayoutParams().width = 100;

            img = (ImageView)viewHolder.itemView.findViewById(R.id.imageView7);
            img.setImageDrawable(_context.getResources().getDrawable(R.drawable.start_circle_00026));
            img.getLayoutParams().height = 100;
            img.getLayoutParams().width = 100;

            img = (ImageView)viewHolder.itemView.findViewById(R.id.imageView8);
            img.setImageDrawable(_context.getResources().getDrawable(R.drawable.start_circle_00026));
            img.getLayoutParams().height = 100;
            img.getLayoutParams().width = 100;


        }
        else {
            Log.i("Selected: ", position+" close");
            ImageView img = (ImageView)viewHolder.itemView.findViewById(R.id.imageView5);
            img.setImageDrawable(null);

            viewHolder.itemView.findViewById(R.id.friend_pic).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.friend_name).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.level).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.imageView3).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.imageView4).setVisibility(View.VISIBLE);

            viewHolder.itemView.findViewById(R.id.chall).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.wins).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.total).setVisibility(View.VISIBLE);

            TextView textView = (TextView)viewHolder.itemView.findViewById(R.id.textView2);
            textView.setText(contact.getName());
            textView.setTextSize(0);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textView3);
            textView.setTextSize(0);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textView4);
            textView.setTextSize(0);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textView5);
            textView.setTextSize(0);

            textView = (TextView)viewHolder.itemView.findViewById(R.id.textView6);
            textView.setTextSize(0);

            img = (ImageView)viewHolder.itemView.findViewById(R.id.imageView6);
            img.setImageDrawable(null);

            img = (ImageView)viewHolder.itemView.findViewById(R.id.imageView7);
            img.setImageDrawable(null);

            img = (ImageView)viewHolder.itemView.findViewById(R.id.imageView8);
            img.setImageDrawable(null);
        }
        // Set item views based on the data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(contact.getName());

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
                .load(R.drawable.challenge)
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
                .override(40,40)
                .into(imageView);

        if (position == 19) Log.i("LAST", "LAST CAT");

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
        }
    }

}
