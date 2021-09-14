package com.edukgapp.controller.neo;

public class FrameworkResponse {
	private String uri;
	private String label;
	private String rel;
	private FrameworkResponse[] sub;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public FrameworkResponse[] getSub() {
		return sub;
	}

	public void setSub(FrameworkResponse[] sub) {
		this.sub = sub;
	}
}
