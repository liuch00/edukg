package com.edukgapp.security;

import com.edukgapp.database.Account;
import com.edukgapp.database.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

// 一个网络请求控制器类，处理登录和注册请求，但验证不会经由这里
@RestController
@CrossOrigin
@RequestMapping("/auth")
public class JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AccountRepository accountRepository;

	// 登录，生成凭证
	@GetMapping("/token")
	public ResponseEntity<String> createAuthenticationToken(
			@RequestParam("username") String username,
			@RequestParam("password") String password
	) {
		try {
			// 校验用户名与密码是否对应
			authenticate(username, password);
			// 获取用户名详情
			final UserDetails userDetails =
					jwtUserDetailsService.loadUserByUsername(username);
			// 生成 token
			final String token = jwtTokenUtil.generateToken(userDetails);
			return ResponseEntity.ok(token);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Username and password not matching");
		}
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, password)
			);
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

	// 注册
	@PostMapping("/account")
	public ResponseEntity<String> createAccount(@RequestBody AuthRequest authRequest) {
		final String username = authRequest.getUsername();
		final String password = authRequest.getPassword();

		if (jwtUserDetailsService.isUsernameOccupied(username)) { // 用户名已占用
			return ResponseEntity.badRequest().body("Username already occupied");
		}
		else { // 正常注册
			Account account = new Account();
			account.setName(username);
			account.setPassword(passwordEncoder.encode(password));
			accountRepository.save(account);
			return createAuthenticationToken(username, password);
		}
	}
}
