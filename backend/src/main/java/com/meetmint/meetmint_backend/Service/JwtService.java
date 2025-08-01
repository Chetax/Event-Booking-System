package com.meetmint.meetmint_backend.Service;

import com.meetmint.meetmint_backend.Dto.UserRequestDto;
import com.meetmint.meetmint_backend.Model.User;
import com.meetmint.meetmint_backend.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtService {

    private final UserRepository userRepository;

    private String secreteKey=null;

    public JwtService( UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(User UserEmailPassword) {
        User user= userRepository.findByEmail( UserEmailPassword.getEmail());
        HashMap<String,Object>claims=new HashMap<>();
        claims.put("role", user.isOrganiser() ? "ADMIN" : "USER");
        return Jwts
                .builder()
                .claims()
                .add(claims)
                .subject( UserEmailPassword.getEmail())
                .issuer( UserEmailPassword.getFirstName()+" "+ UserEmailPassword.getLastName())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+60*60*1000))
                .and()
                .signWith(generateKey())
                .compact();
    }

    public SecretKey generateKey(){
        byte[] decodekey= Decoders.BASE64.decode(getSecreteKey());
        return Keys.hmacShaKeyFor(decodekey);
    }
    public String getSecreteKey()
    {
        return secreteKey="CX0PH373LibivzYlzkQAjNKuaHFOyZCTeY7b6vwq+So=";
    }

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }
    public String extractUserRole(String token) {
        return extractClaims(token, claims -> claims.get("role", String.class));
    }


    private <T>T extractClaims(String token, Function<Claims,T>claimsResolver ) {
        Claims claims=extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String token){
        return   Jwts.parser().verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public String extractEmail(String token) {
        return extractUserName(token); // Subject of the token is email
    }
    public boolean isValidToken(String token, String email) {
        final String userName=extractUserName(token);
        return (userName.equals(email) && !isTokenExpired(token));

    }
    public boolean isAdmin(String token) {
        String role = extractUserRole(token);
        System.out.println("ROLE "+role+"\n\n\n\n\n\n\n\n\n\n");
        return !"ADMIN".equals(role);
    }

    private boolean isTokenExpired(String token) {
        return extracExpiration(token).before(new Date());
    }
    private Date extracExpiration(String token) {
        return extractClaims(token,Claims::getExpiration);
    }

}
