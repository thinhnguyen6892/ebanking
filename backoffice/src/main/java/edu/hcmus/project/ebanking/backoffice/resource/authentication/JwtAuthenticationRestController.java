package edu.hcmus.project.ebanking.backoffice.resource.authentication;

import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.resource.exception.TokenException;
import edu.hcmus.project.ebanking.backoffice.resource.user.dto.UserDto;
import edu.hcmus.project.ebanking.backoffice.security.jwt.JwtTokenUtil;
import edu.hcmus.project.ebanking.backoffice.service.CaptchaValidator;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Objects;

@RestController
public class JwtAuthenticationRestController {

    @Value("${jwt.http.request.header}")
    private String tokenHeader;

    @Value("${app.dev.mode}")
    private Boolean devMode;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private CaptchaValidator captchaValidator;

    @ApiOperation(value = "Request an access token.", response = JwtTokenResponse.class)
    @RequestMapping(value = "${jwt.get.token.uri}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(JwtTokenRequest authenticationRequest)
            throws JwtAuthenticationException {
        if(!captchaValidator.validateCaptcha(authenticationRequest.getReCAPTCHA()) && !devMode){
            throw new TokenException("Captcha is not valid");
        }

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final User userDetails = (User) jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);
        final String role = Base64.getEncoder().encodeToString(userDetails.getRole().getRoleId().getBytes());
        return ResponseEntity.ok(new JwtTokenResponse(token, role, new UserDto(userDetails)));
    }

    @RequestMapping(value = "${jwt.refresh.token.uri}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(tokenHeader);
        final String token = authToken.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);

        final User userDetails = (User)jwtUserDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.canTokenBeRefreshed(token)) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            final String role = Base64.getEncoder().encodeToString(userDetails.getRole().getRoleId().getBytes());
            return ResponseEntity.ok(new JwtTokenResponse(refreshedToken, role, new UserDto(userDetails)));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @ExceptionHandler({JwtAuthenticationException.class})
    public ResponseEntity<String> handleAuthenticationException(JwtAuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    private void authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new JwtAuthenticationException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new JwtAuthenticationException("INVALID_CREDENTIALS", e);
        }
    }

}
