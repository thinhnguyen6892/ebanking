package edu.hcmus.project.ebanking.ws;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class InstantTest {

    public static void main(String[] args) {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("HHmm, dd MMM yyyy");

        LocalDateTime ldt = LocalDateTime.now();

        System.out.println("LocalDateTime : " + format.format(ldt));

        //UTC+8

        ZonedDateTime klDateTime = ldt.atZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        System.out.println("Depart : " + format.format(klDateTime));

        ZonedDateTime parisDateTime = klDateTime.withZoneSameInstant(ZoneId.of("Europe/Paris"));
        System.out.println("Arrive : " + format.format(parisDateTime));

        System.out.println("\n---Detail---");
        System.out.println("Depart : " + klDateTime);
        System.out.println("Arrive : " + parisDateTime);

        System.out.println("\n---Millisecond---");
        System.out.println("Depart : " + klDateTime.toInstant().toEpochMilli());
        System.out.println("Arrive : " + parisDateTime.toInstant().toEpochMilli());
        System.out.println("Date   : " + new Date().getTime());
        System.out.println("Paris  : " + ldt.atZone(ZoneId.of("Europe/Paris")).toInstant().toEpochMilli());

    }
}
