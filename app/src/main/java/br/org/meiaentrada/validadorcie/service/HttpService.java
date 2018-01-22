package br.org.meiaentrada.validadorcie.service;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;

import br.org.meiaentrada.validadorcie.configuration.GlobalConstants;


public class HttpService {

    public static void validateOperador(String codigoAcesso) {

        try {

            String endpoint = GlobalConstants.URL_VALIDATE_OPERADOR + "/" + codigoAcesso;
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET, endpoint, null, response -> {

                try {
                    String email = response.getString("email");
                    Log.i(HttpService.class.getName(), email);

                } catch (JSONException e) {
                    Log.e("", e.getMessage());
                }

            }, error ->
                    Log.e(HttpService.class.getName(), error.getMessage()));

        } catch (Exception e) {

            Log.e(HttpService.class.getName(), e.getMessage());

        }

    }

}
