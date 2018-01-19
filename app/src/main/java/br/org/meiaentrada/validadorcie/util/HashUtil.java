package br.org.meiaentrada.validadorcie.util;

import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashUtil {

    public static String getMD5(byte[] data) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(data);
        return new String(Hex.encode(hash));

    }

    public String getMD5(String contentString) throws NoSuchAlgorithmException {

        return getMD5(contentString.getBytes());

    }

}
