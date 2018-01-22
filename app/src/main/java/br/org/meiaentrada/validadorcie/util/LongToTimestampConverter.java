package br.org.meiaentrada.validadorcie.util;

import org.joda.time.format.DateTimeFormat;

import java.sql.Timestamp;


public class LongToTimestampConverter {

    public static String converter(Long timestamp) {

        try {

            Timestamp date = new Timestamp(timestamp);

            return DateTimeFormat.forPattern("yyyy-MM-dd").print(date.getTime());

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }
        return null;

    }

}
