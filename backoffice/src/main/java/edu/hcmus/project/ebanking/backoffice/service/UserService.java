package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.data.model.*;
import edu.hcmus.project.ebanking.data.model.contranst.AccountType;
import edu.hcmus.project.ebanking.data.repository.AccountRepository;
import edu.hcmus.project.ebanking.data.repository.RoleRepository;
import edu.hcmus.project.ebanking.data.repository.UserRepository;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.AccountDto;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.CreateAccount;
import edu.hcmus.project.ebanking.backoffice.resource.exception.BadRequestException;
import edu.hcmus.project.ebanking.backoffice.resource.user.dto.ClassDto;
import edu.hcmus.project.ebanking.backoffice.resource.user.dto.CreateUserDto;
import edu.hcmus.project.ebanking.backoffice.resource.user.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static edu.hcmus.project.ebanking.backoffice.service.TokenProvider.*;

@Service
public class UserService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Value("${app.user.password.recovery.validation.in.seconds}")
    private Long expiration;

    @Value("${app.dev.mode}")
    private Boolean devMode;

    public List<UserDto> findAllStaffs() {
        return findAllUsers("STAFF", false);
    }

    public List<UserDto> findAllCustomer() {
        return findAllUsers("USER", false);
    }

    public List<UserDto> findAllUsers(String roleStr, boolean showRole) {
        List<User> users;
        if(!StringUtils.isEmpty(roleStr)) {
            Role role = roleRepository.findById(roleStr).get();
            users =  userRepository.findByRole(role);
        } else {
            users= userRepository.findAll();
        }
        return users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId().toString());
            dto.setUsername(user.getUsername());
            dto.setStatus(user.getStatus());
            if(showRole) {
                dto.setUserType(user.getRole().getRoleId());
            }
            dto.setEmail(user.getEmail());
            return dto;
        }).collect(Collectors.toList());
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
        throw new BadRequestException("Account not found in the system");
    }

    public String generatePassword(int length) {
        return tokenProvider.generateRandomSeries(LOWER+UPPER+DIGITS+PUNCTUATION, length);
    }

    public UserDto createEmployee(CreateUserDto dto) {
        return createUser(dto, "STAFF", generatePassword(16), true);
    }

    public UserDto createCustomer(CreateUserDto dto) {
        return createUser(dto, "USER", generatePassword(16),false);
    }

    @Transactional
    public UserDto createUser(CreateUserDto dto, String roleStr, String rawPassword, boolean isEmployee) {
        Optional<Role> roleOp = roleRepository.findById(roleStr);
        if(roleOp.isPresent()) {
            User checkUser = userRepository.findByUsername(dto.getUsername());
            if(checkUser == null) {
                User newUser = new User();
                newUser.setUsername(dto.getUsername());
                newUser.setPassword(passwordEncoder.encode(rawPassword));
                newUser.setRole(roleOp.get());
                newUser.setStatus(Boolean.TRUE);
                newUser.setEmail(dto.getEmail());
                newUser.setFirstName(dto.getFirstName());
                newUser.setLastName(dto.getLastName());
                newUser.setPhone(dto.getPhone());
                newUser = userRepository.save(newUser);

                CreateAccount accountDto = new CreateAccount();
                accountDto.setStatus(true);
                accountDto.setBalance(0.0);
                if(!isEmployee) {
                    accountService.createAccount(newUser, accountDto, AccountType.SYSTEM);
                } else {
                    accountService.createAccount(newUser, accountDto, AccountType.PAYMENT);
                }
                mailService.sendUserPasswordEmail(newUser, rawPassword);
                return new UserDto(newUser);
            }
        }
        throw new BadRequestException("Invalid user information!");
    }

    public UserDto updateCustomer(UserDto dto, long id) {
        return updateUser(dto, id, "USER");
    }

    public UserDto updateEmployee(UserDto dto, long id){
        return updateUser(dto, id, "STAFF");
    }

    @Transactional
    public UserDto updateUser(UserDto dto, long id, String roleStr){
        Optional<Role> roleOp = roleRepository.findById(roleStr);
        if(roleOp.isPresent()){
            Optional<User> upUser = userRepository.findById(id);
            if(upUser.isPresent()) {
                User user = upUser.get();
                user.setFirstName(dto.getFirstName());
                user.setLastName(dto.getLastName());
                if(!StringUtils.isEmpty(dto.getEmail())) {
                    user.setEmail(dto.getEmail());
                }
                user.setPhone(dto.getPhone());
                if(dto.getStatus() != null) {
                    user.setStatus(dto.getStatus());
                }
                userRepository.save(user);
                return dto;
            }
        }
        throw new BadRequestException("User not found exception");
    }

    public String recoverPassword(String email, String redirectUrl, String token, String password) {
        Optional<User> userOpt = userRepository.findOneByEmail(email);
        if (!userOpt.isPresent()) {
            throw new BadRequestException("User not found in the system");
        }
        User user = userOpt.get();
        if (!StringUtils.isEmpty(token)) {
            ZonedDateTime now = ZonedDateTime.now();
            long currentTime =  now.toInstant().toEpochMilli();
            if(!token.equals(user.getOtpCode()) || user.getValidity() < currentTime) {
                throw new BadRequestException("Invalid OTP or expired!");
            }
            if(StringUtils.isEmpty(password)) {
                throw new BadRequestException("Password cannot be blank");
            }
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);
            userRepository.save(user);
            return "Password changed";
        } else {
            if(StringUtils.isEmpty(redirectUrl)) {
                throw new BadRequestException("Redirect Url is required.");
            }

            String emailToken = tokenProvider.generateRandomSeries(DIGITS, 12);
            long currentTime =  ZonedDateTime.now().toInstant().toEpochMilli();
            long expires = currentTime + expiration;
            user.setOtpCode(emailToken);
            user.setValidity(expires);
            userRepository.save(user);
            mailService.sendRecoverPasswordEmail(user, emailToken, redirectUrl);
            if(devMode) {
                return emailToken;
            }
            return "Mail is sent!";
        }
    }

    public String changePassword(User user, String rawOldPass, String rawNewPass) {
        if(passwordEncoder.matches(rawOldPass, user.getPassword())) {
            String encryptedPassword = passwordEncoder.encode(rawNewPass);
            user.setPassword(encryptedPassword);
            userRepository.save(user);
            return "Password changed";
        }
        throw new BadRequestException("Your old password is incorrect");
    }

    public UserDto findUserByUserName(String userName){
        UserDto user = new UserDto();
        if(userName != null && !userName.trim().isEmpty())
            return user;
        User findUser = userRepository.findByUsername(userName);
        user.setEmail(findUser.getEmail());
        user.setId(findUser.getId().toString());
        // user.setFullName();
        // user.setPhone();
        return user;
    }

    public boolean deleteUser(long id){
        Optional<Role> roleOp = roleRepository.findById("STAFF");
        if(roleOp.isPresent()) {
            Optional<User> deUser = userRepository.findById(id);
            if(deUser.isPresent()){
                List<Account> accounts = accountRepository.findAccountsByOwner(deUser.get());
                if (!accounts.isEmpty()){
                    for (Account account : accounts){
                        accountRepository.deleteById(account.getAccountId());
                    }
                }
                userRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    public boolean checkUsername(ClassDto dto){
        String msg;
        User user = userRepository.findByUsername(dto.getUsername());
        if(user != null){
            return false;
        }
        return true;
    }

    public boolean checkEmail(ClassDto dto){
        User user = userRepository.findByEmail(dto.getEmail());
        if(user != null){
            return false;
        }
        return true;
    }

    public UserDto findEmployeeByUsername(String username){
        User user = userRepository.findByUsername(username);
        UserDto dto = new UserDto();
        Optional<Role> roleOp = roleRepository.findById("STAFF");
        if(user.getRole() == roleOp.get()){
            dto.setId(user.getId().toString());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            return dto;
        }
        throw new BadRequestException("Employee not found in the system");
    }
}
