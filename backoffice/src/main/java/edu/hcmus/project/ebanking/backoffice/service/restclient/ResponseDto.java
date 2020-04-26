package edu.hcmus.project.ebanking.backoffice.service.restclient;

public abstract class ResponseDto <T>{
    private String sign;
    private String hash;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public abstract T getResponseContent();
}
