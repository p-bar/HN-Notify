package de.philek.hnnotifier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        List<String> tweets = intent.getStringArrayListExtra(MainActivity.TWEETS);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ListAdapter adapter = new ResultAdapter(getBaseContext(), (ArrayList<String>)tweets);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        View resultContent = inflater.inflate(R.layout.content_result, null);

        resolveTweets(tweets);
    }



    private void resolveTweets(List<String> tweets) {
        if(tweets == null) Toast.makeText(this, "tweets = null", Toast.LENGTH_SHORT).show();
        if(tweets.isEmpty()) Toast.makeText(this, "tweets sind leer", Toast.LENGTH_SHORT).show();
        for(String tweet : tweets) {
            CardView tweetCard = new CardView(this);
            TextView cardContent = new TextView(this);
            cardContent.setText(tweet);

            tweetCard.addView(cardContent);
        }
    }
}
