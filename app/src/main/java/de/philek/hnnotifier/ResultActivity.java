package de.philek.hnnotifier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);

        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());

        RecyclerViewAdapter rv_adapter = new RecyclerViewAdapter(tweets);
        rv.setAdapter(rv_adapter);
    }
}
