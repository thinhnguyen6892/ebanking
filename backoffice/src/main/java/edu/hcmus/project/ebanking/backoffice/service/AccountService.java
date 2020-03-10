package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.Account;
import edu.hcmus.project.ebanking.backoffice.model.contranst.AccountType;
import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.repository.AccountRepository;
import edu.hcmus.project.ebanking.backoffice.repository.UserRepository;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.AccountDto;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.CreateAccount;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.DepositAccount;
import edu.hcmus.project.ebanking.backoffice.resource.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<AccountDto> findUserAccount(Long userId) {
        Optional<User> userOp = userRepository.findById(userId);
        if (userOp.isPresent()) {
            return accountRepository.findAccountsByOwner(userOp.get()).stream()
                    .map(account -> {
                        AccountDto dto = new AccountDto();
                        dto.setAccountId(account.getAccountId());
                        dto.setBalance(account.getBalance());
                        dto.setCreateDate(account.getCreateDate());
                        dto.setExpired(account.getExpired());
                        dto.setOwnerName(account.getOwner().getUsername());
                        dto.setType(account.getType());
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

    public AccountDto findAccountByAccountId(String accountId) {
        AccountDto result = new AccountDto();
        Optional<Account> accountOpt = accountRepository.findById(accountId);
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
    }
}
