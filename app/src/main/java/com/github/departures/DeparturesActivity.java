package com.github.departures;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

public class DeparturesActivity extends AppCompatActivity {
    private static RequestQueue requestQueue;
    private EditText editText;
    private String stop_id = "";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departures);
        requestQueue = Volley.newRequestQueue(this);

        editText = findViewById(R.id.searchBox);
        editText.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (v.getId() == R.id.searchBox && actionId == EditorInfo.IME_ACTION_SEARCH) {
                    v.clearFocus();
                    getStopId(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

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
                    case R.id.navigation_departures:
                        return true;
                    case R.id.navigation_routes:
                        Intent intent = new Intent(DeparturesActivity.this, RoutesActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_news:
                        intent = new Intent(DeparturesActivity.this, NewsActivity.class);
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
        JSONArray stopsArray = response.getJSONArray("departures");
        JSONObject stopsObject;
        JSONObject routeObject;
        JSONObject tripObject;
        String[][] myData = new String[stopsArray.length()][3];
        for (int i = 0; i < stopsArray.length(); i++) {
            stopsObject = stopsArray.getJSONObject(i);
            routeObject = stopsObject.getJSONObject("route");
            tripObject = stopsObject.getJSONObject("trip");
            myData[i][0] = stopsObject.getString("headsign") + " (" + tripObject.getString("trip_headsign") + ")"
                    + "\n" + "    " + stopsObject.getString("expected_mins");
            myData[i][0] += stopsObject.getInt("expected_mins") < 2 ? " min" : " mins";
            myData[i][1] = "#" + routeObject.getString("route_color");
            myData[i][2] = "#" + routeObject.getString("route_text_color");
        }
        mAdapter = new MyAdapter(myData);
        mRecyclerView.setAdapter(mAdapter);
    }
}
