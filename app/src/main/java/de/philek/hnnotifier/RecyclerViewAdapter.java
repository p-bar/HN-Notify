package de.philek.hnnotifier;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alexander on 25.10.2016.
 */

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.TweetViewHolder> {

    static class TweetViewHolder extends RecyclerView.ViewHolder {
        CardView tweetCard;
        TextView tweetText;

        TweetViewHolder(View itemView) {
            super(itemView);

            tweetCard = (CardView) itemView.findViewById(R.id.tweetCard);
            tweetText = (TextView) itemView.findViewById(R.id.tweetText);
        }
    }

    private List<String> tweets;

    RecyclerViewAdapter(List<String> tweets) {
        this.tweets = tweets;
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tweet_card, viewGroup, false);
        return new TweetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TweetViewHolder tweetViewHolder, int i) {
        tweetViewHolder.tweetText.setText(tweets.get(i));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
