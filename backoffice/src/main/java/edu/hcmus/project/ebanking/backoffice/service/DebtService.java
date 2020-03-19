package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.Account;
import edu.hcmus.project.ebanking.backoffice.model.Debt;
import edu.hcmus.project.ebanking.backoffice.model.contranst.DebtStatus;
import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.repository.AccountRepository;
import edu.hcmus.project.ebanking.backoffice.repository.DebtRepository;
import edu.hcmus.project.ebanking.backoffice.repository.UserRepository;
import edu.hcmus.project.ebanking.backoffice.resource.debt.DebtDto;
import edu.hcmus.project.ebanking.backoffice.resource.exception.ResourceNotFoundException;
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

    public List<DebtDto> GetAllDebt(){
        return debtRepository.findAll().stream().map(debt -> {
            DebtDto dto = new DebtDto();
            dto.setStatus(debt.getStatus());
            dto.setHolder(debt.getHolder().getId());
            dto.setDebtor(debt.getDebtor().getId());
            dto.setDebtor_acc(debt.getDebtor_acc().getAccountId());
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
            dto.setDebtor(debt.getDebtor().getId());
            dto.setDebtor_acc(debt.getDebtor_acc().getAccountId());
            dto.setContent(debt.getContent());
            dto.setAmount(debt.getAmount());
            return dto;
        }
        throw new ResourceNotFoundException("Debt not found");
    }

    public List<DebtDto> findDebtbyHolderOrDebtor(long userid) {
        Optional<User> userOp = userRepository.findById(userid);
        if (userOp.isPresent()) {
            return debtRepository.findDebtByHolderOrDebtor(userOp.get(), userOp.get()).stream().map(debt -> {
                DebtDto dto = new DebtDto();
                dto.setStatus(debt.getStatus());
                dto.setHolder(debt.getHolder().getId());
                dto.setDebtor(debt.getDebtor().getId());
                dto.setDebtor_acc(debt.getDebtor_acc().getAccountId());
                dto.setContent(debt.getContent());
                dto.setAmount(debt.getAmount());
                return dto;
            }).collect(Collectors.toList());
        }
        return null;
    }

    public List<DebtDto> findNewDebtByDebtor(long userid){
        Optional<User> userOp = userRepository.findById(userid);
        if(userOp.isPresent()){
            return debtRepository.findNewDebtByDebtorAndStatus(userOp.get(), DebtStatus.NEW).stream().map(debt -> {
                DebtDto dto = new DebtDto();
                dto.setStatus(debt.getStatus());
                dto.setHolder(debt.getHolder().getId());
                dto.setDebtor(debt.getDebtor().getId());
                dto.setDebtor_acc(debt.getDebtor_acc().getAccountId());
                dto.setContent(debt.getContent());
                dto.setAmount(debt.getAmount());
                return dto;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Transactional
    public boolean createDebt(DebtDto dto){
        Optional<User> Debtorop = userRepository.findById(dto.getDebtor());
        Optional<User> Holderop = userRepository.findById(dto.getHolder());
        if (Debtorop.isPresent()) {
            Optional<Account> accop = accountRepository.findById(dto.getDebtor_acc());
            if (accop.isPresent()) {
                Debt newDebt = new Debt();
                newDebt.setCreateDate(new Date());
                newDebt.setStatus(DebtStatus.NEW);
                newDebt.setHolder(Holderop.get());
                newDebt.setDebtor(Debtorop.get());
                newDebt.setDebtor_acc(accop.get());
                newDebt.setContent(dto.getContent());
                newDebt.setAmount(dto.getAmount());
                debtRepository.save(newDebt);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean updateDebt(DebtDto dto, int id){
        Optional<Debt> DebtOp = debtRepository.findById(id);
        Optional<User> Debtorop = userRepository.findById(dto.getDebtor());
        Optional<User> Holderop = userRepository.findById(dto.getHolder());
        Optional<Account> accop = accountRepository.findById(dto.getDebtor_acc());
        if(DebtOp.isPresent()) {
            if(Debtorop.isPresent()){
                if(accop.isPresent()){
                    Debt upDebt = new Debt();
                    upDebt.setCreateDate(new Date());
                    upDebt.setStatus(dto.getStatus());
                    upDebt.setHolder(Holderop.get());
                    upDebt.setDebtor(Debtorop.get());
                    upDebt.setDebtor_acc(accop.get());
                    upDebt.setContent(dto.getContent());
                    upDebt.setAmount(dto.getAmount());
                    debtRepository.save(upDebt);
                    return true;
                }
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

    public boolean changeStatus(DebtDto dto, int id){
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
