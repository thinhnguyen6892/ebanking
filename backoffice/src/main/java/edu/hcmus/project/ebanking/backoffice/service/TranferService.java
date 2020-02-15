package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.*;
import edu.hcmus.project.ebanking.backoffice.repository.AccountRepository;
import edu.hcmus.project.ebanking.backoffice.repository.TransactionRepository;
import edu.hcmus.project.ebanking.backoffice.resource.exception.EntityNotExistException;
import edu.hcmus.project.ebanking.backoffice.resource.exception.InvalidTransactionException;
import edu.hcmus.project.ebanking.backoffice.resource.transfer.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TranferService {

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
            dto.setType(transaction.getType().name());
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
            dto.setType(transaction.getType().name());
            return dto;
        }).collect(Collectors.toList());
    }

    public void send(TransactionDto dto) {
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

        source.setBalance(sourceBalance - transactionAmount);
        accountRepository.save(source);

        Double targetBalance = target.getBalance();
        target.setBalance(targetBalance + transactionAmount);
        accountRepository.save(source);

        Timestamp timestamp = new Timestamp(new Date().getTime());

        Transaction sent = new Transaction();
        sent.setAmount(dto.getAmount());
        sent.setContent(dto.getContent());
        sent.setDate(timestamp);
        sent.setSource(source.getAccountId());
        sent.setTarget(target.getAccountId());
        sent.setStatus(true);
        sent.setType(TransactionType.PAYMENT);
        transactionRepository.save(sent);

        Transaction receive = new Transaction();
        receive.setAmount(dto.getAmount());
        receive.setContent(dto.getContent());
        receive.setDate(timestamp);
        receive.setSource(target.getAccountId());
        receive.setTarget(source.getAccountId());
        receive.setStatus(true);
        receive.setType(TransactionType.INCOME);
        transactionRepository.save(receive);
    }

}
