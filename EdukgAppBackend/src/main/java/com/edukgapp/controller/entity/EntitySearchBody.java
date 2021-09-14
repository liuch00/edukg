package com.edukgapp.controller.entity;

public class EntitySearchBody {
	private EntitySearchInnerBody[] data;
	private Integer code;
	private String msg;

	public void setData(EntitySearchInnerBody[] data) {
		this.data = data;
	}

	public EntitySearchInnerBody[] getData() {
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
