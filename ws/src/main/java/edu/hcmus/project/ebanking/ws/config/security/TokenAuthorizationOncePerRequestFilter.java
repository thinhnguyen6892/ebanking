package edu.hcmus.project.ebanking.ws.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenAuthorizationOncePerRequestFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserDetailsService clientDetailsService;


    @Value("${http.request.header}")
    private String tokenHeader;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        logger.debug("Authentication Request For '{}'", request.getRequestURL());

        final String requestTokenHeader = request.getHeader(this.tokenHeader);

        String clientId = null;
        String token = null;
        String tokenPart = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            token = requestTokenHeader.substring(7);
            Integer clientIndex = token.indexOf('.');
            if(clientIndex < 0) {
                logger.warn("TOKEN_DOES_NOT_VALID");
            } else {
                clientId = token.substring(0, clientIndex);
                tokenPart = token.substring(clientIndex+1);
            }
        } else {
            logger.warn("TOKEN_DOES_NOT_START_WITH_BEARER_STRING");
        }

        logger.debug("TOKEN_APP_ID_VALUE '{}'", clientId);
        if (clientId != null && tokenPart != null &&SecurityContextHolder.getContext().getAuthentication() == null) {

            ClientDetails userDetails = (ClientDetails) this.clientDetailsService.loadUserByUsername(clientId);
            if(bCryptPasswordEncoder.matches(String.format("%s.%s", clientId, userDetails.getSecret()), tokenPart)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        chain.doFilter(request, response);
    }

}
