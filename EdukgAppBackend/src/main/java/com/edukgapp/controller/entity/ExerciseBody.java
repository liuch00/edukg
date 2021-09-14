package com.edukgapp.controller.entity;

public class ExerciseBody {
	private ExerciseInnerBody[] data;
	private Integer code;
	private String msg;

	public ExerciseInnerBody[] getData() {
		return data;
	}

	public void setData(ExerciseInnerBody[] data) {
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
