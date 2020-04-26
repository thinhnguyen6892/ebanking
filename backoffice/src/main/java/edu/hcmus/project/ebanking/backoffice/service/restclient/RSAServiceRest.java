package edu.hcmus.project.ebanking.backoffice.service.restclient;

import edu.hcmus.project.ebanking.backoffice.model.Bank;
import edu.hcmus.project.ebanking.backoffice.service.restclient.dto.RSAAccountInfoDto;
import edu.hcmus.project.ebanking.backoffice.service.restclient.dto.RSARequestDto;
import edu.hcmus.project.ebanking.backoffice.service.restclient.dto.RSAResponseDto;
import edu.hcmus.project.ebanking.backoffice.service.restclient.dto.RSATransactionDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.HashMap;
import java.util.Map;

public interface RSAServiceRest {

    @POST("api/v1/accounts")
    Call<RSAResponseDto<RSAAccountInfoDto>> postAccounts(@Body RSARequestDto<RSAAccountInfoDto> accountInfoDto);

    @POST("api/v1/transactions")
    Call<RSAResponseDto<RSATransactionDto>> postTransactions(@Body RSARequestDto<RSATransactionDto> transactionDto);

    static Map header(Bank bank){
        Map<String, String> header = new HashMap<>();
        header.put("x-api-key", bank.getApiKey());
        header.put("x-time-code", "9999999999");
        return header;
    }
}
