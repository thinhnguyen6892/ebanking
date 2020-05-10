package edu.hcmus.project.ebanking.backoffice.service.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hcmus.project.ebanking.data.model.Bank;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.AccountDto;
import edu.hcmus.project.ebanking.backoffice.resource.exception.ConnectException;
import edu.hcmus.project.ebanking.backoffice.service.SignatureService;
import edu.hcmus.project.ebanking.backoffice.service.restclient.dto.RSAAccountInfoDto;
import edu.hcmus.project.ebanking.backoffice.service.restclient.dto.RSARequestDto;
import edu.hcmus.project.ebanking.backoffice.service.restclient.dto.RSATransactionDto;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class RestClientService {

    @Value("${app.rest.client.request.time-out.in.seconds}")
    private long requestExpires;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SignatureService signatureService;


    public AccountDto getRsaClientAccountInfo(Bank bank, String accountId) {
        Map header = RSAServiceRest.header(bank);
        //creating service
        RSAServiceRest serviceRest = buildRestService(bank, header, RSAServiceRest.class);
        //creating request dto
        RSAAccountInfoDto requestContent = new RSAAccountInfoDto();
        requestContent.setAccount(accountId);
        RSARequestDto<RSAAccountInfoDto> rsaRequestDto = new RSARequestDto<>(requestContent);
        //create hash by formula api key + expired time
        String headerValueString = header.values().stream().map(Object::toString).collect(Collectors.joining()).toString();
        rsaRequestDto.setHash(passwordEncoder.encode(headerValueString));

        //sending and parsing the response
        RSAAccountInfoDto response = handleResponse(serviceRest.postAccounts(rsaRequestDto), resp -> {
            String hash = resp.getHash();
            RSAAccountInfoDto responseAccount = resp.getResponseContent();
            //Check hash???
            return true;
        });
        AccountDto dto = new AccountDto();
        dto.setAccountId(response.getAccount());
        dto.setOwnerName(response.getName());
        return dto;
    }

    public RSATransactionDto makeRsaClientTransaction(Bank bank, String account, Integer amount, String content) {
        Map header = RSAServiceRest.header(bank);

        RSAServiceRest serviceRest = buildRestService(bank, header, RSAServiceRest.class);
        RSATransactionDto requestContent = new RSATransactionDto();
        requestContent.setAccount(account);
        requestContent.setAmount(amount);
        requestContent.setContent(content);
        RSARequestDto<RSATransactionDto> rsaRequestDto = new RSARequestDto(requestContent);

        String headerValueString = header.values().stream().map(Object::toString).collect(Collectors.joining()).toString();
        rsaRequestDto.setHash(passwordEncoder.encode(headerValueString));
        try {
            rsaRequestDto.setSign(signatureService.signWithPrivateKey(objectMapper.writeValueAsString(requestContent)));
        } catch (Exception e) {
            throw new ConnectException(e);
        }
        //check hash and sign
        RSATransactionDto response = handleResponse(serviceRest.postTransactions(rsaRequestDto), resp -> {
            String hash = resp.getHash();
            RSATransactionDto responseTransaction = resp.getResponseContent();
            if(!passwordEncoder.matches(headerValueString, hash)) {
                return false;
            }
            try {
                String contentAsString = objectMapper.writeValueAsString(requestContent);
                String sign = resp.getSign();
                signatureService.verifyWithPublicKey(bank.getSignType(), contentAsString, sign.getBytes(), bank.getKey());
            } catch (Exception e) {
                return false;
            }
            return true;
        });
        return response;
    }

    private <T extends ResponseDto<E>, E> E handleResponse(Call<T> call, Predicate<T> validator) {
        try {
            Response<T> response = call.execute();
            if (!response.isSuccessful()) {
                throw new ConnectException(response.errorBody().string());
            }
            if(!validator.test(response.body())){
                throw new ConnectException("Invalid hash or sign");
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
