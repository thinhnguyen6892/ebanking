package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.*;
import edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionStatus;
import edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionType;
import edu.hcmus.project.ebanking.backoffice.repository.AccountRepository;
import edu.hcmus.project.ebanking.backoffice.repository.BankRepository;
import edu.hcmus.project.ebanking.backoffice.repository.TransactionRepository;
import edu.hcmus.project.ebanking.backoffice.repository.UserRepository;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.DepositAccount;
import edu.hcmus.project.ebanking.backoffice.resource.exception.BadRequestException;
import edu.hcmus.project.ebanking.backoffice.resource.exception.InvalidTransactionException;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.CreateTransactionRequestDto;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.TransactionConfirmationDto;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.TransactionDto;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.TransactionQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private UserRepository userRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${app.transaction.validation.in.seconds}")
    private Long transactionExpiration;

    @Value("${app.dev.mode}")
    private Boolean devMode;


    public List<TransactionDto> findAllTransaction(TransactionQueryDto request) {
        List<Transaction> transactions;
        if(request.getBankId() != null && request.getBankId() != "") {
            Optional<Bank> bankOpt = bankRepository.findById(request.getBankId());
            if(!bankOpt.isPresent()) {
                throw new BadRequestException("Bank not found in the system");
            }
            transactions = transactionRepository.findTransactionsByDateBetweenAndReferenceAndStatusOrderByDateDesc(request.getStartDate(), request.getEndDate(), bankOpt.get(), TransactionStatus.COMPLETED);
        } else {
            transactions = transactionRepository.findTransactionsByDateBetweenAndStatusOrderByDateDesc(request.getStartDate(), request.getEndDate(), TransactionStatus.COMPLETED);
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

    public List<TransactionDto> findUserAccountTransaction(@Nullable User owner, String accountId, TransactionType type) {
        Optional<Account> accountOpt = owner == null ? accountRepository.findById(accountId) : accountRepository.findByOwnerAndAccountId(owner, accountId);
        if(!accountOpt.isPresent()) {
            throw new BadRequestException("Account not found in the system");
        }
        Account account = accountOpt.get();
        return (type != null ? transactionRepository.findTransactionsBySourceAndTypeAndStatusOrderByDateDesc(account.getAccountId(), type, TransactionStatus.COMPLETED) :
                transactionRepository.findTransactionsBySourceAndStatusOrderByDateDesc(account.getAccountId(), TransactionStatus.COMPLETED))
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

    public List<TransactionDto> findAllAccountTransaction(String accountId, TransactionType type) {
        return findUserAccountTransaction(null, accountId, type);
    }

    public TransactionDto requestTransaction(User owner, CreateTransactionRequestDto dto) {
        Optional<Account> sourceOpt = accountRepository.findByOwnerAndAccountId(owner, dto.getSource());
        if(!sourceOpt.isPresent()) {
            throw new BadRequestException("Source account is not exist!");
        }
        Account source = sourceOpt.get();
        Optional<Account> targetOpt = accountRepository.findById(dto.getTarget());
        if(!targetOpt.isPresent()) {
            throw new BadRequestException("Target account is not exist!");
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

        long expires = currentTime + transactionExpiration;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        String opt = tokenProvider.generateRandomSeries("0123456789", 6);
        transaction.setOtpCode(opt);
        transaction.setValidity(expires);

        transaction = transactionRepository.save(transaction);

        TransactionDto transactionDto = new TransactionDto(transaction);
        if(devMode) {
            transactionDto.setOtpCode(opt);
        }
        mailService.sendTransactionConfirmationEmail(owner, opt);
        return transactionDto;
    }


    public void pay(User owner, TransactionConfirmationDto dto) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(dto.getId());
        if(!transactionOptional.isPresent()) {
            throw new BadRequestException("Transaction is not exist!");
        }
        Transaction transaction = transactionOptional.get();
        if(TransactionStatus.COMPLETED == transaction.getStatus() || TransactionStatus.CANCEL == transaction.getStatus()) {
            throw new BadRequestException("Transaction is not exist!");
        }
        ZonedDateTime now = ZonedDateTime.now();
        long currentTime =  now.toInstant().toEpochMilli();
        if(!dto.getOtpCode().equals(transaction.getOtpCode()) || transaction.getValidity() < currentTime) {
            transaction.setStatus(TransactionStatus.CANCEL);
            transactionRepository.save(transaction);
            throw new InvalidTransactionException("Invalid OTP or expired!");
        }
        performTransaction(owner, transaction);
    }

    @Transactional
    public void performTransaction(User owner, Transaction transaction) {
        Optional<Account> sourceOpt = accountRepository.findByOwnerAndAccountId(owner, transaction.getSource());
        if(!sourceOpt.isPresent()) {
            throw new BadRequestException("Source account is not exist!");
        }
        Account source = sourceOpt.get();
        Optional<Account> targetOpt = accountRepository.findById(transaction.getTarget());
        if(!targetOpt.isPresent()) {
            throw new BadRequestException("Target account is not exist!");
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
        receive.setType(TransactionType.DEPOSIT);
        transactionRepository.save(receive);
    }


    @Transactional
    public void depositTransaction(User employee, DepositAccount transactionDto) {
        Account senderAccount, receiverAccount;
        List<Account> systemAccounts = accountRepository.findAccountsByOwner(employee);
        if(systemAccounts.isEmpty()) {
            throw new BadRequestException("Current user don't have permission to perform this deposit transaction.");
        }
        senderAccount = systemAccounts.get(0);

        if(StringUtils.isEmpty(transactionDto.getAccountId())) {
            User receiver = userRepository.findByUsernameAndStatusIsTrue(transactionDto.getUsername());
            if(receiver == null) {
                throw new BadRequestException("User is not exist");
            }
            List<Account> receiverAccounts = accountRepository.findAccountsByOwnerAndType(receiver, "PAYMENT");
            if(receiverAccounts.isEmpty()) {
                throw new BadRequestException("Receiver user doesn't have PAYMENT account.");
            }
            receiverAccount = receiverAccounts.get(0);
        } else {
            Optional<Account> receiverAccountOpt = accountRepository.findById(transactionDto.getAccountId());
            if(!receiverAccountOpt.isPresent()) {
                throw new BadRequestException("Receiver's account doesn't exist.");
            }
            receiverAccount = receiverAccountOpt.get();
        }

        Double targetBalance = receiverAccount.getBalance();
        receiverAccount.setBalance(targetBalance + transactionDto.getAmount());
        accountRepository.save(receiverAccount);

        ZonedDateTime now = ZonedDateTime.now();
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setContent(transactionDto.getContent());
        transaction.setDate(now);
        transaction.setSource(senderAccount.getAccountId());
        transaction.setTarget(receiverAccount.getAccountId());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setFeeType(transactionDto.getFeeType());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

    }

}
