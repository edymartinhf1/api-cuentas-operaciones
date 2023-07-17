package com.bootcamp.bank.operaciones.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
@Component
@Log4j2
@NoArgsConstructor(access= AccessLevel.PRIVATE)
public final class Util {

    private static SecureRandom random = new SecureRandom();
    public static int generateRandomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static String getCurrentDateAsString(String format) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return currentDate.format(formatter);
    }

    public static Date getCurrentDate() {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDate fecha = LocalDate.now();
        return Date.from(fecha.atStartOfDay(defaultZoneId).toInstant());
    }

    public static LocalDateTime getCurrentLocalDate() {
        return LocalDateTime.now();
    }

    public static Date getDatefromString(String fecha) {
        Date fechaConvert =null;
        try {
            String formatDate = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
            fechaConvert = sdf.parse(fecha);
        } catch(ParseException ex){
            log.error(ex.getMessage(),ex);
        }
        return fechaConvert;
    }

    public static LocalDateTime getLocalDatefromString(String fecha) {
        String format="yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(fecha, formatter);
    }
}
