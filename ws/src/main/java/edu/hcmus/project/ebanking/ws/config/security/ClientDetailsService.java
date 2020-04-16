package edu.hcmus.project.ebanking.ws.config.security;

import edu.hcmus.project.ebanking.ws.model.Bank;
import edu.hcmus.project.ebanking.ws.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientDetailsService implements UserDetailsService {

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
        Optional<Bank> bankOpt = bankRepository.findById(clientId);
        if (!bankOpt.isPresent()) {
            throw new UsernameNotFoundException(String.format("CLIENT_NOT_FOUND '%s'.", clientId));
        } else {
            Bank bank = bankOpt.get();
            return new ClientDetails(bank.getId(), passwordEncoder.encode(String.format("%s.%s",bank.getId(), bank.getSecret())), bank.getSecret(), bank.getKey());
        }
    }
}
