package edu.hcmus.project.ebanking.ws.service;

import edu.hcmus.project.ebanking.data.model.contranst.SignType;
import edu.hcmus.project.ebanking.data.model.contranst.TransactionFeeType;
import edu.hcmus.project.ebanking.data.model.contranst.TransactionStatus;
import edu.hcmus.project.ebanking.data.model.contranst.TransactionType;
import edu.hcmus.project.ebanking.ws.config.exception.BadRequestException;
import edu.hcmus.project.ebanking.data.model.*;
import edu.hcmus.project.ebanking.data.repository.AccountRepository;
import edu.hcmus.project.ebanking.data.repository.BankRepository;
import edu.hcmus.project.ebanking.data.repository.TransactionRepository;
import edu.hcmus.project.ebanking.ws.resource.dto.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public CustomerDto findAccountInfo(String accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if(!accountOpt.isPresent()) {
            throw new BadRequestException("Account is not exist!");
        }
        Account account = accountOpt.get();
        User receiver = account.getOwner();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setAccountId(account.getAccountId());
        customerDto.setFirstName(receiver.getFirstName());
        customerDto.setLastName(receiver.getLastName());
        return customerDto;
    }

    public Bank createNewRefBank(ClientRegisterDto dto) throws IOException {
        Bank bank = new Bank();
        bank.setBankName(dto.getName());
        bank.setAddress(dto.getAddress());
        bank.setEmail(dto.getEmail());
        bank.setPhone(dto.getPhone());
        bank.setSecret(dto.getSecret());
        bank.setSignType(dto.getSignType());
        if(SignType.RSA.equals(dto.getSignType())) {
            String fileContent = Arrays.stream(new String(dto.getPublicKey().getBytes()).split(System.lineSeparator()))
                    .filter(line -> !line.startsWith("-----BEGIN") && !line.startsWith("-----END"))
                    .collect(Collectors.joining(System.lineSeparator()));
            bank.setKey(Base64Utils.decode(fileContent.getBytes("UTF-8")));
        } else {
            bank.setKey(dto.getPublicKey().getBytes());
        }
        bank.setApiKey(dto.getApiKey());
        bank.setAccountEndpoint(dto.getAccountEndpoint());
        bank.setTransactionEndpoint(dto.getTransactionEndpoint());
        return bankRepository.save(bank);
    }

    public Bank updateRefBankKey(String id, MultipartFile key) throws IOException {
        Optional<Bank> bankOpt = bankRepository.findById(id);
        if(!bankOpt.isPresent()) {
            throw new BadRequestException("Cannot retrieve bank information!");
        }
        Bank bank = bankOpt.get();
        bank.setKey(key.getBytes());
        return bankRepository.save(bank);
    }

    @Transactional
    public TransactionDto depositTransaction(String bankId, TransactionRequestDto requestDto) {
        Optional<Account> accountOpt = accountRepository.findById(requestDto.getAccId());
        if(!accountOpt.isPresent()) {
            throw new BadRequestException("Account is not exist!");
        }

        Optional<Bank> bankOpt = bankRepository.findById(bankId);
        if(!bankOpt.isPresent()) {
            throw new BadRequestException("Cannot retrieve bank information!");
        }
        Bank bank = bankOpt.get();
        Account account = accountOpt.get();
        User receiver = account.getOwner();
        if(receiver == null || receiver.getStatus() == false) {
            throw new BadRequestException("Account is not exist!");
        }


        Double targetBalance = account.getBalance();
        account.setBalance(targetBalance + requestDto.getAmount());
        accountRepository.save(account);

        ZonedDateTime now = ZonedDateTime.now();
        Transaction transaction = new Transaction();
        transaction.setAmount(requestDto.getAmount());
        transaction.setContent(requestDto.getNote());
        transaction.setDate(now);
        transaction.setSource(account.getAccountId());
        transaction.setReference(bank);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setFeeType(requestDto.getFeeType());
        transaction.setFee(requestDto.getFee());
        transaction.setStatus(TransactionStatus.COMPLETED);
        return new TransactionDto(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionDto withDrawTransaction(String bankId, TransactionRequestDto requestDto) {
        Optional<Account> accountOpt = accountRepository.findById(requestDto.getAccId());
        if(!accountOpt.isPresent()) {
            throw new BadRequestException("Account is not exist!");
        }
        Account account = accountOpt.get();
        User receiver = account.getOwner();
        if(receiver == null || receiver.getStatus() == false) {
            throw new BadRequestException("Account is not exist!");
        }

        Optional<Bank> bankOpt = bankRepository.findById(bankId);
        if(!bankOpt.isPresent()) {
            throw new BadRequestException("Cannot retrieve bank information!");
        }
        Bank bank = bankOpt.get();

        Double sourceBalance = account.getBalance();
        Double fee = 7000d;
        Double transactionAmount = requestDto.getAmount();
        switch (requestDto.getFeeType()){
            case SENDER:
                transactionAmount += fee;
                break;
            case RECEIVER:
                transactionAmount -= fee;
                if(transactionAmount <= 0) {
                    throw new BadRequestException("The transaction amount should greater than transaction fee (" + fee +")!");
                }
                break;
        }

        if(sourceBalance < transactionAmount) {
            throw new BadRequestException("Insufficient balance. Cannot execute this transaction!");
        }

        account.setBalance(sourceBalance - (TransactionFeeType.SENDER.equals(requestDto.getFeeType()) ? transactionAmount : requestDto.getAmount()));
        accountRepository.save(account);

        ZonedDateTime now = ZonedDateTime.now();
        Transaction transaction = new Transaction();
        transaction.setAmount(requestDto.getAmount());
        transaction.setContent(requestDto.getNote());
        transaction.setDate(now);
        transaction.setSource(account.getAccountId());
        transaction.setReference(bank);
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setFeeType(requestDto.getFeeType());
        transaction.setFee(requestDto.getFee());
        transaction.setStatus(TransactionStatus.COMPLETED);
        return new TransactionDto(transactionRepository.save(transaction));
    }

}
