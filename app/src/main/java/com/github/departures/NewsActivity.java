package com.github.departures;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewsActivity extends AppCompatActivity {
    private static RequestQueue requestQueue;

    private String toDisplay = "";
    private TextView textView;
    private TextView errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        requestQueue = Volley.newRequestQueue(this);
        textView = findViewById(R.id.textView2);
        textView.setMovementMethod(new ScrollingMovementMethod());
        errorMsg = findViewById(R.id.textView3);
        apiCall();

        final FloatingActionButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiCall();
            }
        });

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_departures:
                        Intent intent = new Intent(NewsActivity.this, DeparturesActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_routes:
                        intent = new Intent(NewsActivity.this, RoutesActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_news:
                        return true;
                }
                return false;
            }
        });
        navigation.setSelectedItemId(R.id.navigation_news);
    }

    void apiCall() {
        try {
            String url = "https://developer.cumtd.com/api/v2.2/json/getnews?key=806cca7e36284b27ba31bf75ab5ee7a2";
            JsonObjectRequest jsonObjectR = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            errorMsg.setText("");
                            try {
                                callDone(response);
                            } catch (Exception E) {
                                Log.e("Exception", E.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            textView.setText("");
                            Log.e("Error Response", error.toString());
                            errorMsg.setText(getResources().getText(R.string.network_error));
                        }
                    }
            );
            requestQueue.add(jsonObjectR);
        }
        catch(Exception E) {
            E.printStackTrace();
        }
    }

    void callDone(JSONObject response) throws Exception {
        Log.d("response", response.toString());
        JSONArray newsArray = response.getJSONArray("news");
        for (int i = 0; i < 10; i++) {
            JSONObject newsObject = newsArray.getJSONObject(i);
            toDisplay += "<h1>" + newsObject.getString("title") + "</h1>" +
                    "<b>" + newsObject.getString("author") + "</b> " +
                    newsObject.getString("postetd_date") + "<p>" + "    " + newsObject.getString("body") + "</p>";
        }
        textView.setText(Html.fromHtml(toDisplay));
    }

}
