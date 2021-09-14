package com.edukgapp.security;

import com.edukgapp.database.Account;
import com.edukgapp.database.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// 根据用户名加载用户信息，上级调用者会用返回值做用户身份检验
@Service
public class JwtUserDetailsService implements UserDetailsService {
	@Autowired
	private AccountRepository accountRepository;

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		Account account = accountRepository.findByName(s); // 从数据库中取出用户
		if (account != null) {
			// 参数：用户名，密码(这里统一为加密后的串)，权限列表(这里用不到所以是空数组)
			return new User(account.getName(), account.getPassword(), new ArrayList<>());
		}
		else
			throw new UsernameNotFoundException("Use not found for " + s);
	}

	public Boolean isUsernameOccupied(String username) {
		Account account = accountRepository.findByName(username); // 从数据库中取出用户
		return account != null;
	}
}
