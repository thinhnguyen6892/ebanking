package edu.hcmus.project.ebanking.backoffice.service.restclient.dto;


public class PGPRequestDto {
    private String STTTH;
    private Long Time;
    private String PartnerCode;
    private String Hash;
    private String Signature;
    private String STTTHAnother;
    private String Money;

    public String getSTTTH() {
        return STTTH;
    }

    public void setSTTTH(String STTTH) {
        this.STTTH = STTTH;
    }

    public Long getTime() {
        return Time;
    }

    public void setTime(Long time) {
        Time = time;
    }

    public String getPartnerCode() {
        return PartnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        PartnerCode = partnerCode;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String signature) {
        Signature = signature;
    }

    public String getSTTTHAnother() {
        return STTTHAnother;
    }

    public void setSTTTHAnother(String STTTHAnother) {
        this.STTTHAnother = STTTHAnother;
    }

    public String getMoney() {
        return Money;
    }

    public void setMoney(String money) {
        Money = money;
    }
}
