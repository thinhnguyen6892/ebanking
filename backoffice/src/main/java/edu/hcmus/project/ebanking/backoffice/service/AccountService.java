package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.resource.account.dto.StatusAccount;
import edu.hcmus.project.ebanking.data.model.Account;
import edu.hcmus.project.ebanking.data.model.Bank;
import edu.hcmus.project.ebanking.data.model.contranst.AccountType;
import edu.hcmus.project.ebanking.data.model.User;
import edu.hcmus.project.ebanking.data.repository.AccountRepository;
import edu.hcmus.project.ebanking.data.repository.BankRepository;
import edu.hcmus.project.ebanking.data.repository.UserRepository;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.AccountDto;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.CreateAccount;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.DepositAccount;
import edu.hcmus.project.ebanking.backoffice.resource.exception.BadRequestException;
import edu.hcmus.project.ebanking.backoffice.service.restclient.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private RestClientService clientService;


    public List<AccountDto> findUserAccounts(Long userId, boolean details) {
        Optional<User> userOp = userRepository.findById(userId);
        if (userOp.isPresent()) {
            return accountRepository.findAccountsByOwner(userOp.get()).stream()
                    .map(account -> {
                        AccountDto dto = new AccountDto();
                        dto.setAccountId(account.getAccountId());
                        dto.setBalance(account.getBalance());
                        if(details) {
                            dto.setCreateDate(account.getCreateDate());
                            dto.setExpired(account.getExpired());
                        }
                        dto.setOwnerName(account.getOwner().getUsername());
                        dto.setType(account.getType());
                        dto.setStatus(account.getStatus());
                        return dto;
                    }).collect(Collectors.toList());
        }
        throw new BadRequestException("User is not exist");
    }

    public void depositAccount(DepositAccount dto) {
        User employee = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        transactionService.depositTransaction(employee, dto);
    }

    public String createAccount(User owner, CreateAccount dto, AccountType accountType) {
        Account account = new Account();
        account.setType(accountType);
        account.setOwner(owner);
        account.setStatus(dto.getStatus());
        account.setBalance(dto.getBalance());
        account = accountRepository.save(account);
        return account.getAccountId();
    }

    public String createAccount(CreateAccount dto) {
        Optional<User> userOp = userRepository.findById(dto.getOwnerId());
        if (userOp.isPresent()) {
            return createAccount(userOp.get(), dto, AccountType.SAVING);
        } else {
            throw new BadRequestException("User not found in the system");
        }
    }

    public AccountDto findBankAccount(String accountId, String bankId) {
        Optional<Bank> bankOpt = bankRepository.findById(bankId);
        if(!bankOpt.isPresent()) {
            throw new BadRequestException("Invalid Bank Id");
        }
        Bank bank = bankOpt.get();
        switch (bank.getSignType()) {
            case RSA: return clientService.getRsaClientAccountInfo(bank,accountId);
            default: PGP: return null;
        }
    }


    public AccountDto findAccountByAccountId(String accountId) {
        AccountDto result = new AccountDto();
        Optional<Account> accountOpt = accountRepository.findByAccountIdAndTypeIsNot(accountId, AccountType.SYSTEM);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            result.setAccountId(account.getAccountId());
            result.setOwnerName(account.getOwner().getUsername());
        }
        return result;
    }

    public List<AccountDto> findAccountByUserName(String userName) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(userName));
        if (user.isPresent()) {
            return  accountRepository.findAccountsByOwner(user.get()).stream()
                    .map(account -> {
                        AccountDto accountDto = new AccountDto();
                        accountDto.setOwnerName(account.getOwner().getUsername());
                        accountDto.setAccountId(account.getAccountId());
                        return accountDto;
                    }).collect(Collectors.toList());
        }
        return null;
    }

    public StatusAccount closeAccount (String accountId, StatusAccount dto){
        Optional<Account> accountOp = accountRepository.findById(accountId);
        if(accountOp.isPresent()){
            Account account = accountOp.get();
            account.setStatus(dto.getStatus());
            accountRepository.save(account);
            return dto;
        }
        throw new BadRequestException("Account not found exception");
    }
}
