package org.bahmni.feed.openerp.domain.encounter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bahmni.feed.openerp.ObjectMapperRepository;
import org.bahmni.feed.openerp.domain.visit.OpenMRSVisit;
import org.bahmni.feed.openerp.domain.visit.VisitAttributes;
import org.bahmni.openerp.web.request.builder.Parameter;


public class MapERPOrders extends OpenMRSEncounterEvent {

    private OpenMRSEncounter openMRSEncounter;
    private OpenMRSVisit openMRSVisit;

    public MapERPOrders(OpenMRSEncounter openMRSEncounter, OpenMRSVisit openMRSVisit) {
        this.openMRSEncounter = openMRSEncounter;
        this.openMRSVisit = openMRSVisit;

    }

    public List<Parameter> getParameters(String eventId, String feedURIForLastReadEntry, String feedURI) throws IOException {
        List<Parameter> parameters = new ArrayList<>();
        validateUrls(feedURIForLastReadEntry, feedURI);
        if (openMRSEncounter.getEncounterType().compareToIgnoreCase("REG") == 0) {
        	parameters.add(createParameter("category", "update.attributes", "string"));
        }
        else {
        	parameters.add(createParameter("category", "create.sale.order", "string"));
        }
        parameters.add(createParameter("ref", openMRSEncounter.getPatientId(), "string"));
        parameters.add(createParameter("uuid", openMRSEncounter.getPatientUuid(), "string"));
        parameters.add(createParameter("customer_id", openMRSEncounter.getPatientId(), "string"));
        parameters.add(createParameter("encounter_id", openMRSEncounter.getEncounterUuid(), "string"));
        parameters.add(createParameter("feed_uri", feedURI, "string"));
        parameters.add(createParameter("last_read_entry_id", eventId, "string"));
        parameters.add(createParameter("feed_uri_for_last_read_entry", feedURIForLastReadEntry, "string"));
        parameters.add(createParameter("orders", mapOpenERPOrders(), "string"));
        parameters.add(createParameter("locationName", openMRSEncounter.getLocationName(), "string"));
        parameters.add(createParameter("attributes", openMRSEncounter.getgroupMembersJSON(), "string"));
        return parameters;
    }


    private String mapOpenERPOrders() throws IOException {
        OpenERPOrders openERPOrders = new OpenERPOrders(openMRSEncounter.getEncounterUuid());
        List<Provider> providers = openMRSEncounter.getProviders();

        List<OpenMRSObservation> observations = openMRSEncounter.getObservations();
        String providerName = providers.size() != 0 ? providers.get(0).getName() : "";
        for (OpenMRSDrugOrder drugOrder : openMRSEncounter.getDrugOrders()) {
            if(drugOrder.getDrugNonCoded() != null) {
                continue;
            }
            OpenERPOrder openERPOrder = new OpenERPOrder();
            openERPOrder.setVisitId(openMRSEncounter.getVisitUuid());
            openERPOrder.setOrderId(drugOrder.getUuid());
            openERPOrder.setDispensed("false");
            if(observations.size() != 0){
                for (OpenMRSObservation observation : observations) {
                    if(openERPOrder.getOrderId().equals(observation.getOrderUuid())){
                        openERPOrder.setDispensed(observation.getValue());
                    }
                }
            }
            openERPOrder.setPreviousOrderId(drugOrder.getPreviousOrderUuid());
            openERPOrder.setEncounterId(openMRSEncounter.getEncounterUuid());
            openERPOrder.setProductId(drugOrder.getDrugUuid());
            openERPOrder.setProductName(drugOrder.getDrugName());
            openERPOrder.setAction(drugOrder.getAction());
            openERPOrder.setQuantity(drugOrder.getQuantity());
            openERPOrder.setQuantityUnits(drugOrder.getQuantityUnits());
            openERPOrder.setVoided(drugOrder.isVoided());
            openERPOrder.setType(drugOrder.getOrderType());
            openERPOrder.setVisitType(getVisitType());
            openERPOrder.setProviderName(providerName);
            openERPOrder.setConceptName(drugOrder.getConceptName());
            openERPOrders.add(openERPOrder);

        }



        for (OpenMRSOrder order : openMRSEncounter.getOrders()) {
            OpenERPOrder openERPOrder = new OpenERPOrder();
            openERPOrder.setVisitId(openMRSEncounter.getVisitUuid());
            openERPOrder.setOrderId(order.getUuid());
            openERPOrder.setDispensed("false");
            openERPOrder.setPreviousOrderId(order.getPreviousOrderUuid());
            openERPOrder.setEncounterId(openMRSEncounter.getEncounterUuid());
            openERPOrder.setProductId(order.getConceptUuid());
            openERPOrder.setProductName(order.getConceptName());
            openERPOrder.setAction(order.getAction());
            openERPOrder.setQuantity((double) 1);
            openERPOrder.setQuantityUnits("Unit(s)");
            openERPOrder.setVoided(order.isVoided());
            openERPOrder.setType(order.getOrderType());
            openERPOrder.setVisitType(getVisitType());
            openERPOrder.setProviderName(providerName);
            openERPOrders.add(openERPOrder);
        }

        if (openMRSEncounter.getEncounterType().compareToIgnoreCase("REG") == 0){
        	//Add registration fee as order
            for (OpenMRSObservation observation : observations) {
            	List<OpenMRSObservationGroupMember> groupMembers = observation.getgroupMembers();
            	for (OpenMRSObservationGroupMember groupMember :groupMembers  ) {
            		if (groupMember.getconcept().getconceptClass().compareToIgnoreCase("RegFee") == 0 && 
            		   (groupMember.getvalueAsString().compareToIgnoreCase("Yes") == 0)) {
	                    OpenERPOrder openERPOrder = new OpenERPOrder();
	                    openERPOrder.setVisitId(openMRSEncounter.getVisitUuid());
	                    openERPOrder.setOrderId(openMRSEncounter.getVisitUuid());
	                    openERPOrder.setDispensed("false");
	                    openERPOrder.setPreviousOrderId("");
	                    openERPOrder.setEncounterId(openMRSEncounter.getEncounterUuid());
	                    openERPOrder.setProductId(groupMember.getconceptUuid());
	                    openERPOrder.setProductName(groupMember.getconceptNameToDisplay());
	                    openERPOrder.setAction("NEW");
	                    openERPOrder.setQuantity((double) 1);
	                    openERPOrder.setQuantityUnits("Unit(s)");
	                    openERPOrder.setVoided(false);
	                    openERPOrder.setType("Registration Fee");
	                    openERPOrder.setVisitType(getVisitType());
	                    openERPOrder.setProviderName(providerName);
	                    openERPOrders.add(openERPOrder);
	                    break;
            		}
            	}
            }
        }
        return ObjectMapperRepository.objectMapper.writeValueAsString(openERPOrders);
    }
    
    private String getVisitType() {
        for (VisitAttributes visitAttribute : openMRSVisit.getAttributes()) {
            if (visitAttribute.getAttributeType().getDisplay().equals("Visit Status")) {
                return visitAttribute.getValue();
            }
        }
        return null;
    }


}
