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
        if(DebtOp.isPresent()){
            Debt debt = DebtOp.get();
            DebtDto dto = new DebtDto();
            dto.setStatus(debt.getStatus());
            dto.setHolder(debt.getHolder().getId());
            dto.setDebtor(debt.getDebtor().getAccountId());
            dto.setContent(debt.getContent());
            dto.setAmount(debt.getAmount());
            return dto;
        }
        throw new ResourceNotFoundException("Debt not found");
    }

    public Account findAccountByOwner(User user){
        Optional<Account> accountOp = accountRepository.findAccountByOwner(user);
        return accountOp.get();
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
                        dto.setStatus(debt.getStatus());
                        dto.setHolder(debt.getHolder().getId());
                        dto.setHolderFirstName(debt.getHolder().getFirstName());
                        dto.setHolderLastName(debt.getHolder().getLastName());
                        dto.setDebtor(debt.getDebtor().getAccountId());
                        Optional<Account> accountop = accountRepository.findById(dto.getDebtor());
                        Optional<User> userop = userRepository.findById(accountop.get().getOwner().getId());
                        if(userop.isPresent()){
                            dto.setDebtorFirstName(userop.get().getFirstName());
                            dto.setDebtorLastName(userop.get().getLastName());
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
                    dto.setStatus(debt.getStatus());
                    dto.setHolder(debt.getHolder().getId());
                    dto.setHolderFirstName(debt.getHolder().getFirstName());
                    dto.setHolderLastName(debt.getHolder().getLastName());
                    dto.setDebtor(debt.getDebtor().getAccountId());
                    Optional<Account> accountop = accountRepository.findById(dto.getDebtor());
                    Optional<User> userop = userRepository.findById(accountop.get().getOwner().getId());
                    if(userop.isPresent()){
                        dto.setDebtorFirstName(userop.get().getFirstName());
                        dto.setDebtorLastName(userop.get().getLastName());
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
                        dto.setStatus(debt.getStatus());
                        dto.setHolder(debt.getHolder().getId());
                        dto.setHolderFirstName(debt.getHolder().getFirstName());
                        dto.setHolderLastName(debt.getHolder().getLastName());
                        dto.setDebtor(debt.getDebtor().getAccountId());
                        Optional<Account> accountop = accountRepository.findById(dto.getDebtor());
                        Optional<User> userop = userRepository.findById(accountop.get().getOwner().getId());
                        if(userop.isPresent()){
                            dto.setDebtorFirstName(userop.get().getFirstName());
                            dto.setDebtorLastName(userop.get().getLastName());
                        }
                        dto.setCreateDate(debt.getCreateDate());
                        dto.setContent(debt.getContent());
                        dto.setAmount(debt.getAmount());
                        return dto;
                    }).collect(Collectors.toList());
                }
            }
        }
        return null;
    }

    public List<DebtDto> findNewDebtByDebtor(){
        Optional<Account> accountOp = accountRepository.findAccountByOwner(JwtTokenUtil.getLoggedUser());
        if(accountOp.isPresent()){
            return debtRepository.findNewDebtByDebtorAndStatus(accountOp.get(), DebtStatus.NEW).stream().map(debt -> {
                DebtDto dto = new DebtDto();
                dto.setStatus(debt.getStatus());
                dto.setHolder(debt.getHolder().getId());
                dto.setHolderFirstName(debt.getHolder().getFirstName());
                dto.setHolderLastName(debt.getHolder().getLastName());
                dto.setDebtor(debt.getDebtor().getAccountId());
                Optional<Account> accountop = accountRepository.findById(dto.getDebtor());
                Optional<User> userop = userRepository.findById(accountop.get().getOwner().getId());
                if(userop.isPresent()){
                    dto.setDebtorFirstName(userop.get().getFirstName());
                    dto.setDebtorLastName(userop.get().getLastName());
                }
                dto.setCreateDate(debt.getCreateDate());
                dto.setContent(debt.getContent());
                dto.setAmount(debt.getAmount());
                return dto;
            }).collect(Collectors.toList());
        }
        return null;
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
        return null;
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

    public boolean deleteDebt(int id){
        Optional<Debt> deDebt = debtRepository.findById(id);
        if(deDebt.isPresent()) {
            debtRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean changeStatus(CreateDebtDto dto, int id){
        Optional<Debt> DebtOp = debtRepository.findById(id);
        if(DebtOp.isPresent()) {
            Debt upDebt = new Debt();
            upDebt.setStatus(dto.getStatus());
            debtRepository.save(upDebt);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean CancelDebt(CancelDto dto, int id){
        Optional<Debt> debtOp = debtRepository.findById(id);
        if(debtOp.isPresent()){
            Debt debt = debtOp.get();
            if(!debt.getHolder().getId().equals(JwtTokenUtil.getLoggedUser().getId())){
                Optional<Account> accountop = accountRepository.findById(debt.getDebtor().getAccountId());
                Optional<User> userop = userRepository.findById(accountop.get().getOwner().getId());
                if(userop.isPresent()){
                    mailService.sendCancelDebtNotificationEmail(userop.get(), id, debt);
                }
            }
            else{
                Optional<Account> accountop = accountRepository.findById(debt.getDebtor().getAccountId());
                Optional<User> userop = userRepository.findById(accountop.get().getOwner().getId());
                if(!userop.get().getId().equals(JwtTokenUtil.getLoggedUser().getId())){
                    mailService.sendCancelDebtNotificationEmail(debt.getHolder(), id, debt);
                }
            }
            debt.setNote(dto.getNote());
            debt.setStatus(DebtStatus.CANCEL);
            debtRepository.save(debt);
            return true;
        }
        return false;
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
