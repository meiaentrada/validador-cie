package br.org.meiaentrada.validadorcie.service;

import android.webkit.URLUtil;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


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

        barcodeData = barcodeData
                .replace("http://", "")
                .replace("https://", "")
                .replace("www.", "")
                .replace("#val/", "val/");

        String[][] urlsRegexPatterns = {
                {"(cdne.com.br/)(.+?(?=[^A-Z]))/(\\d+)", "$2;$3", null},
                {"(cdne.com.br/val/)(.+?(?=[^A-Z\\d+]))/dt/(\\d+)", "$2;$3", Long.class.getName()},
                {"(cdne.com.br/validador/val/)(.+?(?=[^A-Z\\d+]))/(\\d+)", null},
                {"(cdne.com.br/validador/convenio/)(.+?(?=[^A-Z\\d+]))/(\\d+)", "$2;$3", null},
                {"(cdne.com.br/validador/validardne\\?numero=)((.+?(?=[^A-Z\\d+])))\\&dataNascimento=(\\d+)", "$2;$4", null}
        };

        for (String[] pattern : urlsRegexPatterns) {

            String[] fields = barcodeData.replaceAll(pattern[0], pattern[1]).split(";");
            if (fields.length == 2)
                return new String[]{fields[0], fields[1], pattern[2]};

        }
        return new String[]{};

    }

    public String dateFormatter(String dataNascimento) {

        LocalDateTime temp = new LocalDateTime(dataNascimento);
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");

        return null;

    }

    public String dateFormatter(Long timestamp) {

        LocalDateTime date = new LocalDateTime(timestamp).plusHours(3);
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");

        return dateTimeFormatter.print(date);

    }

}