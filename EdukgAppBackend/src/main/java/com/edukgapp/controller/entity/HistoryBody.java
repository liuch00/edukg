package com.edukgapp.controller.entity;

public class HistoryBody {
	private String label;
	private String course;
	private Long time;

	public HistoryBody(String label, String course, Long time) {
		this.label = label;
		this.course = course;
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

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
}
