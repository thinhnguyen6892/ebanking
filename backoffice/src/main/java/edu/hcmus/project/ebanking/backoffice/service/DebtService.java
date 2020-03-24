package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.Account;
import edu.hcmus.project.ebanking.backoffice.model.Debt;
import edu.hcmus.project.ebanking.backoffice.model.contranst.DebtStatus;
import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.repository.AccountRepository;
import edu.hcmus.project.ebanking.backoffice.repository.DebtRepository;
import edu.hcmus.project.ebanking.backoffice.repository.UserRepository;
import edu.hcmus.project.ebanking.backoffice.repository.SavedAccountRepository;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.AccountDto;
import edu.hcmus.project.ebanking.backoffice.resource.debt.dto.CreateDebtDto;
import edu.hcmus.project.ebanking.backoffice.resource.debt.dto.DebtDto;
import edu.hcmus.project.ebanking.backoffice.resource.exception.ResourceNotFoundException;
import edu.hcmus.project.ebanking.backoffice.resource.user.dto.UserDto;
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
    private SavedAccountRepository savedAccountRepository;

    public List<CreateDebtDto> GetAllDebt(){
        return debtRepository.findAll().stream().map(debt -> {
            CreateDebtDto dto = new CreateDebtDto();
            dto.setStatus(debt.getStatus());
            dto.setHolder(debt.getHolder().getId());
            dto.setDebtor(debt.getDebtor().getAccountId());
            dto.setContent(debt.getContent());
            dto.setAmount(debt.getAmount());
            return dto;
        }).collect(Collectors.toList());
    }

    public CreateDebtDto findDebt(int id){
        Optional<Debt> DebtOp = debtRepository.findById(id);
        if(DebtOp.isPresent()){
            Debt debt = DebtOp.get();
            CreateDebtDto dto = new CreateDebtDto();
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

    public List<CreateDebtDto> findDebtbyHolderOrDebtor(long userid) {
        Optional<User> userOp = userRepository.findById(userid);
        List<Account> accountOp = accountRepository.findAccountsByOwner(userOp.get());
        if(userOp.isPresent()){
            for (Account account : accountOp){
                return debtRepository.findDebtByHolderOrDebtor(userOp.get(), account).stream().map(debt -> {
                    CreateDebtDto dto = new CreateDebtDto();
                    dto.setStatus(debt.getStatus());
                    dto.setHolder(debt.getHolder().getId());
                    dto.setDebtor(debt.getDebtor().getAccountId());
                    dto.setContent(debt.getContent());
                    dto.setAmount(debt.getAmount());
                    return dto;
                }).collect(Collectors.toList());
            }
        }
        return null;
    }

    public List<CreateDebtDto> findNewDebtByDebtor(String accountId){
        Optional<Account> accountOp = accountRepository.findById(accountId);
        if(accountOp.isPresent()){
            return debtRepository.findNewDebtByDebtorAndStatus(accountOp.get(), DebtStatus.NEW).stream().map(debt -> {
                CreateDebtDto dto = new CreateDebtDto();
                dto.setStatus(debt.getStatus());
                dto.setHolder(debt.getHolder().getId());
                dto.setDebtor(debt.getDebtor().getAccountId());
                dto.setContent(debt.getContent());
                dto.setAmount(debt.getAmount());
                return dto;
            }).collect(Collectors.toList());
        }
        return null;
    }

    public List<DebtDto> search (String id){
        Optional<Account> accountOp = accountRepository.findById(id);
        if(accountOp.isPresent()){
            return accountRepository.findByAccountIdStartingWith(id).stream().map(account -> {
                DebtDto dto = new DebtDto();
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
        Optional<User> Holderop = userRepository.findById(dto.getHolder());
        Optional<Account> accop = accountRepository.findById(dto.getDebtor());
        if (accop.isPresent()) {
            Debt newDebt = new Debt();
            newDebt.setCreateDate(new Date());
            newDebt.setStatus(DebtStatus.NEW);
            newDebt.setHolder(Holderop.get());
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
        Optional<User> Holderop = userRepository.findById(dto.getHolder());
        Optional<Account> accop = accountRepository.findById(dto.getDebtor());
        if(DebtOp.isPresent()) {
            if(accop.isPresent()){
                Debt upDebt = new Debt();
                upDebt.setCreateDate(new Date());
                upDebt.setStatus(dto.getStatus());
                upDebt.setHolder(Holderop.get());
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

}
