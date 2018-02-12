package br.org.meiaentrada.validadorcie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import br.org.meiaentrada.validadorcie.configuration.GlobalConstants;


public class DeviceThings {

    private TelephonyManager telephonyManager;
    private SharedPreferences sharedPref;

    @SuppressLint("MissingPermission")
    public DeviceThings(Context context) {

        sharedPref = context.getSharedPreferences(
                GlobalConstants.SHARED_PREF_FILE, Context.MODE_PRIVATE);

//        if (telephonyManager == null) {
//
//            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//            SharedPreferences.Editor editor = sharedPref.edit();
//            editor.putString("deviceId", telephonyManager.getDeviceId());
//            editor.apply();
//
//        }

    }


    public String getDeviceId() {

        return sharedPref.getString("deviceId", "");

    }

    public SharedPreferences getSharedPref() {

        return sharedPref;

    }

}
