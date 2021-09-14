package com.edukgapp.controller.entity;

public class InstanceLinkageInnerBody {

	private static class Result {
		private String entity_type;
		private String entity_url;
		private Long start_index;
		private Long end_index;
		private String entity;

		public void setEntity_type(String entity_type) {
			this.entity_type = entity_type;
		}

		public String getEntity_type() {
			return entity_type;
		}

		public void setEntity_url(String entity_url) {
			this.entity_url = entity_url;
		}

		public String getEntity_url() {
			return entity_url;
		}

		public void setStart_index(Long start_index) {
			this.start_index = start_index;
		}

		public Long getStart_index() {
			return start_index;
		}

		public void setEnd_index(Long end_index) {
			this.end_index = end_index;
		}

		public Long getEnd_index() {
			return end_index;
		}

		public void setEntity(String entity) {
			this.entity = entity;
		}

		public String getEntity() {
			return entity;
		}
	}

	private Result[] results;

	public void setResults(Result[] results) {
		this.results = results;
	}

	public Result[] getResults() {
		return results;
	}
}
