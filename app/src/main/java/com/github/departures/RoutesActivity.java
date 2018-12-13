package com.github.departures;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class RoutesActivity extends AppCompatActivity {
    private static RequestQueue requestQueue;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EditText editText;
    private String stop_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        requestQueue = Volley.newRequestQueue(this);

        editText = findViewById(R.id.searchBox);
        editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

        Button button = findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                editText.clearFocus();
                String input = editText.getText().toString();
                getStopId(input);
            }
        });

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /*DividerItemDecoration itemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);*/

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Intent intent = new Intent(RoutesActivity.this, DeparturesActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_busRoutes:
                        return true;
                    case R.id.navigation_favorites:
                        intent = new Intent(RoutesActivity.this, NewsActivity.class);
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
                    Toast.makeText(getApplicationContext(),
                            getResources().getText(R.string.network_error),
                            Toast.LENGTH_LONG).show();
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
        stop_id = stopsObject.getString("stop_id");
        getRoutes(stop_id);
    }

    void getRoutes(String stop_id) {
        try {
            final String url = "https://developer.cumtd.com/api/v2.2/json/getroutesbystop?key=806cca7e36284b27ba31bf75ab5ee7a2&stop_id=" + stop_id;
            JsonObjectRequest jsonObjectR = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("url", url);
                                Log.d("Response2:", response.toString());
                                callDone(response);
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
    void callDone(JSONObject response) throws Exception{
        JSONArray stopsArray = response.getJSONArray("routes");
        JSONObject routeObject;
        String[][] myData = new String[stopsArray.length()][3];
        for (int i = 0; i < stopsArray.length(); i++) {
            routeObject = stopsArray.getJSONObject(i);
            myData[i][0] = "   " + routeObject.getString("route_short_name") + "  " + routeObject.getString("route_long_name");
            myData[i][1] = "#" + routeObject.getString("route_color");
            myData[i][2] = "#" + routeObject.getString("route_text_color");
        }
        mAdapter = new MyAdapter(myData);
        mRecyclerView.setAdapter(mAdapter);
    }
}
