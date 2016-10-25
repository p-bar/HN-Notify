package de.philek.hnnotifier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
    private static final String PREFERENCES_NAME = "HN-Notify";
    private static final String PREF_FILTER = "filter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final FloatingActionButton fab_Refresh = (FloatingActionButton) findViewById(R.id.fab_Refresh);
        final EditText tf_New_MatchWord = (EditText) findViewById(R.id.tf_New_Matchword);
        Button btn_Add_MatchWord = (Button) findViewById(R.id.btn_Add_Matchword);
        final ListView lv_MatchWords = (ListView) findViewById(R.id.listView_Matchwords);


        final ArrayList<String> matchwordList = new ArrayList<>();
        matchWordAdapter = new ArrayAdapter<>(
                getBaseContext(),
                android.R.layout.simple_list_item_1,
                matchwordList);

        lv_MatchWords.setAdapter(matchWordAdapter);
        lv_MatchWords.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, final View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {

                menu.setHeaderTitle(R.string.options);
                MenuItem item = menu.add(0, v.getId(), 0, R.string.delete);
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

                        //  info.position will give the index of selected item
                        int intIndexSelected=info.position;
                        if(item.getTitle()== getString(R.string.delete))
                        {
                            matchWordAdapter.remove((String)lv_MatchWords.getAdapter().getItem(intIndexSelected));
                            return true;
                        }
                        else{
                            return false;
                        }
                    }
                });

            }
        });

                btn_Add_MatchWord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String matchword = tf_New_MatchWord.getText().toString();

                        if (!matchword.isEmpty()) {
                            addMatchword(tf_New_MatchWord.getText().toString());
                            tf_New_MatchWord.setText("");
                        }

                    }
                });

        fab_Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetworkAvailable()){
                    Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                    System.out.println("no internet connection");
                    return;
                }
                if(!matchwordList.isEmpty()) {
                    new ReadTweetsTask(matchwordList).execute();
                } else {
                    Toast.makeText(MainActivity.this, "Matchword-Liste ist leer!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("RESUME!!!!");
        //load list items from shared preferences
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String filters = prefs.getString(PREF_FILTER, null);
        if(filters != null){
            String[] allFilters = filters.split(";");
            for(String f: allFilters){
                matchWordAdapter.add(f);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //serialize list items into shared preferences
        StringBuilder filterBuilder = new StringBuilder();
        for(int i = 0; i < matchWordAdapter.getCount(); i++){
            filterBuilder.append(matchWordAdapter.getItem(i)+";");
        }
        SharedPreferences sharedpreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(PREF_FILTER, filterBuilder.toString());
        editor.commit();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void addMatchword(String matchword) {
        matchWordAdapter.remove(matchword);
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
