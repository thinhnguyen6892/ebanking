package edu.hcmus.project.ebanking.ws.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.hcmus.project.ebanking.ws.model.SignType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientDetails implements UserDetails {

    private final String clientId;
    private final String password;
    private final String secret;
    private final byte[] key;
    private final SignType signType;
    private final Collection<? extends GrantedAuthority> authorities;

    public ClientDetails(String clientId, String password, String secret, SignType signType, byte[] key) {
        this.clientId = clientId;
        this.password = password;
        this.secret = secret;
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        this.authorities = authorities;
        this.signType = signType;
        this.key = key;
    }

    @Override
    public String getUsername() {
        return clientId;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getSecret() {
        return secret;
    }

    public byte[] getKey() {
        return key;
    }

    public SignType getSignType() {
        return signType;
    }
}
