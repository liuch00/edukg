package com.edukgapp.controller.entity;

// 接受 post 请求的通用模板
public class GeneralInputBody {
	private String course;
	private String text;

	public void setCourse(String course) {
		this.course = course;
	}

	public String getCourse() {
		return course;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
