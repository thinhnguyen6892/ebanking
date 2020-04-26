package edu.hcmus.project.ebanking.backoffice.service.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hcmus.project.ebanking.backoffice.model.Bank;
import edu.hcmus.project.ebanking.backoffice.model.Transaction;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.AccountDto;
import edu.hcmus.project.ebanking.backoffice.resource.exception.ConnectException;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.CreateTransactionRequestDto;
import edu.hcmus.project.ebanking.backoffice.service.restclient.dto.RSAAccountInfoDto;
import edu.hcmus.project.ebanking.backoffice.service.restclient.dto.RSARequestDto;
import edu.hcmus.project.ebanking.backoffice.service.restclient.dto.RSATransactionDto;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RestClientService {

    @Value("${app.rest.client.request.time-out.in.seconds}")
    private long requestExpires;

    @Autowired
    private ObjectMapper objectMapper;


    public AccountDto getRsaClientAccountInfo(Bank bank, String accountId) {
        RSAServiceRest serviceRest = buildRestService(bank, RSAServiceRest.header(bank), RSAServiceRest.class);
        RSAAccountInfoDto accountInfoDto = new RSAAccountInfoDto();
        accountInfoDto.setAccount(accountId);
        RSAAccountInfoDto response = handleResponse(serviceRest.postAccounts(new RSARequestDto<>(accountInfoDto)));
        AccountDto dto = new AccountDto();
        dto.setAccountId(response.getAccount());
        dto.setOwnerName(response.getName());
        return dto;
    }

    public RSATransactionDto makeRsaClientTransaction(Bank bank, String account, Integer amount, String content) {
        RSAServiceRest serviceRest = buildRestService(bank, RSAServiceRest.header(bank), RSAServiceRest.class);
        RSATransactionDto transactionDto = new RSATransactionDto();
        transactionDto.setAccount(account);
        transactionDto.setAmount(amount);
        transactionDto.setContent(content);
        RSATransactionDto response = handleResponse(serviceRest.postTransactions(new RSARequestDto<>(transactionDto)));
        return response;
    }

    private <T extends ResponseDto<E>, E> E handleResponse(Call<T> call) {
        try {
            Response<T> response = call.execute();
            if (!response.isSuccessful()) {
                throw new ConnectException(response.errorBody().string());
            }
            return response.body().getResponseContent();
        } catch (IOException e) {
            throw new ConnectException(e);
        }
    }

    private <T> T buildRestService(Bank bank, Map<String, String> headerMap, Class<T> clzz) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(bank.getClientHost())
                .client(buildClient(bank, headerMap))
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
        return retrofit.create(clzz);
    }

    private OkHttpClient buildClient(Bank bank, Map<String, String> headerMap) {
        Interceptor headerInterceptor = chain -> {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            headerMap.forEach((name, value) -> builder.addHeader(name, value));
            request = builder.build();
            return chain.proceed(request);
        };
        return new OkHttpClient.Builder()
                .readTimeout(requestExpires, TimeUnit.SECONDS)
                .addInterceptor(headerInterceptor)
                .build();
    }


}
