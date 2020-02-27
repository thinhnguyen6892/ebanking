package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.*;
import edu.hcmus.project.ebanking.backoffice.repository.AccountRepository;
import edu.hcmus.project.ebanking.backoffice.repository.BankRepository;
import edu.hcmus.project.ebanking.backoffice.repository.TransactionRepository;
import edu.hcmus.project.ebanking.backoffice.resource.exception.EntityNotExistException;
import edu.hcmus.project.ebanking.backoffice.resource.exception.InvalidTransactionException;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.TransactionDto;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.TransactionRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MailService mailService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    public List<TransactionDto> findAllTransaction(TransactionRequestDto request) {
        List<Transaction> transactions;
        if(request.getBankId() != null && request.getBankId() != "") {
            Optional<Bank> bankOpt = bankRepository.findById(request.getBankId());
            if(!bankOpt.isPresent()) {
                throw new EntityNotExistException("Bank not found in the system");
            }
            transactions = transactionRepository.findTransactionsByDateBetweenAndReferenceAndStatusNotOrderByDateDesc(request.getStartDate(), request.getEndDate(), bankOpt.get(), TransactionStatus.NEW);
        } else {
            transactions = transactionRepository.findTransactionsByDateBetweenAndStatusNotOrderByDateDesc(request.getStartDate(), request.getEndDate(), TransactionStatus.NEW);
        }
        return transactions.stream().map(transaction -> {
            TransactionDto dto = new TransactionDto();
            dto.setAmount(transaction.getAmount());
            dto.setContent(transaction.getContent());
            dto.setCreatedDate(transaction.getDate());
            dto.setSource(transaction.getSource());
            dto.setType(transaction.getType());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<TransactionDto> findAllAccountTransaction(String accountId, TransactionType type) {

        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if(!accountOpt.isPresent()) {
            throw new EntityNotExistException("Account not found in the system");
        }
        Account account = accountOpt.get();
        return (type != null ? transactionRepository.findTransactionsBySourceAndTypeAndStatusNotOrderByDateDesc(account.getAccountId(), type, TransactionStatus.NEW) :
                transactionRepository.findTransactionsBySourceAndStatusNotOrderByDateDesc(account.getAccountId(), TransactionStatus.NEW))
                .stream().map(transaction -> {
            TransactionDto dto = new TransactionDto();
            dto.setAmount(transaction.getAmount());
            dto.setContent(transaction.getContent());
            dto.setCreatedDate(transaction.getDate());
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
        ZonedDateTime now = ZonedDateTime.now();
        long currentTime =  now.toInstant().toEpochMilli();
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setContent(dto.getContent());
        transaction.setDate(now);
        transaction.setSource(source.getAccountId());
        transaction.setTarget(target.getAccountId());
        transaction.setStatus(TransactionStatus.NEW);
        transaction.setType(dto.getType());
        transaction.setFeeType(dto.getFeeType());

        long expires = currentTime + 1000L * (30 * 60);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        String opt = tokenProvider.generateRandomSeries("0123456789", 6);
        transaction.setOtpCode(opt);
        transaction.setValidity(expires);

        transaction = transactionRepository.save(transaction);
        dto.setId(transaction.getId());
        dto.setOtpCode(opt);
        dto.setCreatedDate(now);
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        mailService.sendTransactionConfirmationEmail(userDetails, opt);
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
        long currentTime = System.currentTimeMillis();
        if(!dto.getOtpCode().equals(transaction.getOtpCode()) || transaction.getValidity() < currentTime) {
            transaction.setStatus(TransactionStatus.CANCEL);
            transactionRepository.save(transaction);
            throw new InvalidTransactionException("Invalid OTP or expired!");
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
        receive.setFeeType(transaction.getFeeType());
        receive.setAmount(transaction.getAmount());
        receive.setContent(transaction.getContent());
        receive.setDate(ZonedDateTime.now());
        receive.setSource(target.getAccountId());
        receive.setTarget(source.getAccountId());
        receive.setStatus(TransactionStatus.COMPLETED);
        receive.setType(TransactionType.INCOME);
        transactionRepository.save(receive);
    }

}
