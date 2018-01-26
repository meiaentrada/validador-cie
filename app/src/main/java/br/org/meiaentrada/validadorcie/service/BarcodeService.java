package br.org.meiaentrada.validadorcie.service;

import android.webkit.URLUtil;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import br.org.meiaentrada.validadorcie.enumeration.BarcodeType;


public class BarcodeService {

    public static BarcodeType getBarcodeType(String barcodeData) {

        if (barcodeData.contains("cdne.com.br")) {

            if (getCodigoAndDataNascimento(barcodeData).length == 2)
                return BarcodeType.CDNE_URL;

        } else if (URLUtil.isHttpsUrl(barcodeData) || URLUtil.isHttpUrl(barcodeData))
            return BarcodeType.OTHER_URL;
        return BarcodeType.CERTIFICATE;

    }

    public static String[] getCodigoAndDataNascimento(String barcodeData) {

        String convertionType = getURLConvertionType(barcodeData);

        barcodeData = barcodeData
                .replace("/#val/", "/")
                .replace("/val/", "/")
                .replace("/dt/", "/")
                .replace("&dataNascimento=", "/")
                .replace("/?numero=", "/")
                .replace("?numero=", "/")
                .replace("/validador/", "/")
                .replace("/convenio/", "")
                .replace("/validardne/", "/")
                .replace("/validardne", "/")
                .replace("http://", "")
                .replace("https://", "")
                .replace("www.", "");

        String[] fields = barcodeData
                .replaceAll("(cdne.com.br/)(.+?(?=[^A-Z]))/(\\d+)", "$2;$3")
                .split(";");

        if (fields.length == 2)
            return new String[]{fields[0], getDataNascimento(fields[1], convertionType)};
        return new String[]{};

    }

    public static String getDataNascimento(String dataNascimento, String convertionType) {

        String convertedDataNascimento = "";

        switch (convertionType) {

            case "string_us":
                DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
                Date date = null;
                try {
                    date = format.parse(dataNascimento);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                convertedDataNascimento = date != null ? format.format(date) : "";
                break;
            case "unix_timestamp":
                LocalDateTime temp = new org.joda.time.LocalDateTime(Long.parseLong(dataNascimento)).plusHours(3);
                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                convertedDataNascimento = dateTimeFormatter.print(temp);
                break;

        }
        return convertedDataNascimento;

    }

    public static String getURLConvertionType(String argUrl) {

        String[][] urls = {
                {"cdne.com.br/#val/", "unix_timestamp"},
                {"cdne.com.br/val/", "unix_timestamp"},
                {"cdne.com.br/", "string_us"}
        };

        for (String[] url : urls)
            if (argUrl.contains(url[0]))
                return url[1];
        return null;

    }

}