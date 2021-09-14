package com.edukgapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// 处理 Token 相关任务的工具类
@Component
public class JwtTokenUtil implements Serializable {

	private static final Long serialVersionUID = -2550185165626007488L;
	public static final Long JWT_TOKEN_VALIDITY = (long) 5 * 60 * 60;
	@Value("${jwt.secret}")
	private String secret;

	// --- 解析 token 的方法 --- //
	// 获取用户名
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// 获取过期时间
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	// 基本组件 使用某种方法提取特定信息
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// 基本组件 获取全部信息
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	// --- 生成 token 的方法 --- //
	// 定制化生成方法
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userDetails.getUsername());
	}

	// 通用生成方法
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()
				// 添加内容
				.setClaims(claims) // 传入的任意声明
				.setSubject(subject) // 主体
				.setIssuedAt(new Date(System.currentTimeMillis())) // 签发时间
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)) // 过期
				// 加密并打包
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	// --- 验证 token 的方法 --- //
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
}
