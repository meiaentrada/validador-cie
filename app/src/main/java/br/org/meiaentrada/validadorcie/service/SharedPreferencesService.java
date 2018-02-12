package br.org.meiaentrada.validadorcie.service;

import android.content.Context;
import android.content.SharedPreferences;

import br.org.meiaentrada.validadorcie.DeviceThings;
import br.org.meiaentrada.validadorcie.configuration.GlobalConstants;


public class SharedPreferencesService {

    private SharedPreferences sharedPref;

    public SharedPreferencesService(Context context) {

        sharedPref = new DeviceThings(context).getSharedPref();

    }

    public void setCodigoAcesso(String codigoAcesso) {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(GlobalConstants.SHARED_PREF_CODIGO_ACESSO, codigoAcesso);
        editor.apply();

    }

    public String getCodigoAcesso() {

        return sharedPref.getString(
                GlobalConstants.SHARED_PREF_CODIGO_ACESSO,
                GlobalConstants.SHARED_PREF_DEFAULT);

    }

    public void setEvento(String evento) {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(GlobalConstants.SHARED_PREF_EVENTO, evento);
        editor.apply();

    }

    public String getEvento() {

        return sharedPref.getString(
                GlobalConstants.SHARED_PREF_EVENTO,
                GlobalConstants.SHARED_PREF_DEFAULT);

    }

    public void setEmail(String email) {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(GlobalConstants.SHARED_PREF_EMAIL, email);
        editor.apply();

    }

    public String getEmail() {

        return sharedPref.getString(
                GlobalConstants.SHARED_PREF_EMAIL,
                GlobalConstants.SHARED_PREF_DEFAULT);

    }

}