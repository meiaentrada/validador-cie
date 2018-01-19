package br.org.meiaentrada.validadorcie.util;

import android.util.Log;

import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashUtil {

    public static String getMD5(byte[] data) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(data);
        return new String(Hex.encode(hash));

    }

    public static String getMD5(String contentString) {

        try {
            return getMD5(contentString.getBytes());
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }
        return null;

    }

}
