package com.example.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

public class News extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        final TextView textView = (TextView) findViewById(R.id.textView);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Intent intent = new Intent(News.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_busRoutes:
                        intent = new Intent(News.this, Bus_Routes.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_favorites:
                        return true;
                }
                return false;
            }
        });
    }

    void apiCall() {
        try {
            String url = "https://developer.cumtd.com/api/2.2/json/getnews?key=806cca7e36284b27ba31bf75ab5ee7a2";
            JsonObjectRequest jsonObjectR = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callDone(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error Response", error.toString());
                        }
                    }
            );
        }
        catch(Exception E) {
            E.printStackTrace();
        }
    }

    void callDone(JSONObject response) {
        Log.d("response", response.toString());
    }

}
