package com.edukgapp.controller.entity;

public class TokenRequestEntity {

	private Integer code;
	private String data;
	private String msg;
	private String id;

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
