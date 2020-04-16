package edu.hcmus.project.ebanking.ws.service;

import edu.hcmus.project.ebanking.ws.config.security.ClientDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Service
public class TokenProvider {
    private Random random = new Random();
    private String secretKey = "mySecretKey";
    public static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String DIGITS = "0123456789";
    public static final String PUNCTUATION = "!@#$%&*()_+-=[]|,./?><";

    public String generateRandomSeries(String series, int len){
        char[] token = new char[len];
        for (int i = 0; i < len; i++) {
            token[i] = series.charAt(random.nextInt(series.length()));
        }
        return new String(token);
    }

    public String computeSignature(ClientDetails clientDetails) {
        return computeSignature(clientDetails.getUsername(), clientDetails.getSecret());
    }

    public String computeSignature(String clientId, String secret) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(clientId).append(":");
        signatureBuilder.append(secret);
        signatureBuilder.append(secretKey);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }
        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
    }
}
