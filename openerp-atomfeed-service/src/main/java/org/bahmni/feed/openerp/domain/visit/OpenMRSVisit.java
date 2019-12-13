package org.bahmni.feed.openerp.domain.visit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSVisit {

    private String uuid;
    private List<VisitAttributes> attributes = new ArrayList<>();
    private OpenMRSVisitType visitType;

    public List<VisitAttributes> getAttributes() {
        return attributes;
    }

    public String getUuid() {
        return uuid;
    }

	public OpenMRSVisitType getVisitType() {
		return visitType;
	}
}
