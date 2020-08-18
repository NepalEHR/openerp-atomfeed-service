package org.bahmni.feed.openerp.domain.encounter.bedassignment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSEncounter {
	private String uuid;
	private OpenMRSVisit visit;
	
	public String getUuid() {
		return uuid;
	}

	public OpenMRSVisit getVisit() {
		return visit;
	}

}
