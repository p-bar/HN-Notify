package de.philek.hnnotifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class CheckTwitterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, MODE_PRIVATE);
        String filters = prefs.getString(MainActivity.PREF_FILTER, null);
        if(filters != null){
            new ReadTweetsTask(context, Arrays.asList(filters.split(";"))).execute();
        }



    }
}
