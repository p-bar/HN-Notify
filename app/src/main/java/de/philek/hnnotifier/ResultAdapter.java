package de.philek.hnnotifier;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Philipp on 24.10.2016.
 */

public class ResultAdapter extends ArrayAdapter<String> {

    public ResultAdapter(Context context, ArrayList<String> results){
        super(context, 0, results);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String text = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.result, parent, false);
        }

        TextView resultText = (TextView)convertView.findViewById(R.id.resultText);

        resultText.setText(text);
        return convertView;
    }
}
