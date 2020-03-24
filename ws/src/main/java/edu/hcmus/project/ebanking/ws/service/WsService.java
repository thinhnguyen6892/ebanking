package edu.hcmus.project.ebanking.ws.service;

import edu.hcmus.project.ebanking.ws.config.exception.BadRequestException;
import edu.hcmus.project.ebanking.ws.model.Account;
import edu.hcmus.project.ebanking.ws.model.User;
import edu.hcmus.project.ebanking.ws.repository.AccountRepository;
import edu.hcmus.project.ebanking.ws.resource.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class WsService {

    @Autowired
    private AccountRepository accountRepository;

    public CustomerDto findAccountInfo(String accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if(!accountOpt.isPresent()) {
            throw new BadRequestException("Account is not exist!");
        }
        Account account = accountOpt.get();
        User receiver = account.getOwner();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setAccountId(account.getAccountId());
        customerDto.setFirstName(receiver.getFirstName());
        customerDto.setLastName(receiver.getLastName());
        return customerDto;
    }

}
