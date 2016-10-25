package de.philek.hnnotifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public final static String TWEETS = "tweets";
    private ArrayAdapter<String> matchWordAdapter;
    public static final String PREFERENCES_NAME = "HN-Notify";
    public static final String PREF_FILTER = "filter";

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
                            addMatchWord(tf_New_MatchWord.getText().toString());
                            tf_New_MatchWord.setText("");
                        }

                    }
                });

        fab_Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetworkAvailable()){
                    Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!matchwordList.isEmpty()) {
                    new ReadTweetsTask(getApplicationContext(), matchwordList).execute();
                } else {
                    Toast.makeText(MainActivity.this, "Matchword-Liste ist leer!", Toast.LENGTH_LONG).show();
                }

            }
        });

        //set alarm at specific time
        AlarmManager alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), CheckTwitterReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        10 * 1000, alarmIntent);

// setRepeating() lets you specify a precise custom interval--in this case,
// 1 day
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
               //AlarmManager.IN, alarmIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //load list items from shared preferences
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String filters = prefs.getString(PREF_FILTER, null);
        matchWordAdapter.clear();
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
            filterBuilder.append(matchWordAdapter.getItem(i));
            filterBuilder.append(";");
        }
        SharedPreferences sharedpreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(PREF_FILTER, filterBuilder.toString());
        editor.apply();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void addMatchWord(String matchWord) {
        matchWordAdapter.remove(matchWord);
        matchWordAdapter.add(matchWord);
    }


}
