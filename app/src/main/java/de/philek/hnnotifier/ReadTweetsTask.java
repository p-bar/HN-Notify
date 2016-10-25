package de.philek.hnnotifier;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ReadTweetsTask extends AsyncTask<String, String, ArrayList<String>> {

    private List<String> matchWords;
    private static final String URL = "https://twitter.com/hnfb08?lang=de";
    private static final String FILTER = "dir-ltr";
    private Context context;

    ReadTweetsTask(Context context, List<String> matchwords) {
        super();
        for(String s : matchwords) {
            System.out.println(s);
        }
        this.context = context;
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
            Intent resultIntent = new Intent(context, ResultActivity.class);
            resultIntent.putExtra(MainActivity.TWEETS, strings);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(resultIntent);
        } else {
            Toast.makeText(context, "Keine Tweets gefunden", Toast.LENGTH_SHORT).show();
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

        return foundTweets;
    }
}
