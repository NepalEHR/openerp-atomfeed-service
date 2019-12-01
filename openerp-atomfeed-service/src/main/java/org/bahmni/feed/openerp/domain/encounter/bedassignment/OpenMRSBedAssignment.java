package org.bahmni.feed.openerp.domain.encounter.bedassignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bahmni.feed.openerp.ObjectMapperRepository;
import org.bahmni.feed.openerp.domain.OpenMRSPatient;
import org.bahmni.feed.openerp.domain.encounter.OpenERPOrder;
import org.bahmni.feed.openerp.domain.encounter.OpenERPOrders;
import org.bahmni.feed.openerp.domain.encounter.OpenMRSEncounterEvent;
import org.bahmni.openerp.web.OpenERPException;
import org.bahmni.openerp.web.request.builder.Parameter;
import org.bahmni.openerp.web.service.ProductService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSBedAssignment extends OpenMRSEncounterEvent {
	private OpenMRSEncounter encounter;
	private OpenMRSPatient patient;
	private OpenMRSBed bed;
	private String uuid;
	
	private final String BED_TYPE_ORDER = "Bed Order";
	private final String ACTION_TYPE_NEW = "New";
	
	@Override
	public String toString() {
		return "OpenMRSBedAssignment [encounter=" + encounter + ", patient=" + patient + ", bed=" + bed + "]";
	}

	public boolean shouldERPConsumeEvent() {
		return bed != null && patient != null && encounter != null;
	}
	
	private static Logger logger = Logger.getLogger(OpenMRSBedAssignment.class);
	
	public List<Parameter> getParameters(String eventId, ProductService productService, String feedURIForLastReadEntry, String feedURI)
			throws IOException {
		logger.error("\n\n\n\n\nInside getParameters \n\n\n\n\n");
		validateUrls(feedURIForLastReadEntry, feedURI);

		List<Parameter> parameters = new ArrayList<>();

		String patientId = patient.getDisplay().split(" ")[0];

		parameters.add(createParameter("category", "create.sale.order", "string"));
		parameters.add(createParameter("customer_id", patientId, "string"));
		parameters.add(createParameter("encounter_id", encounter.getUuid(), "string"));
		parameters.add(createParameter("feed_uri", feedURI, "string"));
		parameters.add(createParameter("last_read_entry_id", eventId, "string"));
		parameters.add(createParameter("feed_uri_for_last_read_entry", feedURIForLastReadEntry, "string"));
		parameters.add(createParameter("orders", getOrderJson(productService, bed, encounter), "string"));

		return parameters;
	}

	private String getOrderJson(ProductService productService, OpenMRSBed bed,
			OpenMRSEncounter encounter) throws IOException {
		logger.error("\n\n\n\n\nInside getOrderJson \n\n\n\n\n");
		String productId = productService.findProductByName(bed.getBedType().getName());
		
		logger.error("\n\n\n\n productId:"+productId);

		if (productId == null)
			throw new OpenERPException("Product " + bed.getBedType().getName() + " not Found");

		OpenERPOrder openERPOrder = new OpenERPOrder();
//		openERPOrder.setVisitId(encounter.getVisit().getUuid());
		//        openERPOrder.setVisitType(encounter.getVisit().getVisitType());
		//        openERPOrder.setDescription(encounter.getVisit().getDescription());

		openERPOrder.setProductId(productId);
		openERPOrder.setProductName(bed.getBedType().getName());
		openERPOrder.setQuantity((double) 1);
		openERPOrder.setQuantityUnits("Day(s)");
		openERPOrder.setEncounterId(encounter.getUuid());
		openERPOrder.setVisitId(encounter.getVisit().getUuid());
		
		openERPOrder.setVisitType(encounter.getVisit().getVisitType().getName());
		openERPOrder.setType(BED_TYPE_ORDER);
		openERPOrder.setAction(ACTION_TYPE_NEW);
		openERPOrder.setOrderId(uuid);

		OpenERPOrders orders = new OpenERPOrders(bed.getId());
		orders.getOpenERPOrders().add(openERPOrder);

		return ObjectMapperRepository.objectMapper.writeValueAsString(orders);
	}

	public OpenMRSPatient getPatient() {
		return patient;
	}

	public OpenMRSBed getBed() {
		return bed;
	}

	public OpenMRSEncounter getEncounter() {
		return encounter;
	}

	public String getUuid() {
		return uuid;
	}
	
}
