package edu.hcmus.project.ebanking.backoffice.service.restclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.hcmus.project.ebanking.backoffice.service.restclient.ResponseDto;

public class RSAResponseDto <T>  extends ResponseDto <T> {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @JsonIgnore
    @Override
    public T getResponseContent() {
        return data;
    }
}
