package edu.hcmus.project.ebanking.backoffice.model;

public class Token {
    String token;
    long expires;

    public Token(String token, long expires){
        this.token = token;
        this.expires = expires;
    }

    public String getToken() {
        return token;
    }

    public long getExpires() {
        return expires;
    }

}
