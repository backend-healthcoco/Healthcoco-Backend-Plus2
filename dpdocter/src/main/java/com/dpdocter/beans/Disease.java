package com.dpdocter.beans;

public class Disease {
		private String id;
		
		private String disease;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDisease() {
			return disease;
		}

		public void setDisease(String disease) {
			this.disease = disease;
		}

		@Override
		public String toString() {
			return "Disease [id=" + id + ", disease=" + disease + "]";
		}
}
