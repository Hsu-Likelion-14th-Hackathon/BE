package com.boardingpass.be.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * JWT 발급/검증 유틸.
 * UserDetailsService 의존 없이 토큰 subject로 Authentication을 구성합니다.
 */
@Slf4j
@Component
public class JwtProvider {

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.expiration}")
  private long accessTokenExpirationMillis;

  private final long refreshTokenExpirationMillis = 3 * 60 * 60 * 1000L;

  private Key key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(Long userId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + accessTokenExpirationMillis);

    return Jwts.builder()
        .setSubject(userId.toString())
        .claim("type", "ACCESS")
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(Long userId, String uuid) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMillis);

    return Jwts.builder()
        .setSubject(userId.toString())
        .claim("uuid", uuid)
        .claim("type", "REFRESH")
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public void validateAccessToken(String token) {
    validateTokenWithType(token, "ACCESS");
  }

  public void validateRefreshToken(String token) {
    validateTokenWithType(token, "REFRESH");
  }

  private void validateTokenWithType(String token, String expectedType) {
    try {
      Claims claims = parseClaims(token);
      String type = claims.get("type", String.class);
      if (!expectedType.equals(type)) {
        throw new BadCredentialsException("유효하지 않은 토큰 타입입니다: " + type);
      }
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("JWT 인증 실패: {}", e.getMessage());
      throw new BadCredentialsException("유효하지 않은 JWT", e);
    }
  }

  public Authentication getAuthentication(String token) {
    String userId = getUserIdFromToken(token).toString();
    List<SimpleGrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    UserDetails userDetails = new User(userId, "", authorities);
    return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
  }

  public Long getUserIdFromToken(String token) {
    return Long.parseLong(parseClaims(token).getSubject());
  }

  public String getUuidFromToken(String token) {
    return parseClaims(token).get("uuid", String.class);
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
