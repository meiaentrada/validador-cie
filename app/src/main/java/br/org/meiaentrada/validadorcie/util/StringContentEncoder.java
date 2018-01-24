package br.org.meiaentrada.validadorcie.util;

import java.io.UnsupportedEncodingException;


public class StringContentEncoder {

    public static String makeUtf8(String str) {

        try {

            return new String(str.getBytes("ISO-8859-1"), "UTF-8");


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        return str;

    }

}
