package com.example.hw8_5;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private final int REQ_CODE_SPEECH = 100;
    private Button speechButton;
    private TextView note;
    private Button searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        note = (TextView) findViewById(R.id.note);
        note.setVisibility(View.INVISIBLE);
        sharedPreferencesEditor = this.getSharedPreferences("results",Context.MODE_PRIVATE).edit();
        sharedPreferencesEditor.putString("response","");
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                SharedPreferences sharedPerf = getSharedPreferences("results",Context.MODE_PRIVATE);
                switch(id){

                    case R.id.home_menu_item:
                        break;
                    case R.id.results_menu_item:
                        if(!sharedPerf.getString("response","").equals("")){
                            Intent intent = new Intent(MainActivity.this, Results.class);
                            startActivity(intent);
                        }
                        break;
                    case R.id.map_menu_item:
                        if(!sharedPerf.getString("response","").equals("")){
                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(intent);
                        }

                        break;
                    case R.id.five_day_menu_item:
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
        speechButton = (Button) findViewById(R.id.speech_btn);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");

                try {
                    startActivityForResult(intent,REQ_CODE_SPEECH);
                }catch(ActivityNotFoundException a){
                    Toast.makeText(getApplicationContext(), "Sorry your device not Supported", Toast.LENGTH_LONG).show();
                }
            }
        });
        searchButton = (Button) findViewById(R.id.search_btn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grabStringText();

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case REQ_CODE_SPEECH:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    search(result.get(0));
                }
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void grabStringText(){
        search(((EditText) findViewById(R.id.searchText)).getText().toString());
    }


    public void search(String inputString){
        Pattern regCityState = Pattern.compile("^ *[a-zA-Z]+[a-zA-Z ]* *");
        Pattern regZip = Pattern.compile("^[0-9]{5}$");
        Pattern regCoord = Pattern.compile("^-?[0-9]+\\.?[0-9]*,-?[0-9]+\\.?[0-9]*");
        String urlPrefix = "https://api.openweathermap.org/data/2.5/weather?";
        String urlSuffix = "&appid=7f5e575bc45a764ca1301ccd64fa81fd";
        if (regZip.matcher(inputString).find()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlPrefix + String.format("zip=%s", inputString) + urlSuffix, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    setInput(response);
                    Log.e("MyTag", "start setInput with zip");

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("MyTag", "didn't parse");

                }
            });
            queue.add(request);
        } else if (regCityState.matcher(inputString).find()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlPrefix + String.format("q=%s", inputString) + urlSuffix, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    setInput(response);
                    Log.e("MyTag", "start setInput with city");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("MyTag", "didn't parse");

                }
            });
            queue.add(request);

        } else {

            GPSTracker gps = new GPSTracker(this);
            Integer  longitude = null;
            Integer  latitude = null;
            if(gps.canGetLocation){
                longitude = (int) gps.getLatitude();
                latitude = (int) gps.getLongitude();
            }
            Log.e("MyTag", longitude.toString());
            if(latitude != null) {
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlPrefix + String.format("lat=%s&lon=%s", latitude.toString(), longitude.toString()) + urlSuffix, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setInput(response);
                        Log.e("MyTag", "start setInput with coords");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MyTag", "didn't parse");

                    }
                });
                queue.add(request);
            }

        }

    }
    void setInput(JSONObject response){
        Log.e("MyTag","start setInput");
        try{
            if(response.getString("cod").equals("404")){
                Log.e("MyTag", "response failed");
                return;
            };

            SharedPreferences.Editor data = sharedPreferencesEditor;
            data.putString("response",data.toString());
            JSONObject wind = response.getJSONObject("wind");
            data.putString("windDirection", getDirection(wind.getString("deg")));
            data.putString("windDegree",wind.getString("deg"));
            data.putString("windSpeed", wind.getString("speed"));
            data.putString("windForce",getForce(wind.getString("speed")));
            JSONArray weather = response.getJSONArray("weather");
            data.putString("cloudiness",weather.getJSONObject(0).getString("description"));
            JSONObject main = response.getJSONObject("main");
            data.putString("pressure",main.getString("pressure"));
            data.putString("humidity", main.getString("humidity"));
            JSONObject sys = response.getJSONObject("sys");
            data.putString("sunrise",sys.getString("sunrise"));
            data.putString("sunset", sys.getString("sunset"));
            JSONObject coords = response.getJSONObject("coord");
            data.putString("lat",coords.getString("lat"));
            data.putString("lon",coords.getString("lon"));
            data.putString("temperature",main.getString("temp"));
            data.apply();
            Log.e("MyTag","end set input");
            note.setVisibility(View.VISIBLE);

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this,"Request failed", Toast.LENGTH_LONG).show();
        }

    }
    public String getForce(String speedString){
        double speed = Double.parseDouble(speedString);
        if(speed < 1){
            return "Calm";
        }
        if(speed <4){
            return "Light Air";
        }
        if(speed <8){
            return "Light Breeze";
        }
        if(speed < 13){
            return "Gentle Breeze";
        }
        if(speed< 19){
            return "Moderate Breeze";
        }
        if(speed< 25){
            return "Fresh Breeze";
        }
        if(speed < 32){
            return "Strong Breeze";
        }
        if(speed <39){
            return "Near Gale";
        }
        if(speed < 47){
            return "Gale";
        }
        if(speed< 55){
            return "Strong Gale";
        }
        if(speed < 64){
            return "Whole Gale";
        }
        if(speed <= 75){
            return "Storm Force Winds";
        }
        return "Hurricane Force Winds";
    }
    public String getDirection(String degString){
        double deg = Double.parseDouble(degString);
        if(deg>= 348.75 || deg <= 11.25){
            return "North";
        }
        if(deg<= 78.75){
            return "North East";
        }
        if(deg<= 101.25){
            return "East";
        }
        if(deg<=168.75){
            return "South East";
        }
        if(deg<=191.25){
            return "South";
        }
        if(deg<=258.75){
            return "South West";
        }
        if(deg<=281.25){
            return "West";
        }
        return "North West";

    }

}