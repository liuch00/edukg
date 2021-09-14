package com.edukgapp.controller.entity;

public class EntityDetailInnerBody {

	private static class Property {
		private String predicate;
		private String predicateLabel;
		private String object;

		public Property() {}

		public void setPredicate(String predicate) {
			this.predicate = predicate;
		}

		public String getPredicate() {
			return predicate;
		}

		public void setPredicateLabel(String predicateLabel) {
			this.predicateLabel = predicateLabel;
		}

		public String getPredicateLabel() {
			return predicateLabel;
		}

		public void setObject(String object) {
			this.object = object;
		}

		public String getObject() {
			return object;
		}
	}

	private static class Content {
		private String predicate;
		private String predicate_label;
		private String object_label;
		private String object;
		private String subject_label;
		private String subject;

		public Content() {}

		public void setPredicate(String predicate) {
			this.predicate = predicate;
		}

		public String getPredicate() {
			return predicate;
		}

		public void setPredicate_label(String predicate_label) {
			this.predicate_label = predicate_label;
		}

		public String getPredicate_label() {
			return predicate_label;
		}

		public void setObject(String object) {
			this.object = object;
		}

		public String getObject() {
			return object;
		}

		public void setObject_label(String object_label) {
			this.object_label = object_label;
		}

		public String getObject_label() {
			return object_label;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject_label(String subject_label) {
			this.subject_label = subject_label;
		}

		public String getSubject_label() {
			return subject_label;
		}
	}

	private String label;
	private Property[] property;
	private Content[] content;
	private String uri;

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setProperty(Property[] property) {
		this.property = property;
	}

	public Property[] getProperty() {
		return property;
	}

	public void setContent(Content[] content) {
		this.content = content;
	}

	public Content[] getContent() {
		return content;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
