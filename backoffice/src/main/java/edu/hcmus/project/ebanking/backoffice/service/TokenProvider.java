package edu.hcmus.project.ebanking.backoffice.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TokenProvider {
//    String series  = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();

    public String generateRandomSeries(String series, int len){
        char[] token = new char[len];
        for (int i = 0; i < len; i++) {
            token[i] = series.charAt(random.nextInt(series.length()));
        }
        return new String(token);
    }


}
