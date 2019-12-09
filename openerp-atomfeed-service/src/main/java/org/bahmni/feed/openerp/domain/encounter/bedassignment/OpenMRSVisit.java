package org.bahmni.feed.openerp.domain.encounter.bedassignment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSVisit {
	private String uuid;
	private OpenMRSVisitType visitType;
	
	@Override
	public String toString() {
		return "OpenMRSBedVisit [uuid=" + uuid + ", visitType=" + visitType + "]";
	}

	public String getUuid() {
		return uuid;
	}

	public OpenMRSVisitType getVisitType() {
		return visitType;
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class OpenMRSVisitType {
		private String uuid;
		private String name;
		private String display;
		private Boolean retired;
		
		
		@Override
		public String toString() {
			return "OpenMRSBedVisitType [uuid=" + uuid + ", name=" + name
					+ ", retired=" + retired + "]";
		}

		public String getUuid() {
			return uuid;
		}

		public String getName() {
			return name;
		}

		public Boolean getRetired() {
			return retired;
		}

		public String getDisplay() {
			return display;
		}

	}


}
