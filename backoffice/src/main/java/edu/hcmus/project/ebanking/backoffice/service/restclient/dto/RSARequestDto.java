package edu.hcmus.project.ebanking.backoffice.service.restclient.dto;

public class RSARequestDto <T> {

    public RSARequestDto() {

    }

    public RSARequestDto(T content) {
        this.content = content;
    }

    private T content;
    private String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
