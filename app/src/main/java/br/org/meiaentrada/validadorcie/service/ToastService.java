package br.org.meiaentrada.validadorcie.service;

import android.content.Context;
import android.widget.Toast;


public class ToastService {

    public static void showToast(String message, Context context) {

        int duration = Toast.LENGTH_LONG;
        showToast(message, duration, context);

    }

    public static void showToast(String message, int duration, Context context) {

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();

    }

}
