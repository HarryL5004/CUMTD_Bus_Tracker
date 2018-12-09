package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static RequestQueue requestQueue;
    private TextView textView;
    private EditText editText;
    private String toDisplay = "";
    private String stop_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        editText = (EditText) findViewById(R.id.searchBox);
        editText.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);

        textView = (TextView) findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());

        Button button = (Button) findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                editText.clearFocus();
                editText.clearComposingText();
                String input = editText.getText().toString();
                getStopId(input);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_busRoutes:
                        Intent intent = new Intent(MainActivity.this, Bus_Routes.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_favorites:
                        intent = new Intent(MainActivity.this, News.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }
    void getStopId(String stopName) {
        stopName = stopName.toLowerCase().trim().replace(" and ", "+");
        try {
            final String url = "https://developer.cumtd.com/api/v2.2/json/getstopsbysearch?key=806cca7e36284b27ba31bf75ab5ee7a2&query=" + stopName;
            JsonObjectRequest jsonObjectR = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("url", url);
                                Log.d("Response1:", response.toString());
                                idCallDone(response);
                            } catch (Exception E) {
                                Log.e("Exception", E.toString());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error Response", error.toString());
                    textView.setText("Network Error");
                }
                });
            requestQueue.add(jsonObjectR);
        }
        catch(Exception E) {
            E.printStackTrace();
        }
    }
    void idCallDone(JSONObject response) throws Exception {
        JSONArray stopsArray = response.getJSONArray("stops");
        JSONObject stopsObject = stopsArray.getJSONObject(0);
        //textView.setText(stopsObject.getString("stop_name") + stopsObject.getString("stop_id"));
        stop_id = stopsObject.getString("stop_id");
        getDepartureTime(stop_id);
    }
    void getDepartureTime(String id) {
        try {
            Log.d("stop_id:::::", stop_id);
            final String url = "https://developer.cumtd.com/api/v2.2/json/getdeparturesbystop?key=806cca7e36284b27ba31bf75ab5ee7a2&stop_id=" + id;
            JsonObjectRequest jsonObjectR = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("url", url);
                                Log.d("Response2: ", response.toString());
                                callDone(response);
                            } catch (Exception E) {
                                Log.e("Exception", E.toString());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error Response", error.toString());
                    textView.setText("Network Error");
                }
            });
            requestQueue.add(jsonObjectR);
        }
        catch(Exception E) {
            E.printStackTrace();
        }
    }
    void callDone(JSONObject response) throws Exception{
        JSONArray stopsArray = response.getJSONArray("departures");
        JSONObject stopsObject;
        JSONObject routeObject;
        JSONObject tripObject;
        for (int i = 0; i < stopsArray.length(); i++) {
            stopsObject = stopsArray.getJSONObject(i);
            routeObject = stopsObject.getJSONObject("route");
            tripObject = stopsObject.getJSONObject("trip");
            toDisplay += routeObject.getString("route_short_name") + " " + routeObject.getString("route_long_name") +
                        " " + tripObject.getString("trip_headsign") + " Arriving in: " + stopsObject.getString("expected_mins") + "\n";
        }
        textView.setText(toDisplay);
    }
}
