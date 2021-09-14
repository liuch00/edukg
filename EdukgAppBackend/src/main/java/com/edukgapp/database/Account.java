package com.edukgapp.database;

import javax.persistence.*;
import java.util.List;

// 用户账号
@Entity
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id; // 自动生成，不使用

	private String name;
	private String password; // 规定存储加密后的串

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Favorite> favoriteList; // 收藏

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<History> historyList; // 历史记录

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public List<Favorite> getFavoriteList() {
		return favoriteList;
	}

	public void setFavoriteList(List<Favorite> favoriteList) {
		this.favoriteList = favoriteList;
	}

	public List<History> getHistoryList() {
		return historyList;
	}

	public void setHistoryList(List<History> historyList) {
		this.historyList = historyList;
	}
}
