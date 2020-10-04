package org.bahmni.feed.openerp.domain.encounter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bahmni.feed.openerp.ObjectMapperRepository;
import org.apache.log4j.Logger;
import org.bahmni.feed.openerp.ObjectMapperRepository;
import org.bahmni.feed.openerp.domain.OpenMRSPersonAttribute;
import org.bahmni.feed.openerp.worker.OpenElisSaleOrderEventWorker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSObservation {
	private String encounterDateTime;
	private String visitStartDateTime;
	private String targetObsRelation;
    private List<OpenMRSObservationGroupMember> groupMembers = new ArrayList<>();
    private String conceptUuid;
    private String orderUuid;
    private String conceptNameToDisplay;
    private String value;

    public List<OpenMRSObservationGroupMember> getgroupMembers() {
        return groupMembers;
    }

    public void setgroupMembers(List<OpenMRSObservationGroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }
    
    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getConceptNameToDisplay() {
        return conceptNameToDisplay;
    }

    public void setConceptNameToDisplay(String conceptNameToDisplay) {
        this.conceptNameToDisplay = conceptNameToDisplay;
    }

    public String getconceptUuid() {
        return conceptUuid;
    }

    public void setconceptUuid(String conceptUuid) {
        this.conceptUuid = conceptUuid;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}