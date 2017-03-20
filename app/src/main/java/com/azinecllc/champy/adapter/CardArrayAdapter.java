package com.azinecllc.champy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.model.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * @autor SashaKhyzhun
 * Created on 3/20/17.
 */

public class CardArrayAdapter extends ArrayAdapter<Card> {

    private static final String TAG = "CardArrayAdapter";
    private List<Card> cardList = new ArrayList<Card>();

    private static class CardViewHolder {
        TextView challengeName;
        TextView challengeStreak;
        TextView challengeDay;
        TextView completePercent;
    }

    public CardArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Card Card) {
        cardList.add(Card);
        super.add(Card);
    }

    @Override
    public int getCount() {
        return this.cardList.size();
    }

    @Override
    public Card getItem(int index) {
        return this.cardList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CardViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_card_list, parent, false);
            viewHolder = new CardViewHolder();
            viewHolder.challengeName = (TextView) row.findViewById(R.id.tv_challenge_name);
            viewHolder.challengeStreak = (TextView) row.findViewById(R.id.text_view_streak_n);
            viewHolder.challengeDay = (TextView) row.findViewById(R.id.text_view_day_n);
            viewHolder.completePercent = (TextView) row.findViewById(R.id.tv_percent_complete);
            row.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder) row.getTag();
        }

        Card Card = getItem(position);
        viewHolder.challengeName.setText(Card.getChallengeName());
        viewHolder.challengeDay.setText(Card.getChallengeDay());

        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}