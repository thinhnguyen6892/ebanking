package edu.hcmus.project.ebanking.backoffice.security.jwt;

import edu.hcmus.project.ebanking.backoffice.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.DateUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtTokenUtil implements Serializable {

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_CREATED = "iat";
    private Clock clock = DefaultClock.INSTANCE;

    @Value("${jwt.signing.key.secret}")
    private String secret;

    @Value("${jwt.token.expiration.in.seconds}")
    private Long expiration;

    @Value("${jwt.token.refresh.expiration.in.minutes}")
    private Long refreshExpiration;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getUsernameFromTokenIgnoreExpired(String token, boolean ignored) {
        if(ignored) {
            try {
                return getClaimFromToken(token, Claims::getSubject);
            } catch (ExpiredJwtException e) {
                return e.getClaims().getSubject();
            }
        }
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token, boolean ignored) {
        if(ignored) {
            try {
                return getClaimFromToken(token, Claims::getExpiration);
            } catch (ExpiredJwtException e) {
                return e.getClaims().getExpiration();
            }
        }
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token, false);
            return expiration.before(clock.now());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getExpirationDateFromToken(token, true));
        calendar.add(Calendar.MINUTE, refreshExpiration.intValue());
        final Date expiration = calendar.getTime();
        return !expiration.before(clock.now());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(createdDate)
                .setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public String refreshToken(String token, String subject) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        return Jwts.builder().setClaims(new HashMap<>()).setSubject(subject).setIssuedAt(createdDate)
                .setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails, String subject, boolean ignoreExpired) {
        User user = (User) userDetails;
        return (subject.equals(user.getUsername()) && (ignoreExpired || !isTokenExpired(token)));
    }

    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }

    public static User getLoggedUser(){
        return  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
