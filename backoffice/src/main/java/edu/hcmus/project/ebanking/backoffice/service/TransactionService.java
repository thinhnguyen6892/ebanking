package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.*;
import edu.hcmus.project.ebanking.backoffice.repository.AccountRepository;
import edu.hcmus.project.ebanking.backoffice.repository.TransactionRepository;
import edu.hcmus.project.ebanking.backoffice.resource.exception.EntityNotExistException;
import edu.hcmus.project.ebanking.backoffice.resource.exception.InvalidTransactionException;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    public List<TransactionDto> findAllTransaction() {
        return transactionRepository.findAll().stream().map(transaction -> {
            TransactionDto dto = new TransactionDto();
            dto.setAmount(transaction.getAmount());
            dto.setContent(transaction.getContent());
            dto.setCreated(transaction.getDate());
            dto.setSource(transaction.getSource());
            dto.setType(transaction.getType());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<TransactionDto> findAllAccountTransaction(String accountId) {

        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if(accountOpt.isPresent()) {
            throw new EntityNotExistException("Account not found in the system");
        }
        Account account = accountOpt.get();
        return transactionRepository.findTransactionsBySource(account.getAccountId()).stream().map(transaction -> {
            TransactionDto dto = new TransactionDto();
            dto.setAmount(transaction.getAmount());
            dto.setContent(transaction.getContent());
            dto.setCreated(transaction.getDate());
            dto.setSource(transaction.getSource());
            dto.setType(transaction.getType());
            return dto;
        }).collect(Collectors.toList());
    }

    public TransactionDto requestTransaction(TransactionDto dto) {
        Optional<Account> sourceOpt = accountRepository.findById(dto.getSource());
        if(!sourceOpt.isPresent()) {
            throw new EntityNotExistException("Source account is not exist!");
        }
        Account source = sourceOpt.get();
        Optional<Account> targetOpt = accountRepository.findById(dto.getTarget());
        if(!targetOpt.isPresent()) {
            throw new EntityNotExistException("Target account is not exist!");
        }

        Account target = targetOpt.get();
        Double sourceBalance = source.getBalance();
        Double transactionAmount = dto.getAmount();
        if(sourceBalance < transactionAmount) {
            throw new InvalidTransactionException("Insufficient balance. Cannot execute this transaction!");
        }
        long currentTime = System.currentTimeMillis();
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setContent(dto.getContent());
        transaction.setDate(new Date(currentTime));
        transaction.setSource(source.getAccountId());
        transaction.setTarget(target.getAccountId());
        transaction.setStatus(TransactionStatus.NEW);
        transaction.setType(dto.getType());

        long expires = currentTime + 1000L * (30 * 60);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        String opt = tokenProvider.generateRandomSeries("0123456789", 6);
        transaction.setOtpCode(opt);
        transaction.setValidity(expires);

        transaction = transactionRepository.save(transaction);
        dto.setId(transaction.getId());
        dto.setOtpCode(opt);
        return dto;
    }


    public void pay(TransactionDto dto) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(dto.getId());
        if(!transactionOptional.isPresent()) {
            throw new EntityNotExistException("Transaction is not exist!");
        }
        Transaction transaction = transactionOptional.get();
        if(TransactionStatus.COMPLETED == transaction.getStatus() || TransactionStatus.CANCEL == transaction.getStatus()) {
            throw new EntityNotExistException("Transaction is not exist!");
        }
        if(!dto.getOtpCode().equals(transaction.getOtpCode())) {
            transaction.setStatus(TransactionStatus.CANCEL);
            transactionRepository.save(transaction);
            throw new InvalidTransactionException("Invalid OTP!");
        }
        performTransaction(transaction);
    }

    @Transactional
    public void performTransaction(Transaction transaction) {
        Optional<Account> sourceOpt = accountRepository.findById(transaction.getSource());
        if(!sourceOpt.isPresent()) {
            throw new EntityNotExistException("Source account is not exist!");
        }
        Account source = sourceOpt.get();
        Optional<Account> targetOpt = accountRepository.findById(transaction.getTarget());
        if(!targetOpt.isPresent()) {
            throw new EntityNotExistException("Target account is not exist!");
        }

        Account target = targetOpt.get();
        Double sourceBalance = source.getBalance();
        Double transactionAmount = transaction.getAmount();
        if(sourceBalance < transactionAmount) {
            throw new InvalidTransactionException("Insufficient balance. Cannot execute this transaction!");
        }

        source.setBalance(sourceBalance - transactionAmount);
        accountRepository.save(source);

        Double targetBalance = target.getBalance();
        target.setBalance(targetBalance + transactionAmount);
        accountRepository.save(source);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

        Transaction receive = new Transaction();
        receive.setAmount(transaction.getAmount());
        receive.setContent(transaction.getContent());
        receive.setDate(new Date());
        receive.setSource(target.getAccountId());
        receive.setTarget(source.getAccountId());
        receive.setStatus(TransactionStatus.COMPLETED);
        receive.setType(TransactionType.INCOME);
        transactionRepository.save(receive);
    }

}
