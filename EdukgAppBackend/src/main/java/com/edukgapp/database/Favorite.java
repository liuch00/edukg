package com.edukgapp.database;

import javax.persistence.*;

// 收藏
@Entity
public class Favorite {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long favId;

	private String label; // 标签
	private String course; // 学科代号

	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
	@JoinColumn(name = "id")
	private Account account; // 与账号关联

	public Long getFavId() {
		return favId;
	}

	public void setFavId(Long favId) {
		this.favId = favId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}
