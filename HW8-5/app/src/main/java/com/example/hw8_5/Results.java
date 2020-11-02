package com.example.hw8_5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.Date;

public class Results extends AppCompatActivity {
    private TextView windTextView;
    private  TextView cloudinessTextView;
    private  TextView pressureTextView;
    private  TextView humidityTextView;
    private  TextView sunriseTextView;
    private  TextView sunsetTextView;
    private  TextView geoCordsTextView;
    private  TextView temperatureTextView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        windTextView = (TextView) findViewById(R.id.windTextView);
        cloudinessTextView = (TextView) findViewById(R.id.cloudinessTextView);
        pressureTextView = (TextView) findViewById(R.id.pressureTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        sunriseTextView = (TextView) findViewById(R.id.sunriseTextView);
        sunsetTextView = (TextView) findViewById(R.id.sunsetTextView);
        geoCordsTextView = (TextView) findViewById(R.id.geoCordsTextView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_results);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.nv_results);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent;
                switch (id) {
                    case R.id.home_menu_item:
                        intent = new Intent(Results.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.results_menu_item:
                        break;
                    case R.id.map_menu_item:
                        intent = new Intent(Results.this, MapsActivity.class);
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
        setViews();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setViews(){

        SharedPreferences sharedPref = getSharedPreferences("results",Context.MODE_PRIVATE);
        Double temperature = Double.parseDouble(sharedPref.getString("temperature",""));
        temperature = Math.floor((temperature-273.15)*9/5+32);
        temperatureTextView.setText(temperature.toString() + " °F");
        geoCordsTextView.setText(sharedPref.getString("lat", "")+", "+sharedPref.getString("lon", ""));
        Long sunset = Long.parseLong(sharedPref.getString("sunset", ""))*1000;
        Date dateSunset = new Date(sunset);
        Long sunrise = Long.parseLong(sharedPref.getString("sunrise", ""))*1000;
        Date dateSunrise = new Date(sunrise);
        sunsetTextView.setText(dateSunset.getHours()+":"+dateSunset.getMinutes());
        sunriseTextView.setText(dateSunrise.getHours()+":"+dateSunrise.getMinutes());
        humidityTextView.setText(sharedPref.getString("humidity","")+"%");
        pressureTextView.setText(sharedPref.getString("pressure", ""+" hPa"));
        cloudinessTextView.setText(sharedPref.getString("cloudiness", ""));
        windTextView.setText(sharedPref.getString("windForce", "")+ ", "+sharedPref.getString("windSpeed", "")+" mph, "+sharedPref.getString("windDirection","") + "( " + sharedPref.getString("windDegree", "")+ ")");



    }
}