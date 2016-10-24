package de.philek.hnnotifier;

import android.content.Intent;
import android.os.AsyncTask;
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
    private ArrayAdapter<String> matchWordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton fab_Refresh = (FloatingActionButton) findViewById(R.id.fab_Refresh);
        final EditText tf_New_MatchWord = (EditText) findViewById(R.id.tf_New_Matchword);
        Button btn_Add_MatchWord = (Button) findViewById(R.id.btn_Add_Matchword);
        ListView lv_MatchWords = (ListView) findViewById(R.id.listView_Matchwords);


        final ArrayList<String> matchwordList = new ArrayList<>();
        matchWordAdapter = new ArrayAdapter<>(
                getBaseContext(),
                android.R.layout.simple_list_item_1,
                matchwordList);

        lv_MatchWords.setAdapter(matchWordAdapter);

        btn_Add_MatchWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String matchword = tf_New_MatchWord.getText().toString();

                if(!matchword.equals("")) {
                    addMatchword(tf_New_MatchWord.getText().toString());
                    tf_New_MatchWord.setText("");
                }

            }
        });

        fab_Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!matchwordList.isEmpty()) {
                    new ReadTweetsTask(matchwordList).execute();
                } else {
                    Toast.makeText(MainActivity.this, "Matchword-Liste ist leer!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    private void addMatchword(String matchword) {
        matchWordAdapter.add(matchword);
        Toast.makeText(this, "New matchword added", Toast.LENGTH_SHORT).show();
    }


    public class ReadTweetsTask extends AsyncTask<String, String, ArrayList<String>> {

        private List<String> matchWords;
        private static final String URL = "https://twitter.com/hnfb08?lang=de";
        private static final String FILTER = "dir-ltr";

        private ReadTweetsTask(ArrayList<String> matchwords) {
            super();
            for(String s : matchwords) {
                System.out.println(s);
            }
            this.matchWords = matchwords;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            //super.onPostExecute(strings);
            if(strings != null && !strings.isEmpty()) {
                Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
                resultIntent.putExtra(MainActivity.TWEETS, strings);
                startActivity(resultIntent);
            } else {
                Toast.makeText(MainActivity.this, "Keine Tweets gefunden", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> foundTweets = new ArrayList<>();

            try {
                Document doc = Jsoup.connect(URL).get();
                Elements tweets = doc.getElementsByClass(FILTER);

                for(Element element: tweets){

                    for(String matchWord: matchWords){

                        if(element.html().contains(matchWord)){
                            foundTweets.add(element.text());
                        }

                    }

                }


            } catch(IOException e) {
                e.printStackTrace();
            }
            if(foundTweets != null){
                System.out.println("found tweets empty: " + foundTweets.isEmpty());
            }

            return foundTweets;
        }
    }
}
