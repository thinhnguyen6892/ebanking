package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.Account;
import edu.hcmus.project.ebanking.backoffice.model.Role;
import edu.hcmus.project.ebanking.backoffice.model.Token;
import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.repository.AccountRepository;
import edu.hcmus.project.ebanking.backoffice.repository.RoleRepository;
import edu.hcmus.project.ebanking.backoffice.repository.UserBankRepository;
import edu.hcmus.project.ebanking.backoffice.resource.account.AccountDto;
import edu.hcmus.project.ebanking.backoffice.resource.exception.EntityNotExistException;
import edu.hcmus.project.ebanking.backoffice.resource.exception.ResourceNotFoundException;
import edu.hcmus.project.ebanking.backoffice.resource.exception.TokenException;
import edu.hcmus.project.ebanking.backoffice.resource.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserBankRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setUsername(user.getUsername());
            dto.setStatus(user.getStatus());
            dto.setRole(user.getRole());
            dto.setEmail(user.getEmail());
            return dto;
        }).collect(Collectors.toList());
    }

    public AccountDto findAccount(String accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if(accountOpt.isPresent()) {
            Account account = accountOpt.get();
            AccountDto dto = new AccountDto();
            dto.setAccountId(account.getAccountId());
            dto.setBalance(account.getBalance());
            dto.setCreateDate(account.getCreateDate());
            dto.setExpired(account.getExpired());
            dto.setOwnerName(account.getOwner().getUsername());
            dto.setType(account.getType());
            return dto;
        }
        throw new ResourceNotFoundException("Account not found");
    }

    public List<AccountDto> findUserAccount(Long userId) {
        Optional<User> userOp = userRepository.findById(userId);
        if(userOp.isPresent()) {
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
        throw new EntityNotExistException("User is not exist");
    }

    public String createAccount(AccountDto dto) {
        Optional<User> userOp = userRepository.findById(dto.getOwnerId());
        if(userOp.isPresent()) {
            return createAccount(userOp.get(), dto);
        } else {
            throw new EntityNotExistException("User not found in the system");
        }
    }

    public String updateAccount(AccountDto dto) {
        Optional<Account> accountOpt = accountRepository.findById(dto.getAccountId());
        if(accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setStatus(dto.getStatus());
            account.setBalance(dto.getBalance());
            account = accountRepository.save(account);
            return account.getAccountId();
        }
        throw new EntityNotExistException("Account not found in the system");
    }

    public String createAccount(User owner, AccountDto dto) {
        Account account = new Account();
        account.setType(dto.getType());
        account.setOwner(owner);
        account.setStatus(dto.getStatus());
        account.setBalance(dto.getBalance());
        account = accountRepository.save(account);
        return account.getAccountId();
    }

    @Transactional
    public boolean createUser(UserDto dto) {
        Optional<Role> roleOp = roleRepository.findById("USER");
        if(roleOp.isPresent()) {
            User newUser = new User();
            newUser.setUsername(dto.getUsername());
            newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            newUser.setRole(roleOp.get());
            newUser.setStatus(dto.getStatus());
            newUser.setEmail(dto.getEmail());
            newUser = userRepository.save(newUser);
            AccountDto accountDto = new AccountDto();
            accountDto.setType("Payment");
            accountDto.setStatus(true);
            accountDto.setBalance(0.0);
            createAccount(newUser, accountDto);
            return true;
        }
        return false;
    }
    public String recoverPassword(String email, String token, String password, HttpServletRequest request) throws URISyntaxException {
        Optional<User> userOpt = userRepository.findOneByEmail(email);
        if (!userOpt.isPresent()) {
            throw new EntityNotExistException("User not found in the system");
        }
        User user = userOpt.get();
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(user.getAuthorities());
        if (!StringUtils.isEmpty(token)) {
            if(!tokenProvider.validateToken(token, user)) {
                throw new TokenException("Invalid token!");
            }
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);
            userRepository.save(user);
            return "Password changed";
        } else {
            Token emailToken = tokenProvider.createToken(user);
//                mailService.sendRecoverPasswordEmail(user, emailToken.getToken(), buildBaseUrl(request));
            return emailToken.getToken();
        }
    }


}
