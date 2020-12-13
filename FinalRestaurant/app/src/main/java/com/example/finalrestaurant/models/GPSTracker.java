package com.example.finalrestaurant.models;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class GPSTracker extends Service implements LocationListener {
    private final Context mContext;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    protected LocationManager locationManager;
    // identical to version used during class

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            Log.e("geo tag", "get location");
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled((LocationManager.NETWORK_PROVIDER));
            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.e("Geo tag", "GPS enabled and network enabled false");
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.e("Geo tag", "Network enabled");
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Log.e("Geo tag", "permission failed");
                    } else {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.e("Geo tag", "Network");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.e("geo tag", Double.toString(latitude));

                            }
                        }
                    }
                }
                if (isGPSEnabled) {
                    Log.e("Geo tag", "GPS enabled");
                    if (location == null) {
                        Log.e("Geo tag", "location is null");
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.e("Geo tag", "GPS Enabled");
                        if (locationManager != null) {
                            Log.e("Geo tag","Location manager not null");
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.e("geo tag", "retrieved coords");
                            }else{
                                Log.e("geo tag", "location is null");
                            }
                        }
                    }else{
                        Log.e("Geo tag", String.format("lat:%s,lon%s",latitude,longitude));
                    }
                    Log.e("geo tag", "end of useful part");
                }
            }
        }catch(Exception e){
            Log.e("Geo tag", "wuzzle"+e.getMessage());
            StackTraceElement[] thing= e.getStackTrace();
            for(StackTraceElement elem : thing){
                Log.e("Geo tag",elem.toString());
            }
        }
        return location;
    }
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }else{
            Log.e("My tag", "location is null");
        }
        Log.e("Geo tag special", String.format("long:%s",longitude));
        return longitude;
    }

    public double getLatitude(){

        if(location != null){
            longitude = location.getLatitude();

        }else{
            Log.e("My tag", "location is null");
        }
        return longitude;
    }

    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location){
        this.location = location;

    }

    @Override
    public void onProviderDisabled(String provider){}

    @Override
    public void onProviderEnabled(String provider){}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){}

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }
}
