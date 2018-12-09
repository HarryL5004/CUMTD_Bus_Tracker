package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Bus_Routes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_routes);

        Button button = (Button) findViewById(R.id.search);
        final EditText input = (EditText) findViewById(R.id.searchBox);
        input.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);

        final TextView textView = (TextView) findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                textView.setText(input.getText());
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Intent intent = new Intent(Bus_Routes.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_busRoutes:
                        return true;
                    case R.id.navigation_favorites:
                        intent = new Intent(Bus_Routes.this, News.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }
}
