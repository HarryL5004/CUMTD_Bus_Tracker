package com.example.myapplication;

import android.app.SearchManager;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout layout;
    ConstraintSet set = new ConstraintSet();
    ConstraintSet setSearch = new ConstraintSet();
    boolean state = true;

    Toolbar actionBar;

    RecyclerView resultList;
    RecyclerView.Adapter resultAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSearch.clone(this, R.layout.activity_main_search);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout_main);
        set.clone(layout);

        actionBar = findViewById(R.id.toolbar_main);
        setSupportActionBar(actionBar);

        // TO-DO: Initialize resultAdapter.
        resultList = findViewById(R.id.result_list);
        // resultList.setAdapter(resultAdapter);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // TO-DO: Call search() to retrieve results and show them in resultList.
        }

        try {
            findViewById(R.id.search_view).getVisibility();
        } catch (Exception e) {
            Log.d("EditText", e.toString());
        }

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                toggleSearch();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bookmarks:
                return true;

            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    ArrayList search(final String query) {
        String searchQuery = query;
        try {
            char c = query.charAt(0);
        } catch (NullPointerException e) {
            searchQuery = "";
        }
        ArrayList<View> resultList = new ArrayList<>();
        // TO-DO: Use the method in the dedicated class to retrieve search results.
        return resultList;
    }

    void toggleSearch() {
        TransitionManager.beginDelayedTransition(layout);
        if (state = !state) {
            set.applyTo(layout);
            actionBar.setAlpha(1);
        } else {
            setSearch.applyTo(layout);
            actionBar.setAlpha(0);
        }
    }
}
