package br.org.meiaentrada.validadorcie;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import br.org.meiaentrada.validadorcie.model.DeviceLocation;


public class DeviceLocationListener implements LocationListener {

    public static final String TAG = "DeviceLocationListener";

    private Context mContext;
    private DeviceLocation deviceLocation;

    public DeviceLocationListener(DeviceLocation deviceLocation, Context context) {

        this.mContext = context;
        this.deviceLocation = deviceLocation;

    }

    @Override
    public void onLocationChanged(Location location) {

        deviceLocation.setLatitude(location.getLatitude());
        deviceLocation.setLongitude(location.getLongitude());

        Log.i(TAG, deviceLocation.toString());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        Log.i(TAG, "onStatusChanged");

    }

    @Override
    public void onProviderEnabled(String provider) {

        Log.i(TAG, "onProviderEnabled");

    }

    @Override
    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        mContext.startActivity(intent);

    }

}


































