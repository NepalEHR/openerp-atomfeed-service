package org.bahmni.feed.openerp.domain.encounter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSObservationGroupMember {
    private String valueAsString;
    private String conceptNameToDisplay;
    private String conceptUuid;
    private OpenMRSObservationGroupMemberConcept concept;
    
    public String getvalueAsString() {
        return valueAsString;
    }

    public void setvalueAsString(String valueAsString) {
        this.valueAsString = valueAsString;
    }

    public String getconceptNameToDisplay() {
        return conceptNameToDisplay;
    }

    public void setconceptNameToDisplay(String conceptNameToDisplay) {
        this.conceptNameToDisplay = conceptNameToDisplay;
    }

    public String getconceptUuid() {
        return conceptUuid;
    }

    public void setconceptUuid(String conceptUuid) {
        this.conceptUuid = conceptUuid;
    }

    public OpenMRSObservationGroupMemberConcept getconcept() {
        return concept;
    }

    public void OpenMRSObservationGroupMemberConcept(OpenMRSObservationGroupMemberConcept concept) {
        this.concept = concept;
    }

    public String toString() {
	    return "[" + conceptNameToDisplay + " " + valueAsString +
		       " " +"]";
	}
}