package com.edukgapp.controller.entity;

public class EntityDetailBody {
	private EntityDetailInnerBody data;
	private Integer code;
	private String msg;

	public void setData(EntityDetailInnerBody data) {
		this.data = data;
	}

	public EntityDetailInnerBody getData() {
		return data;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
}
