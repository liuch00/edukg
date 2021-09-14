package com.edukgapp.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// 应用信息
@Entity
public class AppInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String edukgUsername; // edukg 的用户名和密码，用于获取凭证
	private String edukgPassword;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setEdukgUsername(String edukgUsername) {
		this.edukgUsername = edukgUsername;
	}

	public String getEdukgUsername() {
		return edukgUsername;
	}

	public void setEdukgPassword(String edukgPassword) {
		this.edukgPassword = edukgPassword;
	}

	public String getEdukgPassword() {
		return edukgPassword;
	}
}
