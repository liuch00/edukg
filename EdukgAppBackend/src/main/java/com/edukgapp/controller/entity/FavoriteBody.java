package com.edukgapp.controller.entity;

public class FavoriteBody {
	private String course;
	private String label;

	public  FavoriteBody() {}

	public FavoriteBody(String course, String label) {
		this.course = course;
		this.label = label;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
