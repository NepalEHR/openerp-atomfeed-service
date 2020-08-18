package org.bahmni.feed.openerp.domain.visit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSVisitType {
	
	private String uuid;
	private String display;
	
	@Override
	public String toString() {
		return "OpenMRSBedVisitType [uuid=" + uuid + ", display=" + display + "]";
	}

	public String getUuid() {
		return uuid;
	}


	public String getDisplay() {
		return display;
	}
}
