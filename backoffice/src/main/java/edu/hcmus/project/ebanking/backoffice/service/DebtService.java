package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.Account;
import edu.hcmus.project.ebanking.backoffice.model.Debt;
import edu.hcmus.project.ebanking.backoffice.model.contranst.DebtStatus;
import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.repository.AccountRepository;
import edu.hcmus.project.ebanking.backoffice.repository.DebtRepository;
import edu.hcmus.project.ebanking.backoffice.repository.UserRepository;
import edu.hcmus.project.ebanking.backoffice.repository.SavedAccountRepository;
import edu.hcmus.project.ebanking.backoffice.resource.debt.dto.CreateDebtDto;
import edu.hcmus.project.ebanking.backoffice.resource.debt.dto.DebtDto;
import edu.hcmus.project.ebanking.backoffice.resource.debt.dto.DebtUserDto;
import edu.hcmus.project.ebanking.backoffice.resource.exception.ResourceNotFoundException;
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
    private SavedAccountRepository savedAccountRepository;

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

    public List<DebtDto> findDebtbyHolderOrDebtor() {
        Optional<User> userOp = userRepository.findById(JwtTokenUtil.getLoggedUser().getId());
        List<Account> accountOp = accountRepository.findAccountsByOwner(userOp.get());
        if(userOp.isPresent()){
            for (Account account : accountOp){
                return debtRepository.findDebtByHolderOrDebtor(userOp.get(), account).stream().map(debt -> {
                    DebtDto dto = new DebtDto();
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

    public List<DebtDto> findNewDebtByDebtor(){
        Optional<Account> accountOp = accountRepository.findAccountByOwner(JwtTokenUtil.getLoggedUser());
        if(accountOp.isPresent()){
            return debtRepository.findNewDebtByDebtorAndStatus(accountOp.get(), DebtStatus.NEW).stream().map(debt -> {
                DebtDto dto = new DebtDto();
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
                Debt upDebt = new Debt();
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

}
