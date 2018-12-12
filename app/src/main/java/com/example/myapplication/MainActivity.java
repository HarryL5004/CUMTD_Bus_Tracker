package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static RequestQueue requestQueue;
    private EditText editText;
    private String toDisplay = "";
    private String stop_id = "";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        editText = (EditText) findViewById(R.id.searchBox);
        editText.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);

        Button button = (Button) findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                editText.clearFocus();
                String input = editText.getText().toString();
                getStopId(input);
                //getDepartureTime(input);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

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
                    Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
                }
                });
            /*jsonObjectR.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
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
                    //toast.show();
                }
            });
            jsonObjectR.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        String[][] myDataset = new String[stopsArray.length()][3];
        for (int i = 0; i < stopsArray.length(); i++) {
            stopsObject = stopsArray.getJSONObject(i);
            routeObject = stopsObject.getJSONObject("route");
            tripObject = stopsObject.getJSONObject("trip");
            myDataset[i][0] = "   " + stopsObject.getString("headsign") + " " + tripObject.getString("trip_headsign")
                    + "\n" + "       " + stopsObject.getString("expected_mins") + " mins";
            myDataset[i][1] = "#" + routeObject.getString("route_color");
            myDataset[i][2] = "#" + routeObject.getString("route_text_color");
        }
        //textView.setText(toDisplay);
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }
}
