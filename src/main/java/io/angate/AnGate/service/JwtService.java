package io.angate.AnGate.service;

import io.angate.AnGate.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${Jwt.secretKey}")
    private String jwt_secret;

    public SecretKey secretKey(){
        return Keys.hmacShaKeyFor(jwt_secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Users users){
       return Jwts.builder()
               .subject(users.getEmailId())
               .claim("userId",users.getId())
               .claim("username",users.getEmailId())
               .claim("role",users.getRole())
               .claim("type","access")
               .issuedAt(new Date())
               .expiration(new Date(System.currentTimeMillis()+1000*60*10))
               .signWith(secretKey())
               .compact();
    }

    public String getUsernameFromToken(String token){
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

    }

    public boolean isTokenValid(String token , UserDetails userDetails){
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername());
    }

    public boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    public Date getExpiration(String token){
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public Long getUserIdFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }

    public String generateRefreshToken(Users users){
        return Jwts.builder()
                .subject(users.getId().toString())
                .claim("type","refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*60*60*24*7))
                .signWith(secretKey())
                .compact();
    }

    public Claims extractClaim(String token){
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
