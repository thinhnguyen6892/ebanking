package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.Account;
import edu.hcmus.project.ebanking.backoffice.model.Debt;
import edu.hcmus.project.ebanking.backoffice.model.Transaction;
import edu.hcmus.project.ebanking.backoffice.model.contranst.AccountType;
import edu.hcmus.project.ebanking.backoffice.model.contranst.DebtStatus;
import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionFeeType;
import edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionType;
import edu.hcmus.project.ebanking.backoffice.repository.*;
import edu.hcmus.project.ebanking.backoffice.resource.debt.dto.*;
import edu.hcmus.project.ebanking.backoffice.resource.exception.BadRequestException;
import edu.hcmus.project.ebanking.backoffice.resource.exception.ResourceNotFoundException;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.CreateTransactionRequestDto;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.TransactionDto;
import edu.hcmus.project.ebanking.backoffice.resource.user.dto.CreateUserDto;
import edu.hcmus.project.ebanking.backoffice.resource.user.dto.UserDto;
import edu.hcmus.project.ebanking.backoffice.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DebtService {
    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    public List<DebtDto> GetAllDebt(){
        return debtRepository.findAll().stream().map(debt -> {
            DebtDto dto = new DebtDto();
            dto.setStatus(debt.getStatus());
            dto.setHolder(debt.getHolder().getId());
            dto.setDebtor(debt.getDebtor().getAccountId());
            dto.setContent(debt.getContent());
            dto.setAmount(debt.getAmount());
            return dto;
        }).collect(Collectors.toList());
    }

    public DebtDto findDebt(int id){
        Optional<Debt> DebtOp = debtRepository.findById(id);
        DebtDto dto = new DebtDto();
        if(DebtOp.isPresent()){
            Debt debt = DebtOp.get();
            dto.setStatus(debt.getStatus());
            dto.setHolder(debt.getHolder().getId());
            dto.setDebtor(debt.getDebtor().getAccountId());
            dto.setContent(debt.getContent());
            dto.setAmount(debt.getAmount());
            return dto;
        }
        throw new ResourceNotFoundException("Debt not found");
    }

    public List<DebtDto> findDebtbyHolderOrDebtor(int type) {
        Optional<User> userOp = userRepository.findById(JwtTokenUtil.getLoggedUser().getId());
        List<Account> accountOp = accountRepository.findAccountsByOwner(userOp.get());
        if(userOp.isPresent()){
            if(type == 0)
            {
                for (Account account : accountOp){
                    return debtRepository.findDebtByHolderOrDebtor(userOp.get(), account).stream().map(debt -> {
                        DebtDto dto = new DebtDto();
                        dto.setId(debt.getId());
                        dto.setStatus(debt.getStatus());
                        dto.setHolder(debt.getHolder().getId());
                        Optional<User> userOpHolder = userRepository.findById(dto.getHolder());
                        if(userOpHolder.isPresent()){
                            User userholder = userOpHolder.get();
                            UserDto userHolder = new UserDto();
                            if(userHolder != null){
                                userHolder.setId(userholder.getId().toString());
                                userHolder.setFirstName(userholder.getFirstName());
                                userHolder.setLastName(userholder.getLastName());
                                dto.setUserHolder(userHolder);
                            }
                        }
                        dto.setDebtor(debt.getDebtor().getAccountId());
                        Optional<Account> accountop = accountRepository.findById(dto.getDebtor());
                        Optional<User> userop = userRepository.findById(accountop.get().getOwner().getId());
                        if(userop.isPresent()){
                            User userdebtor = userop.get();
                            UserDto userDebtor = new UserDto();
                            if(userDebtor != null){
                                userDebtor.setId(userdebtor.getId().toString());
                                userDebtor.setFirstName(userdebtor.getFirstName());
                                userDebtor.setLastName(userdebtor.getLastName());
                                dto.setUserDebtor(userDebtor);
                            }
                        }
                        dto.setCreateDate(debt.getCreateDate());
                        dto.setContent(debt.getContent());
                        dto.setAmount(debt.getAmount());
                        return dto;
                    }).collect(Collectors.toList());
                }
            }
            else if (type == 1){
                return debtRepository.findDebtByHolder(userOp.get()).stream().map(debt -> {
                    DebtDto dto = new DebtDto();
                    dto.setId(debt.getId());
                    dto.setStatus(debt.getStatus());
                    dto.setHolder(debt.getHolder().getId());
                    Optional<User> userOpHolder = userRepository.findById(dto.getHolder());
                    if(userOpHolder.isPresent()){
                        User userholder = userOpHolder.get();
                        UserDto userHolder = new UserDto();
                        if(userHolder != null){
                            userHolder.setId(userholder.getId().toString());
                            userHolder.setFirstName(userholder.getFirstName());
                            userHolder.setLastName(userholder.getLastName());
                            dto.setUserHolder(userHolder);
                        }
                    }
                    dto.setDebtor(debt.getDebtor().getAccountId());
                    Optional<Account> accountop = accountRepository.findById(dto.getDebtor());
                    Optional<User> userop = userRepository.findById(accountop.get().getOwner().getId());
                    if(userop.isPresent()){
                        User userdebtor = userop.get();
                        UserDto userDebtor = new UserDto();
                        if(userDebtor != null){
                            userDebtor.setId(userdebtor.getId().toString());
                            userDebtor.setFirstName(userdebtor.getFirstName());
                            userDebtor.setLastName(userdebtor.getLastName());
                            dto.setUserDebtor(userDebtor);
                        }
                    }
                    dto.setCreateDate(debt.getCreateDate());
                    dto.setContent(debt.getContent());
                    dto.setAmount(debt.getAmount());
                    return dto;
                }).collect(Collectors.toList());
            }
            else if (type == 2){
                for (Account account : accountOp){
                    return debtRepository.findDebtByDebtor(account).stream().map(debt -> {
                        DebtDto dto = new DebtDto();
                        dto.setId(debt.getId());
                        dto.setStatus(debt.getStatus());
                        dto.setHolder(debt.getHolder().getId());
                        Optional<User> userOpHolder = userRepository.findById(dto.getHolder());
                        if(userOpHolder.isPresent()){
                            User userholder = userOpHolder.get();
                            UserDto userHolder = new UserDto();
                            if(userHolder != null){
                                userHolder.setId(userholder.getId().toString());
                                userHolder.setFirstName(userholder.getFirstName());
                                userHolder.setLastName(userholder.getLastName());
                                dto.setUserHolder(userHolder);
                            }
                        }
                        dto.setDebtor(debt.getDebtor().getAccountId());
                        Optional<Account> accountop = accountRepository.findById(dto.getDebtor());
                        Optional<User> userop = userRepository.findById(accountop.get().getOwner().getId());
                        if(userop.isPresent()){
                            User userdebtor = userop.get();
                            UserDto userDebtor = new UserDto();
                            if(userDebtor != null){
                                userDebtor.setId(userdebtor.getId().toString());
                                userDebtor.setFirstName(userdebtor.getFirstName());
                                userDebtor.setLastName(userdebtor.getLastName());
                                dto.setUserDebtor(userDebtor);
                            }
                        }
                        dto.setCreateDate(debt.getCreateDate());
                        dto.setContent(debt.getContent());
                        dto.setAmount(debt.getAmount());
                        return dto;
                    }).collect(Collectors.toList());
                }
            }
        }
        throw new BadRequestException("Debt not found in the system!");
    }

    public List<DebtDto> findNewDebtByDebtor(){
        Optional<User> userOp = userRepository.findById(JwtTokenUtil.getLoggedUser().getId());
        List<Account> accountOp = accountRepository.findAccountsByOwner(userOp.get());
        if(userOp.isPresent()){
            for (Account account : accountOp){
                return debtRepository.findNewDebtByDebtorAndStatus(account, DebtStatus.NEW).stream().map(debt -> {
                    DebtDto dto = new DebtDto();
                    dto.setId(debt.getId());
                    dto.setStatus(debt.getStatus());
                    dto.setHolder(debt.getHolder().getId());
                    Optional<User> userOpHolder = userRepository.findById(dto.getHolder());
                    if(userOpHolder.isPresent()){
                        User userholder = userOpHolder.get();
                        UserDto userHolder = new UserDto();
                        if(userHolder != null){
                            userHolder.setId(userholder.getId().toString());
                            userHolder.setFirstName(userholder.getFirstName());
                            userHolder.setLastName(userholder.getLastName());
                            dto.setUserHolder(userHolder);
                        }
                    }
                    dto.setDebtor(debt.getDebtor().getAccountId());
                    Optional<Account> accountop = accountRepository.findById(dto.getDebtor());
                    Optional<User> userop = userRepository.findById(accountop.get().getOwner().getId());
                    if(userop.isPresent()){
                        User userdebtor = userop.get();
                        UserDto userDebtor = new UserDto();
                        if(userDebtor != null){
                            userDebtor.setId(userdebtor.getId().toString());
                            userDebtor.setFirstName(userdebtor.getFirstName());
                            userDebtor.setLastName(userdebtor.getLastName());
                            dto.setUserDebtor(userDebtor);
                        }
                    }
                    dto.setCreateDate(debt.getCreateDate());
                    dto.setContent(debt.getContent());
                    dto.setAmount(debt.getAmount());
                    return dto;
                }).collect(Collectors.toList());
            }
        }
        throw new BadRequestException("Debt not found in the system!");
    }

    public List<DebtUserDto> search (String id){
        Optional<Account> accountOp = accountRepository.findById(id);
        if(accountOp.isPresent()){
            return accountRepository.findByAccountIdStartingWith(id).stream().map(account -> {
                DebtUserDto dto = new DebtUserDto();
                dto.setAccountId(account.getAccountId());
                Optional<User> user = userRepository.findById(account.getOwner().getId());
                dto.setFirstName(user.get().getFirstName());
                dto.setLastName(user.get().getLastName());
                dto.setEmail(user.get().getEmail());
                dto.setPhone(user.get().getPhone());
                return  dto;
            }).collect(Collectors.toList());
        }
        throw new BadRequestException("Account not found in the system!");
    }

    public boolean checkContent (String content){
        if(content.isEmpty()){
            return false;
        }
        return true;
    }

    public boolean checkAmount(String number){
        if(number.isEmpty()){
            return false;
        }
        try{
            Double.parseDouble(number);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }

    public boolean checkDebtor (String debtor){
        if(debtor.isEmpty()){
            return false;
        }
        return true;
    }

    @Transactional
    public boolean createDebt(CreateDebtDto dto){
        Optional<Account> accop = accountRepository.findById(dto.getDebtor());
        if (accop.isPresent()) {
            Debt newDebt = new Debt();
            newDebt.setCreateDate(new Date());
            newDebt.setStatus(DebtStatus.NEW);
            newDebt.setHolder(JwtTokenUtil.getLoggedUser());
            newDebt.setDebtor(accop.get());
            newDebt.setContent(dto.getContent());
            newDebt.setAmount(dto.getAmount());
            debtRepository.save(newDebt);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateDebt(CreateDebtDto dto, int id){
        Optional<Debt> DebtOp = debtRepository.findById(id);
        Optional<Account> accop = accountRepository.findById(dto.getDebtor());
        if(DebtOp.isPresent()) {
            if(accop.isPresent()){
                Debt upDebt = DebtOp.get();
                upDebt.setCreateDate(new Date());
                upDebt.setStatus(dto.getStatus());
                upDebt.setHolder(JwtTokenUtil.getLoggedUser());
                upDebt.setDebtor(accop.get());
                upDebt.setContent(dto.getContent());
                upDebt.setAmount(dto.getAmount());
                debtRepository.save(upDebt);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public String CancelDebt(CancelDto dto, int id){
        Optional<Debt> debtOp = debtRepository.findById(id);
        if(dto.getContent().isEmpty()){
            return "Content is empty";
        }
        Debt debt = debtOp.get();
        if(debtOp.isPresent()){
            if(debt.getHolder().getId().equals(JwtTokenUtil.getLoggedUser().getId())){
                Optional<Account> accountop = accountRepository.findById(debt.getDebtor().getAccountId());
                Optional<User> userop = userRepository.findById(accountop.get().getOwner().getId());
                if(userop.isPresent()){
                    mailService.sendCancelDebtNotificationEmail(userop.get(), id, debt, dto.getContent());
                }
            }
            else{
                Optional<Account> accountop = accountRepository.findById(debt.getDebtor().getAccountId());
                Optional<User> userop = userRepository.findById(accountop.get().getOwner().getId());
                if(userop.get().getId().equals(JwtTokenUtil.getLoggedUser().getId())){
                    mailService.sendCancelDebtNotificationEmail(debt.getHolder(), id, debt, dto.getContent());
                }
            }
            debtRepository.deleteById(id);
            return "Successful";
        }
        return "Unsuccessful";
    }

    public DebtPaymentDto pay(Integer id) {
        Optional<Debt> debtOpt = debtRepository.findDebtByIdAndStatus(id, DebtStatus.NEW);
        if(!debtOpt.isPresent()) {
            throw new BadRequestException("Debt not found in the system!");
        }
        Debt debt = debtOpt.get();
        if(!debt.getDebtor().getOwner().getId().equals(JwtTokenUtil.getLoggedUser().getId())) {
            throw new BadRequestException("Debt not found in the system!");
        }
        CreateTransactionRequestDto transactionRequestDto = new CreateTransactionRequestDto();
        transactionRequestDto.setType(TransactionType.PAYMENT);
        transactionRequestDto.setAmount(debt.getAmount());
        transactionRequestDto.setContent(debt.getContent());
        transactionRequestDto.setFeeType(TransactionFeeType.SENDER);
        transactionRequestDto.setSource(debt.getDebtor().getAccountId());
        List<Account> receiverAccounts = accountRepository.findAccountsByOwnerAndType(debt.getHolder(), AccountType.PAYMENT);
        if(receiverAccounts.isEmpty()) {
            throw new BadRequestException("Receiver user doesn't have PAYMENT account.");
        }
        Account receiverAccount = receiverAccounts.get(0);
        transactionRequestDto.setTarget(receiverAccount.getAccountId());
        TransactionDto transactionDto = transactionService.requestTransaction(JwtTokenUtil.getLoggedUser(), transactionRequestDto);
        Optional<Transaction> transactionOptional = transactionRepository.findById(transactionDto.getId());
        if(!transactionOptional.isPresent()) {
            throw new RuntimeException("Cannot create transaction for this action!");
        }
        debt.setPaymentRef(transactionOptional.get());
        debtRepository.save(debt);
        DebtPaymentDto debtPaymentDto = new DebtPaymentDto(debt);
        debtPaymentDto.setTransactionInfo(transactionDto);
        return debtPaymentDto;
    }
}
