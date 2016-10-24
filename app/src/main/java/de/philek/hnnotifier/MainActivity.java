package de.philek.hnnotifier;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final static String TWEETS = "tweets";
    private ArrayAdapter<String> matchwordAdapter;
    private String url = "ENTER TWITTER URL HERE";
    private String filter = "ENTER SEARCH FILTER HERE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab_Refresh = (FloatingActionButton) findViewById(R.id.fab_Refresh);
        final EditText tf_New_Matchword = (EditText) findViewById(R.id.tf_New_Matchword);
        Button btn_Add_Matchword = (Button) findViewById(R.id.btn_Add_Matchword);
        ListView lv_MatchWords = (ListView) findViewById(R.id.listView_Matchwords);


        final ArrayList<String> matchwordList = new ArrayList<>();
        matchwordAdapter = new ArrayAdapter<>(
                getBaseContext(),
                android.R.layout.simple_list_item_1,
                matchwordList);

        lv_MatchWords.setAdapter(matchwordAdapter);

        btn_Add_Matchword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String matchword = tf_New_Matchword.getText().toString();

                if(!matchword.equals("")) {
                    addMatchword(tf_New_Matchword.getText().toString());
                    tf_New_Matchword.setText("");
                }

            }
        });

        fab_Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!matchwordList.isEmpty()) {
                    new ReadTweetsTask(matchwordList).execute(url, filter);
                } else {
                    Toast.makeText(MainActivity.this, "Matchword-Liste ist leer!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    private void addMatchword(String matchword) {
        matchwordAdapter.add(matchword);
        Toast.makeText(this, "New matchword added", Toast.LENGTH_SHORT).show();
    }


    public class ReadTweetsTask extends AsyncTask<String, String, ArrayList<String>> {

        private List<String> matchwords;

        public ReadTweetsTask(ArrayList<String> matchwords) {
            super();
            for(String s : matchwords) {
                System.out.println(s);
            }
            this.matchwords = matchwords;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            //super.onPostExecute(strings);
            if(strings != null & !strings.isEmpty()) {
                Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
                resultIntent.putExtra(MainActivity.TWEETS, strings);
                startActivity(resultIntent);
            } else {
                Toast.makeText(MainActivity.this, "Keine Tweets gefunden", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> foundTweets = null;

            try {
                System.out.println(params[0]);
                Document doc = Jsoup.connect(params[0]).get();
                System.out.println(params[1]);
                Elements tweets = doc.getElementsByClass(params[1]);

                if(tweets != null & !tweets.isEmpty()) {
                    foundTweets = new ArrayList<String>();

                    for(Element tweet : tweets) {
                        String tweetText = tweet.text();

                        for(String matchword : matchwords) {
                            if(tweetText.matches(".*" + matchword + ".*")) {
                                foundTweets.add(tweetText);
                            }
                        }
                    }
                } else {
                    System.out.println("tweets sind leer!");
                    return null;
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
            System.out.println("found tweets empty: " + foundTweets.isEmpty());
            return foundTweets;
        }
    }
}
