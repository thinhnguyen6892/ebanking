package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.resource.account.dto.AccountDto;
import edu.hcmus.project.ebanking.data.model.Account;
import edu.hcmus.project.ebanking.data.model.SavedAccount;
import edu.hcmus.project.ebanking.data.model.User;
import edu.hcmus.project.ebanking.data.model.contranst.AccountType;
import edu.hcmus.project.ebanking.data.repository.AccountRepository;
import edu.hcmus.project.ebanking.data.repository.SavedAccountRepository;
import edu.hcmus.project.ebanking.backoffice.resource.exception.BadRequestException;
import edu.hcmus.project.ebanking.backoffice.resource.receiver.dto.CreateReceiverDto;
import edu.hcmus.project.ebanking.backoffice.resource.receiver.dto.ReceiverDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SavedAccountService {

    @Autowired
    private SavedAccountRepository repository;

    @Autowired
     private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;


    public List<ReceiverDto> search(User owner, String name) {
        return repository.findByOwnerAndNameSuggestionStartingWith(owner, name).stream()
                .map(savedAccount -> {
                    ReceiverDto dto = new ReceiverDto();
                    dto.setId(savedAccount.getId());
                    dto.setAccountId(savedAccount.getAccountId());
                    dto.setBankId(savedAccount.getBankId());
                    dto.setNameSuggestion(savedAccount.getNameSuggestion());
                    dto.setFirstName(savedAccount.getFirstName());
                    dto.setLastName(savedAccount.getLastName());
                    return dto;
                }).collect(Collectors.toList());
    }

    public List<ReceiverDto> findAll(User owner) {
        return repository.findByOwner(owner).stream()
                .map(savedAccount -> {
                    ReceiverDto dto = new ReceiverDto();
                    dto.setId(savedAccount.getId());
                    dto.setAccountId(savedAccount.getAccountId());
                    dto.setBankId(savedAccount.getBankId());
                    dto.setNameSuggestion(savedAccount.getNameSuggestion());
                    dto.setFirstName(savedAccount.getFirstName());
                    dto.setLastName(savedAccount.getLastName());
                    return dto;
                }).collect(Collectors.toList());
    }

    public CreateReceiverDto createReceiver(User owner, CreateReceiverDto dto) {
        String firstName = null, lastName = null, userName = null;
        if(StringUtils.isEmpty(dto.getBankId())) {
            Optional<Account> accountOpt = accountRepository.findByAccountIdAndTypeIsNot(dto.getAccountId(), AccountType.SYSTEM);
            if (!accountOpt.isPresent()) {
                throw new BadRequestException("Account is not exist!");
            }
            Account account = accountOpt.get();
            User receiver = account.getOwner();
            userName = receiver.getUsername();
            firstName = receiver.getFirstName();
            lastName = receiver.getLastName();

        } else {
            AccountDto accountDto = accountService.findBankAccount(dto.getAccountId(), dto.getBankId());
            userName = accountDto.getOwnerName();
        }

        Optional<SavedAccount> checker = repository.findByAccountId(dto.getAccountId());
        if(checker.isPresent()) {
            throw new BadRequestException("This account already saved");
        }

        SavedAccount savedAccount = new SavedAccount();
        savedAccount.setOwner(owner);
        savedAccount.setAccountId(dto.getAccountId());
        savedAccount.setFirstName(firstName);
        savedAccount.setLastName(lastName);
        savedAccount.setBankId(dto.getBankId());
        savedAccount.setNameSuggestion(StringUtils.isEmpty(dto.getNameSuggestion()) ? userName : dto.getNameSuggestion());
        repository.save(savedAccount);
        return dto;
    }

    public CreateReceiverDto updateReceiver(User owner, Integer id, CreateReceiverDto dto) {
        Optional<SavedAccount> savedAccountOpt = repository.findByIdAndOwner(id, owner);
        if(!savedAccountOpt.isPresent()) {
            throw new BadRequestException("Receiver is not exist.");
        }
        SavedAccount savedAccount = savedAccountOpt.get();
        if(StringUtils.isEmpty(dto.getBankId())) {
            Optional<Account> accountOpt = accountRepository.findByAccountIdAndTypeIsNot(dto.getAccountId(), AccountType.SYSTEM);
            if(!accountOpt.isPresent()) {
                throw new BadRequestException("Account is not exist!");
            }
            Account account = accountOpt.get();
            User receiver = account.getOwner();
            savedAccount.setAccountId(account.getAccountId());
            savedAccount.setFirstName(receiver.getFirstName());
            savedAccount.setLastName(receiver.getLastName());
            savedAccount.setNameSuggestion(StringUtils.isEmpty(dto.getNameSuggestion()) ? receiver.getUsername() : dto.getNameSuggestion());
            repository.save(savedAccount);
        }
        return dto;
    }

    public void delete(Integer id, User owner) {
        Optional<SavedAccount> savedAccountOpt = repository.findByIdAndOwner(id, owner);
        if(!savedAccountOpt.isPresent()) {
            throw new BadRequestException("Receiver is not exist.");
        }
        repository.delete(savedAccountOpt.get());
    }
}
