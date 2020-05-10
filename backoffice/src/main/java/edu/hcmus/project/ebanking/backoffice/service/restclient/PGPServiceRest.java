package edu.hcmus.project.ebanking.backoffice.service.restclient;

import edu.hcmus.project.ebanking.data.model.Bank;
import edu.hcmus.project.ebanking.backoffice.service.restclient.dto.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.HashMap;
import java.util.Map;

public interface PGPServiceRest {

    static final String HEADER_EXPIRED_TIME = "x-time-code";
    static final String HEADER_API_KEY = "x-api-key";

    @POST("AnotherInternetbanking/InfoAccount")
    Call<PGPAccountResponseDto> postAccounts(@Body PGPRequestDto accountInfoDto);

    @POST("AnotherInternetbanking/TranferInternerAnotherBank")
    Call<RSAResponseDto<RSATransactionDto>> postTransactions(@Body PGPRequestDto transactionDto);

    static Map header(Bank bank){
        Map<String, String> header = new HashMap<>();
        header.put(HEADER_API_KEY, bank.getApiKey());
        header.put(HEADER_EXPIRED_TIME, "9999999999");
        return header;
    }
}
