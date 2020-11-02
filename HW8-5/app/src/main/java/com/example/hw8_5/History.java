package com.example.hw8_5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class History extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    List<String> history;
    ListView simpleList;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_history);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.Open, R.string.Close);
        history = new ArrayList<String>();
        simpleList = (ListView) findViewById(R.id.listView);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.nv_history);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent;
                switch(id){

                    case R.id.home_menu_item:
                        intent = new Intent(History.this, MainActivity.class);
                        break;
                    case R.id.results_menu_item:
                        intent = new Intent(History.this, Results.class);
                        startActivity(intent);

                        break;
                    case R.id.map_menu_item:
                        intent = new Intent(History.this, MapsActivity.class);
                            startActivity(intent);
                        break;
                    case R.id.five_day_menu_item:
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
        SharedPreferences sharedPref = getSharedPreferences("results", Context.MODE_PRIVATE);
        String lon = sharedPref.getString("lon","20");
        String lat = sharedPref.getString("lat","20");
        Date date = new Date();
        Long root = date.getTime()/1000;


        RequestQueue queue = Volley.newRequestQueue(this);

        for(int i=0; i < 5; i++){
            Long time = root- i*86400;
            queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, String.format("https://api.openweathermap.org/data/2.5/onecall/timemachine?lat=%s&lon=%s&dt=%s&appid=%s",lat,lon,time.toString(),"7f5e575bc45a764ca1301ccd64fa81fd"), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    setInput(response);
                    Log.e("MyTag", "response parsed");

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("MyTag", "didn't parse");

                }
            });
            queue.add(request);
        }
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.layoutlistviewitem,history);
        simpleList.setAdapter(arrayAdapter);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setInput(JSONObject response){
        try{
            JSONObject current = response.getJSONObject("current");
            String temp = current.getString("temp");
            Double temperature = Double.parseDouble(temp);
            temperature = Math.floor((temperature-273.15)*9/5+32);
            temp = temperature.toString() + " °F";
            String pressure = current.getString("pressure")+" hPa";
            String humidity = current.getString("humidity")+"%";
            JSONArray weather = current.getJSONArray("weather");
            String cloudiness = weather.getJSONObject(0).getString("description");
            String output = temp+ ", "+ pressure +", "+ humidity+ ", "+cloudiness;
            history.add(output);
            arrayAdapter.notifyDataSetChanged();


        }catch (Exception e){
            Toast.makeText(this,"WOOPS",Toast.LENGTH_LONG).show();
        }


    }

}