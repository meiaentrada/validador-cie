package br.org.meiaentrada.validadorcie.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;


public class StringContentEncoder {

    public static String makeUtf8(String str) {

        try {

            return new String(str.getBytes("ISO-8859-1"), "UTF-8");


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }
        return str;

    }

    public static String makeUtf8QueryString(String str) {

        try {

            return  URLEncoder.encode(str, "UTF-8");

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }
        return str;

    }

    public static String stripAccents(String input) {

        return input == null ? null :
                Normalizer.normalize(input, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

    }

}
