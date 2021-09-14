package com.edukgapp.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 对每次请求进行过滤，检测是否存在有效的 token
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

		String username = null;
		String token = null;

		// 从 header 中取出 token
		if (requestTokenHeader != null) {
			token = requestTokenHeader;
			try {
				username = jwtTokenUtil.getUsernameFromToken(token);
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			}
		}
		else
			logger.warn("JWT token not found");

		// 检验 token 有效性
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
			if (jwtTokenUtil.validateToken(token, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
						new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities()
						);
				usernamePasswordAuthenticationToken.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request)
				);
				SecurityContextHolder.getContext()
						.setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}
}
