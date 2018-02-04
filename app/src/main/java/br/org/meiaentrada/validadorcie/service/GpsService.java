package br.org.meiaentrada.validadorcie.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;


public class GpsService extends Service {

    public static final String LOCATION_UPDATE = "location_update";

    private LocationListener listener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Intent intent = new Intent(LOCATION_UPDATE);
                intent.putExtra("device_coordinates", "{" +
                                "\"latitude\": " + location.getLatitude() + "," +
                                "\"longitude\": " + location.getLongitude() + "}");
                sendBroadcast(intent);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (locationManager != null)
            locationManager.removeUpdates(listener);

    }

}
