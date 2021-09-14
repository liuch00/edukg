package com.edukgapp.database;

import javax.persistence.*;

// 历史记录
@Entity
public class History {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long hisId;

	private Long time; // 浏览时间
	private String label; // 标签
	private String course; // 学科代号

	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
	@JoinColumn(name = "id")
	private Account account; // 与账号关联

	public Long getHisId() {
		return hisId;
	}

	public void setHisId(Long hisId) {
		this.hisId = hisId;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
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
