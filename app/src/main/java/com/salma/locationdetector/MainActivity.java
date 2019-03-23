package com.salma.locationdetector;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSON_CODE = 0;
    Button getLocationBtn;
    Button sendLocationBtn;
    Button showMapBtn;
    MyLocationListener myLocationListener;
    LocationManager locationManager;
    Address address;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myLocationListener = new MyLocationListener();
        getLocationBtn = findViewById(R.id.BtnGetLocation);
        sendLocationBtn = findViewById(R.id.BtnSendLocation);
        showMapBtn = findViewById(R.id.BtnMap);
        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        sendLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendLocation();
            }
        });
        showMapBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showLocationMap();
            }
        });


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSON_CODE);

                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
                }
        }
    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //if this permission is not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSON_CODE);

        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        }
    }

    private void sendLocation() {
        String retrievedAddress;
        try {

            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);
            if (addresses.size() == 0) {
                Toast.makeText(this, "Location Not Found!", Toast.LENGTH_SHORT).show();
            } else {
                address = addresses.get(0);
                String countryName = address.getCountryName();
                String adminArea = address.getAdminArea();
                String featureName = address.getFeatureName();


                retrievedAddress = countryName + "," + adminArea + "," + featureName;
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"));
                sendIntent.putExtra("address", "01062494707");
                sendIntent.putExtra("sms_body", retrievedAddress);
                startActivity(sendIntent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void showLocationMap() {
        Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
        myIntent.putExtra("Longitude", longitude);
        myIntent.putExtra("Latitude", latitude);

        MainActivity.this.startActivity(myIntent);
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Toast.makeText(MainActivity.this, "Latitude: " + latitude + System.getProperty("line.separator") + "longitude: " + longitude, Toast.LENGTH_LONG).show();
            locationManager.removeUpdates(myLocationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }


}